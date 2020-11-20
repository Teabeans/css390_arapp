package com.example.css390_arapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Toast test 2020.11.18
        val toaster = Toast.makeText( applicationContext, "Hello, World!", 5 )
        toaster.show()

        // Step 1: Create variables to hold all information related to this activity
        // In format: 'val {VariableName} = findViewById<{ViewType}>(R.id.{ViewID})
        val username = findViewById<EditText>(R.id.nameRegister_textview)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login = findViewById<Button>(R.id.button3)

        //Register Button with Firebase
        register_button.setOnClickListener {
            performRegister()
        }

        //Already have account button, redirect to Login Activity
        alreadyHaveAccount_textview.setOnClickListener{
            //launch Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Select Photo Button
        selectPhotoRegister_button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    //variable to hold user image for multiple functions [select, upload to Firebase]
    var selectedPhotoUri: Uri? = null

    //Photo is selected button
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //Proceed, check what image was selected by user
            //where image is stored
            selectedPhotoUri = data.data
            //create bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            //put bitmap into photo button
            selectPhotoRegister_imageview.setImageBitmap(bitmap) //show photo in circle
            selectPhotoRegister_button.alpha = 0f //hide select photo button

        }
    }

    // Do something in response to button
    fun sendMessage(view: View) {
        // For now, just make a toast that says 'Click!'
        val toaster = Toast.makeText( applicationContext, "Click!", 2 )
        toaster.show()

        val username = findViewById<EditText>(R.id.nameRegister_textview)
        val message = username.text.toString()
        val intent = Intent(this, lobby::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)

    }

    /*
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
    */

    //Function for User Registration
    private fun performRegister() {
        val email = emailRegister_textview.text.toString()
        val password = passwordRegister_textview.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText( this, "Please enter an email and password to register!", 2 ).show()
            return
        }
        //Firebase Email/Password Registration
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful) {
                    //Register succeeded, show confirmation toast
                    Toast.makeText(this, "Register Success!", 2).show()

                    //Upload image to Firebase
                    uploadImageToFirebase()

                    //Launch Lobby Activity
                    val message = nameRegister_textview.text.toString()
                    val intent = Intent(this, lobby::class.java).apply {
                        putExtra(EXTRA_MESSAGE, message)
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener{
                //Register Failed, show error toast
                Toast.makeText( this, "Register Failed: ${it.message}", 10 ).show()
            }
    }

    //Function for User Image uploading to Firebase storage
    private fun uploadImageToFirebase() {
        //Safety check
        if (selectedPhotoUri == null){
            return
        }

        //random filename for now
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebase(it.toString())
                }
            }
            .addOnFailureListener{
                //do stuff if fails [log to console etc]
            }
    }

    //Function to add user information and user image to database
    private fun saveUserToFirebase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        //Create user object to variable 'user'
        val user = User(uid, nameRegister_textview.text.toString(), profileImageUrl, "alt long lat")
        //Update user object in database
        ref.setValue(user)
            .addOnSuccessListener {
                //do stuff if complete [log to console etc]
            }
    }

}

//User object for Firebase updating
class User(val uid: String, val username: String, val profileImageUrl: String, val location: String)