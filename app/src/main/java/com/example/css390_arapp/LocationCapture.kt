package com.example.css390_arapp

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.display_location.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LocationCapture : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationButton.setOnClickListener {
            getLocation()
        }

        usernameQueryButton.setOnClickListener{
            queryLocation(usernameQueryText.editableText.toString())
        }
    }

    private fun getLocation(){
        //Check if permission is granted
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // permission granted, start getting current device location

            // Location is either turned off in the device setting or the the device never recorded any location from the Google Map
            if(fusedLocationClient.lastLocation == null){
                Toast.makeText( this, "DID NOT FOUND LAST LOCATION", 1).show()
                return
            }
            // Found last location, get time, altitude, longitude, and latitude of current device and output on screen
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                try{
                    var altAndLong = "alt:${location.altitude}, long:${location.longitude}, lat:${location.latitude}"
                    var timeOfUpdate = getDate(location.time)
                    locationTextDisplay.text = altAndLong
                    timeTextView.text = "This location was update on: " + timeOfUpdate
                    sendLocation(altAndLong, timeOfUpdate)
                } catch (err : IOException){
                    locationTextDisplay.text = "No location found"
                    sendLocation("No location", "Not found")
                    err.printStackTrace()
                }
            }
        }
        else{
            // permission not granted
            locationTextDisplay.text = "Permission not granted"
        }
    }

    //Send location to database with time and tempUsername
    private fun sendLocation(loc : String, time : String){
        if(tempUserNameText.length() <= 0){
            Toast.makeText(this, "Enter a username!", 1).show()
            return
        }

        val username = tempUserNameText.editableText.toString()
        var user = TempLocationUser(username, loc, time) //Create a TempLocationUser object
        val reference = FirebaseDatabase.getInstance().getReference("users")

        reference.child(username).setValue(user).addOnSuccessListener {
            Log.d("Location to database", "Success!")
            Toast.makeText(this, "Location Sent!", 1).show()
        }.addOnFailureListener{
            Log.d("Location to database", "Failure!")
            Toast.makeText(this, "Unable to send location!", 1).show()
        }
    }

    //Find location of specified username in database
    private fun queryLocation(username: String){
        if(username.isEmpty()){
            Toast.makeText( this, "text is empty", 1).show()
            return
        }
        //Grab username from database
        val reference = FirebaseDatabase.getInstance().getReference("users/$username")
        //Display location and timeOfUpdate
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){
                var location = p0.child("location").getValue()
                var timeUpdated = p0.child("timeUpdated").getValue()
                locationDatabaseText.text = location.toString()
                timeOfUpdateText.text = timeUpdated.toString()
            }
            override fun onCancelled(p0: DatabaseError) {
                //error handling
            }
        })

    }

    private fun getDate(millis : Long) : String{
        var dateFormat = "MM/dd/yyyy hh:mm:ss"
        var formatter = SimpleDateFormat(dateFormat)
        return formatter.format(Date(millis))
    }

    class TempLocationUser(val username: String, val location: String, val timeUpdated: String)
}