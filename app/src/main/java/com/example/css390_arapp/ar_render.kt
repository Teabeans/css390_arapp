package com.example.css390_arapp

// For location services

// For intent catching

// Ripped from AR-Core example


import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.core.examples.java.common.helpers.*
import com.google.ar.core.examples.java.common.samplerender.*
import com.google.ar.core.examples.java.common.samplerender.arcore.BackgroundRenderer
import com.google.ar.core.examples.java.common.samplerender.arcore.PlaneRenderer
import com.google.ar.core.examples.java.helloar.HelloArActivity
import java.util.*


class ar_render : AppCompatActivity () {

    // DECLARATION VARIABLES RIPPED FROM ARCORE SAMPLE
    private val TAG = HelloArActivity::class.java.simpleName

    private val SEARCHING_PLANE_MESSAGE = "Searching for surfaces..."
    private val WAITING_FOR_TAP_MESSAGE = "Tap on a surface to place an object."

    // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
    // constants.
    private val sphericalHarmonicFactors = floatArrayOf(
        0.282095f,
        -0.325735f,
        0.325735f,
        -0.325735f,
        0.273137f,
        -0.273137f,
        0.078848f,
        -0.273137f,
        0.136569f
    )

    private val Z_NEAR = 0.1f
    private val Z_FAR = 100f

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private val surfaceView: GLSurfaceView? = null

    private val installRequested = false

    private val session: Session? = null
    private val messageSnackbarHelper = SnackbarHelper()
    private val displayRotationHelper: DisplayRotationHelper? = null
    private val trackingStateHelper = TrackingStateHelper(this)
    private val tapHelper: TapHelper? = null
    private val render: SampleRender? = null

    private val planeRenderer: PlaneRenderer? = null
    private val backgroundRenderer: BackgroundRenderer? = null
    private val virtualSceneFramebuffer: Framebuffer? = null
    private val hasSetTextureNames = false

    private val depthSettings = DepthSettings()
    private val depthSettingsMenuDialogCheckboxes = BooleanArray(2)

    private val instantPlacementSettings = InstantPlacementSettings()
    private val instantPlacementSettingsMenuDialogCheckboxes = BooleanArray(1)

    // Assumed distance from the device camera to the surface on which user will try to place objects.
    // This value affects the apparent scale of objects while the tracking method of the
    // Instant Placement point is SCREENSPACE_WITH_APPROXIMATE_DISTANCE.
    // Values in the [0.2, 2.0] meter range are a good choice for most AR experiences. Use lower
    // values for AR experiences where users are expected to place objects on surfaces close to the
    // camera. Use larger values for experiences where the user will likely be standing and trying to
    // place an object on the ground or floor in front of them.
    private val APPROXIMATE_DISTANCE_METERS = 2.0f

    // Point Cloud
    private val pointCloudVertexBuffer: VertexBuffer? = null
    private val pointCloudMesh: Mesh? = null
    private val pointCloudShader: Shader? = null

    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private val lastPointCloudTimestamp: Long = 0

    // Virtual object (ARCore pawn)
    private val virtualObjectMesh: Mesh? = null
    private val virtualObjectShader: Shader? = null
    private val anchors = ArrayList<Anchor>()

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16) // view x model

    private val modelViewProjectionMatrix = FloatArray(16) // projection x view x model

    private val sphericalHarmonicsCoefficients = FloatArray(9 * 3)
    private val viewInverseMatrix = FloatArray(16)
    private val worldLightDirection = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
    private val viewLightDirection = FloatArray(4) // view x world light direction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_render)

        // Get the Intent that started this activity and extract the string
        val intent = this.getIntent()
        val message : String = intent.getStringExtra("coords")

        // Capture the layout's TextView and set the string as its text
        val tgtTxt = findViewById<TextView>(R.id.captured_coord)
        tgtTxt.text = message

        // Rendering. The Renderers are created here, and initialized when the GL surface is created.
        val surfaceView: GLSurfaceView? = findViewById<GLSurfaceView?>(R.id.surfaceview)
        val displayRotationHelper = DisplayRotationHelper( /*context=*/this)

        // Set up renderer.
        val render = SampleRender(surfaceView, this, assets)

        // Acquire camera permissions

/*

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