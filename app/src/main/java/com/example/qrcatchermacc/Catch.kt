package com.example.qrcatchermacc


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.qrcatchermacc.SavedPreference.getEmail
import com.example.qrcatchermacc.SavedPreference.getImage
import com.example.qrcatchermacc.SavedPreference.getUsername
import com.example.qrcatchermacc.databinding.ActivityCatchBinding
import com.google.firebase.database.FirebaseDatabase
import com.android.volley.Request
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector


class Catch : AppCompatActivity() {

    private lateinit var binding: ActivityCatchBinding
    var gameId : String? = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_catch)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.compassFragment, R.id.mapFragment, R.id.chatFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        gameId = intent.getStringExtra("GameId")
        Log.d("inside catch id",gameId!!)



        val username= getUsername(this)!!
        val email = getEmail(this)!!
        val imageUrl = getImage(this)!!

        val database = FirebaseDatabase.getInstance()

        val playersRef = database.getReference("games").child(gameId!!).child("players")


        val player = Player(id = email , username = username, latitude = 0.0, longitude = 0.0, imageUrl = imageUrl)
        val update = mapOf(username to player)
        playersRef.updateChildren(update)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catch, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.qr_scanner -> {
            // do stuff
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                requestCameraPermission()
            } else {
                // Permission has already been granted
                ScanQRCode(binding.root)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Show an explanation to the user
            // as to why the permission is needed
        } else {
            // No explanation needed, request the permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
    permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
        CAMERA_PERMISSION_REQUEST_CODE -> {
            // If request is cancelled, the result arrays are empty
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted
                ScanQRCode(binding.root)
            } else {
                // Permission denied
                // Disable the functionality that depends on this permission
            }
            return
        }
        // Other 'case' lines to check for other
        // permissions this app might request
    }
}


    fun ScanQRCode(view: View) {
        // Create an Intent to start the camera app
        val scanQRCodeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Start the camera app and wait for the result
        startActivityForResult(scanQRCodeIntent, REQUEST_IMAGE_CAPTURE)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the QR code image from the data Intent
            val image = data?.extras?.get("data") as Bitmap

            // Convert the QR code image to a String
            val qrCodeString = decodeQRCodeImage(image)

            // Do something with the QR code String (e.g. display it on a TextView)
            //textView.text = qrCodeString
            Log.d("PPPPPPPPPPPP",qrCodeString)
        }
    }

    private fun decodeQRCodeImage(image: Bitmap): String {
        // Create a BarcodeDetector
        val barcodeDetector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        // Create a Frame from the QR code image
        val frame = Frame.Builder().setBitmap(image).build()

        // Decode the QR code image
        val qrCodeString = barcodeDetector.detect(frame)

        // Return the QR code String
        // Get the first QR code from the list
        val qrCode = qrCodeString.valueAt(0)

        // Return the QR code String
        return qrCode.rawValue
    }

    override fun onDestroy() {
        super.onDestroy()
        val myPlayerRef =  FirebaseDatabase.getInstance().getReference("games").child(gameId!!).child("players").child(getUsername(this)!!)
        myPlayerRef.removeValue()

        var url="https://bbooss97.pythonanywhere.com/deletePlayers?id="+gameId!!
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Do something with the response
                Log.d("ZZZZZZZZZZZZZZZZZZZ",response.toString())
            },
            {error ->
                // Handle error
                Log.d("error",error.toString())
            })

        queue.add(stringRequest)

    }
}