package com.example.qrcatchermacc


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.android.volley.Request
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.database.*
import org.json.JSONObject


class Catch : AppCompatActivity() {

    private lateinit var binding: ActivityCatchBinding
    var gameId : String? = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 1
    private lateinit var database : FirebaseDatabase
    private lateinit var flag: String
    private lateinit var winListener : ValueEventListener
    private lateinit var winRef: DatabaseReference
    var iconUrl : String? = null

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
    

        val username= getUsername(this)!!
        val email = getEmail(this)!!
        val imageUrl = getImage(this)!!

        database = FirebaseDatabase.getInstance()

        var gameRef=database.getReference("games").child(gameId!!)

        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val game = dataSnapshot.getValue(Game::class.java)
                if (game != null) {
                    var targetLatitude = game.latitude!!
                    var targetLongitude = game.longitude!!
                    //async call to weatherapi
                    weatherCall(targetLatitude,targetLongitude)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


        val playersRef = database.getReference("games").child(gameId!!).child("players")


        val player = Player(id = email , username = username, latitude = 0.0, longitude = 0.0, imageUrl = imageUrl)
        val update = mapOf(username to player)
        playersRef.updateChildren(update)

        var flagRef=database.getReference("games").child(gameId!!).child("flag")

        winRef=database.getReference("games").child(gameId!!).child("win")

        winListener=object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var win = dataSnapshot.getValue(Boolean::class.java)!!
                if (win) {
                    val intentWin = Intent(this@Catch, Win::class.java)
                    intentWin.putExtra("GameId",gameId)
                    startActivity(intentWin)
                    this@Catch.finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        winRef.addValueEventListener(winListener)

        flagRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                flag = dataSnapshot.getValue(String::class.java)!!
                // Do something with the boolean value
                }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }

        })
    }

    fun closeCatch(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm exit")
        //builder.setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
        builder.setMessage("Are you sure you want to exit the game?")

        builder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        closeCatch()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catch, menu)
        return true
    }


    fun calledScanQRCode(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestCameraPermission()
        } else {
            // Permission has already been granted
            ScanQRCode(binding.root)
        }
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
        else -> {
            closeCatch() //super.onOptionsItemSelected(item)
            true
        }

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

            if (flag.contentEquals(qrCodeString)){
                val winRef = database.getReference("games").child(gameId!!).child("win")
                winRef.setValue(true)
            }else{
                Toast.makeText(this, "Wrong or not detected correctly QRcode, try again", Toast.LENGTH_LONG)
                .show()
            }
            //return
        }else if(requestCode == REQUEST_IMAGE_CAPTURE){
            Toast.makeText(this, "QRcode not detected correctly, try again", Toast.LENGTH_LONG)
            .show()
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
        var qrCode=""
        if( qrCodeString.size() !=0){
            qrCode = qrCodeString.valueAt(0).rawValue
        }


        // Return the QR code String
        return qrCode
    }

    override fun onDestroy() {
        super.onDestroy()
        winRef.removeEventListener(winListener)
        val myPlayerRef =  FirebaseDatabase.getInstance().getReference("games").child(gameId!!).child("players").child(getUsername(this)!!)
        myPlayerRef.removeValue()

        deletePlayers(0)

    }

    fun deletePlayers(r:Int){
        if (r>=5){
            return
        }
        var url="https://bbooss97.pythonanywhere.com/deletePlayers?id="+gameId!!
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Do something with the response
                
            },
            {error ->
                // Handle error
               
                deletePlayers(r+1)
            })

        //stringRequest.retryPolicy = DefaultRetryPolicy(10, 5, 2F)
        queue.add(stringRequest)
    }

    fun weatherCall(latitude: Double?,longitude:Double?){
        val queue = Volley.newRequestQueue(this)
        val API_KEY="e007861412303123ec33263be342a8fe"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$API_KEY"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->

                val jsonObject = JSONObject(response)
                val iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon")
                iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

            },
            {
                // Handle error
                
            })
        queue.add(stringRequest)

    }

}