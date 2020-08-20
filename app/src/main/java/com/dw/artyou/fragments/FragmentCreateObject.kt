package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.dw.artyou.interfaces.ObjectTypeInterface
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.ObjectTypeModel
import com.dw.artyou.recyclerAdapters.ObjectTypeAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_create_object.*
import org.json.JSONArray
import java.io.IOException
import kotlin.collections.ArrayList

class FragmentCreateObject : Fragment(),
    ObjectTypeInterface {


    private var result: Boolean = false
    private var selectedObjectId: String = ""
    private var selectedBitmap: Bitmap? = null
    private lateinit var objectlist: ArrayList<ObjectTypeModel>
    private lateinit var dialog_list: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_object, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getObjectType()
        objectlist = ArrayList()
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val parms = LinearLayout.LayoutParams(width, width)
        rl_image_create_object.layoutParams = parms


        clickEvents()

    }


    //===============NEXT PAGE============
    fun nextPress() {
        if (et_titel_create_object.text.toString().isEmpty()) {
            et_titel_create_object.error = "Titel should not be empty"
        } else if (et_object_type_create_object.text.toString().isEmpty()) {
            et_object_type_create_object.error = "Object type should not be empty"
        } else if (selectedBitmap == null) {
            Toast.makeText(context, "Please select image", Toast.LENGTH_SHORT).show()
        } else {
            activity!!.supportFragmentManager.beginTransaction()
                .add(
                    R.id.frame_extra_activity,
                    FragmentCreateStory(
                        et_titel_create_object.text.toString(),
                        selectedBitmap,
                        selectedObjectId, null
                    )
                ).addToBackStack(null)
                .commit()
//                createObject(
//                    selectedBitmap,
//                    et_titel_create_object.text.toString(),
//                    selectedObjectId
//                )
        }
    }

    //=========================Click Events=====================//
    fun clickEvents() {
        rl_collection_create_object.setOnClickListener(View.OnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.frame_extra_activity, FragmentUserCollection()).addToBackStack(null)
                .commit()
        })

        tv_story_fragment_top_next.setOnClickListener(View.OnClickListener {
            nextPress()

        })

        tv_next_object_bottom.setOnClickListener(View.OnClickListener {
            nextPress()
        })


        tv_story_fragment_return.setOnClickListener(View.OnClickListener {
            activity!!.finish()
        })


        rl_story_frag_select_image.setOnClickListener {

            result = HelperMethods.checkPermission(activity)

            if (result) {
                CropImage.activity().setAspectRatio(1, 1).start(context!!, this)
            }
        }
        iv_story_frag_image.setOnClickListener(View.OnClickListener {
            rl_story_frag_select_image.performClick()
        })
        et_object_type_create_object.setOnClickListener(View.OnClickListener { popupObjectType() })
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = CropImage.getActivityResult(data).uri
                val bmp = getBitmapFromUri(result)
                selectedBitmap = bmp
                iv_story_frag_image.setImageBitmap(bmp)
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor =
            activity!!.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor =
            parcelFileDescriptor!!
                .getFileDescriptor()
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor!!.close()
        return image
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == HelperMethods.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            CropImage.activity().start(context!!, this)
        }
    }

    //============================Create Object Api======================//
//    private fun createObject(bitmap: Bitmap?, titel: String, typeId: String) {
//        pb_create_object.visibility = View.VISIBLE
//        Utility.disableTouch(true, activity)
//        val userType = "{\"type\":\"USER\"}"
//        Log.d("hwdvhwqvd", userType)
//        val volleyMultipartRequest: VolleyMultipartRequest =
//            object : VolleyMultipartRequest(
//                Request.Method.POST, Api.CREATE_OBJECT,
//                Response.Listener<NetworkResponse> { networkResponse ->
//                    pb_create_object.visibility = View.GONE
//                    Utility.disableTouch(false, activity)
//                    Log.d("jhdjuwqd", networkResponse.statusCode.toString())
//                    var stringResponse = ""
//                    try {
//                        stringResponse = String(
//                            networkResponse.data,
//                            charset(HttpHeaderParser.parseCharset(networkResponse.headers))
//                        )
//                        val obj = JSONObject(stringResponse)
//                        val obje = obj.getJSONObject("object")
//                        val object_id = obje.getString("id")
//                        val ownerObject = obje.getJSONObject("owner")
//                        val ownerId = ownerObject.getString("id")
//                        val ownerType = ownerObject.getString("type")
//                        activity!!.supportFragmentManager.beginTransaction()
//                            .replace(
//                                R.id.frame_extra_activity,
//                                FragmentCreateStory(object_id, bitmap, ownerId, ownerType)
//                            )
//                            .commit()
//                    } catch (e: UnsupportedEncodingException) {
//                        e.printStackTrace()
//                    }
//
//                },
//                Response.ErrorListener { error ->
//                    activity!!.runOnUiThread(Runnable {
//                        Utility.disableTouch(false, activity)
//                        pb_create_object.visibility = View.GONE
//                    })
//
//                    val response = error.networkResponse
//                    if (error is ServerError && response != null) {
//                        try {
//                            val res = String(
//                                response.data,
//                                charset(HttpHeaderParser.parseCharset(response.headers))
//                            )
//                            val obj: JSONObject = JSONObject(res)
//                            Log.d("wejbjewb", "Error : ${res}")
//
//                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT)
//                                .show()
//
//                        } catch (e2: JSONException) {
//                            e2.printStackTrace()
//                            Log.d("wjkdncwj", "Error e2 : ${e2.message}")
//
//                        }
//                    }
//                    Log.d("wiegde", error.toString())
//                }) {
//
//                override fun getHeaders(): MutableMap<String, String> {
//                    val tokenParams: MutableMap<String, String> =
//                        HashMap()
//                    tokenParams["Authorization"] =
//                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
//                    Log.d("ejfrjef", tokenParams.toString())
//                    return tokenParams
//                }
//
//                @Throws(AuthFailureError::class)
//                override fun getParams(): Map<String, String>? {
//                    val params: MutableMap<String, String> =
//                        HashMap()
//                    params["name"] = titel
//                    params["type"] = typeId
//                    params["owner"] = userType
//                    return params
//                }
//
//                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                @Throws(IOException::class)
//                override fun getByteData(): Map<String, DataPart>? {
//                    val params: MutableMap<String, DataPart> =
//                        HashMap()
//                    if (bitmap != null) {
//                        params["cover"] = DataPart(
//                            SystemClock.elapsedRealtime().toString() + ".png",
//                            getFileDataFromBitmap(bitmap)
//                        )
//                    }
//                    return params
//                }
//            }
//        volleyMultipartRequest.setRetryPolicy(
//            DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            )
//        )
//        VolleySingleton.getInstance(context).addToRequestQueue(volleyMultipartRequest)
//    }


//    //================Convert bitmap to byte array=============//
//    private fun getFileDataFromBitmap(bitmap: Bitmap): ByteArray? {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        return byteArrayOutputStream.toByteArray()
//    }

    //=================Filter From List=============//
    fun filter(text: String?): ArrayList<ObjectTypeModel>? {
        Log.d("jkhdgf", text!!)
        val filterdNames: ArrayList<ObjectTypeModel> = ArrayList()
        for (s in objectlist) {
            val name: String = s.name!!
            if (name.contains(text, ignoreCase = true)) {
                filterdNames.add(s)
            }
        }
        return filterdNames
    }

    //=======================Popup For Object Type=================//
    private fun popupObjectType() {
        val adapter: ObjectTypeAdapter
        val builder =
            AlertDialog.Builder(context!!)
        val layoutInflater = layoutInflater
        @SuppressLint("InflateParams") val customView: View =
            layoutInflater.inflate(R.layout.popup_lists, null)
        val rec_branch: RecyclerView = customView.findViewById(R.id.rec_branch)
        val searchView = customView.findViewById<EditText>(R.id.searchView)
        rec_branch.layoutManager = LinearLayoutManager(context!!)
        adapter = ObjectTypeAdapter(context!!, objectlist, this)
        rec_branch.adapter = adapter
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (searchView.getText().toString().length >= 1) {
                    rec_branch.adapter = ObjectTypeAdapter(
                        context!!,
                        filter(searchView.text.toString())!!,
                        this@FragmentCreateObject
                    )

                } else {
                    rec_branch.adapter = adapter
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        builder.setView(customView)
        dialog_list = builder.create()
        dialog_list.show()
    }

    //===================Api For Objects=======================//
    fun getObjectType() {
        val request =
            StringRequest(Request.Method.GET, Api.GET_OBJECT_TYPE,
                Response.Listener { response ->
                    Log.d("response", response)
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length() - 1) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        objectlist.add(
                            ObjectTypeModel(
                                jsonObject.getString("id"),
                                jsonObject.getString("name")
                            )
                        )
                    }
                    Log.d("idhdedw", objectlist.size.toString())
                },
                Response.ErrorListener {

                })
        VolleySingleton.getInstance(context!!).addToRequestQueue(request)
    }


    override fun onItemClick(objectTypeModel: ObjectTypeModel) {
        dialog_list.cancel()
        selectedObjectId = objectTypeModel.id
        et_object_type_create_object.setText(objectTypeModel.name)
    }
}
