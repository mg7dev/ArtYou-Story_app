package com.dw.artyou.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dw.artyou.R
import com.dw.artyou.activities.MainActivity
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.fragment_thank_you.*

class FragmentThankYou(val bitmap: Bitmap?, val storyId: String) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thank_you, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("wefghwe", storyId)
        tv_thnku_home_next.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activity!!.finishAffinity()
            }
        })

        iv_thanku_get_link.setOnClickListener(View.OnClickListener {
            showQrDialog(false)
        })

        iv_thanku_qr.setOnClickListener(View.OnClickListener {
            showQrDialog(true)
        })

        if (bitmap != null) {
            clickEvents(bitmap, HelperMethods.generateStoryLink(storyId))
        }
    }

    fun showQrDialog(isQr: Boolean) {
        val builder = AlertDialog.Builder(context)
        val v = layoutInflater.inflate(R.layout.popup_qr_code, null)
        val iv_qr_popup: ImageView = v.findViewById(R.id.iv_qr_popup)
        val btn_cancel_qr_popup: Button = v.findViewById(R.id.btn_cancel_qr_popup)
        val btn_share_qr_popup: Button = v.findViewById(R.id.btn_share_qr_popup)
        val tv_qr_popup: TextView = v.findViewById(R.id.tv_qr_popup)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()
        val link = HelperMethods.generateStoryLink(storyId)

        if (isQr) {
            iv_qr_popup.visibility = View.VISIBLE
            iv_qr_popup.setImageBitmap(textToImage(link, 2000, 2000))
        } else {
            tv_qr_popup.visibility = View.VISIBLE
            tv_qr_popup.setText(link)
        }

        btn_share_qr_popup.setOnClickListener(View.OnClickListener {
            if (tv_qr_popup.visibility == View.VISIBLE) {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, "Stories from Art You Global")
                share.putExtra(Intent.EXTRA_TEXT, link)

                startActivity(Intent.createChooser(share, "Art You Global"))
            } else {
                val share = Intent(Intent.ACTION_SEND)
                share.putExtra(Intent.EXTRA_SUBJECT, "Stories from Art You Global")
                share.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            context!!.getContentResolver(),
                            textToImage(
                                link,
                                2000,
                                2000
                            ),
                            "From Art You",
                            "Story of art you"
                        )
                    )
                )
                share.setType("*/*")

                startActivity(Intent.createChooser(share, "Art You Global"))
            }
        })
        btn_cancel_qr_popup.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }

    fun clickEvents(bitmap: Bitmap, link: String) {
        iv_thanku_fb.setOnClickListener(View.OnClickListener {
            shareImage("com.facebook.katana", bitmap, link)
        })

        iv_thanku_insta.setOnClickListener(View.OnClickListener {
            shareImage("com.instagram.android", bitmap, link)
        })

        iv_thanku_whatsapp.setOnClickListener(View.OnClickListener {
            shareImage("com.whatsapp", bitmap, link)
        })

        iv_thanku_twitter.setOnClickListener(View.OnClickListener {
            shareImage("com.twitter.android", bitmap, link)
        })
    }

    //===================Share Post to Social Media=================//
    fun shareImage(packageName: String, bitmap: Bitmap, link: String) {
        var intent: Intent? = context!!.getPackageManager().getLaunchIntentForPackage(packageName)
        if (intent != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage(packageName)
            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(
                    MediaStore.Images.Media.insertImage(
                        context!!.getContentResolver(),
                        bitmap,
                        "I am Happy",

                        "Share happy !"
                    )
                )
            )
            shareIntent.setType("*/*")
            startActivity(shareIntent)
        } else {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$packageName")
            startActivity(intent)
        }
    }


    //===================Create QR code=====================//
    @Throws(WriterException::class, NullPointerException::class)
    private fun textToImage(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix
        bitMatrix = try {
            MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE,
                width, height, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap =
            Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}
