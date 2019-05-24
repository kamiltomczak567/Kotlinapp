package kamil.restau

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_order1.*
import kamil.restau.databinding.FragmentOrder1Binding

class OrderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentOrder1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order1, container, false)
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

        nextButton.setOnClickListener { next() }

        firstNameInput.setText(app.user.firstName)
        lastNameInput.setText(app.user.lastName)
        phoneInput.setText(app.user.phone)

        app.cart.delivery = Delivery()
        app.cart.refreshProductsData()
    }

    private fun next() {
        val errors = mutableListOf<String>()
        if (firstNameInput.text.isEmpty()) errors.add("Podaj swoje imię!")
        if (lastNameInput.text.isEmpty()) errors.add("Podaj swoje nazwisko!")
        if (phoneInput.text.isEmpty()) errors.add("Podaj telefon kontaktowy!")


        if (errors.isEmpty()) {
            app.cart.delivery.firstName = firstNameInput.text.toString()
            app.cart.delivery.lastName = lastNameInput.text.toString()
            app.cart.delivery.phone = phoneInput.text.toString()

            app.cart.delivery.userType = ""
            if (studentCheckBox.isChecked) app.cart.delivery.userType = "student"

            (activity as? MainActivity)?.addFragment(Order2Fragment())
        } else {
            var error = ""
            errors.forEachIndexed { index, it ->
                if (index!=0) error += "\n"
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
