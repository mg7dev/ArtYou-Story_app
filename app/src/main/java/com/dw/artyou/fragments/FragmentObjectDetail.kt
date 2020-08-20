package com.dw.artyou.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.designoweb.marketplace.subcontractor.activity.api.Retro

import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.*
import com.dw.artyou.recyclerAdapters.ExperienceAdapter
import com.dw.artyou.recyclerAdapters.ObjectDetailAdapter
import com.dw.artyou.recyclerAdapters.ObjectTypeAdapter
import com.dw.felgen_inserate.helperMethods.SessionManager
import kotlinx.android.synthetic.main.fragment_fragment_object_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FragmentObjectDetail(var objectId: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_object_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sl_object_detail.setOnRefreshListener {
            getDetails()
        }

        getDetails()
    }


    //===========getDetails===============//
    fun getDetails() {
        Retro.Api(context!!).getObjectDetail(objectId)
            .enqueue(object : Callback<ObjectDetailModel> {
                override fun onResponse(
                    call: Call<ObjectDetailModel>,
                    response: retrofit2.Response<ObjectDetailModel>
                ) {
                    if (response.isSuccessful) {
                        if (sl_object_detail.isRefreshing) {
                            sl_object_detail.isRefreshing = false
                        }
                        Log.d("jkfhrje", response.body().toString())
                        setdata(response.body()!!, activity)
                    }
                }

                override fun onFailure(call: Call<ObjectDetailModel>, t: Throwable) {
                }
            })

        Retro.Api(context!!).getExperiences(objectId).enqueue(object : Callback<ExperienceModel> {
            override fun onResponse(
                call: Call<ExperienceModel>,
                response: retrofit2.Response<ExperienceModel>
            ) {
                if (response.isSuccessful) {
                    Log.d("euferuf", response.body().toString())
                    setExperienceRecycler(response.body()?.experiences as ArrayList)
                }
            }

            override fun onFailure(call: Call<ExperienceModel>, t: Throwable) {
            }
        })
    }

    fun setdata(objectDetailModel: ObjectDetailModel, context: Activity?) {
        if (context != null) {
            if ((context as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.frame_main) is FragmentObjectDetail) {
                pb_object_detail.visibility = View.GONE
                nv_object_detail.visibility = View.VISIBLE
                val list = ArrayList<ObjectTypeModel>()
                val displayMetrics = DisplayMetrics()
                activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = displayMetrics.widthPixels
                val parms = LinearLayout.LayoutParams(width, width)
                iv_object_detail.layoutParams = parms

                iv_object_detail.setOnClickListener(View.OnClickListener {
                    activity!!.supportFragmentManager.beginTransaction()
                        .add(R.id.frame_main, FragmentZoomPhoto(objectDetailModel.cover, null))
                        .addToBackStack(null).commit()
                })

                Glide.with(context!!).load(Api.getImage(objectDetailModel.cover))
                    .placeholder(R.drawable.demo_img).into(iv_object_detail)

                Glide.with(context!!).load(Api.getImage(objectDetailModel.owner.avatar))
                    .apply(RequestOptions().circleCrop())
                    .placeholder(R.drawable.card_image)
                    .into(iv_profile_object_detail)

                tv_titel_object_detail.text = objectDetailModel.owner.name
                //tv_sub_titel_object_detail.text = objectDetailModel.owner.type
                if (objectDetailModel.creator != null) {
                    tv_creator_object_detail.visibility = View.VISIBLE
                    tv_creator_object_detail.text = objectDetailModel.creator
                }

                if (objectDetailModel.museology != null) {
                    tv_object_titel_object_detail.visibility = View.VISIBLE
                    tv_object_titel_object_detail.text = objectDetailModel.museology.title
                    list.add(
                        ObjectTypeModel(
                            "Material",
                            objectDetailModel.museology?.material?.toString()
                        )
                    )
                    list.add(
                        ObjectTypeModel(
                            "Technique",
                            objectDetailModel.museology?.technique?.toString()
                        )
                    )
                    list.add(
                        ObjectTypeModel(
                            "Description",
                            objectDetailModel.museology?.summaryDescription?.toString()
                        )
                    )
                    list.add(
                        ObjectTypeModel(
                            "Collection",
                            objectDetailModel.museology?.collectionType?.toString().toLowerCase()
                        )
                    )
                }


                list.add(
                    ObjectTypeModel(
                        "Object Type",
                        objectDetailModel.type.toLowerCase(Locale.ROOT)
                    )
                )
                list.add(ObjectTypeModel("Media Type", objectDetailModel.media_type))
                if (objectDetailModel.dimensions != null) {
                    list.add(
                        ObjectTypeModel(
                            "Dimensions (H,W,D)",
                            objectDetailModel.dimensions?.height?.toString() + "x" +
                                    objectDetailModel.dimensions?.width?.toString() + "x" +
                                    objectDetailModel.dimensions?.depth?.toString() + " " + objectDetailModel.dimensions?.unit?.toString()
                        )
                    )
                }

                list.add(
                    ObjectTypeModel(
                        "Availability",
                        objectDetailModel.availability?.toString()
                    )
                )
                if (objectDetailModel.price != null) {
                    list.add(
                        ObjectTypeModel(
                            "Price",
                            objectDetailModel.price?.value?.toString() + " " + objectDetailModel.price?.currency?.toString()
                        )
                    )
                }
                val subList: ArrayList<ObjectTypeModel> = ArrayList()
                for (items in list) {
                    if (items.name != null) {
                        subList.add(items)
                    }
                }
                rv_object_detail.adapter = ObjectDetailAdapter(context, subList)
            }
        }
    }

    fun setExperienceRecycler(experience: ArrayList<Experiences>) {
        rv_experience_object_detail.adapter = ExperienceAdapter(context!!, experience)
    }
}