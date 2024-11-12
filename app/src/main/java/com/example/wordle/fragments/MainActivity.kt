package com.example.wordle.fragments

import WordleAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wordle.R
import com.example.wordle.business.Cell

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid: RecyclerView = findViewById(R.id.wordleGrid)
        val cells = List(30) { Cell() } // 30 cells (6 rows of 5 for Wordle)

        val adapter = WordleAdapter(cells)
        grid.adapter = adapter
    }
}