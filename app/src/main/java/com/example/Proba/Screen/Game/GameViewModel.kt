package com.example.Proba.Screen.Game
import android.content.res.AssetManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.Proba.States.Difficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

typealias QModel = Map<String, Map<Int, Double>>

class GameViewModel(
    assets: AssetManager,
    difficulty: Difficulty
) {
    private val difficultyStr: String = when (difficulty) {
        Difficulty.Easy -> "easy"
        Difficulty.Medium -> "medium"
        Difficulty.Hard -> "hard"
    }

    private val model: QModel = loadModel(assets)

    var board by mutableStateOf("         ") // 9 razmaka
        private set

    var statusText by mutableStateOf("Ti si X. Na potezu si.")
        private set

    var gameOver by mutableStateOf(false)
        private set

    var isThinking by mutableStateOf(false)
        private set

    fun reset() {
        board = "         "
        statusText = "Ti si X. Na potezu si."
        gameOver = false
        isThinking = false
    }

    fun onHumanClick(index: Int) {
        if (gameOver || isThinking) return
        if (index !in 0..8) return
        if (board[index] != ' ') return

        // X potez
        board = setAt(board, index, 'X')

        // Provera kraja posle X
        if (finishIfEnded()) return

        // AI potez ide malo kasnije (UI će pozvati aiMove() posle delay)
        statusText = "AI razmišlja…"
        isThinking = true
    }

    fun aiMove() {
        if (gameOver || !isThinking) return

        board = nextBoard(board, difficultyStr, 'O', model)

        // Provera kraja posle O
        if (finishIfEnded()) return

        statusText = "Na potezu si (X)."
        isThinking = false
    }

    // ---------------- Interno ----------------

    private fun finishIfEnded(): Boolean {
        val w = winner(board)
        if (w != null) {
            statusText = when (w) {
                'X' -> "Bravo! Pobedio je X 🎉"
                'O' -> "AI je pobedio (O) 🤖"
                else -> "Pobeda: $w"
            }
            gameOver = true
            isThinking = false
            return true
        }
        if (isDraw(board)) {
            statusText = "Nerešeno!"
            gameOver = true
            isThinking = false
            return true
        }
        return false
    }

    private fun loadModel(assets: AssetManager): QModel {
        // Fajl mora biti u: app/src/main/assets/q_table.json
        val text = assets.open("q_table.json").bufferedReader().readText()
        val type = object : TypeToken<QModel>() {}.type
        return Gson().fromJson(text, type)
    }

    private fun nextBoard(state: String, difficulty: String, aiPlayer: Char, model: QModel): String {
        val qValues = model[state]
        val validMoves = state.indices.filter { state[it] == ' ' }
        if (validMoves.isEmpty()) return state

        val move = when {
            qValues == null -> validMoves.random()
            difficulty == "easy" -> validMoves.random()
            difficulty == "medium" -> validMoves
                .sortedByDescending { qValues[it] ?: 0.0 }
                .take(3)
                .random()
            else -> validMoves.maxBy { qValues[it] ?: 0.0 }
        }

        return setAt(state, move, aiPlayer)
    }

    private fun setAt(s: String, index: Int, ch: Char): String =
        s.substring(0, index) + ch + s.substring(index + 1)

    private fun winner(b: String): Char? {
        val wins = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (w in wins) {
            val a = b[w[0]]
            if (a != ' ' && a == b[w[1]] && a == b[w[2]]) return a
        }
        return null
    }

    private fun isDraw(b: String): Boolean = winner(b) == null && b.none { it == ' ' }
}
