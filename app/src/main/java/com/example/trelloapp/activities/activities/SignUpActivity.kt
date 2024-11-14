package com.example.trelloapp.activities.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.trelloapp.R
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    var dialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        val toolbar = findViewById<Toolbar>(R.id.tbToolBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val btnSignUp = findViewById<Button>(R.id.btn_signUp)
        btnSignUp.setOnClickListener {
            progressbar()
        lifecycleScope.launch {
            registerForAccount()
        }

        }

        auth = FirebaseAuth.getInstance()


    }

    private suspend fun registerForAccount(){
        val name  = findViewById<EditText>(R.id.etName).text.toString()
        val password  = findViewById<EditText>(R.id.etPassword).text.toString()
        val email  = findViewById<EditText>(R.id.etEmail).text.toString()

        if (name.isEmpty() && password.isEmpty() && email.isEmpty()){
            progressbarDismiss()
            Toast.makeText(this,"please fill the empty fields",Toast.LENGTH_LONG).show()

        }else{
            withContext(Dispatchers.IO){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        task->
                    if (task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!
                        val user  = Users(firebaseUser.uid,name,registeredEmail)
                        FirebaseClass().registerUser(this@SignUpActivity,user)


                        //val firebaseUser : FirebaseUser = task.result.user!!

                    }else{
                        runOnUiThread {
                            progressbarDismiss()
                            Toast.makeText(
                                this@SignUpActivity,
                                "oh no! something wrong happened, try again!",
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

    fun setThingsUp(){
        progressbarDismiss()
        Toast.makeText(this@SignUpActivity,
            "you have successfully registered to our app!",
            Toast.LENGTH_LONG).show()
        auth.signOut()
        finish()

    }
}