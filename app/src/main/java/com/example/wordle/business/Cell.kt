package com.example.wordle.business

import android.graphics.Color

// Cell.kt
data class Cell(
    var letter: Char? = null,
    var color: Int = Color.LTGRAY // Default color
) {
}
fun getCellColorFor(state: CellState): Int {
    return when (state) {
        CellState.NOT_EVALUATED -> Color.LTGRAY
        CellState.NOT_CONTAINS -> Color.DKGRAY
        CellState.CONTAINS_WRONG_POSITION -> Color.YELLOW
        CellState.RIGHT_POSITION -> Color.GREEN
    }
}
