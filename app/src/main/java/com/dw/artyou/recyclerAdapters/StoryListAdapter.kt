package com.dw.artyou.recyclerAdapters

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.R
import com.dw.artyou.fragments.FragmentObjectDetail
import com.dw.artyou.fragments.FragmentStoryDeatil
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.models.Storye


class StoryListAdapter(var context: Context, var list: ArrayList<Storye>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recyler_story_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list[position].objects.size > 0 && list[position].objects[0].`object`.cover != null) {

            Glide.with(context!!).load(Api.getImage(list[position].objects[0].`object`.cover))
                .placeholder(R.drawable.demo_img).into(holder.iv_rec_story_list)
            //holder.iv_rec_story_list.setOnClickListener(clickImageview(list[position].objects[0].`object`.cover))
        } else {
//            list.removeAt(position)
//            notifyDataSetChanged()
            Glide.with(context!!).load(R.drawable.demo_img)
                .placeholder(R.drawable.demo_img).into(holder.iv_rec_story_list)
            //holder.iv_rec_story_list.setOnClickListener(clickImageview(""))
        }

        holder.tv_rec_story_list_description.text = list[position].description
        holder.tv_rec_story_list_title.text =
            list[position].creator.name
        holder.tv_rec_story_list_sub_title.text = list[position].creator.segment

        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val parms = LinearLayout.LayoutParams(width, width)
        holder.iv_rec_story_list.layoutParams = parms

        Glide.with(context!!).load(Api.getImage(list[position].creator.avatar))
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.card_image)
            .into(holder.iv_rec_story_list_profile)

        holder.itemView.setOnClickListener(View.OnClickListener {
            Log.d("wqdohqwd", list[position].objects[0].toString())
            var storyId = list[position].id
            if (list[position].objects.size > 1) {
                Log.d("wfjkhfew", "IF CONDITION");
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .add(R.id.frame_main, FragmentStoryDeatil(storyId)).addToBackStack(null)
                    .commit()
            } else {
                Log.d("wfjkhfew", "ELSE CONDITION");
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .add(
                        R.id.frame_main,
                        FragmentObjectDetail(list[position].objects[0].`object`.id)
                    ).addToBackStack(null).commit()
            }

        })

        holder.iv_share_story_rec.setOnClickListener(View.OnClickListener {
            shareLink(HelperMethods.generateStoryLink(list[position].id))
        })

        holder.tl_rec_story_list_profile.setOnClickListener(View.OnClickListener { })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun shareLink(link: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Stories from Art You Global")
        share.putExtra(Intent.EXTRA_TEXT, link)

        context.startActivity(Intent.createChooser(share, "Share link!"))
    }

//    fun clickImageview(url: String): View.OnClickListener {
//        return View.OnClickListener {
//            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                .add(R.id.frame_main, FragmentZoomPhoto(url, null))
//                .addToBackStack(null).commit()
//        }
//    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
