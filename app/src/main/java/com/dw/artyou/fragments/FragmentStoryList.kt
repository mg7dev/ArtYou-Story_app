package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.designoweb.marketplace.subcontractor.activity.api.Retro
import com.dw.artyou.R
import com.dw.artyou.database.StoryRoom
import com.dw.artyou.database.story
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.models.Storye
import com.dw.artyou.models.StoryesListModel
import com.dw.artyou.recyclerAdapters.StoryListAdapter
import com.dw.artyou.recyclerAdapters.StoryListOfflineAdapter
import kotlinx.android.synthetic.main.fragment_story_list.*
import retrofit2.Call
import retrofit2.Callback


class FragmentStoryList : Fragment() {
    private lateinit var rv_story_list: RecyclerView
    lateinit var rv_story_list_offline: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_story_list, container, false)
        initUI(v)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sl_story_list.setOnRefreshListener {
            getStories()
        }
        //getStories()
    }

    fun initUI(view: View) {
        rv_story_list = view.findViewById(R.id.rv_story_list)
        rv_story_list_offline = view.findViewById(R.id.rv_story_list_offline)

    }

    fun getStories() {
        activity!!.runOnUiThread(Runnable {
            HelperMethods.disableTouch(true, activity)
        })

        Log.d("wdnjdn", "Method Called")

        Retro.Api(context!!).getStoriesHome().enqueue(object : Callback<StoryesListModel> {
            override fun onResponse(
                call: Call<StoryesListModel>,
                response: retrofit2.Response<StoryesListModel>
            ) {
                activity!!.runOnUiThread(Runnable {
                    if (sl_story_list.isRefreshing) {
                        sl_story_list.isRefreshing = false
                    }
                    HelperMethods.disableTouch(false, activity)
                    pb_story_list.visibility = View.GONE
                })
                if (response.isSuccessful) {
                    if (rv_story_list.visibility == View.GONE) {
                        rv_story_list.visibility = View.VISIBLE
                        rv_story_list_offline.visibility = View.GONE
                    }

                    rv_story_list.adapter =
                        StoryListAdapter(
                            context!!,
                            response.body()!!.storyes as ArrayList<Storye>
                        )
                    saveDataOffline(response.body()!!)

                } else {
                    val data = HelperMethods.parseError(response)
                    Toast.makeText(context, data?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryesListModel>, t: Throwable) {
                activity!!.runOnUiThread(Runnable {
                    HelperMethods.disableTouch(false, activity)
                    pb_story_list.visibility = View.GONE
                })
            }
        })
    }

    fun saveDataOffline(storyListModel: StoryesListModel) {
        val storyList: ArrayList<story> = ArrayList()
        val profileList: ArrayList<ByteArray?> = ArrayList()
        val coverList: ArrayList<ByteArray?> = ArrayList()
        val titelList: ArrayList<String> = ArrayList()
        val segmentList: ArrayList<String> = ArrayList()
        val desList: ArrayList<String> = ArrayList()
        val idList: ArrayList<String> = ArrayList()
        val myOptions = RequestOptions()
            .override(100, 100)
        if (storyListModel.storyes.size > 5) {
            for (i in 0..4) {
                Glide.with(context!!)
                    .asBitmap()
                    .apply(myOptions)
                    .load(Api.getImage(storyListModel.storyes[i].creator.avatar))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            Log.d("wuidh", "...." + HelperMethods.getFileDataFromBitmap(resource))
                            profileList.add(HelperMethods.getFileDataFromBitmap(resource))
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            profileList.add("aaa".toByteArray())
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
                Glide.with(context!!)
                    .asBitmap()
                    .apply(myOptions)
                    .load(Api.getImage(storyListModel.storyes[i].objects[0].`object`.cover))
                    .placeholder(R.drawable.demo_img)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            Log.d(
                                "wofjwdiof",
                                "...." + HelperMethods.getFileDataFromBitmap(resource)
                            )
                            coverList.add(HelperMethods.getFileDataFromBitmap(resource))
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            coverList.add("aaa".toByteArray())
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
                idList.add(storyListModel.storyes[i].id)
                titelList.add(storyListModel.storyes[i].creator.name)
                segmentList.add(storyListModel.storyes[i].creator.segment)
                desList.add(storyListModel.storyes[i].description)

            }
            var isRunning = false
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
//                    Log.d("sqdswdc", "Running..." + coverList.size + "..." + profileList.size)
                    if (coverList.size >= 5 && profileList.size >= 5) {
                        isRunning = true
                        Log.d("eugfe", "Condition True")
                        for (i in 0..4) {
                            storyList.add(
                                story(
                                    idList[i],
                                    titelList[i],
                                    segmentList[i],
                                    desList[i],
                                    profileList[i],
                                    coverList[i]
                                )
                            )
                        }
                        AddData(context, storyList).execute()
                    }
                    if (!isRunning) {
                        handler.postDelayed(this, 50)
                    }

                }
            })
        }
    }

    fun scrollPosition() {
        if (rv_story_list.adapter != null) {
            rv_story_list.smoothScrollToPosition(0)
        }
    }

    companion object {
        val DATABSE_NAME = "storydatabase.db"

        class AddData internal constructor(var context: Context?, val list: ArrayList<story>) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = ""

            override fun doInBackground(vararg params: Int?): String? {
                if (context != null) {
                    val db = StoryRoom(context!!)
                    Log.d("wgfyuwegwef", "Method Called")
                    Log.d("hwdfhwd", "Size:- " + list.size)
                    if (db.isOpen) {
                        db.storyDao().deleteAll()
                        for (items in list) {
                            db.storyDao().insertStory(items)
                        }
                        db.close()
                        this.cancel(true)
                    }
                }

                return resp
            }

        }

        class GetData internal constructor(val context: Context, val recyclerview: RecyclerView) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = ""

            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: Int?): String? {
                Log.d("wgfyuwegwef", "Method Called")
                val db = StoryRoom(context)

                val list = db.storyDao().getAll() as ArrayList
                (context as AppCompatActivity).runOnUiThread(Runnable {
                    recyclerview.adapter = StoryListOfflineAdapter(context, list)
                })

                Log.d("wyegcyu", "...." + list.size)
                db.close()
                this.cancel(true)

                return resp
            }

        }
    }

    fun changeRecyclers(boolean: Boolean) {
        if (!boolean) {
            rv_story_list.visibility = View.GONE
            rv_story_list_offline.visibility = View.VISIBLE
            pb_story_list.visibility = View.GONE
            GetData(context!!, rv_story_list_offline).execute()
        } else {
            Log.d("wdgyu", "ELSE CONDITION..." + boolean)
            getStories()
            rv_story_list.visibility = View.VISIBLE
            rv_story_list_offline.visibility = View.GONE
        }
    }

}
