package com.example.wordle.business

enum class CellState {
    NOT_EVALUATED,
    CONTAINS_WRONG_POSITION,
    NOT_CONTAINS,
    RIGHT_POSITION
}