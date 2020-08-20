package com.dw.artyou.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.VolleySingleton
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.mynameismidori.currencypicker.CurrencyPicker
import com.mynameismidori.currencypicker.CurrencyPickerListener
import kotlinx.android.synthetic.main.fragment_language.*
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


/**
 * A simple [Fragment] subclass.
 */
class FragmentLangauge : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()

    }

    fun setViews() {
        val userModel = SessionManager.getInstance(context!!)!!.userModel
        Glide.with(context!!)
            .load(Api.getImage(userModel?.avatar))
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_account_circle_blue).into(iv_userprofile_language)

        tv_username_language.text = userModel?.name
        tv_language_language.text = userModel?.language
        tv_currency_language.text = userModel?.currency
        tv_currency_language.setOnClickListener(View.OnClickListener {
            val picker =
                CurrencyPicker.newInstance("Choose your currency") // dialog title

            picker.setListener { name, code, symbol, flagDrawableResID ->
                picker.dismiss()
                changeCurrency(code)
                Log.d("jhfgeu", name + "..." + code + "..." + symbol)
            }
            picker.show(childFragmentManager, "CURRENCY_PICKER")
        })
        tv_return_back_language.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })
    }


    //======================Change Currency ==============/
    fun changeCurrency(currency: String) {
        activity!!.runOnUiThread(Runnable {
            pb_language.visibility = View.VISIBLE
            HelperMethods.disableTouch(true, activity)
        })
        val str = ("{\n" +
                "  \"currency\": \"" + currency + "\"\n" +
                "}")
        Log.d("jiihdsjis", str)

        try {
            val requestBody = str
            Log.d("wjhvwhv", requestBody)
            val stringRequest: StringRequest = object : StringRequest(
                Method.PUT,
                Api.CHANGE_CURRENCY,
                Response.Listener { response -> },
                Response.ErrorListener { error ->
                    activity!!.runOnUiThread(Runnable {
                        HelperMethods.disableTouch(false, activity)
                        pb_language.visibility = View.GONE
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
                    userModel!!.currency = jsonObject.getString("currency")

                    SessionManager.getInstance(context!!)!!.userModel = userModel
                    activity!!.runOnUiThread(Runnable {

                        tv_currency_language.text = jsonObject.getString("currency")
                        HelperMethods.disableTouch(false, activity)
                        pb_language.visibility = View.GONE
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
