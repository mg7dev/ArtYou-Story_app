package com.dw.artyou.recyclerAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dw.artyou.interfaces.CollectionRecyclerClick
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.models.ObjectCollectModel

class ChooseCollectionAdapter(
    var context: Context,
    var list: ArrayList<ObjectCollectModel>,
    var collectionRecyclerClick: CollectionRecyclerClick
) :
    RecyclerView.Adapter<ChooseObjectHolder>() {

    var selectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseObjectHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_choose_object, parent, false)
        return ChooseObjectHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseObjectHolder, position: Int) {
        holder.rb_rec_choose_collection.setOnClickListener(View.OnClickListener {

            var isChecked = holder.rb_rec_choose_collection.isChecked

            collectionRecyclerClick.onItemClick(position,isChecked)
//            notifyDataSetChanged()
//            select object event    //
        })
        Log.d("wfjgwe", list[position].cover)
        Glide.with(context).load(Api.getImage(list[position].cover))
            .placeholder(R.drawable.demo_img).into(holder.iv_rec_choose_collection)

        holder.tv_rec_name_choose_collection.text = list[position].name
        holder.tv_rec_type_choose_collection.text = list[position].type
        holder.rb_rec_choose_collection.isChecked = selectPosition == position
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ChooseObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rb_rec_choose_collection: CheckBox =
        itemView.findViewById(R.id.cb_rec_choose_collection)
    val tv_rec_type_choose_collection: TextView =
        itemView.findViewById(R.id.tv_rec_type_choose_collection)
    val tv_rec_name_choose_collection: TextView =
        itemView.findViewById(R.id.tv_rec_name_choose_collection)
    val iv_rec_choose_collection: ImageView = itemView.findViewById(R.id.iv_rec_choose_collection)
}