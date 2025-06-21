package com.neuraltechnologies.printeasy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val shopCodeEditText: EditText = findViewById(R.id.shopCodeEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val shopCode = shopCodeEditText.text.toString().trim()
            if (shopCode.isNotEmpty()) {
                // Save shop code to SharedPreferences
                val sharedPref = getSharedPreferences("PrintEasyPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("shopName", shopCode)
                    apply()
                }
                // Go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter a shop code", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 