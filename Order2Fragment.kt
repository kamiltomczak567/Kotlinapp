package kamil.restau

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_order2.*
import kamil.restau.databinding.FragmentOrder2Binding

class Order2Fragment : Fragment() {

    private val cityForm = Dialog(App.activity)
    private var city: DeliveryWhiteList? = null
    private var freeDelivery = false
    private var pointsBuy = false
    private var userType = app.cart.delivery.userType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentOrder2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order2, container, false)
        binding.cart = app.cart
        userType = app.cart.delivery.userType
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Zamówienie"
            it.toolbar.subtitle = "Dostawa"
            it.navigationView.setCheckedItem(R.id.nav_cart)
        }

        refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        payTypeButtons.visibility = View.GONE

        pointsBuy = false
        cashButton.setBackgroundResource(R.drawable.button)
        pointsButton.setBackgroundColor(Color.TRANSPARENT)

        cashButton.setOnClickListener {
            pointsBuy = false
            cashButton.setBackgroundResource(R.drawable.button)
            pointsButton.setBackgroundColor(Color.TRANSPARENT)
            refresh()
        }
        pointsButton.setOnClickListener {
            pointsBuy = true
            pointsButton.setBackgroundResource(R.drawable.button)
            cashButton.setBackgroundColor(Color.TRANSPARENT)
            refresh()
        }

        nextButton.setOnClickListener { next() }

        addressInput.setText(app.user.address)

        cityInput.setOnClickListener { cityForm.show() }
        cityInput.setOnFocusChangeListener { _, b -> if (b) cityForm.show() }
        cityInput.keyListener = null

        cityForm.setContentView(R.layout.modal_city)
        cityForm.setTitle("Dostawa")

        val pinned = mutableListOf<String>()
        app.deliveryWhiteList.forEach { if (it.pinned) pinned.add(it.name) }
        pinned.sort()
        val unpinned = mutableListOf<String>()
        app.deliveryWhiteList.forEach { if (!it.pinned) unpinned.add(it.name) }
        unpinned.sort()
        val cityArray = mutableListOf<String>()
        cityArray.addAll(pinned)
        cityArray.addAll(unpinned)

        val cityList: ListView = cityForm.findViewById(R.id.cityList)
        cityList.adapter = ArrayAdapter<String>(
                App.instance,
                R.layout.simple_list_item,
                cityArray)

        cityList.setOnItemClickListener { _, _, i, _ ->
            app.deliveryWhiteList.forEach { item ->
                if (item.name == cityArray[i]) {
                    city = item
                    cityInput.setText(item.name)
                    refresh()
                }
            }
            cityForm.dismiss()
        }

        if (city==null && app.user.city.isNotEmpty()) {
            app.deliveryWhiteList.forEachIndexed { index, item ->
                if (item.name == app.user.city) {
                    cityList.setSelection(index)
                    city = item
                    cityInput.setText(item.name)
                    refresh()
                }
            }
        }
        refresh()
    }

    private fun refresh() {
        app.cart.delivery = Delivery()
        app.cart.refreshProductsData()
        city?.let {
            freeDelivery = it.freePrice.isNotEmpty() && it.freePrice == "0"

            if (freeDelivery) {
                payTypeButtons.visibility = View.GONE
                deliveryPriceText.text = "Darmowa dostawa\n"
            } else {
                payTypeButtons.visibility = View.VISIBLE
                val deliveryText = if (pointsBuy) {
                    String.format("%.0f PKT", it.pkt.toFloat())
                } else {
                    String.format("%.2f zł", it.price.toFloat())
                }
                var text = String.format("Cena dostawy: %s", deliveryText)
                if (it.freePrice.isNotEmpty() && it.freePrice != "-1")
                    text += String.format("\nDarmowa dostawa dla zamówienia powyżej: %.2f zł", it.freePrice.toFloat())
                deliveryPriceText.text = text
            }

            app.cart.delivery.city = city?.name ?: ""
            app.cart.delivery.address = addressInput.text.toString()

            if (freeDelivery) {
                app.cart.delivery.price = "0"
                app.cart.delivery.pkt = "0"
            } else {
                if (pointsBuy) {
                    app.cart.delivery.pointsBuy = true
                    app.cart.delivery.price = "0"
                    app.cart.delivery.pkt = city?.pkt ?: ""
                } else {
                    app.cart.delivery.pointsBuy = false
                    app.cart.delivery.price = city?.price ?: ""
                    app.cart.delivery.pkt = "0"
                }
            }
            app.cart.refreshProductsData()
        }
    }

    private fun next() {
        refresh()

        city?.let {
            val totalPkt = app.cart.totalPkt.toInt() + it.pkt.toInt()
            if (pointsBuy && totalPkt > app.user.totalPkt.toInt()) {
                activity?.let {
                    AlertDialog.Builder(it)
                            .setIcon(R.drawable.ic_stars)
                            .setTitle("Brak wystarczającej ilości punktów!")
                            .setMessage("Nie masz aktualnie wystarczającej ilości punktów, aby zamówić dostawę za punkty.")
                            .setPositiveButton("Ok", null)
                            .show()
                    return
                }
            }
        }

        val errors = mutableListOf<String>()
        if (cityInput.text.isEmpty() || city == null) errors.add("Wybierz miejscowość dostawy!")
        if (city?.addressRequired != false && addressInput.text.isEmpty()) errors.add("Podaj adres dostawy!")

        if (errors.isEmpty()) {
            app.cart.delivery.userType = userType
//            (activity as? MainActivity)?.addFragment(Order3Fragment())
            (activity as? MainActivity)?.addFragment(Order25Fragment())
        } else {
            var error = ""
            errors.forEachIndexed { index, it ->
                if (index != 0) error += "\n"
                error += "- " + it
            }
            activity?.let {
                AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Brak wymaganych danych!")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show()
            }
        }
    }
}
