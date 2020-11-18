package com.example.css390_arapp

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.css390_arapp.ui.login.LoggedInUserView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Toast test 2020.11.18
        val toaster = Toast.makeText( applicationContext, "Hello, World!", 5 )
        toaster.show()

        // Step 1: Create variables to hold all information related to this activity
        // In format: 'val {VariableName} = findViewById<{ViewType}>(R.id.{ViewID})
        val username = findViewById<EditText>(R.id.editTextTextPersonName)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login = findViewById<Button>(R.id.button3)
    }

    // Do something in response to button
    fun sendMessage(view: View) {
        // For now, just make a toast that says 'Click!'
        val toaster = Toast.makeText( applicationContext, "Click!", 2 )
        toaster.show()

        val username = findViewById<EditText>(R.id.editTextTextPersonName)
        val message = username.text.toString()
        val intent = Intent(this, lobby::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        val i = Intent(this@MainActivity, lobby::class.java)
        startActivity( i )
    }

}