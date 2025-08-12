package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.TextView
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private var aiIsPlaying = false

    private lateinit var mode: String

    private var playingAgainstAI = false
    private var aiDifficulty = Difficulty.MEDIUM

    private lateinit var statusText: TextView
    private lateinit var buttons: Array<Button>
    private var currentPlayer = "X"
    private var board = Array(9) { "" }
    private var gameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mode = intent.getStringExtra("mode") ?: "PvP"

        statusText = findViewById(R.id.textViewStatus)

        buttons = Array(9) { i ->
            findViewById(resources.getIdentifier("button$i", "id", packageName))
        }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                makeMove(button, index)
            }
        }

        findViewById<Button>(R.id.buttonReset).setOnClickListener {
            resetGame()
        }

        findViewById<Button>(R.id.buttonVoltar).setOnClickListener {
            startActivity(Intent(this, ModeSelectionActivity::class.java))
            finish()
        }

        playingAgainstAI = intent.getBooleanExtra("PLAYING_AGAINST_AI", false)

        val diffIndex = intent.getIntExtra("AI_DIFFICULTY", 1)
        aiDifficulty = when (diffIndex) {
            0 -> Difficulty.EASY
            1 -> Difficulty.MEDIUM
            2 -> Difficulty.HARD
            else -> Difficulty.MEDIUM
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
        aiIsPlaying = false
    }

    private fun makeMove(button: Button, index: Int) {
        if (board[index].isEmpty() && gameActive) {
            board[index] = currentPlayer

            // animação
            val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            bounceAnim.interpolator = BounceInterpolator()
            button.startAnimation(bounceAnim)
            button.text = currentPlayer

            if (checkWin()) {
                statusText.text = "Jogador $currentPlayer venceu!"
                gameActive = false
                aiIsPlaying = false
            } else if (board.all { it.isNotEmpty() }) {
                statusText.text = "Empate!"
                gameActive = false
                aiIsPlaying = false
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                statusText.text = "Vez do Jogador $currentPlayer"

                if (mode == "PvAI" && currentPlayer == "O" && gameActive && !aiIsPlaying) {
                    aiIsPlaying = true
                    makeAIMove()
                } else {
                    aiIsPlaying = false
                }
            }
        }
    }

    private fun makeAIMove() {
        when (aiDifficulty) {
            Difficulty.EASY -> makeRandomMove()
            Difficulty.MEDIUM -> makeMediumMove()
            Difficulty.HARD -> makeHardMove()
        }
    }

    private fun makeRandomMove() {
        val emptyIndices = board.withIndex()
            .filter { it.value.isEmpty() }
            .map { it.index }

        if (emptyIndices.isNotEmpty()) {
            val aiMove = emptyIndices.random()
            val button = buttons[aiMove]
            button.postDelayed({
                makeMove(button, aiMove)
                aiIsPlaying = false
            }, 500)
        } else {
            aiIsPlaying = false
        }
    }

    private fun makeMediumMove() {
        val blockMove = findBlockingMove("X")
        if (blockMove != -1) {
            val button = buttons[blockMove]
            button.postDelayed({
                makeMove(button, blockMove)
                aiIsPlaying = false
            }, 500)
        } else {
            makeRandomMove()
        }
    }

    private fun makeHardMove() {
        val bestMove = minimax(board.copyOf(), "O").index
        if (bestMove != -1) {
            val button = buttons[bestMove]
            button.postDelayed({
                makeMove(button, bestMove)
                aiIsPlaying = false
            }, 500)
        } else {
            makeRandomMove()
        }
    }

    private data class Move(var index: Int, var score: Int)

    private fun minimax(newBoard: Array<String>, currentPlayer: String): Move {
        val availSpots = newBoard.withIndex().filter { it.value.isEmpty() }.map { it.index }

        if (checkWinForBoard(newBoard, "X")) {
            return Move(-1, -10)
        } else if (checkWinForBoard(newBoard, "O")) {
            return Move(-1, 10)
        } else if (availSpots.isEmpty()) {
            return Move(-1, 0)
        }

        val moves = mutableListOf<Move>()

        for (i in availSpots) {
            val move = Move(i, 0)
            val boardCopy = newBoard.copyOf()
            boardCopy[i] = currentPlayer

            val result = if (currentPlayer == "O") {
                minimax(boardCopy, "X")
            } else {
                minimax(boardCopy, "O")
            }
            move.score = result.score
            moves.add(move)
        }

        return if (currentPlayer == "O") {
            moves.maxByOrNull { it.score } ?: Move(-1, 0)
        } else {
            moves.minByOrNull { it.score } ?: Move(-1, 0)
        }
    }

    private fun checkWinForBoard(boardToCheck: Array<String>, player: String): Boolean {
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
            if (boardToCheck[pos[0]] == player &&
                boardToCheck[pos[1]] == player &&
                boardToCheck[pos[2]] == player) {
                return true
            }
        }
        return false
    }

    private fun findWinningMove(player: String): Int {
        val winPositions = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6)
        )
        for (pos in winPositions) {
            val marks = pos.map { board[it] }
            if (marks.count { it == player } == 2 && marks.count { it.isEmpty() } == 1) {
                return pos[marks.indexOfFirst { it.isEmpty() }]
            }
        }
        return -1
    }

    private fun findBlockingMove(opponent: String): Int {
        return findWinningMove(opponent)
    }
}
