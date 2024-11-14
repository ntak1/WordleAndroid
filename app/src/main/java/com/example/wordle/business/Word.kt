package com.example.wordle.business

data class Word(
    val word: String,
    val labels: List<String>
) {
    fun getWordFrequencyMap(): MutableMap<Char, Int> {
        return (word.groupingBy { it }.eachCount()).toMutableMap()
    }
}