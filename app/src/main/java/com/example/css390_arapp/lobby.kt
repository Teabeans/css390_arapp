package com.example.css390_arapp

import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()      // Bind the database into the application
        val myRef = database.getReference("message") // Create a Key
        myRef.setValue("Hello, World!")                    // Set a Value
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

        val database = FirebaseDatabase.getInstance()      // Bind the database into the application
        val dbKey = database.getReference(recvKey) // Create a Key

        var pulledValue: Any? = null
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pulledValue = dataSnapshot.value
                // Pop a toast to confirm keypress and Key:Value capture
                val toaster = Toast.makeText(applicationContext, "DB_RECV - $recvKey : $pulledValue", 2)
                toaster.show()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        dbKey.addListenerForSingleValueEvent(menuListener)


    }
}