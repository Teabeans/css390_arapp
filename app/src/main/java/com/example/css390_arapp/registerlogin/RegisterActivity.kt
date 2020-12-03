package com.example.css390_arapp.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.css390_arapp.R
import com.example.css390_arapp.lobby
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Register Button with Firebase
        register_button.setOnClickListener {
            performRegister()
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
        val user = User(uid, nameRegister_textview.text.toString(), profileImageUrl, "alt long lat", "time")
        //Update user object in database
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Save User to Firebase:", "Success!")

                //Launch Lobby Activity
                val intent = Intent(this, lobby::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //clear activities
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Save User to Firebase:", "Failure: ${it.message}")
            }
    }

}

//User object for Firebase updating
class User(val uid: String, val username: String, val profileImageUrl: String, val location: String, val timeUpdated: String)