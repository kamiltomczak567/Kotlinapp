package kamil.restau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_register.*
import kamil.restau.component.Toster

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Rejestracja"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_settings)
        }
        registerButton.setOnClickListener { register() }
    }

    private fun register() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val password2 = passwordConfirmInput.text.toString()

        if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toster(activity, "Uzupełnij wszystkie pola!", Toster.LENGTH_LONG).show()
            return
        }
//
//        if (!App.isEmailValid(password)) {
//            Toster(activity, "Nieprawidłowy adres e-mail!", Toster.LENGTH_LONG).show()
//            return
//        }

        if (password.length<8) {
            Toster(activity, "Hasło musi posiadać conajmniej 8 znaków!", Toster.LENGTH_LONG).show()
            return
        }

        if (password != password2) {
            Toster(activity, "Podane hasła nie pasują do siebie!", Toster.LENGTH_LONG).show()
            return
        }

        val user = User()
        user.firstName = ""
        user.lastName = ""
        user.email = emailInput.text.toString()
        user.notifications = App.notifications

        app.userManager.createAccountEmail(user, password, this::loginSuccess, this::loginFailure)

    }

    private fun loginSuccess() {
        if (app.userManager.Auth()) {
            app.database.addOrdersListener()
            app.database.addUserListener()
        }
        (activity as? MainActivity)?.startFragment(HomeFragment())
    }

    private fun loginFailure(error: String) {
        App.Log("RegisterFragment: loginFailure: " + error, TAG)
    }

}
