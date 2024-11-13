package com.example.wordle.fragments

import WordleAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wordle.R
import com.example.wordle.business.Cell
import com.example.wordle.business.WordleState
import com.example.wordle.helper.loadWords

// MainActivity.kt
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid: RecyclerView = findViewById(R.id.wordleGrid)
        val cells = List(WordleState.HEIGHT * WordleState.WIDTH) { Cell() } // 30 cells (6 rows of 5 for Wordle)

        val wordleState = WordleState()
        wordleState.chosenWord = loadWords(this).random()
        Log.d(WordleState.LOGGER_TAG, String.format("Chosen Word= [%s]", wordleState.chosenWord!!.word))

        val adapter = WordleAdapter(cells, wordleState)
        grid.adapter = adapter
    }
}