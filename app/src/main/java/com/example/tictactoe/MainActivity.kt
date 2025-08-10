package com.example.tictactoe

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.TextView




class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var buttons: Array<Button>
    private var currentPlayer = "X"
    private var board = Array(9) { "" }
    private var gameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.textViewStatus)

        buttons = Array(9) { i ->
            findViewById(resources.getIdentifier("button$i", "id", packageName))
        }

        buttons.forEachIndexed { index, button ->


            button.setOnClickListener {
                makeMove(button, index)
            }


        }

        (findViewById(R.id.buttonReset) as Button).setOnClickListener {
            resetGame()
        }

    }

    private fun checkWin(): Boolean {
        val winPositions = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        for (pos in winPositions) {
            if (board[pos[0]] == currentPlayer &&
                board[pos[1]] == currentPlayer &&
                board[pos[2]] == currentPlayer
            ) {
                return true
            }
        }
        return false
    }

    private fun resetGame() {
        board = Array(9) { "" }
        buttons.forEach { it.text = "" }
        currentPlayer = "X"
        gameActive = true
        statusText.text = "Vez do Jogador $currentPlayer"
    }

    private fun makeMove(button: Button, index: Int) {
        if (board[index].isEmpty() && gameActive) {
            board[index] = currentPlayer

            val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            bounceAnim.interpolator = BounceInterpolator()
            button.startAnimation(bounceAnim)

            button.text = currentPlayer

            if (checkWin()) {
                statusText.text = "Jogador $currentPlayer venceu!"
                gameActive = false
            } else if (board.all { it.isNotEmpty() }) {
                statusText.text = "Empate!"
                gameActive = false
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                statusText.text = "Vez do Jogador $currentPlayer"
            }
        }
    }


}