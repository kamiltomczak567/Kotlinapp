package kamil.restau

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news.*
import kamil.restau.databinding.FragmentNewsBinding

class NewsFragment() : Fragment() {

    var news: News = News()

//    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
//        override fun onGlobalLayout() {
//            if (headerTextFrame.measuredHeight > 0) {
//                StikkyHeaderBuilder.stickTo(scrollView)
//                        .setHeader(header)
//                        .minHeightHeader(headerTextFrame.measuredHeight)
//                        .animator(ParallaxStikkyAnimator())
//                        .build()
//
//                headerTextFrame.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
////                if (recyclerView?.adapter?.itemCount ?: 0 > 0) recyclerView?.scrollToPosition(0)
//            }
//        }
//    }

    constructor(news: News): this() {
        this.news = news
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentNewsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)
        binding.news = news
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.storage.loadNewsImage(bannerImage, news)

        val html = App.css + news.content

        webView.loadData(html, "text/html; charset=utf-8", "UTF-8")
        webView.setBackgroundColor(Color.TRANSPARENT)

//        headerTextFrame.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.toolbar.title = "Aktualności"
//        it.toolbar.title = news.title
//        it.toolbar.subtitle = "Aktualności"
//        it.navigationView.setCheckedItem(R.id.nav_products)
        }
    }

    override fun onStop() {
        super.onStop()
//        headerTextFrame.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

//    private inner class ParallaxStikkyAnimator : HeaderStikkyAnimator() {
//        override fun getAnimatorBuilder(): AnimatorBuilder {
//            return AnimatorBuilder.create().applyVerticalParallax(bannerImage)
//        }
//    }
}
