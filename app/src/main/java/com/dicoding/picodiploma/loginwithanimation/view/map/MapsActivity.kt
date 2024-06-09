package com.dicoding.picodiploma.loginwithanimation.view.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getLocation()
        addMarkers()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            }
        }

    private fun getLocation() {
        when (ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )) {
            PackageManager.PERMISSION_GRANTED -> {
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun addMarkers() {
        val markerIdMap = HashMap<Marker, String>()

        viewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is ResultValue.Loading -> showLoading(true)
                is ResultValue.Success -> handleSuccess(result.data.listStory, markerIdMap)
                is ResultValue.Error -> showError()
            }
        }
    }

    private fun handleSuccess(
        listStory: List<ListStoryItem>,
        markerIdMap: HashMap<Marker, String>
    ) {
        listStory.forEach { data ->
            val lat = data.lat as Double
            val lon = data.lon as Double
            val name = data.name
            val description = data.description

            val latLng = LatLng(lat, lon)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet(description)
                    .icon(
                        vectorToBitmap(
                            R.drawable.ic_baseline_location_pin_24,
                            Color.parseColor("#FC585C")
                        )
                    )
            )

            marker?.let { id -> markerIdMap[id] = data.id }
            boundsBuilder.include(latLng)
        }

        adjustCameraBounds()
        setMarkerClickListener(markerIdMap)
        showLoading(false)
    }

    private fun adjustCameraBounds() {
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMarkerClickListener(markerIdMap: HashMap<Marker, String>) {
        mMap.setOnInfoWindowClickListener { marker ->
            val id = markerIdMap[marker]
            id?.let { showStoryDetailsFromTheMarker(it) }
        }
    }

    private fun showError() {
        Toast.makeText(this, getString(R.string.txt_error_map), Toast.LENGTH_SHORT).show()
    }


    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e(TAG_BITMAP_HELPER, "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun showStoryDetailsFromTheMarker(id: String) {
        val intentToDetail = Intent(this, DetailActivity::class.java)
        intentToDetail.putExtra(STORY_ID, id)
        startActivity(intentToDetail)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG_BITMAP_HELPER = "BitmapHelper"
        const val STORY_ID = "STORY_ID"
    }
}