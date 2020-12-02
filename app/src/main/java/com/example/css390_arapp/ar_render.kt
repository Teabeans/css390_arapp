package com.example.css390_arapp

// For location services

// For intent catching
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ar_render : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_render)

        // Get the Intent that started this activity and extract the string
        val intent = this.getIntent()
        val message : String = intent.getStringExtra("coords")

        // Capture the layout's TextView and set the string as its text
        val tgtTxt = findViewById<TextView>(R.id.captured_coord)
        tgtTxt.text = message



        // Acquire camera permissions
/*        arFragment = sceneform_fragment as ArFragment

        // Adds a listener to the ARSceneView
        // Called before processing each frame
        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
            onUpdate()
        }
*/
        // Render camera to background


        // AR magic?

    }
}