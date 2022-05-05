package com.bangkit.story.ui.view

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.bangkit.story.R
import com.bangkit.story.databinding.ActivityMapsBinding
import com.bangkit.story.ui.viewmodel.MapsViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        initToolbar()
        initMap()
    }

    private fun initViewBinding() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        supportActionBar?.apply {
            this.title = getString(R.string.loc_stories)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
        }

        mapsViewModel.getAllStoriesForMaps().observe(this) { stories ->
            val boundsBuilder = LatLngBounds.builder()

            stories?.forEach { story ->
                val location = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)

                map.apply {
                    addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(story.name)
                            .snippet(story.description)
                    )

                    // set boundaries
                    boundsBuilder.include(location)
                    val bounds = boundsBuilder.build()
                    animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                }
            }
        }

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val setStyle = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!setStyle)
                Log.e(TAG, "Style parsing failed.")
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}