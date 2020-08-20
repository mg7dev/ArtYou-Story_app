package com.dw.artyou.recyclerAdapters

import android.app.AlertDialog
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.R
import com.dw.artyou.fragments.FragmentObjectDetail
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.models.Object
import android.content.DialogInterface;
import com.dw.artyou.fragments.FragmentStoryDeatil


class StoryDeatilAdapter(
    var context: Context,
    var list: ArrayList<Object>,
    var userimage: String,
    var segment: String,
    var storyId:String,
    var creatorName:String
) :
    RecyclerView.Adapter<StoryDeatilHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryDeatilHolder {
        Log.d("pdjwoqjd", list.toString())
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_story_detail, parent, false)
        return StoryDeatilHolder(view)
    }

    override fun onBindViewHolder(holder: StoryDeatilHolder, position: Int) {

        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val parms = LinearLayout.LayoutParams(width, width)
        holder.iv_story_detail_rec.layoutParams = parms
        Glide.with(context!!).load(Api.getImage(list.get(position).`object`.cover)).placeholder(
            R.drawable.demo_img
        )
            .into(holder.iv_story_detail_rec)
        holder.tv_titel_story_detail_rec.text = creatorName
        Glide.with(context!!).load(Api.getImage(userimage)).apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.card_image)
            .into(holder.iv_user_story_detail_rec)

        holder.tv_detail_story_detail_rec.text = list[position].text
        holder.tv_sub_titel_story_detail_rec.text = segment
        holder.tv_creator_story_detail_rec.text =
            "Creator (" + list[position].`object`.owner.type + ")"

        holder.iv_report_rec_detail.setOnClickListener(createPopupMenu(holder.iv_report_rec_detail ,position))

//        holder.iv_story_detail_rec.setOnClickListener(View.OnClickListener {
//            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                .add(R.id.frame_main, FragmentZoomPhoto(list.get(position).`object`.cover, null))
//                .addToBackStack(null).commit()
//        })

        holder.rl_story_detail_profile_rec.setOnClickListener(View.OnClickListener { })

        holder.itemView.setOnClickListener(View.OnClickListener {
            Log.d("pdjwoqjd", "Clicked")
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                //.add(R.id.frame_main, FragmentObjectDetail("5865af5965ae44000145940e"))
                .add(R.id.frame_main, FragmentObjectDetail(list[position].`object`.id))
                .addToBackStack(null).commit()
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun createPopupMenu(it: View,position: Int): View.OnClickListener {
        return View.OnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menu.add(Menu.NONE, 1, 1, "Report")
            //==============delete story===============//
            popupMenu.menu.add(Menu.NONE, 2, 2, "Erase Stories")//cora
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                val i = item.itemId
                if( i == 2) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Erase Story!")
                        .setMessage("Do you want to delete this stroies?")
                        .setPositiveButton("No"){dialogInterface,which ->
                        }
                        .setNegativeButton("Yes"){dialogInterface, which ->
                            //==============202007180123==============//
                            var owner = list[position].`object`.owner
                            HelperMethods.deleteStory(context, storyId,owner)
                        }
                    val alertDialog: AlertDialog = builder.create()

                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
                if (i == 1) {
                    HelperMethods.reportStory(context, "Test", storyId)
                    true
                } else {
                    false
                }
            }
        }
    }
}

class StoryDeatilHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iv_report_rec_detail:ImageView = itemView.findViewById(R.id.iv_report_rec_detail)
    val rl_story_detail_profile_rec: RelativeLayout =
        itemView.findViewById(R.id.rl_story_detail_profile_rec)
    val iv_story_detail_rec: ImageView = itemView.findViewById(R.id.iv_story_detail_rec)
    val iv_user_story_detail_rec: ImageView = itemView.findViewById(R.id.iv_user_story_detail_rec)
    val tv_sub_titel_story_detail_rec: TextView =
        itemView.findViewById(R.id.tv_sub_titel_story_detail_rec)
    val tv_titel_story_detail_rec: TextView = itemView.findViewById(R.id.tv_titel_story_detail_rec)
    val tv_detail_story_detail_rec: TextView =
        itemView.findViewById(R.id.tv_detail_story_detail_rec)
    val tv_creator_story_detail_rec: TextView =
        itemView.findViewById(R.id.tv_creator_story_detail_rec)

}