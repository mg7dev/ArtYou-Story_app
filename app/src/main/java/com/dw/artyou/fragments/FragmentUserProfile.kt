package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.Telephone
import com.dw.artyou.models.UserInfoModel
import com.dw.artyou.recyclerAdapters.MobileRecyclerAdapter
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.google.gson.Gson
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException

/**
 * A simple [Fragment] subclass.
 */
class FragmentUserProfile : Fragment() {
    private var result: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userModel = SessionManager.getInstance(context!!)!!.userModel
        Log.d("wdjqkhwdjwq", userModel!!.telephone.size.toString())
        Glide.with(context!!)
            .load(Api.getImage(SessionManager.getInstance(context!!)!!.userModel!!.avatar))
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_account_circle_blue).into(iv_user_profile)

        tv_return_back_user_profile.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })

        et_name_user_profile.setText(userModel!!.name)
        et_email_user_profile.setText(userModel.email)
        tv_name_profile.text = userModel.name
        rv_user_mobile.adapter =
            MobileRecyclerAdapter(context!!, userModel.telephone)

        tv_profile_change_photo.setOnClickListener(View.OnClickListener {
            result = HelperMethods.checkPermission(activity)

            if (result) {
                CropImage.activity().setAspectRatio(1, 1).start(context!!, this)
            }
        })

        iv_user_add_mobile.setOnClickListener(View.OnClickListener { showPopup() })

        ll_change_password_profile.setOnClickListener(View.OnClickListener { showPopupPassword() })
        ll_change_email_profile.setOnClickListener(View.OnClickListener { changeEmail() })
        ll_change_name_profile.setOnClickListener(View.OnClickListener { changeName() })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        val result = CropImage.getActivityResult(data).uri
                        val file = File(result.path)
                        if (file.length() <= 1048576) {
                            Glide.with(this)
                                .asBitmap()
                                .load(result)
                                .placeholder(R.drawable.demo_img)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        HelperMethods.uploadImage(
                                            resource,
                                            context!!,
                                            iv_user_profile,
                                            pb_user_profile, false
                                        )
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })
                        } else {
                            Toast.makeText(
                                context,
                                "Image size not more than 1 MB",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.d("djkwehjwe", e.toString())
                    }

                }
            }
        }
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


    //==================Change Name  Popup=============//
    fun changeName() {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_change_name, null)

        val et_fn_change_name: EditText = customView.findViewById(R.id.et_fn_change_name)
        val et_ln_change_name: EditText = customView.findViewById(R.id.et_ln_change_name)
        val btn_confirm_change_name: Button = customView.findViewById(R.id.btn_confirm_change_name)
        val btn_cancel_change_name: Button = customView.findViewById(R.id.btn_cancel_change_name)


        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        btn_confirm_change_name.setOnClickListener(View.OnClickListener {
            if (et_fn_change_name.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter first name", Toast.LENGTH_SHORT).show()
            } else if (et_ln_change_name.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter last name", Toast.LENGTH_SHORT).show()
            } else {
                dialog.cancel()
                val str: String = ("{\n" +
                        "  \"first_name\": \"" + et_fn_change_name.text.toString() + "\",\n" +
                        "  \"last_name\": \"" + et_ln_change_name.text.toString() + "\"\n" +
                        "}")
                changeUserdetails(str)
            }

        })
        btn_cancel_change_name.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }

    //==================Change Email  Popup=============//
    fun changeEmail() {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_forgot_password, null)

        val et_email_fp: EditText =
            customView.findViewById(R.id.et_email_fp)
        val btn_send_fp: Button = customView.findViewById(R.id.btn_send_fp)
        val btn_cancel_fp: Button = customView.findViewById(R.id.btn_cancel_fp)


        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        btn_send_fp.setOnClickListener(View.OnClickListener {
            if (et_email_fp.text.toString().isEmpty()) {
                Toast.makeText(context, R.string.please_enter_email, Toast.LENGTH_SHORT).show()
            } else if (HelperMethods.isValidEmail(et_email_fp.text.toString())) {
                Toast.makeText(context, R.string.enter_valid_email, Toast.LENGTH_SHORT).show()
            } else {
                dialog.cancel()
                val str: String = ("{\n" +
                        "  \"email\": \"" + et_email_fp.text.toString() + "\"\n" +
                        " \n" +
                        "}")
                changeUserdetails(str)
            }

        })
        btn_cancel_fp.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }

    //==============Popup add mobile============//
    fun showPopup() {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.mobile_popup, null)
        val ccp_mobile_popup: CountryCodePicker = customView.findViewById(R.id.ccp_mobile_popup)
        val btn_add_mobile: Button = customView.findViewById(R.id.btn_add_mobile)
        val et_number_popup: EditText = customView.findViewById(R.id.et_number_popup)
        val btn_cancel_mobile: Button = customView.findViewById(R.id.btn_cancel_mobile)


        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        btn_add_mobile.setOnClickListener(View.OnClickListener {
            if (et_number_popup.text.toString().isEmpty()) {
                Toast.makeText(
                    context,
                    getText(R.string.please_enter_mobile_number),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                dialog.cancel()
                addNumber(
                    ccp_mobile_popup.selectedCountryCode,
                    et_number_popup.text.toString()
                )
            }
        })

        btn_cancel_mobile.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }

    //==================Popup Change password=============//
    fun showPopupPassword() {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_change_password, null)

        val et_email_change_password: EditText =
            customView.findViewById(R.id.et_email_change_password);
        val et_password_change_password: EditText =
            customView.findViewById(R.id.et_password_change_password);
        val et_confirm_password_change_password: EditText =
            customView.findViewById(R.id.et_confirm_password_change_password);
        val btn_submit_change_password: Button =
            customView.findViewById(R.id.btn_submit_change_password);
        val btn_cancel_change_password: Button =
            customView.findViewById(R.id.btn_cancel_change_password);

        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()

        btn_cancel_change_password.setOnClickListener(View.OnClickListener { dialog.cancel() })
        btn_submit_change_password.setOnClickListener(View.OnClickListener {
            if (et_password_change_password.text.toString().isEmpty()) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (et_confirm_password_change_password.text.toString().isEmpty()) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_confirm_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!et_confirm_password_change_password.text.toString().equals(
                    et_password_change_password.text.toString()
                )
            ) {
                Toast.makeText(
                    activity,
                    getString(R.string.passwords_should_be_same),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (et_email_change_password.text.toString().isEmpty()) {
                Toast.makeText(activity, getString(R.string.please_enter_email), Toast.LENGTH_SHORT)
                    .show()
            } else if (HelperMethods.isValidEmail(et_email_change_password.text.toString())) {
                Toast.makeText(activity, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT)
                    .show()
            } else {
                dialog.cancel()
                changePassword(
                    et_email_change_password.text.toString(),
                    et_password_change_password.text.toString(),
                    et_confirm_password_change_password.text.toString()
                )
            }
        })
    }

    //==================Show Popup Verification================//
    fun showPopupVerification(id: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_verification_code, null)
        val otp_text1: EditText = customView.findViewById(R.id.otp_text1)
        val otp_text2: EditText = customView.findViewById(R.id.otp_text2)
        val otp_text3: EditText = customView.findViewById(R.id.otp_text3)
        val otp_text4: EditText = customView.findViewById(R.id.otp_text4)
        val tv_resend_otp: TextView = customView.findViewById(R.id.tv_resend_otp)
        val pb_verification: ProgressBar = customView.findViewById(R.id.pb_verification)
        val btn_confirm_verification: Button =
            customView.findViewById(R.id.btn_confirm_verification)
        val btn_cancel_verification: Button = customView.findViewById(R.id.btn_cancel_verification)
        otp_text1.addTextChangedListener(object : TextWatcher {
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
                if (otp_text1.getText().toString().length >= 1) {
                    otp_text2.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        otp_text2.addTextChangedListener(object : TextWatcher {
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
                if (otp_text2.getText().toString().length > 0) {
                    otp_text3.requestFocus()
                } else if (otp_text2.getText().toString().length <= 0) {
                    otp_text1.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        otp_text3.addTextChangedListener(object : TextWatcher {
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
                if (otp_text3.getText().toString().length > 0) {
                    otp_text4.requestFocus()
                } else if (otp_text3.getText().toString().length <= 0) {
                    otp_text2.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        otp_text4.addTextChangedListener(object : TextWatcher {
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
                if (otp_text4.getText().toString().length <= 0) {
                    otp_text3.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        tv_resend_otp.setOnClickListener(View.OnClickListener {
            numberCodeRequest(id)
            dialog.cancel()
        })
        btn_cancel_verification.setOnClickListener(View.OnClickListener {
            dialog.cancel()
        })
        btn_confirm_verification.setOnClickListener(View.OnClickListener {
            if (otp_text1.text.toString().isEmpty() || otp_text2.text.toString().isEmpty() || otp_text3.text.toString().isEmpty() || otp_text4.text.toString().isEmpty()) {
                Toast.makeText(context, "All fields are compulsory", Toast.LENGTH_SHORT).show()
            } else {
                val code =
                    otp_text1.text.toString() + otp_text2.text.toString() + otp_text3.text.toString() + otp_text4.text.toString()
                numberVerificationRequest(id, code, dialog, pb_verification)
            }
        })
    }

    //===============Add Number Api===================//
    fun addNumber(code: String, number: String) {
        pb_user_profile.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: String = ("{\n" +
                "  \"country_code\": \"" + "+" + code + "\",\n" +
                "  \"number\": \"" + number + "\"\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.ADD_NUMBER,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_user_profile.visibility = View.GONE
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
                        pb_user_profile.visibility = View.GONE
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
                    val userModel = SessionManager.getInstance(context!!)!!.userModel
                    Log.d("wdjqkhwdjwq", userModel!!.telephone.size.toString() + " Before")
                    userModel.telephone
                        .add(
                            Telephone(
                                jsonObject.getString("id"),
                                jsonObject.getBoolean("active"),
                                jsonObject.getString("country_code"),
                                jsonObject.getString("number")
                            )
                        )
                    numberCodeRequest(jsonObject.getString("id"))
                    Log.d("wdjqkhwdjwq", userModel.telephone.size.toString())
                    SessionManager.getInstance(context!!)!!.userModel = userModel

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

    //=====================Change password=============//
    fun changePassword(email: String, password: String, confirmPassword: String) {
        pb_user_profile.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: String = ("{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"confirm\": \"" + confirmPassword + "\",\n" +
                "  \"token\": \"regre\"\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.CHANGE_PASSWORD,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_user_profile.visibility = View.GONE
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
                        pb_user_profile.visibility = View.GONE
                        Toast.makeText(context, "Password Change successfully", Toast.LENGTH_SHORT)
                            .show()
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("responseStringh", responseString)
                    Log.d("responseStringh", response.toString())

                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )


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

    //==================Number Code Request==============//
    fun numberCodeRequest(id: String) {
        activity!!.runOnUiThread(Runnable {
            pb_user_profile.visibility = View.VISIBLE
            HelperMethods.disableTouch(true, activity)
        })

        val str: String = ("{\n" +
                "  \"id\": \"" + id + "\"\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.REQUEST_CODE,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_user_profile.visibility = View.GONE
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
                        pb_user_profile.visibility = View.GONE
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("ewfwdcdvv", responseString)
                    Log.d("wervvxcsw", response.toString())
                    showPopupVerification(id)
                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )
                    return Response.success(
                        responseString,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            }
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("wqdd", e.toString())

        }
    }

    //======================Number Verification Request==============//
    fun numberVerificationRequest(id: String, code: String, dialog: AlertDialog, pb: ProgressBar) {
        pb.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: String = ("{\n" +
                "  \"code\": \"" + code + "\",\n" +
                "  \"id\": \"" + id + "\"\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.VERIFY_CODE,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb.visibility = View.GONE
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
                        pb.visibility = View.GONE
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("dfdscd", responseString)
                    Log.d("cdsxaz", response.toString())
                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )

                    val jsonObject = JSONObject(res)
                    val ok = jsonObject.getBoolean("ok")
                    if (ok) {
                        dialog.cancel()
                    } else {
                        (context as AppCompatActivity).runOnUiThread(Runnable {
                            Toast.makeText(context, jsonObject.getString("why"), Toast.LENGTH_SHORT)
                                .show()
                        })
                    }
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

    //==================change user details=============//
    fun changeUserdetails(str: String) {
        activity!!.runOnUiThread(Runnable {
            pb_user_profile.visibility = View.VISIBLE
            HelperMethods.disableTouch(true, activity)
        })

        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.PATCH,
                Api.CHANGE_DETAILS,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_user_profile.visibility = View.GONE
                    })
                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj = JSONObject(res)
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

                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("ewfwdcdvv", responseString)
                    Log.d("wervvxcsw", response.toString())
                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )
                    val jsonObject = JSONObject(res)
                    Log.d("jhvhdcdwe", jsonObject.toString())
                    val userModel = SessionManager.getInstance(context!!)!!.userModel
                    userModel!!.email = jsonObject.getString("email")
                    userModel.name = jsonObject.getString("name")
                    SessionManager.getInstance(context!!)!!.userModel = userModel
                    activity!!.runOnUiThread(Runnable {
                        tv_name_profile.text = jsonObject.getString("name")
                        et_name_user_profile.text = jsonObject.getString("name")
                        et_email_user_profile.text = jsonObject.getString("email")
                        HelperMethods.disableTouch(false, activity)
                        pb_user_profile.visibility = View.GONE
                    })
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
}
