package kamil.restau

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kamil.restau.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentCartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        binding.cart = app.cart
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Zamówienie"
            it.toolbar.subtitle = "Koszyk"
            it.navigationView?.setCheckedItem(R.id.nav_cart)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(activity)
        layout.orientation = LinearLayout.VERTICAL
        recyclerView.layoutManager = layout
        recyclerView.adapter = CartAdapter(app.cart)

        removeAllButton.setOnClickListener { clearCart() }
        orderButton.setOnClickListener { order() }
    }

    private fun clearCart() {
        if (app.cart.products.isNotEmpty()) {
            activity?.let {
                AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Zamówienie")
                        .setMessage("Czy napewno chcesz uzunąć wszystkie producky z zamówienia?")
                        .setPositiveButton("Tak") { _, _ ->
                            app.cart.clearCart()
                            recyclerView?.adapter?.notifyDataSetChanged()
                        }
                        .setNegativeButton("Nie", null)
                        .show()
            }
        }
    }

    fun order() {
        if (app.cart.productsQuantity!="0") {
            if(app.userManager.Auth()) {
                if (app.cart.totalPkt.toInt()<=app.user.totalPkt.toInt()) {
                    //TODO: Spawdzenie czy estauracja jest czynna
                    val czynna = true
                    if(czynna) {
                        orderConfirmDialog()
                    } else {
                        //TODO: modal restaurantloyalty nie czynna
                        activity?.let {
                            AlertDialog.Builder(it)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Restauracja nieczynna")
                                    .setMessage("Restauracja jest aktualnie nie czynna... Czy napewno chcesz złożyć zamówienie?")
                                    .setPositiveButton("Tak") { _, _ ->
                                        orderConfirmDialog()
                                    }
                                    .setNegativeButton("Nie", null)
                                    .show()
                        }
                    }
                } else {
                    //TODO: Modal brak wystarczającej ilośc punktów
                    activity?.let {
                        AlertDialog.Builder(it)
                                .setIcon(R.drawable.ic_stars)
                                .setTitle("Brak wystarczającej ilości punktów!")
                                .setMessage("Nie masz aktualnie wystarczającej ilości punktów, aby zrealizować to zamówienie.")
                                .setPositiveButton("Ok", null)
                                .show()
                    }
                }
            } else {
                //TODO: goto login
                activity?.let {
                    AlertDialog.Builder(it)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Przed złożeniem zamówienia musisz się zalogować")
                            .setMessage("Przejść do logowania?")
                            .setPositiveButton("Tak") { _, _ ->
                                (activity as? MainActivity)?.changeFragment(LoginFragment())
                            }
                            .setNegativeButton("Nie", null)
                            .show()
                }
            }
        } else {
            //TODO: modal koszyk pusty
            activity?.let {
                AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Brak produktów w zamówieniu")
                        .setMessage("Musisz najpierw dodać jakieś produkty do zamówienia.")
                        .setPositiveButton("Ok", null)
                        .show()
            }
        }
    }

    private fun orderConfirmDialog() {
//        AlertDialog.Builder(activity, app.dialogStyle)
//                .setIcon(android.R.drawable.ic_menu_send)
//                .setTitle("Zamówienie")
//                .setMessage("Przejść dalej?")
//                .setPositiveButton("Tak") { _, _ ->

//                    val cart = app.cart.products.filter { it.quantity!="0" }
//                    app.cart.clearCart()
//                    app.cart.addProducts(cart)
//                    app.cart.refreshProductsData()

                    (activity as? MainActivity)?.addFragment(OrderFragment())
//                }
//                .setNegativeButton("Nie", null)
//                .show()
    }
}
