package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ModeSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)

        val btnPvP = findViewById<Button>(R.id.btnPvP)
        val btnPvAI = findViewById<Button>(R.id.btnPvAI)

        btnPvP.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mode", "PvP")
            startActivity(intent)
        }

        btnPvAI.setOnClickListener {
            val difficulties = arrayOf("Fácil", "Médio", "Difícil")
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)

            builder.setTitle("Escolha a dificuldade")
            builder.setItems(difficulties) { dialog, which ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("mode", "PvAI")
                intent.putExtra("AI_DIFFICULTY", which)
                startActivity(intent)
            }
            builder.show()
        }
    }
}
