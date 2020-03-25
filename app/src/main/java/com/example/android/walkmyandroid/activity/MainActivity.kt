/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.walkmyandroid.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.walkmyandroid.R
import com.example.android.walkmyandroid.adapter.LocationAdapter
import com.example.android.walkmyandroid.model.LocationModel
import com.example.android.walkmyandroid.task.FetchAddressTask
import com.example.android.walkmyandroid.task.FetchAddressTask.OnTaskCompleted
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnTaskCompleted {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    var mTrackingLocation = false
    private lateinit var listLocation: ArrayList<LocationModel>
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        initRecyclerView()

        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY)
        }

        btnLocation.setOnClickListener {
            if (mTrackingLocation) {
                stopTrackingLocation()
            } else {
                startTrackingLocation()
            }
        }
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (mTrackingLocation) {
                    FetchAddressTask(this@MainActivity, this@MainActivity).execute(locationResult.lastLocation)
                }
                super.onLocationResult(locationResult)
            }
        }
    }

    private fun initRecyclerView() {
        listLocation = ArrayList()
        locationAdapter = LocationAdapter(this@MainActivity, listLocation)
        recLayout.layoutManager = LinearLayoutManager(this@MainActivity)
        recLayout.setHasFixedSize(true)
        recLayout.adapter = locationAdapter

    }

    private fun startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
            imgPrev.visibility = View.GONE
            tvHint.visibility = View.GONE
            recLayout.visibility = View.VISIBLE
            mTrackingLocation = !mTrackingLocation
            btnLocation.setImageResource(R.drawable.ic_stop)
        }
    }

    private fun stopTrackingLocation() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        btnLocation.setImageResource(R.drawable.ic_start)
        mTrackingLocation = !mTrackingLocation
    }

    private val locationRequest: LocationRequest
        get() {
            val locationRequest = LocationRequest()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 5000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return locationRequest
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTrackingLocation()
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation)
        super.onSaveInstanceState(outState)
    }

    override fun onTaskCompleted(result: LocationModel) {
        tvHint.text = getString(R.string.address_text,
                result, System.currentTimeMillis())
        listLocation.add(result)
        locationAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation()
            mTrackingLocation = true
        }
        super.onPause()
    }

    override fun onResume() {
        if (mTrackingLocation) {
            startTrackingLocation()
        }
        super.onResume()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val TRACKING_LOCATION_KEY = "tracking_location"
    }

}