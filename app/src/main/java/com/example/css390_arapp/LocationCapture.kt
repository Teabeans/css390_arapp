package com.example.css390_arapp

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException

class LocationCapture : AppCompatActivity() {
    private lateinit var locationText : TextView
    private lateinit var getLocationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_location)

        locationText = findViewById(R.id.locationTextView)
        getLocationButton = findViewById(R.id.getLocationButton)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationButton.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // permission granted, start getting current device location
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                    if(location != null){
                        try{
                            var altAndLong = location.altitude.toString() + " " + location.longitude
                            locationText.text = altAndLong
                        } catch (err : IOException){
                            err.printStackTrace()
                        }
                    }
                }
            }
            else{
                // permission not granted
                // TODO
            }
        }
    }

}