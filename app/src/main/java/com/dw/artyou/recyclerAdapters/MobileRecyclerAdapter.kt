package com.dw.artyou.recyclerAdapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.models.Telephone
import com.dw.felgen_inserate.helperMethods.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MobileRecyclerAdapter(
    var context: Context,
    var list: ArrayList<Telephone>
) :
    RecyclerView.Adapter<MobileRecyclerHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MobileRecyclerHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_mobile, parent, false)
        return MobileRecyclerHolder(view)
    }

    override fun onBindViewHolder(holder: MobileRecyclerHolder, position: Int) {
        holder.tv_code_mobile_recycler.text = list[position].country_code
        holder.tv_mobile_recycler.text = list[position].number.toString()
        holder.iv_delete_mobile_recycler.setOnClickListener(View.OnClickListener {
            showPopup(list[position].id, position)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun showPopup(phone_id: String, position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_logout, null)
        val tv_logout: TextView = customView.findViewById(R.id.tv_logout)
        val btn_sure_logout: Button = customView.findViewById(R.id.btn_sure_logout)
        val btn_cancel_logout: Button = customView.findViewById(R.id.btn_cancel_logout)

        tv_logout.setText("Are you sure, you want to remove number?")
        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        btn_sure_logout.setOnClickListener(View.OnClickListener {
            deletePhone(context, phone_id, position)
            dialog.cancel()
        })
        btn_cancel_logout.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }

    fun deletePhone(context: Context?, phone_id: String, position: Int) {

        try {
            val requestQueue = Volley.newRequestQueue(context)

            val stringRequest: StringRequest = object : StringRequest(
                Method.DELETE,
                Api.deletePhone(phone_id),
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

                override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
                    var responseString: String = ""

                    responseString = response.statusCode.toString()
                    Log.d("qfdcdxd", responseString)
                    Log.d("cdecsaxcsa", response.toString())

                    val v = list.removeAt(position)
                    Log.d("kjdhwqjd", v.toString())
                    (context as AppCompatActivity).runOnUiThread(Runnable {
                        notifyDataSetChanged()
                    })

                    val userInfoModel = SessionManager.getInstance(context!!)!!.userModel
                    val value = userInfoModel!!.telephone.removeAt(position)
                    Log.d("kjdhwqjd", value.toString())
                    SessionManager.getInstance(context)!!.userModel = userInfoModel
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

class MobileRecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_code_mobile_recycler: TextView = itemView.findViewById(R.id.tv_code_mobile_recycler)
    val tv_mobile_recycler: TextView = itemView.findViewById(R.id.tv_mobile_recycler)
    val iv_delete_mobile_recycler: ImageView = itemView.findViewById(R.id.iv_delete_mobile_recycler)

    //===================Delete Number================//

}