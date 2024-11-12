package com.example.wordle.business

import android.graphics.Color

// Cell.kt
data class Cell(
    var letter: Char? = null,
    var color: Int = Color.LTGRAY // Default color
)