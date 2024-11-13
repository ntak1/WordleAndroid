package com.example.wordle.business

class WordleState {
    companion object {
        const val LANGUAGE: String = "en"
        const val WIDTH: Int = 5
        const val HEIGHT: Int = 6
        const val LOGGER_TAG = "naomitkm_debug"
    }
    var chosenWord: Word? = null
    var currentRow: Int = 0
    var currentWord: String? = null
}