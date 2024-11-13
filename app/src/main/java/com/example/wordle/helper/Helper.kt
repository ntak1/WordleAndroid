package com.example.wordle.helper

import android.content.Context
import com.example.wordle.business.Word
import com.example.wordle.business.WordleState
import com.google.gson.Gson


fun loadWords(context: Context): List<Word> {
    val wordsListFilePath = "words_" + WordleState.LANGUAGE + ".json"
    val wordsJsonString =  loadJsonFromAssets(context, wordsListFilePath)
    val words: List<Word> = Gson().fromJson(wordsJsonString, Array<Word>::class.java).toList()
    return words
}

private fun loadJsonFromAssets(context: Context, fileName: String): String? {
    return try {
        // Open the JSON file from assets using the context's AssetManager
        val inputStream = context.assets.open(fileName)
        inputStream.bufferedReader().use { it.readText() } // Read the file as text
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null if an exception occurs
    }
}