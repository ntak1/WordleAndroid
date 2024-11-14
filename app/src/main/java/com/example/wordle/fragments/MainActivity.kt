package com.example.wordle.fragments

import WordleAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wordle.R
import com.example.wordle.business.Cell
import com.example.wordle.business.WordleState
import com.example.wordle.helper.loadWords

// MainActivity.kt
class MainActivity : AppCompatActivity() {
    private lateinit var wordleState: WordleState
    private lateinit var adapter: WordleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("WordleStats", MODE_PRIVATE)
        wordleState = WordleState(sharedPreferences)
        wordleState.chosenWord = loadWords(this).random()
        Log.d(WordleState.LOGGER_TAG, String.format("Chosen Word= [%s]", wordleState.chosenWord!!.word))
        wordleState.gameState.observe(this) {
            gameState ->
            run {
                if (gameState == WordleState.GameState.GAME_WON) {
                    showGameResultPopup("You Won!")
                } else if (gameState == WordleState.GameState.GAME_LOST) {
                    showGameResultPopup("You lost!")
                } else if (gameState == WordleState.GameState.GAME_RESET) {
                    wordleState.reset()
                    wordleState.chosenWord = loadWords(this).random()
                    wordleState.gameState.value = WordleState.GameState.GAME_ON
                    Log.d(WordleState.LOGGER_TAG, String.format("Chosen Word Reset= [%s]", wordleState.chosenWord!!.word))
                }
            }
        }

        val grid: RecyclerView = findViewById(R.id.wordleGrid)
        val cells = List(WordleState.HEIGHT * WordleState.WIDTH) { Cell() } // 30 cells (6 rows of 5 for Wordle)
        adapter = WordleAdapter(cells, wordleState)
        grid.adapter = adapter

        // Set up the reset button click listener
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            resetGame()
        }
    }

    private fun showGameResultPopup(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(message)
            .setNegativeButton("Reset Game") { dialog, _ ->
                resetGame()
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun resetGame() {
        adapter.resetBoard()
        wordleState.reset()
        // Reset game logic, such as clearing the board or restarting the state
    }
}