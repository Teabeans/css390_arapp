package com.example.css390_arapp.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Toast
import com.example.css390_arapp.MainActivity
import com.example.css390_arapp.R
import com.example.css390_arapp.lobby
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Login Button with Firebase
        loginLogin_button.setOnClickListener {
            performLogin()
        }

        registerTextView.setOnClickListener{
            //launch Register Activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        bypassLoginButton.setOnClickListener{
            val intent = Intent(this, lobby::class.java)
            startActivity(intent)
        }

        oldMainLogin_Button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
                    val intent = Intent(this, lobby::class.java).apply {
                        putExtra(AlarmClock.EXTRA_MESSAGE, email)
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener{
                //Login Failed, show error toast
                Toast.makeText( this, "Login Failed: ${it.message}", 10 ).show()
            }
    }

    fun bypassLogin(view: View) {
        // For now, just make a toast that says 'Click!'
        val toaster = Toast.makeText( applicationContext, "Bypassing...", 2 )
        toaster.show()

        val message = "DEV_USER"
        val intent = Intent(this, lobby::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
        }
        startActivity(intent)

    }
}