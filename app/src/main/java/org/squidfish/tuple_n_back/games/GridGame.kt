package org.squidfish.tuple_n_back.games

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.models.GridPosition
import org.squidfish.tuple_n_back.models.GridEngine

@SuppressLint("UnrememberedMutableState")
@Composable
fun GridGame(highlightedPosition: Int) {
    //var manualRecompose by mutableStateOf(false)

    //if (gridViewModel.forceRecomposition) {
    //    manualRecompose = !manualRecompose
    //    gridViewModel.forceRecomposition = false
    //}

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GridSquare(GridPosition.UP_LEFT, highlightedPosition)
            GridSquare(GridPosition.UP, highlightedPosition)
            GridSquare(GridPosition.UP_RIGHT, highlightedPosition)
        }
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            GridSquare(GridPosition.LEFT, highlightedPosition)
            GridSquare(GridPosition.CENTER, highlightedPosition)
            GridSquare(GridPosition.RIGHT, highlightedPosition)
        }
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            GridSquare(GridPosition.DOWN_LEFT, highlightedPosition)
            GridSquare(GridPosition.DOWN, highlightedPosition)
            GridSquare(GridPosition.DOWN_RIGHT, highlightedPosition)
        }
    }
}

@Composable
fun GridSquare(position: GridPosition, highlightedPosition: Int) {

    val pos = position.ordinal

    val cellColor = if (pos == highlightedPosition) Color.Red else Color.Transparent

    Box(modifier = Modifier
        .size(100.dp)
        .fillMaxSize()
        .padding(4.dp)
        .background(cellColor, RectangleShape)
        .border(width = 1.dp, color = Color.Black)
    )
}

@Preview
@Composable
fun GridGamePreview() {
    GridGame(GridPosition.UP.ordinal)
}