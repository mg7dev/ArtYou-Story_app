package com.dw.artyou.recyclerAdapters

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.R
import com.dw.artyou.database.story
import com.dw.artyou.fragments.FragmentObjectDetail
import com.dw.artyou.fragments.FragmentStoryDeatil
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.models.Storye
import java.util.logging.Handler


class StoryListOfflineAdapter(var context: Context, var list: ArrayList<story>) :
    RecyclerView.Adapter<OfflineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineViewHolder {

        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recyler_story_list, parent, false)
        return OfflineViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfflineViewHolder, position: Int) {

        Log.d("fhgwehf", "..."+list[position].cover)
        Glide.with(context!!).load(list[position].cover)
            .placeholder(R.drawable.demo_img).into(holder.iv_rec_story_list)

        holder.tv_rec_story_list_description.text = list[position].description
        holder.tv_rec_story_list_title.text =
            list[position].title
        holder.tv_rec_story_list_sub_title.text = list[position].segment

        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val parms = LinearLayout.LayoutParams(width, width)
        holder.iv_rec_story_list.layoutParams = parms

        Glide.with(context!!).load(list[position].profile)
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.card_image)
            .into(holder.iv_rec_story_list_profile)

        holder.itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        })

        holder.iv_share_story_rec.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT)
                .show()
        })

        holder.tl_rec_story_list_profile.setOnClickListener(View.OnClickListener { })
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class OfflineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iv_share_story_rec: ImageView = itemView.findViewById(R.id.iv_share_story_rec)
    val tl_rec_story_list_profile: RelativeLayout =
        itemView.findViewById(R.id.tl_rec_story_list_profile)
    val iv_rec_story_list: ImageView = itemView.findViewById(R.id.iv_rec_story_list)
    val tv_rec_story_list_sub_title: TextView =
        itemView.findViewById(R.id.tv_rec_story_list_sub_title);
    val tv_rec_story_list_description: TextView =
        itemView.findViewById(R.id.tv_rec_story_list_description)
    val tv_rec_story_list_title: TextView = itemView.findViewById(R.id.tv_rec_story_list_title)
    val iv_rec_story_list_profile: ImageView = itemView.findViewById(R.id.iv_rec_story_list_profile)

}