package kamil.restau

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_promotions.*
import kamil.restau.databinding.FragmentPromotionsBinding

class PromotionsFragment() : Fragment() {

    var promotion: Promotion = Promotion()

    constructor(promotion: Promotion): this() {
        this.promotion = promotion
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentPromotionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_promotions, container, false)
        binding.promotion = promotion
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.storage.loadPromotionImage(bannerImage, promotion)

        val html = App.css + promotion.desc

        webView.loadData(html, "text/html; charset=utf-8", "UTF-8")
        webView.setBackgroundColor(Color.TRANSPARENT)

//        headerTextFrame.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.toolbar.title = "Promocje"
//        it.toolbar.title = news.title
//        it.toolbar.subtitle = "Aktualności"
//        it.navigationView.setCheckedItem(R.id.nav_products)
        }
    }
}
