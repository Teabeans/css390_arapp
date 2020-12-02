package com.example.css390_arapp

// For location services

// For intent catching
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ar_render : AppCompatActivity() {

    lateinit private var latLong : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_render)

        // Get the Intent that started this activity and extract the string
        val intent = Intent(this, MainActivity::class.java)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.captured_coord).apply {
            // this.text = message
            this.text = "THIS IS A TEST"
        }

    }


    // Acquire the current user location and load into the location variable that the AR engine will use
    private fun getLocation(): String {
//        var fusedLocationClient = new FusedLocationProviderClient()
        return "No lat:long found"
/*
        //Check if permission is granted
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // We have permission to do so, so get device location
            // In the event that a location could not be ascertained for any reason...
            if(fusedLocationClient.lastLocation == null){
                Toast.makeText( this, "Null location", 1).show()
                // Abort
                return "Cannot determine location"
            }

            // Capture location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                try{
                    latLong = "LAT: ${location.latitude} : LONG: ${location.longitude}"
                }
                catch (err : IOException) {

                }
            }
        }

        // Otherwise, no permission was granted
        else{
            // permission not granted
            Toast.makeText( this, "Permission not granted!", 5).show()
        }
        */
        return "This is a placeholder latlong"
    }
}