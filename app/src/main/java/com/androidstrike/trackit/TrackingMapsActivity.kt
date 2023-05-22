//package com.androidstrike.trackit
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.androidstrike.trackit.databinding.ActivityTrackingMapsBinding
//import com.androidstrike.trackit.model.MyLocation
//import com.androidstrike.trackit.utils.Common
//import com.androidstrike.trackit.utils.Common.PUBLIC_LOCATION
//import com.google.android.gms.maps.model.MapStyleOptions
//import com.google.firebase.database.*
//
//class TrackingMapsActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {
//
//    private lateinit var mMap: GoogleMap
//    lateinit var trackingUserLocation: DatabaseReference
//    private lateinit var binding: ActivityTrackingMapsBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityTrackingMapsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        registerEventRealTime()
//    }
//
//    private fun registerEventRealTime() {
//        trackingUserLocation = FirebaseDatabase.getInstance()
//            .getReference(PUBLIC_LOCATION)
//            .child(Common.trackingUser!!.uid!!)
//
//        trackingUserLocation.addValueEventListener(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        mMap.uiSettings.isZoomControlsEnabled = true
//
//        //Skin
//        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_uber_style))
//
//    }
//
//    override fun onDataChange(snapshot: DataSnapshot) {
//        if (snapshot.value != null){
//            val location = snapshot.getValue(MyLocation::class.java)
//
//            //add marker
//            val userMarker = LatLng(location!!.latitude, location.longitude)
//            mMap!!.addMarker(MarkerOptions().position(userMarker).title(Common.trackingUser!!.email)
//                .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.time))))
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 16f))
//        }
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        trackingUserLocation.addValueEventListener(this)
//    }
//
//    override fun onStop() {
//        trackingUserLocation.removeEventListener(this)
//        super.onStop()
//    }
//}