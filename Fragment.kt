package kamil.restau

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.Fragment as Base

open class Fragment: Base() {
    var TAG = Fragment::class.java.simpleName
    val app = App.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this::class.java.simpleName
        App.Log("onCreate", TAG, "Fragment")
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.Log("onViewCreated", TAG, "Fragment")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        App.Log("onActivityCreated", TAG, "Fragment")
    }

    override fun onStart() {
        super.onStart()
        App.Log("onStart", TAG, "Fragment")

        activity?. let {
            if (it.supportFragmentManager.backStackEntryCount > 0)
                it.toolbar.navigationIcon = ContextCompat.getDrawable(it, R.drawable.ic_arrow_back)
            else
                it.toolbar.navigationIcon = ContextCompat.getDrawable(it, R.drawable.ic_menu)
        }
    }

    override fun onStop() {
        super.onStop()
        App.Log("onStop", TAG, "Fragment")
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.Log("onDestroy", TAG, "Fragment")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        App.Log("onSaveInstanceState", TAG, "Fragment")
    }

    open fun refreshOrders() { }
}