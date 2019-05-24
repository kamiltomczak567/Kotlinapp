package kamil.restau

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_login.*
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import java.util.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import kamil.restau.component.Toster

class LoginFragment : Fragment() {
    lateinit var mFacebookCallbackManager: CallbackManager
    lateinit var mFacebookLoginManager: LoginManager
    lateinit var mGoogleApiClient: GoogleApiClient
    val RC_SIGN_IN = 600

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFacebookCallbackManager = CallbackManager.Factory.create()
        mFacebookLoginManager = LoginManager.getInstance()
        mFacebookLoginManager.registerCallback(mFacebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                App.Log("facebook:onSuccess:" + loginResult, TAG)
                app.userManager.handleFacebookAccessToken(loginResult.accessToken, this@LoginFragment::loginSuccess, this@LoginFragment::loginFailure)
            }
            override fun onCancel() {
                App.Log("facebook:onCancel", TAG)
                loginFailure("facebook:onCancel")
            }
            override fun onError(error: FacebookException) {
                App.Log("facebook:onError - " + error, TAG)
                loginFailure("facebook:onError - " + error)
            }
        })

        activity?.let {
            mGoogleApiClient = GoogleApiClient.Builder(it)
                    .enableAutoManage(it, {

                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, app.googleSignInOptions)
                    .build()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Logowanie"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_settings)
        }

        if (dialog==null) setTermsModal()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener { logIn() }
        registerEmailButton.setOnClickListener { registerEmail() }
        loginGmailButton.setOnClickListener { loginGmail() }
        loginFacebookButton.setOnClickListener { loginFacebook() }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let {
            mGoogleApiClient.stopAutoManage(it)
            mGoogleApiClient.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                app.userManager.firebaseAuthWithGoogle(result, this::loginSuccess, this::loginFailure)
            } else {
                loginFailure()
            }
        }
    }

    private fun logIn() {
        val login = emailInput.text.toString()
        val password = passwordInput.text.toString()
        if (login.isEmpty() || password.isEmpty()) {
            Toster(activity, "Uzupełnij wszystkie pola!", Toster.LENGTH_LONG).show()
            return
        }

        if (!App.isEmailValid(login)) {
            Toster(activity, "Nieprawidłowy adres e-mail!", Toster.LENGTH_LONG).show()
            return
        }

        setButtons(false)
        showLoader(true)
        app.userManager.logInEmail(login, password, this::loginSuccess, this::loginFailure)
    }

    private fun registerEmail() {
        (App.activity as? MainActivity)?.addFragment(RegisterFragment())
    }

    private fun loginGmail() {
        setButtons(false)
        showLoader(true)
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginFacebook() {
        setButtons(false)
        showLoader(true)
        mFacebookLoginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        mFacebookLoginManager.toString()
    }

    fun loginSuccess() {
        if (app.userManager.Auth()) {
            app.database.addOrdersListener()
            app.database.addUserListener()
        }
//        (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
//        (App.activity as? MainActivity)?.addFragment(ProfileFragment())
        (App.activity as? MainActivity)?.startFragment(HomeFragment())
    }

    fun loginFailure(error: String="") {
        setButtons(true)
        showLoader(false)
        if (error.isNotEmpty()) Toster(App.activity, error, Toster.LENGTH_SHORT).show()
    }

    private fun setButtons(enabled: Boolean) {
        loginButton.isEnabled = enabled
        loginGmailButton.isEnabled = enabled
        loginFacebookButton.isEnabled = enabled
        registerEmailButton.isEnabled = enabled
    }

    private fun showLoader(enabled: Boolean) {
        if (enabled) {
            loaderFrame.visibility = View.VISIBLE
            formFrame.visibility = View.GONE
        } else {
            loaderFrame.visibility = View.GONE
            formFrame.visibility = View.VISIBLE
        }
    }


    private fun setTermsModal() {
        dialog = Dialog(App.activity)
        dialog?.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.modal_terms)

            it.setTitle("REGULAMIN")

            val displayRectangle = Rect()
            App.activity?.window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
            it.window.setLayout( (displayRectangle.width() * 0.95f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

            val titleText: TextView = it.findViewById(R.id.titleText)
            titleText.text = "REGULAMIN"

            val contentText: TextView = it.findViewById(R.id.contentText)
            app.database.getString("settings/terms", { terms ->
                contentText.text = terms
            })

            val okButton: Button = it.findViewById(R.id.okButton)
            okButton.setOnClickListener{ dialog?.hide() }

            termsButton.setOnClickListener { dialog?.show() }
        }
    }
}
