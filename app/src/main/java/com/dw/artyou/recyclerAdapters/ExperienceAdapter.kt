package com.dw.artyou.recyclerAdapters


import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dw.artyou.R
import com.dw.artyou.helper.Api
import com.dw.artyou.models.Experiences


class ExperienceAdapter(
    var context: Context,
    var list: ArrayList<Experiences>
) :
    RecyclerView.Adapter<ExperienceHolder>() {

    private var audioManager: AudioManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recycler_experience, parent, false)
        return ExperienceHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceHolder, position: Int) {
        val model = list[position]
        if (model.created_at.length > 10) {
            holder.tv_date_rec_exp.text = model.created_at.substring(0, 10)
        } else {
            holder.tv_date_rec_exp.text = model.created_at
        }

        Log.d("ewef", model.date)
        when {
            model.type.equals("IMAGE", true) -> {

                holder.iv_rec_exp.visibility = View.VISIBLE
                Glide.with(context as AppCompatActivity).load(Api.getImage(model.content))
                    .placeholder(R.drawable.demo_img)
                    .into(holder.iv_rec_exp)
            }
            model.type.equals("TEXT", true) -> {
                holder.tv_text_rec_exp.visibility = View.VISIBLE
                holder.tv_text_rec_exp.text = model.content
            }
            model.type.equals("AUDIO", true) -> {
                lateinit var mediaPlayer: MediaPlayer
                holder.rl_rec_exp.visibility = View.VISIBLE
                mediaPlayer = MediaPlayer()
                try {
                    Log.d("yufgey", model.content)
                    mediaPlayer.setDataSource(Api.getImage(model.content))
                    mediaPlayer.prepare()
//                    mediaPlayer.start()
                    holder.iv_play_exp.setOnClickListener(View.OnClickListener {
                        Log.d("fgeu", "PLay button")
                        initControls()
                        mediaPlayer.start()
                        holder.iv_play_exp.visibility = View.GONE
                        holder.iv_pause_exp.visibility = View.VISIBLE

                    })
                    holder.iv_pause_exp.setOnClickListener(View.OnClickListener {
                        Log.d("fgeu", "Pause button")
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            holder.iv_play_exp.visibility = View.VISIBLE
                            holder.iv_pause_exp.visibility = View.GONE
                        }

                    })
                    holder.sb_rec_exp.max = mediaPlayer.duration
                    val mHandler = Handler()
                    (context as AppCompatActivity).runOnUiThread(object : Runnable {
                        override fun run() {
                            if (mediaPlayer != null) {
                                val mCurrentPosition: Int = mediaPlayer.currentPosition
                                holder.sb_rec_exp.setProgress(mCurrentPosition)
                            }
                            mHandler.postDelayed(this, 1000)
                        }
                    })

                    holder.sb_rec_exp.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            if (mediaPlayer != null && fromUser) {
                                mediaPlayer.seekTo(progress)
                            }
                        }
                    })
                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(Api.getImage(model.content));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("yudfqeyf", e.toString())
                }
            }
            model.type.equals("VIDEO", true) -> {
                var str = model.content
                Log.d("crcr3", str.substring(str.length - 11, str.length))
                holder.wv_rec_exp.visibility = View.VISIBLE
                val ws: WebSettings = holder.wv_rec_exp.getSettings()
                ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                ws.pluginState = WebSettings.PluginState.ON
                ws.javaScriptEnabled = true
                holder.wv_rec_exp.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
                holder.wv_rec_exp.reload()

                holder.wv_rec_exp.loadData(
                    getHTML(str.substring(str.length - 11, str.length)),
                    "text/html",
                    "UTF-8"
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    private fun initControls() {
        try {
            audioManager =
                (context as AppCompatActivity).getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getHTML(videoId: String): String? {
        // LogShowHide.LogShowHideMethod("video-id from html url= ", "" + html);
        return ("<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 96%;"
                + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"http://www.youtube.com/embed/" + videoId
                + "?&theme=dark&autohide=2&modestbranding=1&showinfo=0&autoplay=1\"fs=0\" frameborder=\"0\" "
                + "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n")
    }
}

class ExperienceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iv_rec_exp: ImageView = itemView.findViewById(R.id.iv_rec_exp)
    val tv_text_rec_exp: TextView = itemView.findViewById(R.id.tv_text_rec_exp)
    val wv_rec_exp: WebView = itemView.findViewById(R.id.wv_rec_exp)
    val iv_play_exp: ImageView = itemView.findViewById(R.id.iv_play_exp)
    val rl_rec_exp: RelativeLayout = itemView.findViewById(R.id.rl_rec_exp)
    val iv_pause_exp: ImageView = itemView.findViewById(R.id.iv_pause_exp)
    val sb_rec_exp: SeekBar = itemView.findViewById(R.id.sb_rec_exp)
    val tv_date_rec_exp: TextView = itemView.findViewById(R.id.tv_date_rec_exp)
}