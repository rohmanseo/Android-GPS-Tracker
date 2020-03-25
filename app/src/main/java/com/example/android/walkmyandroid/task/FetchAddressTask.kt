package com.example.android.walkmyandroid.task

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.text.TextUtils
import com.example.android.walkmyandroid.model.LocationModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FetchAddressTask(private val context: Context, private val onTaskCompleted: OnTaskCompleted) : AsyncTask<Location?, Void?, LocationModel>() {
    override fun doInBackground(vararg locations: Location?): LocationModel? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val location = locations[0]
        var addresses: List<Address>? = null
        val resultMessage: String

        try {
            if (location != null) {
                addresses = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        resultMessage = if (addresses == null || addresses.isEmpty()) {
            "No address found"
        } else {
            val address = addresses[0]
            val addressParts = ArrayList<String?>()
            for (i in 0..address.maxAddressLineIndex) {
                addressParts.add(address.getAddressLine(i))
            }
            TextUtils.join("\n", addressParts)
        }
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return LocationModel(currentTime, resultMessage, location?.latitude.toString(), location?.longitude.toString())
    }

    override fun onPostExecute(result: LocationModel) {
        onTaskCompleted.onTaskCompleted(result)
        super.onPostExecute(result)
    }

    interface OnTaskCompleted {
        fun onTaskCompleted(result: LocationModel)
    }
}