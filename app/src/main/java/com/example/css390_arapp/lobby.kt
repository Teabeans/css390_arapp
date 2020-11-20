package com.example.css390_arapp

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.renderscript.Sampler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class lobby : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.statusReport).apply {
            text = message
        }

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()      // Bind the database into the application
        val myRef = database.getReference("message") // Create a Key
        myRef.setValue("Hello, World!")                    // Set a Value

        //If user is not logged in, return to Register/Main screen
        verifyUserIsLoggedIn()

    }

    // Send a key:value pair to the database
    fun db_send(view: View) {
        val sendKeyField = findViewById<EditText>(R.id.db_send_key)
        val sendKey = sendKeyField.text.toString()

        val sendValField = findViewById<EditText>(R.id.db_send_value)
        val sendVal = sendValField.text.toString()

        val database = FirebaseDatabase.getInstance()      // Bind the database into the application
        val dbKey = database.getReference(sendKey) // Create a Key
        dbKey.setValue(sendVal)
    }

    // Request information from the database
    fun db_recv(view: View) {

        val recvKeyField = findViewById<EditText>(R.id.db_recv_key)
        val recvKey = recvKeyField.text.toString()

        val database = FirebaseDatabase.getInstance() // Bind the database into the application
        val dbKey = database.getReference(recvKey)    // Create a Key

        var pulledValue: Any? = null
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pulledValue = dataSnapshot.value
                // Pop a toast to confirm keypress and Key:Value capture
                val toaster = Toast.makeText(applicationContext, "DB_RECV - $recvKey : $pulledValue", 2)
                toaster.show()

                // Change the field to the pulledValue
                val textView = findViewById<TextView>(R.id.db_recv_value).apply {
                    text = "Test $pulledValue"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        dbKey.addListenerForSingleValueEvent(menuListener)
    }

    //Verify user is logged on, else send to register screen
    //If logged on, show user details
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //clear activities
            startActivity(intent)
        }
        //Populate fields with user info for testing/confirmation
        //Populate UID
        findViewById<TextView>(R.id.uuidfirebase_lobby_textview).apply {
            text = uid
        }
        //Grab Username and populate field
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){
                var username = p0.child("username").getValue()
                //now we have the username, so update field
                findViewById<TextView>(R.id.userfirebase_lobby_textview).apply{
                    text = username.toString()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                //error handling
            }
        })
    }

    //Sign Out button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu options
        when (item?.itemId){
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //clear activities
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Menu Options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}