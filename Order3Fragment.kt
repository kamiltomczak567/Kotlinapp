package kamil.restau

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_order3.*
import kamil.restau.databinding.FragmentOrder3Binding

class Order3Fragment : Fragment() {
    var payMethod = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentOrder3Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order3, container, false)
        binding.cart = app.cart
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Zamówienie"
            it.toolbar.subtitle = "Dostawa"
            it.navigationView.setCheckedItem(R.id.nav_cart)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton.setOnClickListener { send() }

        payMethod = 1
        cashButton.setBackgroundResource(R.drawable.button)
        cardButton.setBackgroundColor(Color.TRANSPARENT)

        cashButton.setOnClickListener {
            payMethod = 1
            cashButton.setBackgroundResource(R.drawable.button)
            cardButton.setBackgroundColor(Color.TRANSPARENT)
        }
        cardButton.setOnClickListener {
            payMethod = 2
            cardButton.setBackgroundResource(R.drawable.button)
            cashButton.setBackgroundColor(Color.TRANSPARENT)
        }

        var text = "Cena zamówienia:"
        if (app.cart.totalPrice!="0") {
            text += String.format(" %.2f zł", app.cart.totalPrice.toFloat())
            if (app.cart.totalPkt!="0") text += " +"
        }
        if (app.cart.totalPkt!="0") text += String.format(" %s PKT", app.cart.totalPkt)
        priceText.text = text


        app.cart.refreshFullData()
        app.deliveryWhiteList.firstOrNull { it.name == app.user.city }.let {
            if ( it?.freePrice!=null && it.freePrice.isNotEmpty() && it.freePrice != "-1" && app.cart.totalPrice.toFloat() >= it.freePrice.toFloat() ) {
                app.cart.delivery.price = "0"
                app.cart.delivery.pkt = "0"
                app.cart.refreshFullData()
            }
        }

//        App.Dump(app.cart, TAG)


        text = "Koszt dostawy:"
        text += if (app.cart.delivery.price=="0" && app.cart.delivery.pkt=="0") {
            " Darmowa dostawa"
        } else {
            if (app.cart.delivery.pointsBuy) {
                String.format(" %s PKT", app.cart.delivery.pkt)
            } else {
                String.format(" %.2f zł", app.cart.delivery.price.toFloat())
            }
        }
        deliveryText.text = text

        val totalPrice = app.cart.totalPrice.toFloat()
        val totalPkt = app.cart.totalPkt.toFloat()

        if (app.cart.discount.promotions.isNotEmpty() && app.cart.discount.discount.toFloat()!=0f) {
            promotionsText.text = String.format("Zniżka: %.2f zł", app.cart.discount.discount.toFloat())
            promotionsLayout.visibility = View.VISIBLE

            promotionsButton.setOnClickListener {
                showDiscountsModal()
            }
        } else {
            promotionsLayout.visibility = View.GONE
        }

        text = "Łączny koszt:"
        if (totalPrice!=0f) {
            text += String.format(" %.2f zł", totalPrice)
            if (totalPkt!=0f)text += " +"
        }
        if (totalPkt!=0f) text += String.format(" %.0f PKT", totalPkt)
        totalText.text = text
    }

    private fun send() {
        val totalPkt = app.cart.totalPkt.toInt() + app.cart.delivery.pkt.toInt()
        if (totalPkt > app.user.totalPkt.toInt()) {
            activity?.let {
                AlertDialog.Builder(it)
                        .setIcon(R.drawable.ic_stars)
                        .setTitle("Brak wystarczającej ilości punktów!")
                        .setMessage("Nie masz aktualnie wystarczającej ilości punktów, aby złożyć zamówienie.")
                        .setPositiveButton("Ok", null)
                        .show()
            }
            return
        }
        activity?.let {
            AlertDialog.Builder(it)
                    .setIcon(android.R.drawable.ic_menu_send)
                    .setTitle("Potwierdzenie")
                    .setMessage("Złożyć zamówienie?")
                    .setPositiveButton("Tak") { _, _ ->
                        app.cart.payMethod = payMethod
                        app.cart.comment = messageInput.text.toString()
                        app.cart.sendOrder()
                        orderSendSuccess()
                    }
                    .setNegativeButton("Nie", null)
                    .show()
        }
    }

    private fun orderSendSuccess() {
        app.cart.clearCart()
        (App.activity as? MainActivity)?.changeFragment(OrdersFragment())

        activity?.let {
            AlertDialog.Builder(it)
                    .setIcon(android.R.drawable.ic_menu_send)
                    .setTitle("Wysłano zamówienie")
                    .setMessage("Twoje zamówienie zostało złożone")
                    .setNegativeButton("OK", null)
                    .show()
        }
    }

    private fun showDiscountsModal() {
        val dialog = Dialog(App.activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.modal_text)

        val displayRectangle = Rect()
        App.activity?.window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
        dialog.window.setLayout( (displayRectangle.width() * 0.95f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        val titleText: TextView = dialog.findViewById(R.id.titleText)
        titleText.text = "ZAWARTE PROMOCJE"

        val promotionsText: TextView = dialog.findViewById(R.id.contentText)
        var text = ""

        app.cart.discount.promotions.forEachIndexed { index, item ->
            if(index!=0) text += "\n\n"
            text += item.name + String.format("\n-%.2f zł", item.discount.toFloat())
        }

        promotionsText.text = text

        dialog.show()
    }

}
