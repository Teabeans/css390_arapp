package com.example.css390_arapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login2.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        //Login Button with Firebase
        loginLogin_button.setOnClickListener {
            performLogin()
        }
    }

    //Function for User Login
    private fun performLogin() {
        val email = emailLogin_textview.text.toString()
        val password = passwordLogin_textview.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText( this, "Please enter an email and password to login!", 2 ).show()
            return
        }
        //Firebase Email/Password Login
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful) {
                    //Login succeeded, show confirmation toast
                    Toast.makeText(this, "Login Success!", 2).show()

                    //Launch Lobby Activity
                    val intent = Intent(this, lobby::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener{
                //Login Failed, show error toast
                Toast.makeText( this, "Login Failed: ${it.message}", 10 ).show()
            }
    }
}