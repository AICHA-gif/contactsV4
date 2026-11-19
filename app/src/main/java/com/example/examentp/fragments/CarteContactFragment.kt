package com.example.examenTP.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.examenTP.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment :
    Fragment(R.layout.fragment_carte_contact),
    OnMapReadyCallback {

    companion object {

        fun newInstance(lat: Double, lon: Double) =
            CarteContactFragment().apply {
                arguments = Bundle().apply {
                    putDouble("latitude", lat)
                    putDouble("longitude", lon)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val lat = arguments?.getDouble("latitude") ?: return
        val lon = arguments?.getDouble("longitude") ?: return

        val position = LatLng(lat, lon)

        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title("Position du contact")
        )

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(position, 15f)
        )
    }
}