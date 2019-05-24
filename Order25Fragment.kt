package kamil.restau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kamil.restau.api.Api

class Order25Fragment : Fragment() {
    val thisFragment = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_order25, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Zamówienie"
            it.toolbar.subtitle = "Dostawa"
            it.navigationView.setCheckedItem(R.id.nav_cart)
        }

        test()
    }

    private fun next() {
        if (thisFragment.isVisible) {
            App.activity?.supportFragmentManager?.popBackStack()
            (activity as? MainActivity)?.addFragment(Order3Fragment())
        }
    }

    private var loading = false
    private fun test() {
        if (!loading) {
            val retrofit = Retrofit.Builder().baseUrl(Api.baseURL).addConverterFactory(GsonConverterFactory.create()).build()

            val api = retrofit.create(Api::class.java)
            app.cart.refreshProductsData()

            api.checkOrder( app.cart ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        if (res!=null) {
                            val resObj: Discount = App.fromJson(res)

                            app.cart.discount = resObj
//                            App.Log(res.get("address").asString, TAG)
//                            App.Log(res.toString(), TAG)
//                            App.Log(ress.delivery.address)
                            next()
//                            App.activity?.supportFragmentManager?.popBackStack()
                        } else {
                            App.Log("res=null", TAG)
                            App.Dump(response, TAG)
//                            if (thisFragment.isVisible) App.activity?.supportFragmentManager?.popBackStack()
                            app.cart.discount = Discount()
                            next()
                        }
                    } else {
                        App.Log("Failure", TAG)
                        App.Dump(response, TAG)
//                        if (thisFragment.isVisible) App.activity?.supportFragmentManager?.popBackStack()
                        app.cart.discount = Discount()
                        next()
                    }
                    loading = false
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    App.Log("onFailure: " + t.localizedMessage, TAG)
//                    loading = false
//                    if (thisFragment.isVisible) App.activity?.supportFragmentManager?.popBackStack()
                    app.cart.discount = Discount()
                    next()
                }
            })
        }
    }
}
