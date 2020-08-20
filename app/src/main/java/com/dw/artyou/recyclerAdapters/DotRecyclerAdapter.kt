package com.dw.artyou.recyclerAdapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dw.artyou.R
import com.dw.artyou.models.ObjectTypeModel

class DotRecyclerAdapter(
    var context: Context,
    var count: Int,
    var selected: Int
) :
    RecyclerView.Adapter<DotHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_dot_layout, parent, false)
        return DotHolder(view)
    }

    override fun onBindViewHolder(holder: DotHolder, position: Int) {
        if (selected == position) {
            holder.ll_dot_recycler.setBackgroundResource(R.drawable.dot_selected)
        } else {
            holder.ll_dot_recycler.setBackgroundResource(R.drawable.dot_not_selected)
        }
    }

    override fun getItemCount(): Int {
        return count
    }
}

class DotHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var ll_dot_recycler: LinearLayout = itemView.findViewById(R.id.ll_dot_recycler)
}