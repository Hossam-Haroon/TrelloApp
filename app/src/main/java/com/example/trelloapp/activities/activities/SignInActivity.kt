package com.example.trelloapp.activities.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.trelloapp.R
import com.example.trelloapp.activities.models.Users
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    var dialog : Dialog? = null
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val toolbar = findViewById<Toolbar>(R.id.tbSignIn)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val btnSignIn = findViewById<Button>(R.id.btn_signIn)
        auth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener {
            progressbar()
            lifecycleScope.launch {
                signInAccount()
            }

        }
    }

    fun LoginSuccess(user : Users){
        progressbarDismiss()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        Toast.makeText(this@SignInActivity,
            "you are Loged In now!",
            Toast.LENGTH_LONG).show()
        finish()
    }

    private suspend fun signInAccount(){
        val email = findViewById<EditText>(R.id.et_email_signIn).text.toString()
        val password = findViewById<EditText>(R.id.et_password_signIn).text.toString()
        if (email.isEmpty()&&password.isEmpty()){
            progressbarDismiss()
            Toast.makeText(this,"you need to fill the fields!!",Toast.LENGTH_LONG).show()
        }else{
            withContext(Dispatchers.IO){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        task->
                    if (task.isSuccessful){
                       runOnUiThread{
                            progressbarDismiss()
                            startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                            Toast.makeText(this@SignInActivity,
                                "you are Loged In now!",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }

                    }else {
                        runOnUiThread {
                            progressbarDismiss()
                            Toast.makeText(
                                this@SignInActivity,
                                "Oh no! something wrong with the email or the password or both!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

        }

    }
    private fun progressbar(){
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.progress_bar)
        dialog!!.show()
    }
    private fun progressbarDismiss(){
        dialog!!.dismiss()
    }
}