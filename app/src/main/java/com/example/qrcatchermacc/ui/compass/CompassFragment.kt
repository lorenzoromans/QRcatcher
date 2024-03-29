package com.example.qrcatchermacc.ui.compass


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.service.autofill.Validators.not
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.Game
import com.example.qrcatchermacc.SavedPreference.getUsername
import com.example.qrcatchermacc.databinding.FragmentCompassBinding
import com.google.android.gms.location.*
import com.google.firebase.database.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.*
import kotlin.text.Typography.degree


class CompassFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!

    //-------------------------------
    var mAzimuth = 0f
    private var a = 0.55f
    private var mSensorManager: SensorManager? = null
    private var mRotationV: Sensor? = null
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    var haveSensor = false
    var haveSensor2 = false
    var rMat = FloatArray(9)
    var orientation = FloatArray(3)
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    private var currentDegree = 0f

    var targetLatitude: Double = 41.914192
    var targetLongitude: Double = 12.530910
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var previousLatitude: Double = 0.0
    var previousLongitude: Double = 0.0
    

    private var catchEnded: Boolean = false
    lateinit var username: String
    lateinit var playerRef: DatabaseReference

    //-------------------------------
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private var distanza: Double =0.0
    private var setted: Boolean = false
    private var inCompass: Boolean = true

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val database = FirebaseDatabase.getInstance()
        val gamesRef = database.getReference("games")
        val gameId = requireActivity().getIntent()!!.getExtras()!!.getString("GameId")

        username = getUsername(requireContext())!!

        val gameRef = gamesRef.child(gameId!!)
        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val game = dataSnapshot.getValue(Game::class.java)
                if (game != null) {
                    targetLatitude = game.latitude!!
                    targetLongitude = game.longitude!!
                    // Do something with the latitude and longitude
                } else {
                    // The game document was not found
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // An error occurred
            }
        })


        playerRef = gameRef.child("players").child(username)

        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.scannerImage.setOnClickListener{
            (activity as Catch).calledScanQRCode()
        }

        mSensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationRequest!!.setInterval(2 * 1000) // 2 seconds
        locationRequest!!.setFastestInterval(2 * 1000) // 2 seconds

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                // Update the latitude and longitude when the location changes

                try{
                    val iconUrl = (activity as Catch).iconUrl
                    if (iconUrl != null && !setted && inCompass){
                        binding.weatherFetching.setVisibility(View.GONE)
                        binding.weatherImage.setVisibility(View.VISIBLE)
                        Glide.with(requireContext()).load(iconUrl).into(binding.weatherImage)
                        setted = true
                    }
                }catch(e : Exception){ /* no need to manage the exception */}


                val location: Location? = p0?.lastLocation
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    //update the location
                    val update = mapOf("latitude" to latitude, "longitude" to longitude)
                    if (!catchEnded){
                        playerRef.updateChildren(update)
                    }

                    //set previous lat and long
                    previousLatitude = latitude
                    previousLongitude = longitude

                    try{
                    setTextViewDistance()
                    }catch(e : Exception){
                        Log.d("eccezione compass","eccezione binding compass")
                    }
                }
            }
        }

        //Initialize the listeners
        start()
        //check if the permissions have been granted by the user and if the GPS has been activated
        if (!checkPermissions()) {
            requestPermissions()
        }
        if (!isLocationEnabled()) {
            Toast.makeText(requireActivity(), "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }


        return root
    }


    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        inCompass=true
        mFusedLocationClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.myLooper()!!
        )

    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            var newMAzimuth = ((Math.toDegrees(
                SensorManager.getOrientation(
                    rMat,
                    orientation
                )[0].toDouble()
            ) + 360).toInt() % 360).toFloat()

            if(Math.abs(newMAzimuth-mAzimuth)<180){
                mAzimuth=a*mAzimuth + (1-a)*newMAzimuth
            }else{
                mAzimuth=newMAzimuth
            }

        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.size)
            mLastAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.size)
            mLastMagnetometerSet = true
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer)
            SensorManager.getOrientation(rMat, orientation)
            var newMAzimuth = ((Math.toDegrees(
                SensorManager.getOrientation(
                    rMat,
                    orientation
                )[0].toDouble()
            ) + 360).toInt() % 360).toFloat()

            if(Math.abs(newMAzimuth-mAzimuth)<180){
                mAzimuth=a*mAzimuth + (1-a)*newMAzimuth
            }else{
                mAzimuth=newMAzimuth
            }
        }

        mAzimuth = Math.round(mAzimuth).toFloat()

        val angle = -mAzimuth + getBearing(
            latitude,
            longitude,
            targetLatitude,
            targetLongitude
        ).toFloat()

        /*
        val ra = RotateAnimation(
            currentDegree,
            angle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        // how long the animation will take place
        ra.duration = 210

        // set the animation after the end of the reservation status
        //changed to false
        ra.fillAfter = false

        // Start the animation if visible
        //if (binding.imageViewCompass.visibility==View.VISIBLE){
        //    binding.imageViewCompass.startAnimation(ra)
       // }
       */
        binding.imageViewCompass.rotation = angle

        currentDegree = angle
    }


    fun getBearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val lat = Math.toRadians(lat1)
        val latT = Math.toRadians(lat2)
        val longDiff = Math.toRadians(lng2 - lng1)
        val y = sin(longDiff) * cos(latT)
        val x = cos(lat) * sin(latT) - sin(lat) * cos(latT) * cos(longDiff)
        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // earth radius in meters
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)
        val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun setTextViewDistance(){
        distanza=distance(latitude,longitude,targetLatitude,targetLongitude)

        if(distanza < 10.00){
            binding.imageViewCompass.setVisibility(View.GONE)
            binding.scannerImage.setVisibility(View.VISIBLE)
            binding.progressBar.setVisibility(View.GONE)
        }else{
            binding.imageViewCompass.setVisibility(View.VISIBLE)
            binding.scannerImage.setVisibility(View.GONE)
            binding.progressBar.setVisibility(View.GONE)
        }


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //getLocation()
            }
        }
    }


    fun start() {
        if (mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if (mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || mSensorManager!!.getDefaultSensor(
                    Sensor.TYPE_MAGNETIC_FIELD
                ) == null
            ) {
                Toast.makeText(
                    requireContext(),
                    "Your device doesn't support the Compass.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                mMagnetometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
                haveSensor = mSensorManager!!.registerListener(
                    this,
                    mAccelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
                haveSensor2 = mSensorManager!!.registerListener(
                    this,
                    mMagnetometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        } else {
            mRotationV = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            haveSensor =
                mSensorManager!!.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI)
        }
    }


    override fun onPause() {
        super.onPause()
        inCompass=false
    }


    override fun onResume() {
        super.onResume()
        inCompass=true
        start()
    }

    override fun onDestroyView(){
        super.onDestroyView()
        setted=false

    }

    override fun onDestroy() {
        super.onDestroy()
        catchEnded=true
        _binding = null
        val sensorList = mSensorManager?.getSensorList(Sensor.TYPE_ALL)
        if (sensorList != null) {
            for (sensor in sensorList) {
                mSensorManager?.unregisterListener(this, sensor)
            }
        }
        mFusedLocationClient.removeLocationUpdates(locationCallback!!)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /** not implemented */  }
}