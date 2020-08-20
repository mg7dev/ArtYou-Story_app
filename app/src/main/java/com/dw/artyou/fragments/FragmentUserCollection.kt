package com.dw.artyou.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.interfaces.CollectionRecyclerClick

import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.*
import com.dw.artyou.recyclerAdapters.ChooseCollectionAdapter
import com.dw.felgen_inserate.helperMethods.SessionManager
import kotlinx.android.synthetic.main.fragment_user_collection.*
import org.json.JSONArray
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 */
class FragmentUserCollection : Fragment(),
    CollectionRecyclerClick {

    override fun onItemClick(position:Int,isChecked:Boolean) {
        var collected: ArrayList<Int> ? = null
        if(collectList!=null){
            collected = collectList
        }
        // to get the result as list
        if(collected!=null && isChecked==false){
            var index = collected.lastIndexOf(position)
            if(index == 0 && collected.size==1){
                collectList = null
                Log.d("collect--------->" , collectList.toString())

            }else{
                collected.removeAt(index)
                collectList = collected
                Log.d("collect--------->" , collectList.toString())
            }
            return
        }
        if(collected != null && isChecked==true){
            collected.add(position)
            collectList = collected
            Log.d("collect--------->" , collectList.toString())
            return
        }
        if(collected == null && isChecked==true){
            collected = arrayListOf(position)
            collectList = collected
            Log.d("collect--------->" , collectList.toString())
            return
        }
        //=====colect object lestenser======//
    }

    lateinit var chooseCollectionAdapter: ChooseCollectionAdapter
    var objectCollectModel: ObjectCollectModel? = null
    var collectList: ArrayList<Int>? = null
    lateinit var list: ArrayList<ObjectCollectModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        getObjects()
        Glide.with(context!!)
            .load(Api.getImage(SessionManager.getInstance(context!!)!!.userModel!!.avatar))
            .placeholder(R.drawable.ic_account_circle_blue)
            .apply(RequestOptions.circleCropTransform()).into(iv_profile_collection)
        clickEvents(this)

    }

    //===================Click Events======================//
    fun clickEvents(collectionRecyclerClick: CollectionRecyclerClick) {
        tv_collection_top_next.setOnClickListener(View.OnClickListener {
            nextButton()
        })

        tv_collection_bottom_next.setOnClickListener(View.OnClickListener {
            nextButton()
        })

        tv_collection_top_back.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })

        et_search_collection.addTextChangedListener(object : TextWatcher {
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
                if (et_search_collection.text.toString().isNotEmpty()) {
                    rv_choose_collection.adapter =
                        ChooseCollectionAdapter(
                            context!!,
                            filter(et_search_collection.text.toString())!!,
                            collectionRecyclerClick
                        )
                } else {
                    rv_choose_collection.adapter = chooseCollectionAdapter
                }
            }
        })
    }

    //====================Next Button Function===========//
    fun nextButton() {

        if (collectList?.size != 0) {
            Log.d("list------------>",list.toString())
            var collectlist = emptyList<ObjectCollectModel>().toMutableList()
            for (item in this!!.collectList!!){
                collectlist.add(list[item])
            }
            activity!!.supportFragmentManager.beginTransaction()
                .add(
                    R.id.frame_extra_activity,
                    FragmentCreateStory(
                        null,
                        null,
                        null, collectlist
                    )
                ).addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(context!!, "Please select any object", Toast.LENGTH_SHORT).show()
        }

    }

    //=======================Get Object List===============//
    fun getObjects() {
        Log.d("dwwwdc", "Method Called")
        val request =
            object : StringRequest(
                Request.Method.GET, Api.GET_OBJECTS,
                Response.Listener { response ->
                    pb_collection.visibility = View.GONE
                    Log.d("jiwedjw", response)
                    var jsonResponse: JSONArray? = null
                    try {
                        jsonResponse = JSONArray(response)

                        for (i in 0 until jsonResponse.length()) {
                            val obj = jsonResponse.getJSONObject(i)
                            val ownerObject = obj.getJSONObject("owner")
                            list.add(
                                ObjectCollectModel(
                                    obj.getString("active"),
                                    obj.getString("created_at"),

                                    obj.getString("creator"),
                                    obj.getString("dimensions"),
                                    obj.getString("media_type"),

                                    obj.getString("name"),
                                    Owner(
                                        ownerObject.getString("id"),
                                        ownerObject.getString("type")
                                    ),
                                    obj.getString("type"),
                                    obj.optString("cover"),
                                    obj.getString("id")
                                )
                            )
                            Log.d("usvahcxvsa", "..." + obj.optString("cover"))
                        }
                        chooseCollectionAdapter = ChooseCollectionAdapter(context!!, list, this)
                        rv_choose_collection.adapter = chooseCollectionAdapter

                    } catch (e: Exception) {

                        Log.d("jhwehfwfwe", e.toString())
                    }

                },
                Response.ErrorListener {
                    pb_collection.visibility = View.GONE
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["Authorization"] =
                        "Bearer " + SessionManager.getInstance(context!!)!!.authToken
                    Log.d("paramndj", params.toString())
                    return params
                }
            }
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    //==============Filter Collection======================//
    fun filter(text: String?): ArrayList<ObjectCollectModel>? {
        Log.d("jkhdgf", text!!)
        val filterdNames: ArrayList<ObjectCollectModel> = ArrayList()
        for (s in list) {
            val name: String = s.name
            val id: String = s.id
            val type: String = s.type
            if (name.contains(text, ignoreCase = true) || id.contains(
                    text,
                    ignoreCase = true
                ) || type.contains(text, true)
            ) {
                filterdNames.add(s)
            }
        }
        return filterdNames
    }
}
