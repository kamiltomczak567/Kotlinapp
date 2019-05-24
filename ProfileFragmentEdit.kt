package kamil.restau

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kamil.restau.component.Toster
import kamil.restau.databinding.FragmentProfileEditBinding

class ProfileFragmentEdit : Fragment() {

    private val cityForm = Dialog(App.activity)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentProfileEditBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false)
        binding.user = app.user
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        if (!app.userManager.Auth()) {
            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
            (App.activity as? MainActivity)?.addFragment(LoginFragment())
        }

        activity?.let {
            it.toolbar.title = "Panel użytkownika"
            it.toolbar.subtitle = "Edycja danych"
            it.navigationView.setCheckedItem(R.id.nav_settings)
        }

        changeButton.setOnClickListener { changeData() }

        setData()
    }

    private fun changeData() {
        if (app.user.firstName != firstNameInput.text.toString() ||
                app.user.lastName != lastNameInput.text.toString() ||
                app.user.phone != phoneInput.text.toString() ||
                app.user.city != cityInput.text.toString() ||
                app.user.address != addressInput.text.toString()
                ) {
            activity?.let {
                AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_menu_send)
                        .setTitle("Zmiana danych")
                        .setMessage("Czy napewno zmienić dane w profilu?")
                        .setPositiveButton("Tak") { _, _ ->
                            app.user.firstName = firstNameInput.text.toString()
                            app.user.lastName = lastNameInput.text.toString()
                            app.user.phone = phoneInput.text.toString()
                            app.user.city = cityInput.text.toString()
                            app.user.address = addressInput.text.toString()
                            app.database.updateUser(app.user)
                            Toster(context, "Dane zostały zmienione!", Toster.LENGTH_SHORT).show()
                            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
                        }
                        .setNegativeButton("Nie", null)
                        .show()
            }
        } else {
            Toster(activity, "Brak danych do zmiany").show()
        }
    }

    private fun setData() {
        cityInput.setOnClickListener { cityForm.show() }
        cityInput.setOnFocusChangeListener { _, b -> if (b) cityForm.show() }
        cityInput.keyListener = null

        cityForm.setContentView(R.layout.modal_city)
        cityForm.setTitle("Dostawa")

        val cityArray = mutableListOf<String>()
        app.deliveryWhiteList.forEach { cityArray.add(it.name) }

        if (cityArray.contains(app.user.city)) cityInput.setText(app.user.city)

        val additionsList: ListView = cityForm.findViewById(R.id.cityList)
        additionsList.adapter = ArrayAdapter<String>(
                App.instance,
                R.layout.simple_list_item,
                cityArray)

        additionsList.setOnItemClickListener { _, _, i, _ ->
            cityInput.setText( cityArray[i] )
            cityForm.dismiss()
        }
    }
}
