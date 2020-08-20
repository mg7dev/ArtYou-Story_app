package com.dw.artyou.helper

import android.Manifest
import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.dw.artyou.activities.MainActivity
import com.dw.artyou.models.ErrorModel
import com.dw.artyou.models.Owner
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

class HelperMethods {

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return TextUtils.isEmpty(target) || !Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun setSnackBar(snackbar: Snackbar, isVisible: Boolean) {
            if (isVisible) {
                snackbar.dismiss()
            } else {
                snackbar.show()
            }
        }

        fun disableTouch(b: Boolean, activity: Activity?) {
            if (activity != null) {
                if (b) {
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                } else {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            } else {
                Log.d("hwgwhgd", "null value")
            }
        }

        val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun checkPermission(context: Context?): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            return if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            (context as Activity?)!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        val alertBuilder =
                            AlertDialog.Builder(context)
                        alertBuilder.setCancelable(true)
                        alertBuilder.setTitle("Permission Required")
                        alertBuilder.setMessage("Storage permission is necessary")
                        alertBuilder.setPositiveButton(
                            R.string.yes
                        ) { dialog, which ->
                            ActivityCompat.requestPermissions(
                                (context as Activity?)!!,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                            )
                        }
                        val alert = alertBuilder.create()
                        alert.show()
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(Color.parseColor("#5b2622"))
                        alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(Color.parseColor("#5b2622"))
                    } else {
                        ActivityCompat.requestPermissions(
                            (context as Activity?)!!,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    false
                } else {
                    true
                }
            } else {
                true
            }
        }

        //==================Report Story=============//
        fun reportStory(context: Context?, comment: String, story_id: String) {

            val str = ("{\n" +
                    "  \"comment\": \"" + comment + "\",\n" +
                    "  \"storye_id\": \"" + story_id + "\"\n" +
                    "}")
            try {
                val requestQueue = Volley.newRequestQueue(context)

                val requestBody = str
                Log.d("wdwqqwd", requestBody)
                val stringRequest: StringRequest = object : StringRequest(
                    Method.POST,
                    Api.REPORT_STORY,
                    Response.Listener { response -> },
                    Response.ErrorListener { error ->
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(
                                    response.data,
                                    charset(HttpHeaderParser.parseCharset(response.headers))
                                )
                                val obj: JSONObject = JSONObject(res)
                                Log.d("wqdsc", "Error : ${res}")

                                Toast.makeText(
                                    context,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } catch (e2: JSONException) {
                                e2.printStackTrace()
                                Log.d("wqedwqd", "Error e2 : ${e2.message}")

                            }
                        }
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    override fun getHeaders(): MutableMap<String, String> {
                        val params: MutableMap<String, String> = HashMap<String, String>()
                        params["Authorization"] =
                            "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                        return params
                    }

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        return try {
                            requestBody.toByteArray(charset("utf-8"))
                        } catch (uee: UnsupportedEncodingException) {
                            VolleyLog.wtf(
                                "Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody,
                                "utf-8"
                            )
                            return "".toByteArray()
                        }
                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
                        var responseString: String = ""

                        responseString = response.statusCode.toString()
                        Log.d("qfdcdxd", responseString)
                        Log.d("cdecsaxcsa", response.toString())


                        return Response.success(
                            responseString,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    }
                }
                requestQueue.add(stringRequest)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        //==============202007180123==============//
        fun deleteStory(context: Context?,story_id: String,owner:Owner){

            val str = (
                    "{\n"+
                    "   \"owner\": {\n"+
                    "      \"type\": \""+   owner.type  +"\",\n"+
                    "      \"id\"  : \""+   owner.id    +"\" \n"+
                    "}\n"+
                    "}"
                    )
            try {
                val requestQueue = Volley.newRequestQueue(context)

                val requestBody = str
                Log.d("wdwqqwd", requestBody)
                val stringRequest: StringRequest = object : StringRequest(
                    Method.DELETE,
                    Api.DELETE_STORY+story_id,
                    Response.Listener { response -> },
                    Response.ErrorListener { error ->
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(
                                    response.data,
                                    charset(HttpHeaderParser.parseCharset(response.headers))
                                )
                                val obj: JSONObject = JSONObject(res)
                                Log.d("wqdsc", "Error : ${res}")

                                Toast.makeText(
                                    context,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } catch (e2: JSONException) {
                                e2.printStackTrace()
                                Log.d("wqedwqd", "Error e2 : ${e2.message}")

                            }
                        }
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    override fun getHeaders(): MutableMap<String, String> {
                        val params: MutableMap<String, String> = HashMap<String, String>()
                        params["Authorization"] =
                            "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                        return params
                    }

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        return try {
                            requestBody.toByteArray(charset("utf-8"))
                        } catch (uee: UnsupportedEncodingException) {
                            VolleyLog.wtf(
                                "Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody,
                                "utf-8"
                            )
                            return "".toByteArray()
                        }
                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
                        var responseString: String = ""

                        responseString = response.statusCode.toString()
                        Log.d("qfdcdxd", responseString)
                        Log.d("cdecsaxcsa", response.toString())


                        return Response.success(
                            responseString,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    }
                }
                requestQueue.add(stringRequest)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        //============================Update Profile Image=================//

        fun getFileDataFromBitmap(bitmap: Bitmap): ByteArray? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }

        @Throws(IOException::class)
        private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
            val parcelFileDescriptor =
                (context as AppCompatActivity).contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor =
                parcelFileDescriptor!!
                    .getFileDescriptor()
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor!!.close()
            return image
        }

        //====================Upload Profile Image Api===============//
        fun uploadImage(
            bitmap: Bitmap,
            context: Context,
            profile_image: ImageView,
            progressBar: ProgressBar,
            isMain: Boolean
        ) {

            progressBar.visibility = View.VISIBLE
            disableTouch(true, (context as AppCompatActivity))
            val volleyMultipartRequest: VolleyMultipartRequest =
                object : VolleyMultipartRequest(
                    Request.Method.PATCH, Api.UPLOAD_PROFILE,
                    Response.Listener<NetworkResponse> { networkResponse ->
                        (context as AppCompatActivity).runOnUiThread(Runnable {
                            disableTouch(false, (context as AppCompatActivity))
                            progressBar.visibility = View.GONE

                        })

                        Log.d("ergre", networkResponse.statusCode.toString())
                        var stringResponse = ""
                        try {
                            stringResponse = String(
                                networkResponse.data,
                                charset(HttpHeaderParser.parseCharset(networkResponse.headers))
                            )
                            val obj = JSONObject(stringResponse)
                            val mediaObject = obj.getJSONObject("media")
                            val userModel = SessionManager.getInstance(context)!!.userModel!!
                            userModel.avatar = mediaObject.getString("filename")
                            SessionManager.getInstance(context)!!.userModel = userModel
                            if (isMain) {
                                (context as MainActivity).loadUserProfile()
                            }

                            Glide.with(context).load(bitmap).into(profile_image)
                            Log.d("fjehwef", obj.toString())

                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }

                    },
                    Response.ErrorListener { error ->
                        (context as AppCompatActivity).runOnUiThread(Runnable {
                            HelperMethods.disableTouch(false, (context as AppCompatActivity))
                            progressBar.visibility = View.GONE

                        })
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(
                                    response.data,
                                    charset(HttpHeaderParser.parseCharset(response.headers))
                                )
                                val obj: JSONObject = JSONObject(res)
                                Log.d("thngfg", "Error : ${res}")

                                Toast.makeText(
                                    context,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } catch (e2: JSONException) {
                                e2.printStackTrace()
                                Log.d("fbrgrg", "Error e2 : ${e2.message}")

                            }
                        }
                        Log.d("rg4tef", error.toString())
                    }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val tokenParams: MutableMap<String, String> =
                            HashMap()
                        tokenParams["Authorization"] =
                            "Bearer " + SessionManager.getInstance(context)!!.authToken
                        Log.d("ejfrjef", tokenParams.toString())
                        return tokenParams
                    }

                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> =
                            HashMap()

                        params["type"] = "PHOTO"

                        return params
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Throws(IOException::class)
                    override fun getByteData(): Map<String, DataPart>? {
                        val params: MutableMap<String, DataPart> =
                            HashMap()

                        params["file"] = DataPart(
                            SystemClock.elapsedRealtime().toString() + ".png",
                            getFileDataFromBitmap(bitmap)
                        )

                        return params
                    }
                }
            volleyMultipartRequest.setRetryPolicy(
                DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            VolleySingleton.getInstance(context).addToRequestQueue(volleyMultipartRequest)
        }

        fun generateStoryLink(storyId: String): String {
            return "https://webapi-staging.artyou.global/storyes/public/" + storyId
        }
        fun parseError(response: retrofit2.Response<*>): ErrorModel? {
            val error: ErrorModel
            try {
                error = Gson().fromJson(response.errorBody()?.charStream(), ErrorModel::class.java)
            } catch (e: Exception) {
                return ErrorModel()
            }
            return error
        }

    }


}