package com.example.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.socket.client.Socket
import io.socket.engineio.client.EngineIOException


class EntranceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
        val button: TextView = findViewById(R.id.button)
        button.setOnClickListener { enterChatRoom() }
    }
    private fun enterChatRoom() {
        val txtUsername = findViewById<EditText>(R.id.userName)
        val userName = txtUsername.text.toString()
        if (userName.isNotEmpty()) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("username", userName)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show();
        }
    }
}