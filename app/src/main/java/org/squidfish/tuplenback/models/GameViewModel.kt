package org.squidfish.tuplenback.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.squidfish.tuplenback.data.LocalStorageRepository
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.engines.GameEngine

private const val TAG = "GameViewModel"

/**
 * The views pass events to this class
 *
 * TODO: The game should handle its game engines.
 *
 * @property[gameState] The [GameState] used to push data to the views.
 * @property[_gameState] [gameState], but mutable and used internally
 * @property[timerJob] Timer, used to count when a round will end in a game.
 * @property[gameEngines] List of game engines for the current [Game].
 * @property[game] The type of [Game] that is being played.
 *
 * @see[AppEvent]
 */
class GameViewModel(private val repository: LocalStorageRepository) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private val gameEngines = mutableMapOf<GameModule, GameEngine>()
    var game: Game? = null

    init {
        viewModelScope.launch {
            val prevGame = repository.getRecent()?.gameType

            if (prevGame != null) {
                game = prevGame
            }
        }
    }

    /**
     * Receive and handle view events
     *
     * @see[AppEvent]
     */
    fun onEvent(event: AppEvent) = when (event) {
        is AppEvent.PlayAgain -> {
            Log.i(TAG, "Playing again")
            playAgain()
        }
        is AppEvent.ResetGameState -> {
            Log.i(TAG, "Resetting game state")
            resetGameState()
        }
        is AppEvent.MakeMnemonicRepeatGuess -> {
            Log.i(TAG, "Handling button guess")
            handleGuess(event.gameMod)
        }
        is AppEvent.AbortOngoingGame -> {
            Log.i(TAG, "Aborting game")
            abortGame()
        }
        is AppEvent.StartGame -> {
            Log.i(TAG, "Starting game: $event.game")
            startGame(event.game)
        }
    }

    /**
     * Starts the recentmost non-abandoned game
     */
    private fun playAgain() {
        viewModelScope.launch {
            val prevGame = repository.getRecent()?.gameType

            if (prevGame == null) {
                return@launch
            }

            startGame(prevGame)
        }
    }

    /**
     * Starts a new game. Also handles all necessary initialization
     */
    private fun startGame(game: Game) {
        this.game = game

        gameEngines.clear()
        initGameEngines()
        initGameState()
        Log.i(TAG, "Starting game")
        startNewRound()
    }

    /**
     * Reset the game state. This does NOT change the loaded game modules i.e. their respective
     * engines.
     */
    private fun resetGameState() {
        gameEngines.forEach { (_, engine) ->
            engine.resetStats()
        }

        _gameState.update { GameState() }
    }

    /**
     * Create a new [GameEngine] for all game modules in [game]. If a [GameEngine] already exists,
     * it is overwritten.
     */
    private fun initGameEngines() {
        Log.i(TAG, "Initializing game engines")

        val game = requireNotNull(game) { throw IllegalStateException("Game cannot be null") }

        if (game.modules.isEmpty() == true) {
            throw IllegalArgumentException("No game types set in settings")
        }

        game.modules.forEach {
            gameEngines[it] = it.toGameEngine(game.settings.recallsBack, game.settings.repeatChance)
        }
    }

    /**
     * Set the correct game module information to the [gameState].
     */
    private fun initGameState() {
        Log.i(TAG, "Initializing game state")

        val game = requireNotNull(game) { throw IllegalStateException("Game cannot be null") }

        val gameStartTime = System.currentTimeMillis()

        game.modules.forEach { module ->
            _gameState.update {
                it.apply {
                    it.mnemonicIds[module] = 0
                    it.gameStatsModel[module] = GameStatsModel(
                        gameEndTime = gameStartTime,
                        gameType = game,
                        gameModule = module,
                        difficulty = game.settings.recallsBack,
                    )
                    it.recallCheck[module] = RecallCheck.NONE
                }
            }
        }
    }

    /**
     * Handles a mnemonic repeat guess event.
     *
     * When recall button is pressed, handleGuess is called. Updates some stats and changes
     * gameState, so that the recall button is coloured appropriately.
     *
     * @param[game] the game module the guess is made towards.
     *
     */
    private fun handleGuess(game: GameModule) {
        Log.d(TAG, "$game Button pressed")

        if (gameEngines[game] == null) {
            Log.e(TAG, "$game is not part of the loaded game engines")
            return
        }

        gameEngines[game]?.updateStats(true)
        _gameState.update {
            it.apply {
                it.recallCheck[game] =
                    if (gameEngines[game]?.isRepeat == true) RecallCheck.CORRECT else RecallCheck.INCORRECT
            }
        }
    }

    /**
     * Starts a new game round, that is, creates a new mnemonic and start the round timer
     * coroutine.
     *
     * @see [GameEngine.createNewState]
     */
    private fun startNewRound() {
        Log.d(TAG, "Starting new round")
        _gameState.update { it.copy(currentRound = it.currentRound + 1) }

        Log.d(TAG, "Generating round mnemonics")
        gameEngines.forEach { (game, gameEngine) ->
            _gameState.update { it.apply { it.mnemonicIds[game] = gameEngine.createNewState() } }
        }

        val game = requireNotNull(game) { throw IllegalStateException("Game cannot be null") }

        Log.d(TAG, "Creating timer coroutine")
        timerJob = viewModelScope.launch {
            for (time in 0..game.settings.milliPerRound step game.settings.timerUpdateInterval) {
                _gameState.update {
                    it.copy(roundProgress = time.toFloat() / game.settings.milliPerRound)
                }

                delay(game.settings.timerUpdateInterval)
            }

            endRound()
        }
    }

    /**
     * Ends round, i.e, stops the round timer, increments the round counter and resets the recall
     * check state. On game end, adds stats to the [GameState].
     */
    private fun endRound() {
        Log.d(TAG, "Ending round")

        // stop timer
        timerJob?.cancel()

        // updating missed recall stats and resetting button recall state
        gameEngines.forEach { (game, gameEngine) ->
            if (_gameState.value.recallCheck[game] == RecallCheck.NONE) {
                gameEngine.updateStats(false)
            } else {
                _gameState.update { it.apply { it.recallCheck[game] = RecallCheck.NONE } }
            }
        }

        val game = requireNotNull(game) { throw IllegalStateException("Game cannot be null") }

        // end round or end game and get stats
        if (_gameState.value.currentRound >= game.settings.totalRounds) {
            _gameState.update {
                it.copy(gameOver = true).apply {
                    gameEngines.forEach { (game, gameEngine) ->
                        if (gameStatsModel[game] == null) {
                            throw IllegalStateException("Game and GameStats module discrepancy")
                        }

                        val stats = gameEngine.getStats()

                        gameStatsModel[game]?.let { it1 ->
                            gameStatsModel[game] = it1.copy(
                                correctRecalls = stats.correctRecalls,
                                incorrectRecalls = stats.incorrectRecalls,
                                missedRecalls = stats.missedRecalls,
                            )
                        }
                    }
                }
            }

            // save stats
            viewModelScope.launch {
                for (stats in gameState.value.gameStatsModel) {
                    (repository as LocalStorageRepository).insert(stats.value)
                }
            }
        } else {
            startNewRound()
        }
    }

    /**
     * Ends the ongoing game before it has finished. Stats are not saved
     */
    private fun abortGame() {
        Log.i(TAG, "Aborting game")

        timerJob?.cancel()
    }

    override fun onCleared() {
        Log.d(TAG, "Ending game")
        timerJob?.cancel()
        super.onCleared()
    }
}
