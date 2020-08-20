package com.dw.artyou.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.dw.artyou.R
import com.dw.artyou.activities.MainActivity
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.VolleySingleton
import com.dw.artyou.models.*
import com.dw.artyou.recyclerAdapters.DotRecyclerAdapter
import com.dw.artyou.recyclerAdapters.StoryDeatilAdapter
import com.dw.felgen_inserate.helperMethods.SessionManager
import kotlinx.android.synthetic.main.fragment_story_detail.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap
import kotlin.collections.set


/**
 * A simple [Fragment] subclass.
 */
class FragmentStoryDeatil(var storyId: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sl_story_detail.setOnRefreshListener { getStoryDetail() }
        getStoryDetail()
    }

    //========================Set Recycler========================//
    fun setRecycler(
        list: ArrayList<Object>,
        userImage: String,
        segment: String,
        creatorName: String
    ) {
        rv_story_detail_dots.adapter = DotRecyclerAdapter(context!!, list.size, 0)
        Log.d("khfid", list.size.toString())
        val snapHelper = LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv_story_detail)
        rv_story_detail.adapter =
            StoryDeatilAdapter(context!!, list, userImage, segment, storyId, creatorName)

        rv_story_detail.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = rv_story_detail.layoutManager as LinearLayoutManager
                rv_story_detail_dots.adapter = DotRecyclerAdapter(
                    context!!,
                    list.size,
                    manager.findFirstCompletelyVisibleItemPosition()
                )
            }
        })
    }

    //===========================Get story detail from api===============//

    fun getStoryDetail() {
        Log.d("wdnjdn", "Method Called")

        val request =
            object : StringRequest(
                Request.Method.GET, Api.getStoryDetail(storyId),
                Response.Listener { response ->
                    pb_story_detail.visibility = View.GONE
                    if (sl_story_detail.isRefreshing) {
                        sl_story_detail.isRefreshing = false
                    }
                    Log.d("jiwedjw", response)
                    var jsonResponse: JSONObject? = null
                    try {
                        jsonResponse = JSONObject(response)
                        val storyObject = jsonResponse.getJSONObject("storye")
                        val objectArray = storyObject.getJSONArray("objects")
                        val objectList = ArrayList<Object>()
                        for (i in 0 until objectArray.length()) {
                            val jsonObject = objectArray.getJSONObject(i)
                            val objDetails = jsonObject.getJSONObject("object")
                            val ownerObject = objDetails.getJSONObject("owner")
                            objectList.add(
                                Object(
                                    ObjectX(
                                        objDetails.optString("entity$"),
                                        objDetails.optString("name"),
                                        objDetails.optString("type"),
                                        Owner(
                                            ownerObject.optString("id"),
                                            ownerObject.optString("type")
                                        ),
                                        objDetails.optString("cover"),
                                        //objDetails.optString("creator"),
                                        objDetails.optString("id")
                                    ), jsonObject.optString("text")
                                )
                            )
                        }

                        val experienceList = ArrayList<Experience>()
                        val expArray = storyObject.getJSONArray("experiences")
                        if (expArray.length() > 0) {
                            for (i in 0 until expArray.length() - 1) {
                                val expObject = expArray.getJSONObject(i)
                                val type = expObject.optString("type")
                                var content: String? = null
                                var contentList: ArrayList<Content>? = null
                                if (type.equals("TEXT", true)) {
                                    content = expObject.optString("content")
                                } else {
                                    contentList = ArrayList()
                                    val contentArray = expObject.getJSONArray("content")
                                    for (j in 0 until contentArray.length() - 1) {
                                        val contentObject = contentArray.getJSONObject(j)
                                        contentList.add(
                                            Content(
                                                contentObject.optString("filename"),
                                                contentObject.optString("caption")
                                            )
                                        )
                                    }
                                }
                                val ownerObject = expObject.getJSONObject("owner")
                                experienceList.add(
                                    Experience(
                                        Owner(
                                            ownerObject.optString("id"),
                                            ownerObject.optString("type")
                                        ),
                                        type,
                                        expObject.optString("visibility"),
                                        contentList, content,
                                        expObject.optString("date"),
                                        expObject.optString("created_at"),
                                        expObject.optString("id")

                                    )
                                )
                            }
                        }


                        val storyOwner = storyObject.getJSONObject("owner")
                        val creatorObject = storyObject.getJSONObject("creator")
                        val likeObject = storyObject.getJSONObject("like")
                        val storydetailModel: StoryDetailModel = StoryDetailModel(
                            storyObject.optString("title"),
                            storyObject.optString("description"),
                            objectList,
                            Owner(
                                storyOwner.optString("id"),
                                storyOwner.optString("type")
                            ),
                            storyObject.optString("date"),
                            storyObject.optString("hidden"),
                            storyObject.optString("views"),
                            storyObject.optString("created_at"),
                            storyObject.optString("id"),
                            experienceList,
                            Creator(
                                creatorObject.optString("cover"),
                                creatorObject.optString("id"),
                                creatorObject.optString("name"),
                                creatorObject.optString("segment")
                            ),
                            likeObject.optString("liked")
                        )
                        Log.d("kjkdw", likeObject.optString("liked"))
                        if (objectList.size > 1) {
                            Log.d("jheehf", "IF CONDITION")
                            setRecycler(
                                objectList,
                                storydetailModel.creator.cover,
                                storydetailModel.creator.segment,
                                storydetailModel.creator.name
                            )
                        } else {
                            Log.d("jheehf", "ELSE CONDITION")
                            MainActivity.isLink = true
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                                .add(
                                    R.id.frame_main,
                                    FragmentObjectDetail(objectList[0].`object`.id)
                                ).commit()
                        }

                    } catch (e: Exception) {
                        Log.d("jhwehfwfwe", e.toString())
                    }

                },
                Response.ErrorListener {
                    Log.d("kjwkdd", it.toString())
                    pb_story_detail.visibility = View.GONE
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
}
