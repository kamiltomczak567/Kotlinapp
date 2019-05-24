package kamil.restau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_keyboard.*
import kamil.restau.databinding.FragmentKeyboardBinding


class KeyboardFragment : Fragment() {
    var price = "0"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentKeyboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_keyboard, container, false)
//        binding.price = "0"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        num0Button.setOnClickListener { priceAdd(0) }
        num1Button.setOnClickListener { priceAdd(1) }
        num2Button.setOnClickListener { priceAdd(2) }
        num3Button.setOnClickListener { priceAdd(3) }
        num4Button.setOnClickListener { priceAdd(4) }
        num5Button.setOnClickListener { priceAdd(5) }
        num6Button.setOnClickListener { priceAdd(6) }
        num7Button.setOnClickListener { priceAdd(7) }
        num8Button.setOnClickListener { priceAdd(8) }
        num9Button.setOnClickListener { priceAdd(9) }
        deleteButton.setOnClickListener { priceRemove() }
        okButton.setOnClickListener {
            if (price.isNotEmpty() && price!="0")
                (activity as MainActivity).addFragment( QRCodeFragment( "USER|" + app.user.id + "|" +price, price) )
        }

        priceText.text = price + " zł"
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.toolbar.title = "Podaj kwotę rachunku"
            it.toolbar.subtitle = ""
//        activity.navigationView.setCheckedItem(R.id.nav_settings)
        }
    }

    private fun priceAdd(num: Int) {
        if (price.length<5) {
            if (price=="0") price = ""
            price += num
            priceText.text = price + " zł"
        }
    }

    private fun priceRemove() {
        if (price.isNotEmpty()) {
            price = price.substring(0, price.length-1)
            if (price.isEmpty()) price = "0"
            priceText.text = price + " zł"
        }
    }
}
