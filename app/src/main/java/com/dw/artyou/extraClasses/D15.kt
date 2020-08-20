package com.dw.artyou.extraClasses


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dw.artyou.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.fragment_d15.*


/**
 * A simple [Fragment] subclass.
 */
class D15 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d15, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_dummy.setImageBitmap(textToImage("Hello World", 2000, 2000))

    }

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
