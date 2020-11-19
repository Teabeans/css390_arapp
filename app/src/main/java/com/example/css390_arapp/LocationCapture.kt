package com.example.css390_arapp

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LocationCapture : AppCompatActivity() {
    private lateinit var locationText : TextView
    private lateinit var getLocationButton: Button
    private lateinit var timeText : TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_location)

        locationText = findViewById(R.id.locationTextDisplay)
        getLocationButton = findViewById(R.id.getLocationButton)
        timeText = findViewById(R.id.timeTextView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationButton.setOnClickListener {
            //Check if permission is granted
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // permission granted, start getting current device location
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                    if(location != null){
                        try{
                            var altAndLong = "Altitude: " + location.altitude.toString() + ", Longitude:" + location.longitude
                            locationText.text = altAndLong
                            timeText.text = "This location was update on: "+getDate(location.time)
                        } catch (err : IOException){
                            err.printStackTrace()
                        }
                    }else{
                        locationText.text = "No location found"
                    }
                }
            }
            else{
                // permission not granted
                locationText.text = "Permission not granted"
            }
        }
    }

    private fun getDate(millis : Long) : String{
        var dateFormat = "MM/dd/yyyy hh:mm:ss"
        var formatter : SimpleDateFormat = SimpleDateFormat(dateFormat)
        return formatter.format(Date(millis))
    }
}