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
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.engines.GameEngine
import org.squidfish.tuplenback.utils.BadConfigurationError
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.onError
import org.squidfish.tuplenback.utils.onSuccess

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
class GameViewModel(private val repository: RecentRepository<GameModel>) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private val gameEngines = mutableMapOf<GameModule, GameEngine>()
    var game: Game? = null
        private set

    /**
     * Get the [PlayerPerformanceStats] from all [GameEngine]s in use
     */
    val playerStats: Map<GameModule, PlayerPerformanceStats>
        get() = gameEngines.mapValues { (_, engine) -> engine.getStats() }

    init {
        viewModelScope.launch {

            repository.getRecent().onSuccess {
                it?.gameType?.let { prevGame -> game = prevGame }
            }.onError {
                Log.e(TAG, "Cannot initialize ViewModel. Repository error: $it")
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
            handleGuess(event.gameMod).onError {
                Log.e(TAG, it.toString())
            }
        }
        is AppEvent.AbortOngoingGame -> {
            Log.i(TAG, "Aborting game")
            abortGame()
        }
        is AppEvent.StartGame -> {
            Log.i(TAG, "Starting game: $event.game")
            startGame(event.game).onError {
                Log.e(TAG, it.toString())
            }
        }
    }

    /**
     * Starts the recentmost non-abandoned game
     */
    private fun playAgain() {
        viewModelScope.launch {
            repository.getRecent().onSuccess {
                if (it == null) {
                    return@launch
                }

                startGame(it.gameType)
            }.onError {
                Log.e(TAG, "Cannot play again. Repository error: $it")
            }
        }
    }

    /**
     * Starts a new game. Also handles all necessary initialization
     */
    private fun startGame(game: Game): Result<Unit, Error> {
        this.game = game

        gameEngines.clear()

        initGameEngines().onError { return Result.Error(it) }
        initGameState().onError { return Result.Error(it) }

        Log.i(TAG, "Starting game")
        startNewRound().onError { return Result.Error(it) }

        return Result.Success(Unit)
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
     * Create a new [GameEngine] for each game module in [game]. If a [GameEngine] already exists,
     * it is overwritten.
     */
    private fun initGameEngines(): Result<Unit, Error> {
        Log.i(TAG, "Initializing game engines")

        val game = game ?: return Result.Error(BadConfigurationError.UnsetGame)

        if (game.modules.isEmpty()) {
            return Result.Error(BadConfigurationError.MissingGameModules)
        }

        game.modules.forEach {
            gameEngines[it] = it.toGameEngine(game.settings.recallsBack, game.settings.repeatChance)
        }

        return Result.Success(Unit)
    }

    /**
     * Set the correct game module information to the [gameState].
     */
    private fun initGameState(): Result<Unit, Error> {
        Log.i(TAG, "Initializing game state")

        val game = game ?: return Result.Error(BadConfigurationError.UnsetGame)

        game.modules.forEach { module ->
            _gameState.update {
                it.apply {
                    it.mnemonicIds[module] = 0
                    it.recallCheck[module] = RecallCheck.NONE
                }
            }
        }

        return Result.Success(Unit)
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
    private fun handleGuess(game: GameModule): Result<Unit, Error> {
        Log.d(TAG, "$game Button pressed")

        if (gameEngines[game] == null) {
            Log.e(TAG, "$game is not part of the loaded game engines")
            return Result.Error(BadConfigurationError.MissingGameModules)
        }

        gameEngines[game]?.updateStats(true)
        _gameState.update {
            it.apply {
                it.recallCheck[game] =
                    if (gameEngines[game]?.isRepeat == true) RecallCheck.CORRECT else RecallCheck.INCORRECT
            }
        }

        return Result.Success(Unit)
    }

    /**
     * Starts a new game round, that is, creates a new mnemonic and start the round timer
     * coroutine.
     *
     * @see [GameEngine.createNewState]
     */
    private fun startNewRound(): Result<Unit, Error> {
        Log.d(TAG, "Starting new round")

        Log.d(TAG, "Generating round mnemonics")
        val mnemonicIds = gameState.value.mnemonicIds
        gameEngines.forEach { (game, gameEngine) ->
            mnemonicIds[game] = gameEngine.createNewState()
            mnemonicIds[game]?.let { gameEngine.onNewRound(it) }
        }
        _gameState.update { it.copy(currentRound = it.currentRound + 1, mnemonicIds = mnemonicIds) }

        val game = game ?: return Result.Error(BadConfigurationError.UnsetGame)

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

        return Result.Success(Unit)
    }

    /**
     * Ends round, i.e, stops the round timer, increments the round counter and resets the recall
     * check state. On game end, adds stats to the [GameState].
     */
    private fun endRound(): Result<Unit, Error> {
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

        val game = game ?: return Result.Error(BadConfigurationError.UnsetGame)

        // end round or end game and get stats
        if (_gameState.value.currentRound >= game.settings.totalRounds) {
            _gameState.update {
                it.copy(gameOver = true)
            }

            // save stats
            viewModelScope.launch {
                repository.insert(
                    GameModel(
                        gameType = game,
                        gameSettings = game.settings,
                        playerStats = playerStats,
                        gameEndTime = System.currentTimeMillis(),
                    ),
                )
            }
        } else {
            startNewRound()
        }

        return Result.Success(Unit)
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
