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
        wordleState.gameState.observe(this) {
            gameState ->
            run {
                if (gameState == WordleState.GameState.GAME_WON) {
                    showGameResultPopup("You Won!")
                } else if (gameState == WordleState.GameState.GAME_LOST) {
                    showGameResultPopup("You lost!")
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
        // Set up the showAnswer button click listener
        val showAnswerButton = findViewById<Button>(R.id.showAnswer)
        showAnswerButton.setOnClickListener {
            showAnswer()
        }
    }

    private fun showGameResultPopup(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun resetGame() {
        adapter.resetBoard()
        wordleState.reset()
        wordleState.chosenWord = loadWords(this).random()
        Log.d(WordleState.LOGGER_TAG, "chosenWord ${wordleState.chosenWord}")
        wordleState.gameState.value = WordleState.GameState.GAME_ON
        // Reset game logic, such as clearing the board or restarting the state
    }

    private fun showAnswer() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Answer")
            .setMessage(wordleState.chosenWord?.word.toString())
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}