package kamil.restau

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_order_show.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ValidFragment")
class OrderShowFragment(val order: Order) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val binding: FragmentOrderShowBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_show, container, false)
//        return binding.root
        return inflater.inflate(R.layout.fragment_order_show, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (!app.userManager.Auth()) {
            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
            (App.activity as? MainActivity)?.addFragment(LoginFragment())
            return
        }

        activity?.let {
            it.toolbar.title = "Zamówienie"
            it.navigationView.setCheckedItem(R.id.nav_orders)
        }

        generateButton.setOnClickListener { if (order.pointsAvailable) generateQR() }

        changeQrVisibity()

        order.refreshFullData()

        refreshStatus()

        dateText.text = SimpleDateFormat("dd.MM.yyyy").format(Date(order.createdAt))
        timeText.text = SimpleDateFormat("HH:mm:ss").format(Date(order.createdAt))

        productsCountText.text = order.productsQuantity

        if (order.totalPrice.isNotEmpty() && order.totalPrice!="0")
            priceText.text = String.format("%.2f", order.totalPrice.toFloat()) + " zł"
        else
            priceText.visibility = View.GONE

        if (order.totalPkt.isNotEmpty() && order.totalPkt!="0")
            pricePktText.text = order.totalPkt + " PKT"
        else
            pricePktText.visibility = View.GONE

        var productsList = ""
        order.products.forEach {
            productsList += it.quantity + "x "
            productsList += it.product.name
            if (it.product.specialProduct.isNotEmpty()) {
                when (it.product.specialProduct) {
                    "half" -> {
                        productsList += " - "
                        it.special.forEachIndexed { index, special ->
                            if (index!=0) productsList += "/"
                            val name = special.product?.name
                            productsList += name
                        }
                    }
                }
            }
            if (it.additions.isNotEmpty()) productsList += " + dodatki"
            if (it.totalPrice.isNotEmpty() && it.totalPrice.toFloat()!=0.0f) productsList += " (" + String.format("%.2f", it.totalPrice.toFloat()) + " zł)"
            if (it.totalPkt.isNotEmpty() && it.totalPkt.toInt()!=0) productsList += " (" + it.totalPkt.toInt() + " PKT)"
            if (it!=order.products.last()) productsList += "\n"
        }
        productsText.text = productsList

        if (order.delivery.firstName.isNotEmpty())
            firstNameText.text = "Imię: " + order.delivery.firstName
        else
            firstNameText.visibility = View.GONE

        if (order.delivery.lastName.isNotEmpty())
            lastNametext.text = "Nazwisko: " + order.delivery.lastName
        else
            lastNametext.visibility = View.GONE

        if (order.delivery.city.isNotEmpty())
            cityText.text = "Miejscowość: " + order.delivery.city
        else
            cityText.visibility = View.GONE

        if (order.delivery.city.isNotEmpty())
            addressText.text = "Adres: " + order.delivery.address
        else
            addressText.visibility = View.GONE

        if (order.delivery.price.isNotEmpty() && order.delivery.price!="0")
            deliveryCostText.text = "Cena dostawy: " + String.format("%.2f zł", order.delivery.price.toFloat())
        else
            deliveryCostText.visibility = View.GONE

        if (order.discount.discount.toFloat()!=0f) {
            discountText.text = String.format("%.2f zł", order.discount.discount.toFloat())
            discountsLayout.visibility = View.VISIBLE

        } else {
            discountsLayout.visibility = View.GONE
        }

        if (order.comment.isNotEmpty())
            msgText.text = order.comment
        else
            msgBox.visibility = View.GONE
    }

    private fun changeQrVisibity() {
        if (order.pointsAvailable) {
            generateButton.visibility = View.VISIBLE
            generateButton.isEnabled = true
        } else {
            generateButton.visibility = View.GONE
            generateButton.isEnabled = false
        }
    }

    private fun refreshStatus() {
        val status = App.getStatusTranslation(order.status)
//        when(order.status) {
//            "new" -> status = "Nowe"
//            "accepted" -> status = "Zaakceptowane"
//            "completed" -> status = "Zakończone"
//        }

        activity?.toolbar?.subtitle = status
        statusText?.text = status
    }


    override fun refreshOrders() {
        refreshStatus()
    }

    private fun generateQR() {
        (activity as? MainActivity)?.addFragment( QRCodeFragment( "ORDER|"+order.id ) )
    }
}
