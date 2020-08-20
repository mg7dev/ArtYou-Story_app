package com.dw.artyou.recyclerAdapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dw.artyou.R
import com.dw.artyou.models.ObjectTypeModel

class ObjectDetailAdapter(
    var context: Context,
    var list: ArrayList<ObjectTypeModel>
) :
    RecyclerView.Adapter<ObjectDetailHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectDetailHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_object_detail, parent, false)
        return ObjectDetailHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectDetailHolder, position: Int) {
        if (list[position].name != null) {
            holder.tv_name_rec_object_detail.text = list[position].id
            holder.tv_value_rec_object_detail.text = list[position].name
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        Log.d("2euideu", list.size.toString())
        return list.size
    }
}

class ObjectDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_name_rec_object_detail: TextView = itemView.findViewById(R.id.tv_name_rec_object_detail)
    val tv_value_rec_object_detail: TextView =
        itemView.findViewById(R.id.tv_value_rec_object_detail)
}