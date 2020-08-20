package com.dw.artyou.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dw.artyou.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_experience.*

class BottomSheetFragment(val fragmentCreateStory: FragmentCreateStory) : Fragment() {
    private var image = "IMAGE"
    private var text = "TEXT"
    private var video = "VIDEO"
    private var audio = "AUDIO"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_experience, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_bottom_sheet.setOnClickListener(View.OnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        })
        tv_text_bottom_story.setOnClickListener(clickEvents(text))
        tv_image_bottom_story.setOnClickListener(clickEvents(image))
        tv_video_bottom_story.setOnClickListener(clickEvents(video))
        tv_audio_bottom_story.setOnClickListener(clickEvents(audio))
    }

    fun clickEvents(type: String): View.OnClickListener {
        return View.OnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            fragmentCreateStory.showDialog()
            fragmentCreateStory.popupViewOnClick(type)
        }

    }

}
