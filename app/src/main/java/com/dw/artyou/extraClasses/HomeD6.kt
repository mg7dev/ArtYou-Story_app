package com.dw.artyou.extraClasses


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.custom_bottom_bar.*
import kotlinx.android.synthetic.main.fragment_home_d6.*
import android.media.MediaPlayer
import android.widget.SeekBar
import android.media.AudioManager
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.Context
import android.widget.Toast
import com.dw.artyou.fragments.FragmentCreateObject


class HomeD6 : Fragment() {
    var mediaPlayer: MediaPlayer? = null
    var seekbar_home_d6:SeekBar? = null


    private var audioManager: AudioManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.dw.artyou.R.layout.fragment_home_d6, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekbar_home_d6 = view.findViewById(com.dw.artyou.R.id.seekbar_home_d6)

        //mediaPlayer = MediaPlayer.create(context, com.dw.artyou.R.raw.demo)

        btn_complete_history.setOnClickListener{
            activity!!.supportFragmentManager.beginTransaction().add(com.dw.artyou.R.id.frame_main,
                FragmentCreateObject()
            ).addToBackStack(null).commit()
        }

        iv_home_d6_menu.setOnClickListener{
            Toast.makeText(context,"Under Development !",Toast.LENGTH_SHORT).show()
        }

        iv_home_d6_play.setOnClickListener{
            mediaPlayer!!.start()

            iv_home_d6_play.visibility = View.GONE
            iv_home_d6_pause.visibility = View.VISIBLE
        }

        iv_home_d6_pause.setOnClickListener{
           mediaPlayer!!.pause()

            iv_home_d6_play.visibility = View.VISIBLE
            iv_home_d6_pause.visibility = View.GONE
        }

    initControls()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer!!.stop()
        mediaPlayer!!.release()

    }


    private fun initControls() {
        try {
            audioManager = activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            seekbar_home_d6!!.setMax(
                audioManager!!
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            )
            seekbar_home_d6!!.setProgress(
                audioManager!!
                    .getStreamVolume(AudioManager.STREAM_MUSIC)
            )


            seekbar_home_d6!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(arg0: SeekBar) {}

                override fun onStartTrackingTouch(arg0: SeekBar) {}

                override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                    audioManager!!.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, 0
                    )
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
