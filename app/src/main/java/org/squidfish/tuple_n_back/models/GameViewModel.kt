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

class GameViewModel(val gameSettings: GameSettings) : ViewModel() {
    val TAG = "GameViewModel"

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private val gameEngines = mutableMapOf<GameType, GameEngine>()

    init {
        initGameEngines()
        initGameState(gameSettings.games)
    }

    fun startGame() {
        Log.i(TAG,"Starting game")
        startNewRound()
    }

    fun resetGameState() {
        gameEngines.forEach { (game, engine) ->
            engine.resetStats()
        }

        _gameState.value = GameState()
        initGameState(gameSettings.games)

    }

    private fun initGameEngines() {
        Log.i(TAG, "Initializing game engines")
        if (gameSettings.games.isEmpty()) {
            throw IllegalArgumentException("No game types set in settings")
        }

        gameSettings.games.forEach { gameEngines[it] = it.toGameEngine(gameSettings.recallsBack, gameSettings.repeatChance) }
    }

    private fun initGameState(games: List<GameType>) {
        Log.i(TAG, "Initializing game state")
        games.forEach { game ->
            _gameState.update { it.apply {
                it.mnemonicIds[game] = 0
                it.gameStats[game] = GameStats()
                it.recallCheck[game] = RecallCheck.NONE
                }
            }
        }
    }

    /**
     * When recall button is pressed, handleGuess is called. Updates some stats and changes
     * gameState, so that the recall button is coloured appropriately.
     */
    fun handleGuess(game: GameType) {
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

        Log.d(TAG, "Creating timer coroutine")
        timerJob = viewModelScope.launch {
            for (time in 0..gameSettings.milliPerRound step gameSettings.timerUpdateInterval) {
                _gameState.update {
                    it.copy(roundProgress = time.toFloat() / gameSettings.milliPerRound)
                }

                delay(gameSettings.timerUpdateInterval)
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

        // end round or end game and get stats
        if (_gameState.value.currentRound >= gameSettings.totalRounds) {
            Log.i(TAG, "Ending game")
            val stats: MutableMap<GameType, GameStats> = mutableMapOf()
            gameEngines.forEach { (game, gameEngine) ->
                stats[game] = gameEngine.getStats()
            }
            _gameState.update { it.copy(gameStats = stats, gameOver = true) }
        } else {
            startNewRound()
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Ending game")
        timerJob?.cancel()
        super.onCleared()
    }
}