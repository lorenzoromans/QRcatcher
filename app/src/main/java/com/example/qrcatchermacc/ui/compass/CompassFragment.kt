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
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.qrcatchermacc.databinding.FragmentCompassBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.NonCancellable.start
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassFragment : Fragment(), SensorEventListener{
    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!

    //-------------------------------
    var compassImg: ImageView? = null
    var mAzimuth = 0
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

    private var targetLatitude: Double = 41.914192
    private var targetLongitude: Double = 12.530910
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    //-------------------------------


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val compassViewModel =
            ViewModelProvider(this).get(CompassViewModel::class.java)

        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCompass
        compassViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        mSensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager /**AppCompatActivity.SENSOR_SERVICE*/
        compassImg = binding.imageViewCompass
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //Initialize the listeners
        start()
        //check if the permissions have been granted by the user and if the GPS has been activated
        if (!checkPermissions()){
            requestPermissions()
        }
        if(!isLocationEnabled()){
            Toast.makeText(requireActivity(), "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        getLocation()

        return root
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            mAzimuth = (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0].toDouble()) + 360).toInt() % 360
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
            mAzimuth = (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0].toDouble()) + 360).toInt() % 360
        }
        mAzimuth = Math.round(mAzimuth.toFloat())

        getLocation()
        val angle = -mAzimuth.toFloat() + getBearing(latitude, longitude, targetLatitude, targetLongitude).toFloat()
        Log.d("AAAAAAAAAA", angle.toString())
        Log.d("DDDDDDDDDD", (-mAzimuth).toString())
        Log.d("BBBBBBBBBB", latitude.toString())
        Log.d("CCCCCCCCCC", longitude.toString())
        val ra = RotateAnimation(
            currentDegree,
            //(-mAzimuth.toFloat() + getBearing(latitude, longitude, targetLatitude, targetLongitude).toFloat()),
            angle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        // how long the animation will take place
        ra.duration = 210

        // set the animation after the end of the reservation status
        ra.fillAfter = true

        // Start the animation
        compassImg!!.startAnimation(ra)
        //currentDegree = -mAzimuth.toFloat()
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


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        //------------------------------------------------------
                        latitude = list[0].latitude
                        longitude = list[0].longitude
                    }
                }
            } else {
                /**
                Toast.makeText(requireActivity(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                */
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
                getLocation()
            }
        }
    }


    fun start() {
        if (mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if (mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || mSensorManager!!.getDefaultSensor(
                    Sensor.TYPE_MAGNETIC_FIELD
                ) == null
            ) {
                Toast.makeText(requireContext(), "Your device doesn't support the Compass.", Toast.LENGTH_SHORT).show()
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
        /**
        if (haveSensor) {
            mSensorManager!!.unregisterListener(this, mRotationV)
        } else {
            mSensorManager!!.unregisterListener(this, mAccelerometer)
            mSensorManager!!.unregisterListener(this, mMagnetometer)
        }
        */

    }


    override fun onResume() {
        super.onResume()
        start()
    }

/**
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val sensorList = mSensorManager?.getSensorList(Sensor.TYPE_ALL)
        if (sensorList != null) {
            for (sensor in sensorList) {
                mSensorManager?.unregisterListener(this, sensor)
            }
        }
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        val sensorList = mSensorManager?.getSensorList(Sensor.TYPE_ALL)
        if (sensorList != null) {
            for (sensor in sensorList) {
                mSensorManager?.unregisterListener(this, sensor)
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {   /** not implemented */   }

}



    /**
    companion object {
        fun newInstance() = CompassFragment()
    }

    private lateinit var viewModel: CompassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        // TODO: Use the ViewModel
    }


}*/