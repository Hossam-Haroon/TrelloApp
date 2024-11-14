package com.example.trelloapp.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.trelloapp.R
import com.example.trelloapp.activities.firebase.FirebaseClass

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        installSplashScreen()

        val currentUser = FirebaseClass().userId()
        if (currentUser.isNotEmpty()){
            startActivity(Intent(this,MainActivity::class.java))
        }


        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


    }

}