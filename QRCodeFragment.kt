package kamil.restau

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_qrcode.*
import com.google.zxing.WriterException
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_main.*
import kamil.restau.databinding.FragmentQrcodeBinding

class QRCodeFragment() : Fragment() {
    var code: String = ""

    var price: String = ""

    constructor(code: String): this() {
        this.code = code
    }

    constructor(code: String, price: String ): this() {
        this.code = code
        this.price = price
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentQrcodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_qrcode, container, false)
        binding.user = app.user
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Kod QR"
            it.toolbar.subtitle = "Posiadasz " + app.user.totalPkt + " PKT"
        }
        val qrCodeMsg = getString(R.string.qr_code_msg)
        val codeEncrypted = App.base64encode(code)
        val qrCode = qrCodeMsg + "|" + codeEncrypted

        val bitmap = generateQR(qrCode)
        qrCodeImage.setImageBitmap(bitmap)

        if (price.isEmpty()) {
//            qrPriceText.visibility = View.GONE
//            qrPriceText2.visibility = View.GONE
            qrPriceText.text = "ZAMÓWIENIE"
            qrPriceText2.text = "ZAMÓWIENIE"
        } else {
            qrPriceText.text = price + " zł"
            qrPriceText2.text = price + " zł"
        }
    }

    private fun generateQR(str: String): Bitmap? {
        var bitmap: Bitmap? = null
        val width = 256
        val height = 256

        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, width, height)
//            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
//                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.argb(255,242,230,94) else Color.argb(0, 0, 0, 0))
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return bitmap
    }
}
