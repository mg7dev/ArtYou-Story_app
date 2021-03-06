package com.dw.artyou.recyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.dw.artyou.interfaces.ObjectTypeInterface
import com.dw.artyou.R
import com.dw.artyou.models.ObjectTypeModel

class ObjectTypeAdapter(
    var context: Context,
    var list: ArrayList<ObjectTypeModel>,
    var objectTypeInterface: ObjectTypeInterface
) :
    RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_object_type_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tv_rec_type_object.text = list[position].name
        holder.itemView.setOnClickListener(View.OnClickListener {
            objectTypeInterface.onItemClick(list[position])
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_rec_type_object: TextView = itemView.findViewById(R.id.tv_rec_type_object)

}