package com.example.qrcatchermacc.ui.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.target.CustomTarget
import com.example.qrcatchermacc.Player
import com.example.qrcatchermacc.R
import com.example.qrcatchermacc.SavedPreference.getUsername
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.api.ResourceProto.resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*


class MapFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var players: List<Player?> = ArrayList()
    private var allMarkers: ArrayList<Marker> = ArrayList()
    private lateinit var job: Job
    private var continua = true
    private lateinit var myLocation: LatLng


    //permission checks suppressed since they are already done in the compass fragment and the compassFragment is still listening for position to update the DB of the players
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        //added variables
        map = googleMap

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val rome = LatLng(41.890984526885234, 12.503624605850224)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(rome, 15f)
        googleMap.animateCamera(cameraUpdate)
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)

        val database = FirebaseDatabase.getInstance()
        val gamesRef = database.getReference("games")
        val gameId = requireActivity().getIntent()!!.getExtras()!!.getString("GameId")


        val playersRef = gamesRef.child(gameId!!).child("players")


        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d("aaaaaaaa", "CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                players = dataSnapshot.children.map { it.getValue(Player::class.java) }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show()

        //val currentLocation = map.myLocation
        if (myLocation != null) {
            //val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15f)
            map.animateCamera(cameraUpdate)
        }

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


    }

    override fun onMapReady(p0: GoogleMap) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        //job.cancel()
        continua = false
    }

    override fun onStart() {
        super.onStart()
        val username = getUsername(requireContext())!!
        job = MainScope().launch {
            while (true) {
                Log.d(
                    "TTTTTTTTTTTTTTTTTTTTTT",
                    "inside the coroutine---------------------------------------"
                )

                //NEL THREAD----
                for (mLocationMarker in allMarkers) {
                    mLocationMarker.remove()
                }
                allMarkers.clear()
                for (player in players) {

                    val playerLoc = LatLng(player?.latitude!!, player?.longitude!!)
                    if (username == player?.username) {
                        myLocation = playerLoc
                    }

                    Glide.with(requireContext())
                        .asBitmap()
                        .load(player.imageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val res = BitmapDescriptorFactory.fromBitmap(resource)
                                val mLocationMarker: Marker = map.addMarker(
                                    MarkerOptions().position(playerLoc).title(player?.username)
                                        .icon(res)
                                )!! // add the marker to Map
                                allMarkers.add(mLocationMarker) // add the marker to array
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // Clear the Bitmap if needed
                            }
                        })

                }
                delay(3000)
            }
        }
    }
}