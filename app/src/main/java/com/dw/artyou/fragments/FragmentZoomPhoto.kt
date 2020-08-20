package com.dw.artyou.fragments


import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import kotlinx.android.synthetic.main.fragment_fragment_zoom_photo.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentZoomPhoto(var imageUrl: String?, var bitmap:Bitmap?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_zoom_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(imageUrl!=null){
            Glide.with(activity!!).load(Api.getImage(imageUrl)).placeholder(R.drawable.demo_img)
                .into(iv_zoom_photo)
        }else{
            Glide.with(activity!!).load(bitmap).placeholder(R.drawable.demo_img)
                .into(iv_zoom_photo)
        }

    }
}
