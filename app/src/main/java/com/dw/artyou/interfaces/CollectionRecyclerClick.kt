package com.dw.artyou.interfaces

import com.dw.artyou.models.ObjectCollectModel

interface CollectionRecyclerClick {

    fun onItemClick(position:Int,isChecked:Boolean)
}