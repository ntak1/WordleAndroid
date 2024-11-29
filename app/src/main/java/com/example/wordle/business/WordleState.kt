package com.example.wordle.business

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WordleState(private val sharedPreferences: SharedPreferences): ViewModel() {
    init {
        loadStats()
    }
    companion object {
        const val LANGUAGE: String = "en"
        const val WIDTH: Int = 5
        const val HEIGHT: Int = 6
        const val LOGGER_TAG = "naomitkm_debug"
    }
    var chosenWord: Word? = null
    var gameState = MutableLiveData(GameState.GAME_ON)

    var currentRow: Int = 0
    private var gamesWon: Int = 0
    private var gamesLost: Int = 0


     enum class GameState {
         GAME_ON,
         GAME_WON,
         GAME_LOST,
         GAME_RESET,
    }

    fun reset() {
        chosenWord = null
        currentRow = 0
    }

    fun setLostGame() {
        gameState.value = GameState.GAME_LOST
        gamesLost += 1
        saveStats()
    }

    fun setGameWon() {
        gameState.value = GameState.GAME_WON
        gamesWon += 1
        saveStats()
    }

    private fun saveStats() {
        val editor = sharedPreferences.edit()
        editor.putInt("gamesWon", gamesWon)
        editor.putInt("gamesLost", gamesLost)
        editor.apply() // Asynchronously save the data
    }

    private fun loadStats() {
        gamesWon = sharedPreferences.getInt("gamesWon", 0) // Default to 0 if not set
        gamesLost = sharedPreferences.getInt("gamesLost", 0)
    }
}