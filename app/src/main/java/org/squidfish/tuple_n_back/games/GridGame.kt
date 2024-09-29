package org.squidfish.tuple_n_back.games

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.models.GridPosition

@Composable
fun GridGame(highlightPosition: GridPosition) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GridSquare(GridPosition.UP_LEFT, highlightPosition)
            GridSquare(GridPosition.UP, highlightPosition)
            GridSquare(GridPosition.UP_RIGHT, highlightPosition)
        }
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            GridSquare(GridPosition.LEFT, highlightPosition)
            GridSquare(GridPosition.CENTER, highlightPosition)
            GridSquare(GridPosition.RIGHT, highlightPosition)
        }
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            GridSquare(GridPosition.DOWN_LEFT, highlightPosition)
            GridSquare(GridPosition.DOWN, highlightPosition)
            GridSquare(GridPosition.DOWN_RIGHT, highlightPosition)
        }
    }
}

@Composable
fun GridSquare(position: GridPosition, highlightPosition: GridPosition) {

    val cellColor = if (position == highlightPosition) Color.Red else Color.Transparent

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
    GridGame(GridPosition.UP_LEFT)
}