package kamil.restau

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kamil.restau.component.Toster
import kamil.restau.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener { saveSettings() }
    }

    override fun onStart() {
        super.onStart()

        if (!app.userManager.Auth()) {
            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
            (App.activity as? MainActivity)?.addFragment(LoginFragment())
            return
        }

        App.Dump(app.user, TAG)

        activity?.let {
            it.toolbar.title = "Ustawienia"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_settings)
        }
//        app.user.notifications.containsKey("news") &&
        if (app.user.notifications["news"]==true) newsNotificationCB.isChecked = true
        if (app.user.notifications["promotions"]==true) promotionsNotificationCB.isChecked = true
        if (app.user.notifications["other"]==true) otherNotificationCB.isChecked = true
    }

    private fun saveSettings() {
        app.user.notifications["news"] = newsNotificationCB.isChecked
        app.user.notifications["promotions"] = promotionsNotificationCB.isChecked
        app.user.notifications["other"] = otherNotificationCB.isChecked
        app.user.subscribeNotifications()
        Toster(activity, "Ustawienia zostały zapisane!", Toster.LENGTH_SHORT).show()
        (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
    }
}
