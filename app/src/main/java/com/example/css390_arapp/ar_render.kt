package com.example.css390_arapp

// For location services

// For intent catching

// Ripped from AR-Core example


import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.core.examples.java.common.helpers.*
import com.google.ar.core.examples.java.common.samplerender.*
import com.google.ar.core.examples.java.common.samplerender.arcore.BackgroundRenderer
import com.google.ar.core.examples.java.common.samplerender.arcore.PlaneRenderer
import com.google.ar.core.examples.java.helloar.HelloArActivity
import kotlinx.android.synthetic.main.activity_ar_render.*
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

    private var REQUEST_CAMERA_PERMISSION = 100
    private lateinit var imageDimension : Size
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSessions : CameraCaptureSession
    private lateinit var captureRequestBuilder : CaptureRequest.Builder
    private lateinit var mBackgroundHandler : Handler
    private lateinit var mBackgroundThread: HandlerThread
    // Camera State Call Back
    var stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(cameraDevice: CameraDevice, i: Int) {
            cameraDevice.close()
            //cameraDevice = null
        }
    }

    // Camera to texture preview
    private fun createCameraPreview() {
        try {
            var texture = cameraView.surfaceTexture
            assert(texture != null)
            texture.setDefaultBufferSize(imageDimension.width, imageDimension.height)
            var surface = Surface(texture)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice.createCaptureSession(
                Arrays.asList(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        cameraCaptureSessions = cameraCaptureSession
                        Toast.makeText(this@ar_render, "Camera!", 1).show()
                        updatePreview()
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(this@ar_render, "Changed", 1).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // Update Camera Preview
    private fun updatePreview() {
        if (cameraDevice == null) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (cameraView.isAvailable()) openCamera() else cameraView.setSurfaceTextureListener(
            textureListener
        )
    }

    override fun onPause() {
        stopBackgroundThread()
        super.onPause()
    }

    private fun stopBackgroundThread() {
        mBackgroundThread.quitSafely()
        try {
            mBackgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread.start()
        mBackgroundHandler = Handler()
    }

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
        //render = SampleRender(surfaceView, this, assets)

        // Acquire camera permissions
        var textureView = cameraView
        assert(textureView != null)
        textureView.setSurfaceTextureListener(textureListener)
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

    var textureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {}
        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    }

    // Open camera and output to text preview
    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            var cameraID = manager.cameraIdList[0]
            var characteristics = manager.getCameraCharacteristics(cameraID)
            var map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            assert(map != null)
            imageDimension = map!!.getOutputSizes(SurfaceTexture::class.java)[0]
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            manager.openCamera(cameraID, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // In format #.### : #.###
    // Assumes coord1 is where we're standing, coord2 is target
    fun calcHaversine( coord1 : String, coord2 : String ) {
        println( "Lat/Long 1 : ${coord1}")
        println( "Lat/Long 2 : ${coord2}")
        var lati1 : Double = 0.0
        var long1 : Double = 1.0

        var lati2 : Double = 2.2
        var long2 : Double = 4.0

        var reader = Scanner( coord1 )
        lati1 = reader.nextDouble()
        reader.next()
        long1 = reader.nextDouble()

        reader = Scanner( coord2 )
        lati2 = reader.nextDouble()
        reader.next()
        long2 = reader.nextDouble()

        // Constant for Earth's radius (assumes a true sphere; ignores actual oblate shape)
        val RADIUS = 6371e3
        val φ1 = lati1 * Math.PI/180; // φ, λ in radians
        val φ2 = lati2 * Math.PI/180;
        val Δφ = (lati2-lati1) * Math.PI/180;
        val Δλ = (long2-long1) * Math.PI/180;

        val a = Math.sin(Δφ/2) * Math.sin(Δφ/2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ/2) * Math.sin(Δλ/2);
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        val d = (RADIUS * c) / 1000; // in kilometres
/*
        println( "Lat 1: ${lati1}")
        println( "Long1: ${long1}")
        println( "Lat 2: ${lati2}")
        println( "Long2: ${long2}")
        println( "ΔDist: ${d}")
        println( "Rad  : ${c}")
*/
    }
}