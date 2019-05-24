package kamil.restau

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_product.*
import kamil.restau.databinding.FragmentProductBinding

class ProductFragment() : Fragment() {

    var product: Product = Product()

    constructor(product: Product) : this() {
        this.product = product
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentProductBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false)
        binding.product = product
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val additions = mutableListOf<String>()
        product.additions.forEach { id ->
            app.productAdditions.firstOrNull { it.id == id }?.let { additions.add(it.name + "   +" + String.format("%.2f zł", it.price.toFloat())) }
        }

        addToCartButton.setOnClickListener {
            Product.showAddModal(product)
        }

        App.instance.storage.loadProductImage(bannerImage, product)

        pktText.text = String.format("%.2f zł", product.price.toFloat())

        val html = App.css + product.desc
        descView.loadData(html, "text/html; charset=utf-8", "UTF-8")
        descView.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.toolbar.title = product.name
            it.toolbar.subtitle = app.productCategory.firstOrNull { it.id == product.productCategory }?.name ?: ""
            it.navigationView.setCheckedItem(R.id.nav_products)
        }

        if (product.showPrice) {
            priceBox.visibility = View.VISIBLE
        } else {
            priceBox.visibility = View.GONE
        }
    }
}
