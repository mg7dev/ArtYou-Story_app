package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.dw.artyou.R
import com.dw.artyou.helper.*
import com.dw.artyou.models.FileUploadModel
import com.dw.artyou.recyclerAdapters.ObjectCollectionAdapter
import com.dw.artyou.models.ObjectCollectModel
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_story.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentCreateStory(
    var titel: String?,
    var bitmap: Bitmap?,
    var selectedObjectId: String?,
    var objectCollectModel: MutableList<ObjectCollectModel>?
) :
    Fragment() {

    var isObjectComplete = false
    var isStoryNextPress = false
    var objectId: String = ""
    var ownerType: String = ""
    var ownerId: String = ""
    val IMAGE_CODE = 546
    val AUDIO_CODE = 678
    var file: File? = null
    lateinit var objectCollectionAdapter: ObjectCollectionAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_story, container, false)
    }


    @SuppressLint("LongLogTag")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ownerType   = this!!.objectCollectModel!![0].owner.type
        ownerId     = this!!.objectCollectModel!![0].owner.id
        objectCollectionAdapter = ObjectCollectionAdapter(context!!,
            (objectCollectModel as ArrayList<ObjectCollectModel>?)!!,null)
        rv_object_list.adapter = objectCollectionAdapter

//        val ll_bottom_sheet: LinearLayout = view.findViewById(R.id.ll_bottom_sheet)
//        val bottomSheetBehavior = BottomSheetBehavior.from(ll_bottom_sheet)
        // bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

//        bottomSheetBehavior.setBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                // React to state change
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> {
//                        btn_share_create_story.visibility = View.VISIBLE
//                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        btn_share_create_story.visibility = View.GONE
//                    }
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                    }
//                    BottomSheetBehavior.STATE_DRAGGING -> {
//                    }
//                    BottomSheetBehavior.STATE_SETTLING -> {
//                    }
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // React to dragging events
//            }
//        })
        if (titel != null) {
            createObject(bitmap, titel!!, selectedObjectId!!)
        } else {

        }



        ll_add_story.setOnClickListener(View.OnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.frame_extra_activity, BottomSheetFragment(this))
                .addToBackStack(null).commit()
            //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        })

        clickEvents()
//        tv_text_bottom_story.setOnClickListener(View.OnClickListener {
//            showDialog()
//            popupViewOnClick(text)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        })
//        tv_image_bottom_story.setOnClickListener(View.OnClickListener {
//            showDialog()
//            popupViewOnClick(image)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        })
//        tv_video_bottom_story.setOnClickListener(View.OnClickListener {
//            showDialog()
//            popupViewOnClick(video)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        })
//        tv_audio_bottom_story.setOnClickListener(View.OnClickListener {
//            showDialog()
//            popupViewOnClick(audio)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        })
    }

    //===============Click Events============//
    @SuppressLint("LongLogTag")
    private fun clickEvents() {
        et_story_frag_text.setOnTouchListener(View.OnTouchListener { v: View?, event: MotionEvent? ->
            nv_create_story.requestDisallowInterceptTouchEvent(true)
            false
        })
        tv_next_create_story.setOnClickListener(View.OnClickListener {

            Log.d("whatisobjectcollection------------", "this is---"+ownerType)
            Log.d("whatisobjectcollection-----------------", "this is---"+ ownerId)

            if (et_story_frag_text.text.toString().isNotEmpty()) {
                if (!isObjectComplete) {
                    objectCollectModel?.let { it1 ->
                        createStory(
                            it1,
                            ownerType,
                            ownerId
                        )
                    }
                } else {
                    isStoryNextPress = true
                }
            } else {
                Toast.makeText(context, "Please fill the details", Toast.LENGTH_SHORT).show()
            }

        })
        btn_share_create_story.setOnClickListener(View.OnClickListener { tv_next_create_story.performClick() })
        tv_return_create_story.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })
        et_story_frag_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun afterTextChanged(editable: Editable) {
                tv_size_create_story.text = (256 - et_story_frag_text.text.length).toString()

            }
        })
    }

    //====================Create Object Api===============//
    private fun createObject(bitmap: Bitmap?, titel: String, typeId: String) {
        val userType = "{\"type\":\"USER\"}"
        Log.d("hwdvhwqvd", userType)
        val volleyMultipartRequest: VolleyMultipartRequest =
            object : VolleyMultipartRequest(
                Request.Method.POST, Api.CREATE_OBJECT,
                Response.Listener<NetworkResponse> { networkResponse ->
                    HelperMethods.disableTouch(false, activity)
                    Log.d("jhdjuwqd", networkResponse.statusCode.toString())
                    var stringResponse = ""
                    try {
                        stringResponse = String(
                            networkResponse.data,
                            charset(HttpHeaderParser.parseCharset(networkResponse.headers))
                        )
                        val obj = JSONObject(stringResponse)
                        val obje = obj.getJSONObject("object")
                        objectId = obje.getString("id")
                        val ownerObject = obje.getJSONObject("owner")
                        ownerId = ownerObject.getString("id")
                        ownerType = ownerObject.getString("type")
                        isObjectComplete = true
                        if (et_story_frag_text.text.toString().isNotEmpty()) {
                            if (isStoryNextPress) {
                                objectCollectModel?.let { createStory(it, ownerType, ownerId) }
                            }
                        } else {
                            Toast.makeText(context, "Please fill the details", Toast.LENGTH_SHORT)
                                .show()
                        }
                        //addExperience(objectId)
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                    })

                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj: JSONObject = JSONObject(res)
                            Log.d("wejbjewb", "Error : ${res}")

                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT)
                                .show()

                        } catch (e2: JSONException) {
                            e2.printStackTrace()
                            Log.d("wjkdncwj", "Error e2 : ${e2.message}")

                        }
                    }
                    Log.d("wiegde", error.toString())
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val tokenParams: MutableMap<String, String> =
                        HashMap()
                    tokenParams["Authorization"] =
                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                    Log.d("ejfrjef", tokenParams.toString())
                    return tokenParams
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["name"] = titel
                    params["type"] = typeId
                    params["owner"] = userType
                    return params
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Throws(IOException::class)
                override fun getByteData(): Map<String, DataPart>? {
                    val params: MutableMap<String, DataPart> =
                        HashMap()
                    if (bitmap != null) {
                        params["cover"] = DataPart(
                            SystemClock.elapsedRealtime().toString() + ".png",
                            getFileDataFromBitmap(bitmap)
                        )
                    }
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

    //================Convert bitmap to byte array=============//
    private fun getFileDataFromBitmap(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    //======================Api For Create Story================//
    fun createStory(objectCollectModel:MutableList<ObjectCollectModel>, ownerType: String, ownerId: String) {
        var objstr = ""
        var index = 0
        for (item in objectCollectModel){
            objstr += "    {\n" +
            "      \"object_id\": \"" + item.id + "\",\n" +
            "      \"text\": \"text\"\n" +
            "    }"
            if(index<objectCollectModel.size-1){
                objstr+=",\n"
                index++
            }
        }
        pb_create_story.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: String = ("{\n" +
                "  \"objects\": [\n" +
                objstr +
                "  ],\n" +
                "  \"title\": \"" + titel + "\",\n" +
                "  \"location\": {\n" +
                "    \"address\": \"string\",\n" +
                "    \"country\": {\n" +
                "      \"short_name\": \"India\",\n" +
                "      \"long_name\": \"In\"\n" +
                "    },\n" +
                "    \"geometry\": [\n" +
                "      28.5729847,\n" +
                "      77.32490430000001\n" +
                "    ]\n" +
                "  },\n" +
                "  \"description\": \"" + et_story_frag_text.text.toString() + "\",\n" +
                "  \"date\": \"2020-02-11\",\n" +
                "  \"hidden\": false,\n" +
                "  \"owner\": {\n" +
                "    \"type\": \"" + ownerType + "\",\n" +
                "    \"id\": \"" + ownerId + "\"\n" +
                "  }\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.CREATE_STORY,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_create_story.visibility = View.GONE
                    })
                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj: JSONObject = JSONObject(res)
                            Log.d("wejbjewb", "Error : ${res}")

                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT)
                                .show()

                        } catch (e2: JSONException) {
                            e2.printStackTrace()
                            Log.d("wjkdncwj", "Error e2 : ${e2.message}")
                        }
                    }
                }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val tokenParams: MutableMap<String, String> =
                        HashMap()
                    tokenParams["Authorization"] =
                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                    Log.d("ejfrjef", tokenParams.toString())
                    return tokenParams
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

                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_create_story.visibility = View.GONE
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("responseStringh", responseString)
                    Log.d("responseStringh", response.toString())

                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )

                    val jsonObject = JSONObject(res)
                    val storyObject = jsonObject.getJSONObject("storye")
                    val storyId = storyObject.getString("id")
                    Log.d("jhjegfwe", jsonObject.toString())
                    activity!!.supportFragmentManager.beginTransaction().replace(
                        R.id.frame_extra_activity,
                        FragmentThankYou(bitmap, storyId)
                    ).commit()
//                    startActivity(Intent(context!!, ThankYouActivity::class.java))
//                    activity!!.finish()
                    return Response.success(
                        responseString,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            }
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    //===================Upload Files=====//
    private fun uploadFile(
        ownerId: String,
        fileType: String,
        file: File
    ) {
        val owner = ("{\n" +
                "    \"type\": \"" + "USER" + "\",\n" +
                "    \"id\": \"" + ownerId + "\"\n" +
                "  }")

        Log.d("edghf", owner)
        pb_create_story.visibility = View.VISIBLE
        val volleyMultipartRequest: VolleyMultipartRequest =
            object : VolleyMultipartRequest(
                Request.Method.POST, Api.UPLOAD_FILE,
                Response.Listener<NetworkResponse> { networkResponse ->
                    activity!!.runOnUiThread(Runnable {
                        pb_create_story.visibility = View.GONE
                    })

                    Log.d("jhdjuwqd", networkResponse.statusCode.toString())
                    var stringResponse = ""
                    try {
                        stringResponse = String(
                            networkResponse.data,
                            charset(HttpHeaderParser.parseCharset(networkResponse.headers))
                        )
                        val gson = Gson()
                        val fileModel = gson.fromJson<FileUploadModel>(
                            stringResponse.toString(),
                            FileUploadModel::class.java
                        )
                        Log.d("wdjhwehdg", fileModel.media.id)
                        addExperience(objectId, fileType, fileModel.media.filename)

                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        pb_create_story.visibility = View.GONE
                    })

                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj: JSONObject = JSONObject(res)
                            Log.d("wejbjewb", "Error : ${res}")

                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT)
                                .show()

                        } catch (e2: JSONException) {
                            e2.printStackTrace()
                            Log.d("wjkdncwj", "Error e2 : ${e2.message}")

                        }
                    }
                    Log.d("wiegde", error.toString())
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val tokenParams: MutableMap<String, String> =
                        HashMap()
                    tokenParams["Authorization"] =
                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                    Log.d("ejfrjef", tokenParams.toString())
                    return tokenParams
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["caption"] = fileType
                    params["owner"] = owner
                    return params
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Throws(IOException::class)
                override fun getByteData(): Map<String, DataPart>? {
                    val params: MutableMap<String, DataPart> =
                        HashMap()

                    params["file"] = DataPart(
                        file.getName(), convertFileToByte(file)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("edghf", "IF CON")
        if (resultCode == Activity.RESULT_OK) { //the selected audio.
            val uri = data!!.data
            Log.d("tyft", FileUtils.getPath(context, uri))
            Log.d(
                "jhwdgcc",
                File(FileUtils.getPath(context, uri)).length().toString() + ""
            )
            file = File(FileUtils.getPath(context, uri))
            tv_name_file_popup.text = file?.name
        }
    }


    //===========Convert File to byte array=========//
    fun convertFileToByte(file: File): ByteArray {
        val bytesArray = ByteArray(file.length().toInt())

        val fis = FileInputStream(file)
        fis.read(bytesArray) //read file into bytes[]

        fis.close()

        return bytesArray
    }

    //==============Api Add Experience============//
    fun addExperience(object_id: String, type: String, content: String) {

        val c = Calendar.getInstance().getTime()
        System.out.println("Current time => " + c);

        val df = SimpleDateFormat("dd-MMM-yyyy");
        var formattedDate = df.format(c);
        pb_create_story.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: String = ("{\n" +
                "  \"type\": \"" + type + "\",\n" +
                "  \"visibility\": \"PUBLIC\",\n" +
                "  \"date\": \"" + formattedDate + "\",\n" +
                "  \"content\": \"" + content + "\",\n" +
                "  \"owner\": {\n" +
                "    \"type\": \"USER\",\n" +
                "    \"id\": \"" + SessionManager.getInstance(context!!)!!.userModel!!.id + "\"\n" +
                "  }\n" +
                "}")
        Log.d("efdcdc", str)

        try {
            val requestBody = str
            Log.d("wfewecsd", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.addExperience(object_id),
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_create_story.visibility = View.GONE
                    })
                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj = JSONObject(res)
                            Log.d("23khfgeh", obj.toString())
                            Log.d("wejbjewb", "Error : ${res}")

                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT)
                                .show()

                        } catch (e2: JSONException) {
                            e2.printStackTrace()
                            Log.d("wjkdncwj", "Error e2 : ${e2.message}")
                        }
                    }
                }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val tokenParams: MutableMap<String, String> =
                        HashMap()
                    tokenParams["Authorization"] =
                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                    Log.d("ejfrjef", tokenParams.toString())
                    return tokenParams
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

                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_create_story.visibility = View.GONE
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("regrevfeve", responseString)
                    Log.d("rgf54fefd", response.toString())

                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )

                    val jsonObject = JSONObject(res)
                    Log.d("jhjegfwe", jsonObject.toString())
                    return Response.success(
                        responseString,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            }
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private lateinit var tv_name_file_popup: TextView
    private lateinit var et_text_file_popup: EditText
    private lateinit var et_video_file_popup: EditText
    private lateinit var rl_image_file_popup: RelativeLayout
    private lateinit var rl_audio_file_popup: RelativeLayout
    private lateinit var btn_add_file_popup: Button
    private lateinit var btn_cancel_file_popup: Button
    private lateinit var ll_size_exp_popup: LinearLayout
    private lateinit var tv_size_exp_popup: TextView
    private var image = "IMAGE"
    private var text = "TEXT"
    private var video = "VIDEO"
    private var audio = "AUDIO"
    private var type = "";


    fun showDialog() {
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.popup_file_upload, null)
        et_text_file_popup = view.findViewById(R.id.et_text_file_popup)
        et_video_file_popup = view.findViewById(R.id.et_video_file_popup)
        tv_name_file_popup = view.findViewById(R.id.tv_name_file_popup)
        rl_image_file_popup = view.findViewById(R.id.rl_image_file_popup)
        rl_audio_file_popup = view.findViewById(R.id.rl_audio_file_popup)
        btn_add_file_popup = view.findViewById(R.id.btn_add_file_popup)
        btn_cancel_file_popup = view.findViewById(R.id.btn_cancel_file_popup)
        tv_size_exp_popup = view.findViewById(R.id.tv_size_exp_popup)
        ll_size_exp_popup = view.findViewById(R.id.ll_size_exp_popup)

        rl_image_file_popup.setOnClickListener(View.OnClickListener {
            val intent_upload = Intent()
            intent_upload.type = "image/*"
            intent_upload.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent_upload, IMAGE_CODE)
        })
        rl_audio_file_popup.setOnClickListener(View.OnClickListener {
            val intent_upload = Intent()
            intent_upload.type = "audio/*"
            intent_upload.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent_upload, AUDIO_CODE)
        })

        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        et_text_file_popup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun afterTextChanged(editable: Editable) {
                tv_size_exp_popup.text = (2000 - et_text_file_popup.text.length).toString()

            }
        })

        btn_cancel_file_popup.setOnClickListener(View.OnClickListener { dialog.cancel() })
        btn_add_file_popup.setOnClickListener(View.OnClickListener {
            if (isObjectComplete) {
                if (type.equals(video)) {
                    dialog.cancel()
                    addExperience(objectId, type, et_video_file_popup.text.toString())


                } else if (type.equals(text)) {
                    if (et_text_file_popup.text.toString().isNotEmpty()) {
                        dialog.cancel()
                        addExperience(objectId, type, et_text_file_popup.text.toString())
                    } else {
                        Toast.makeText(
                            context,
                            "Text should not be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (type.equals(image)) {
                    if (file != null) {
                        uploadFile(
                            SessionManager.getInstance(context!!)!!.userModel!!.id,
                            "IMAGE",
                            file!!
                        )
                        dialog.cancel()
                    } else {
                        Toast.makeText(context, "PLease select image file", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else if (type.equals(audio)) {
                    if (file != null) {
                        uploadFile(
                            SessionManager.getInstance(context!!)!!.userModel!!.id,
                            "AUDIO",
                            file!!
                        )
                        dialog.cancel()
                    } else {
                        Toast.makeText(context, "PLease select audio file", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    fun popupViewOnClick(type: String) {
        ll_size_exp_popup.visibility = View.GONE
        this.type = type
        if (type.equals(image)) {
            changeViews(rl_image_file_popup)
        } else if (type.equals(video)) {
            changeViews(et_video_file_popup)
        } else if (type.equals(audio)) {
            changeViews(rl_audio_file_popup)
        } else if (type.equals(text)) {
            ll_size_exp_popup.visibility = View.VISIBLE
            changeViews(et_text_file_popup)
        }
    }

    fun changeViews(view: View) {
        et_text_file_popup.visibility = View.GONE
        et_video_file_popup.visibility = View.GONE
        rl_audio_file_popup.visibility = View.GONE
        rl_image_file_popup.visibility = View.GONE

        view.visibility = View.VISIBLE
    }


}
