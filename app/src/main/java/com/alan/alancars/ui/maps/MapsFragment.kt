package com.alan.alancars.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alan.alancars.HomeMainActivity
import com.alan.alancars.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vmadalin.easypermissions.EasyPermissions
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()
        initUserLocation()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun initUserLocation() {
        if (hasLocationPermission()){
            mFusedLocationClient.lastLocation.addOnSuccessListener {
                val location: Location? = it
                if (location != null) {
                    userLocation = location
                    initMap()
                }
            }
        } else {
            requestLocationPermission()
        }
    }


    private fun requestLocationPermission(){
        EasyPermissions.requestPermissions(
            this,
            "Application cannot function without Location Permission",
            HomeMainActivity.PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private val callback = OnMapReadyCallback { googleMap ->
        var userLoc: LatLng
        if (userLocation != null){
            userLoc = LatLng(userLocation!!.latitude, userLocation!!.longitude)
            googleMap.addMarker(MarkerOptions().position(userLoc).title("Welcome in da Hood User"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc))
        } else {
            userLoc = LatLng(45.84288826227391, 15.969145936964923)
        }
        googleMap.addMarker(MarkerOptions().position(userLoc).title("Welcome in da Hood"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc))
    }
}