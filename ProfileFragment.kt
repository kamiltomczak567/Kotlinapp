package kamil.restau

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kamil.restau.component.Toster
import kamil.restau.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private val cityForm = Dialog(App.activity)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.user = app.user
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton.setOnClickListener { logOut() }
        editButton.setOnClickListener { (activity as? MainActivity)?.addFragment(ProfileFragmentEdit()) }
        settingsButton.setOnClickListener { (activity as? MainActivity)?.addFragment(SettingsFragment()) }
    }

    override fun onStart() {
        super.onStart()

        if (!app.userManager.Auth()) {
            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
            (App.activity as? MainActivity)?.addFragment(LoginFragment())
            return
        }

        activity?.let {
            it.toolbar.title = "Panel użytkownika"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_settings)
        }
    }

    private fun logOut() {
        activity?.let {
            AlertDialog.Builder(it)
                    .setIcon(android.R.drawable.ic_menu_send)
                    .setTitle("Wylogowanie")
                    .setMessage("Czy napewno chcesz się wylogować z konta?")
                    .setPositiveButton("Tak") { _, _ ->
                        app.userManager.logOut()
                        (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
                        Toster(activity, "Zostałeś wylogowany", Toster.LENGTH_LONG).show()
                    }
                    .setNegativeButton("Nie", null)
                    .show()
        }
    }
}
