package org.squidfish.tuple_n_back.models

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

class GameViewModel() : ViewModel() {
    val TAG = "GameViewModel"

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private val gameEngines = mutableMapOf<GameType, GameEngine>()
    var game: Game = Game.None // TODO: load game played on last session


    fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.PlayAgain -> {
                Log.i(TAG,"Playing again")
                startGame(game)
            }
            is AppEvent.ResetGameState -> {
                Log.i(TAG,"Resetting game state")
                resetGameState()
            }
            is AppEvent.MakeMnemonicRepeatGuess -> {
                Log.i(TAG,"Handling button guess")
                handleGuess(event.game)
            }
            is AppEvent.AbortOngoingGame -> {
                Log.i(TAG,"Aborting game")
                abortGame()
            }
            is AppEvent.StartGame -> {
                Log.i(TAG,"Starting game: $event.game")
                startGame(event.game)
            }
        }
    }

    private fun startGame(game: Game) {
        this.game = game

        gameEngines.clear()
        initGameEngines()
        initGameState()
        Log.i(TAG,"Starting game")
        startNewRound()
    }

    private fun resetGameState() {
        verifyGame()

        // TODO: this for PlayAgain?
        gameEngines.forEach { (_, engine) ->
            engine.resetStats()
        }

        _gameState.value = GameState()
    }

    private fun initGameEngines() {
        Log.i(TAG, "Initializing game engines")

        verifyGame()

        if (game.modules.isEmpty()) {
            throw IllegalArgumentException("No game types set in settings")
        }

        game.modules.forEach {
            gameEngines[it] = it.toGameEngine(game.settings.recallsBack, game.settings.repeatChance)
        }
    }

    private fun initGameState() {
        Log.i(TAG, "Initializing game state")

        verifyGame()

        game.modules.forEach { module ->
            _gameState.update { it.apply {
                it.mnemonicIds[module] = 0
                it.gameStats[module] = GameStats()
                it.recallCheck[module] = RecallCheck.NONE
                }
            }
        }
    }

    /**
     * When recall button is pressed, handleGuess is called. Updates some stats and changes
     * gameState, so that the recall button is coloured appropriately.
     */
    private fun handleGuess(game: GameType) {
        Log.d(TAG, "$game Button pressed")

        if (gameEngines[game] == null) {
            Log.e(TAG, "$game is not part of the loaded game engines")
            return
        }

        gameEngines[game]?.updateStats(true)
        if (gameEngines[game]?.isRepeat == true) {
            _gameState.update { it.apply {it.recallCheck[game] = RecallCheck.CORRECT} }
        } else {
            _gameState.update { it.apply {it.recallCheck[game] = RecallCheck.INCORRECT} }
        }
    }

    /**
     * Starts a new game round, that is, generates a new mnemonic number and start the round timer
     * coroutine.
     */
    private fun startNewRound() {
        Log.d(TAG, "Starting new round")
        _gameState.update { it.copy (currentRound = it.currentRound + 1) }

        Log.d(TAG, "Generating round mnemonics")
        gameEngines.forEach { (game, gameEngine) ->
            _gameState.update { it.apply { it.mnemonicIds[game] = gameEngine.createNewState() } }
        }

        verifyGame()

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
     * check state. On game end, adds stats to the game state
     */
    private fun endRound() {
        Log.d(TAG, "Ending round")

        // stop timer
        timerJob?.cancel()

        // updating missed recall stats and resetting button recall state
        gameEngines.forEach { (game, gameEngine) ->
            if (_gameState.value.recallCheck[game] == RecallCheck.NONE) {
                gameEngine.updateStats(false)
            }

            if (_gameState.value.recallCheck[game] != RecallCheck.NONE) {
                _gameState.update { it.apply { it.recallCheck[game] = RecallCheck.NONE } }
            }
        }

        verifyGame()
        // end round or end game and get stats
        if (_gameState.value.currentRound >= game.settings.totalRounds) {
            _gameState.update { it.copy(gameOver = true).apply {
                gameEngines.forEach { (game, gameEngine) ->
                    it.gameStats[game] = gameEngine.getStats()
                }
            }}
        } else {
            startNewRound()
        }
    }

    private fun abortGame() {
        Log.i(TAG, "Aborting game")

        timerJob?.cancel()
    }

    /**
     * Check if game is set i.e. is not Game.None. If not, log and throw an exception
     */
    private fun verifyGame() {
        if (game == Game.None) {
            Log.e(TAG, "Cannot reset game state - game is not set")
            throw IllegalStateException("Game is not set")
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Ending game")
        timerJob?.cancel()
        super.onCleared()
    }
}