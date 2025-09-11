package org.squidfish.tuplenback.presentation.screen.gameselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionAction.GameSelected

class GameSelectionViewModel : ViewModel() {
    private var hasLoadedInitialData = false

    private val _eventFlow = Channel<GameSelectionEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    private val _uiState = MutableStateFlow(GameSelectionState())
    val uiState = _uiState
        .onStart {
            if (hasLoadedInitialData) return@onStart

            loadData()
            hasLoadedInitialData = true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _uiState.value,
        )

    fun onAction(action: GameSelectionAction) {
        when (action) {
            is GameSelected -> viewModelScope.launch {
                _eventFlow.send(GameSelectionEvent.StartGame(action.game))
            }
        }
    }

    private suspend fun loadData() {
        _uiState.update {
            // TODO: use game enum entry to determine uniqueness
            it.copy(games = Game.entries.map { it.asModel })
        }
    }
}
