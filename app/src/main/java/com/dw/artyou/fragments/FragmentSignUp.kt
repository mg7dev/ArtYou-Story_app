package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.designoweb.marketplace.subcontractor.activity.api.Retro
import com.dw.artyou.helper.Api
import com.dw.artyou.R
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.interfaces.SegmentInterface
import com.dw.artyou.models.Data
import com.dw.artyou.models.ObjectTypeModel
import com.dw.artyou.models.UserSegmentModel
import com.dw.artyou.recyclerAdapters.SegmentAdapter
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_story_list.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.UnsupportedEncodingException


/**
 * A simple [Fragment] subclass.
 */
class FragmentSignUp : Fragment(), SegmentInterface {

    private var dialog_list: AlertDialog? = null
    private var segmentList: ArrayList<Data>? = null
    private var segmentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Retro.Api(context!!).getUserSegment().enqueue(object :
            retrofit2.Callback<UserSegmentModel> {
            override fun onResponse(
                call: Call<UserSegmentModel>,
                response: retrofit2.Response<UserSegmentModel>
            ) {
                if (response.isSuccessful) {
                    segmentList = response.body()?.data as ArrayList<Data>
                } else {
                    val data = HelperMethods.parseError(response)
                    Toast.makeText(context, data?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserSegmentModel>, t: Throwable) {

            }
        })

        clickEvents()
    }

    fun clickEvents() {
        sign_up_segment_tv.setOnClickListener(View.OnClickListener {
            popupUserSegment()
        })
        tv_tnd_signup.setOnClickListener(View.OnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://artyou.global/terms/"))
            startActivity(browserIntent)
        })

        btn_create_acc.setOnClickListener(View.OnClickListener {
            if (sign_up_first_name_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(activity, getString(R.string.please_enter_name), Toast.LENGTH_SHORT)
                    .show()
            } else if (sign_up_last_name_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_last_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_user_name_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_username),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (ccp_sign_up_country?.selectedCountryName?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_select_country),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_mobile_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_mobile_number),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (segmentId.equals("")) {
                Toast.makeText(
                    activity,
                    getString(R.string.select_user_segment),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_gender_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_select_gender),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_lang_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_language),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_email_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(activity, getString(R.string.please_enter_email), Toast.LENGTH_SHORT)
                    .show()
            } else if (HelperMethods.isValidEmail(sign_up_email_et.text.toString())) {
                Toast.makeText(activity, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT)
                    .show()
            } else if (sign_up_password_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sign_up_cnf_password_et?.text?.isNullOrEmpty()!!) {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_confirm_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!sign_up_cnf_password_et.text.toString().equals(sign_up_password_et.text.toString())) {
                Toast.makeText(
                    activity,
                    getString(R.string.passwords_should_be_same),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (!cb_tnd_signup.isChecked) {
                    Toast.makeText(
                        activity,
                        getString(R.string.read_and_accept_terms),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    registerUser(context)
                }

            }
        })
    }

    //=============Register Api=============//
    fun registerUser(context: Context?) {
        signup_progressbar.visibility = View.VISIBLE
        HelperMethods.disableTouch(true, activity)
        val str: kotlin.String? = ("{\n" +
                "  \"first_name\": \"" + sign_up_first_name_et.text.toString() + "\",\n" +
                "  \"last_name\": \"" + sign_up_last_name_et.text.toString() + "\",\n" +
                "  \"username\": \"" + sign_up_user_name_et.text.toString() + "\",\n" +
                "  \"password\": \"" + sign_up_password_et.text.toString() + "\",\n" +
                "  \"email\": \"" + sign_up_email_et.text.toString() + "\",\n" +
                "  \"segment\": \"" + segmentId + "\",\n" +
                "  \"country\": \"" + ccp_sign_up_country.selectedCountryName.toString() + "\",\n" +
                "  \"currency\": \"AED\",\n" +
                "  \"language\": \"en\"\n" +
                "\n" +
                "}")
        try {
            val requestQueue = Volley.newRequestQueue(context)

            val requestBody = str!!
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Api.REGISTER,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    signup_progressbar.visibility = View.GONE
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
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(
                            false,
                            activity
                        )
                    })
                    var responseString: String = ""
                    responseString = response.statusCode.toString()
                    Log.d("responseStringh", responseString)
                    Log.d("responseStringh", response.toString())

                    val res = String(
                        response.data,
                        charset(HttpHeaderParser.parseCharset(response.headers))
                    )
                    val obj = JSONObject(res)
                    val nick = obj.getString("nick")
                    val email = obj.getString("email")
                    val name = obj.getString("name")
                    val active = obj.getString("active")
                    val whenn = obj.getString("when")
                    val failedLoginCount = obj.getString("failedLoginCount")
                    val confirmed = obj.getString("confirmed")
                    //val social = obj.getString("nick") it is a array
                    // val telephone = obj.getString("telephone") it is a array
                    //val address = obj.getString("address") it is a array
                    val first_name = obj.getString("first_name")
                    val last_name = obj.getString("last_name")
                    val segment = obj.getString("segment")
                    val country = obj.getString("country")
                    val currency = obj.getString("currency")
                    val language = obj.getString("language")
                    val id = obj.getString("id")

                    if (responseString.equals("200")) {
                        activity!!.runOnUiThread(Runnable { activity!!.onBackPressed() })
                    }

                    Log.d(
                        "hwdwwe",
                        nick + "..." + email + "..." + name + "..." + active + "..." + whenn + "..." + failedLoginCount + "..." + confirmed + "..." + first_name + "..." + last_name + "..." + segment + "..." + country + "..." +
                                currency + "..." + language + "..." + id
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

    //====================Popup Segment================//
    private fun popupUserSegment() {
        val adapter: SegmentAdapter
        val builder =
            AlertDialog.Builder(context!!)
        val layoutInflater = layoutInflater
        @SuppressLint("InflateParams") val customView: View =
            layoutInflater.inflate(R.layout.popup_lists, null)
        val rec_branch: RecyclerView = customView.findViewById(R.id.rec_branch)
        val searchView = customView.findViewById<EditText>(R.id.searchView)
        rec_branch.layoutManager = LinearLayoutManager(context!!)
        adapter = SegmentAdapter(context!!, segmentList!!, this)
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
                    rec_branch.adapter = SegmentAdapter(
                        context!!,
                        filter(searchView.text.toString())!!,
                        this@FragmentSignUp
                    )

                } else {
                    rec_branch.adapter = adapter
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        builder.setView(customView)
        dialog_list = builder.create()
        dialog_list!!.show()
    }

    override fun onItemClick(data: Data) {
        dialog_list?.cancel()
        segmentId = data.id
        sign_up_segment_tv.text = data.name
    }

    //============Filter Method===================//
    fun filter(text: String?): ArrayList<Data>? {
        Log.d("jkhdgf", text!!)
        val filterdNames: ArrayList<Data> = ArrayList()
        for (s in segmentList!!) {
            val name: String = s.name!!
            if (name.contains(text, ignoreCase = true)) {
                filterdNames.add(s)
            }
        }
        return filterdNames
    }
}
