package com.dw.artyou.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject

import androidx.fragment.app.Fragment
import com.android.volley.*
import com.designoweb.marketplace.subcontractor.activity.api.Retro
import com.dw.artyou.R
import com.dw.artyou.activities.MainActivity
import com.dw.artyou.activities.SignUp
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.UserInfoModel
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import retrofit2.Call
import retrofit2.Callback
import java.io.UnsupportedEncodingException

import java.util.*


class FragmentLogin : Fragment() {

    private val EMAIL: String = "email"
    private var callbackManager: CallbackManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        LoginManager.getInstance().logOut()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val token = AccessToken.getCurrentAccessToken()

                    if (token != null) {
                        socialLogin(
                            token.toString(),
                            token.dataAccessExpirationTime.toString(),
                            "facebook"
                        )
                    }

                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        try {
                            Log.d("wjhfbwebfw", response.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() { // App code
                    Log.d("wjhfbwebfw", "Cancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.d("wjhfbwebfw", exception.toString())// App code
                }
            })

        clickEvents()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .enableAutoManage(activity!!, GoogleApiClient.OnConnectionFailedListener { })
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        google_signin_button.setSize(SignInButton.SIZE_STANDARD)
        google_signin_button.setScopes(gso.getScopeArray())
        google_signin_button.performClick()
    }


    //========================Click Events====================//
    fun clickEvents() {
        tv_fp_login.setOnClickListener(View.OnClickListener { showPopup(false) })
        btn_sign_in.setOnClickListener(View.OnClickListener {

            if (et_login_username?.text?.toString()?.isNullOrEmpty()!!) {
                Toast.makeText(context, "Please enter user name", Toast.LENGTH_SHORT)
                    .show()

            } else if (et_login_password?.text?.toString()?.isNullOrEmpty()!!) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                loginUser(context)
            }
        })

        btn_join.setOnClickListener(View.OnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.frame_signup, FragmentSignUp()).addToBackStack(null).commit()
        })
        iv_login_facebook.setOnClickListener({
            LoginManager.getInstance().logInWithReadPermissions(
                this@FragmentLogin,
                Arrays.asList("email", "public_profile", "user_friends")
            )
        })
        iv_google_login.setOnClickListener({
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })


    }


    //================================Login User======================//
    fun loginUser(context: Context?) {
        login_progressbar.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)

        val str = ("{\n" +
                "  \"username\": \"" + et_login_username.text.toString().replace(
            " ",
            ""
        ) + "\",\n" +
                "  \"password\": \"" + et_login_password.text.toString() + "\"\n" +
                "}")

        try {
            val requestQueue = Volley.newRequestQueue(context)

            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.LOGIN,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    login_progressbar.visibility = View.GONE
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(
                            false,
                            activity
                        )
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

                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(
                            false,
                            activity
                        )
                    })
                    responseString = response.statusCode.toString()
                    Log.d("responseStringh", responseString)
                    Log.d("responseStringh", response.toString())

                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )
                    val obj = JSONObject(res)
                    val user = obj.getJSONObject("user")
                    val name = user.getString("name")
                    val segment = user.getString("segment")
                    val token = obj.getString("token")

                    SessionManager.getInstance(context!!)!!.userName = name
                    SessionManager.getInstance(context)!!.authToken = token
                    SessionManager.getInstance(context)!!.userSegment = segment

                    Log.d("jwfehwjd", name + "...." + segment + "..." + token)

                    Retro.Api(context!!).getUserInfo().enqueue(object : Callback<UserInfoModel> {
                        override fun onResponse(
                            call: Call<UserInfoModel>,
                            response: retrofit2.Response<UserInfoModel>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("jhgve", response.body().toString())
                                SessionManager.getInstance(context)!!.userModel = response.body()
                                startActivity(Intent(context, MainActivity::class.java))
                            }
                        }

                        override fun onFailure(call: Call<UserInfoModel>, t: Throwable) {
                        }
                    })

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


    //=====================Result Google/facebook Login result===================//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    //=======================Handle Google Signin Result================//
    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("jhcdhbdh", "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            Log.e("jhcdhbdh", "display name: " + acct!!.displayName!!)
            var name = acct.displayName!!
            var email = acct.email!!

            Log.e("jhcdhbdh", "Name: $name, email: $email")
            // check_con = "gm"
            val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
            val account = result.signInAccount
            val asyncTask: AsyncTask<*, *, *> =
                object : AsyncTask<Any?, Any?, Any?>() {
                    override fun doInBackground(objects: Array<Any?>): Any? {
                        val accessToken: String = GoogleAuthUtil.getToken(
                            context!!.applicationContext,
                            account!!.getAccount(),
                            scope,
                            Bundle()
                        )
                        Log.d(
                            "ivbfdbv",
                            "accessToken:$accessToken"
                        ) //accessToken
                        socialLogin(accessToken, "", "google")
                        this.cancel(true)
                        return null
                    }
                }
            asyncTask.execute()
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    //===================Social Login=================//
    fun socialLogin(token: String, expDate: String, type: String) {

        val request: StringRequest = object :
            StringRequest(
                Method.POST,
                Api.SOCIAL_LOGIN,
                com.android.volley.Response.Listener { response ->
                    Log.d("vfhfbd", response)
                    var jsonResponse: JSONObject? = null
                    try {


                    } catch (e: JSONException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener {
                    val response = it.networkResponse
                    if (it is ServerError && response != null) {
                        try {
                            val res = String(
                                response.data,
                                charset(HttpHeaderParser.parseCharset(response.headers))
                            )
                            val obj: JSONObject = JSONObject(res)
                            Toast.makeText(
                                context,
                                obj.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (e2: JSONException) {
                            e2.printStackTrace()
                        }
                    }

                }) {
            override fun getParams(): Map<String, String> {
                val sendData: MutableMap<String, String> = HashMap()
                sendData["token"] = token
                sendData["media"] = type
                sendData["expiration_date"] = expDate
                return sendData
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    //===============Popup Forgot Password===========//
    fun showPopup(isEmailSend: Boolean) {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_forgot_password, null)
        val et_email_fp: EditText =
            customView.findViewById(R.id.et_email_fp)
        val btn_send_fp: Button = customView.findViewById(R.id.btn_send_fp)
        val btn_cancel_fp: Button = customView.findViewById(R.id.btn_cancel_fp)
        val btn_cancel_fp_mail: Button = customView.findViewById(R.id.btn_cancel_fp_mail)
        val ll_fp_mail_popup: LinearLayout = customView.findViewById(R.id.ll_fp_mail_popup)
        val ll_fp_popup: LinearLayout = customView.findViewById(R.id.ll_fp_popup)

        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()

        if (!isEmailSend) {
            ll_fp_popup.visibility = View.VISIBLE
            btn_send_fp.setOnClickListener(View.OnClickListener {
                if (et_email_fp.text.toString().isEmpty()) {
                    Toast.makeText(context, R.string.please_enter_email, Toast.LENGTH_SHORT).show()
                } else if (HelperMethods.isValidEmail(et_email_fp.text.toString())) {
                    Toast.makeText(context, R.string.enter_valid_email, Toast.LENGTH_SHORT).show()
                } else {
                    dialog.cancel()
                    sendMail(context, et_email_fp.text.toString())
                }

            })
            btn_cancel_fp.setOnClickListener(View.OnClickListener { dialog.cancel() })
        } else {
            ll_fp_mail_popup.visibility = View.VISIBLE
            btn_cancel_fp_mail.setOnClickListener(View.OnClickListener {
                dialog.cancel()
            })
        }
    }

    //================Send Mail============//
    fun sendMail(context: Context?, email: String) {
        login_progressbar.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)

        val str = ("{\n" +
                "  \"email\": \"" + email + "\"\n" +
                "}")

        try {
            val requestQueue = Volley.newRequestQueue(context)

            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.FORGOT_PASSWORD,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        login_progressbar.visibility = View.GONE
                        HelperMethods.disableTouch(
                            false,
                            activity
                        )
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

                    activity!!.runOnUiThread(Runnable {
                        login_progressbar.visibility = View.GONE
                        HelperMethods.disableTouch(
                            false,
                            activity
                        )
                    })
                    responseString = response.statusCode.toString()
                    Log.d("rhrth", responseString)
                    Log.d("gerger", response.toString())
                    (context as SignUp).showEmailSnackbar()
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
            requestQueue.add(stringRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}
