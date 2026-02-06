package com.example.Proba.States

enum class Difficulty { Easy, Medium, Hard }

sealed class Screen {
    data object Welcome : Screen()
    data object DifficultyPicker : Screen()
    data class Game(val difficulty: Difficulty) : Screen()
}
