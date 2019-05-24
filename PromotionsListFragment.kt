package kamil.restau

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import it.carlom.stikkyheader.core.StikkyHeaderBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_products_list.*
import it.carlom.stikkyheader.core.animator.AnimatorBuilder
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator
import kotlinx.android.synthetic.main.fragment_products_list_header.*
import android.view.ViewTreeObserver

class PromotionsListFragment() : Fragment() {

    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (headerTextFrame.measuredHeight > 0) {
                StikkyHeaderBuilder.stickTo(recyclerView)
                        .setHeader(header)
                        .minHeightHeader(headerTextFrame.measuredHeight)
                        .animator(ParallaxStikkyAnimator())
                        .build()

                headerTextFrame.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (recyclerView?.adapter?.itemCount?:0>0) recyclerView?.scrollToPosition(0)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_promotions_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "Promocje"
            it.toolbar.subtitle = ""
        }

//        activity?.navigationView?.setCheckedItem(R.id.nav_products)
    }

    override fun onStop() {
        super.onStop()
        headerTextFrame.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(activity)
        layout.orientation = LinearLayout.VERTICAL
        recyclerView?.layoutManager = layout
//        recyclerView.isNestedScrollingEnabled = false
        setRecycleViewAdapter()

        swipeRefresh.setOnRefreshListener {
            refreshItems()
        }
        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView?, newState: Int) {
                if (swipeRefresh?.isRefreshing == true) rv?.stopScroll()
                super.onScrollStateChanged(recyclerView, newState)
                if (swipeRefresh?.isRefreshing == true) rv?.stopScroll()
            }
            override fun onScrolled(rv: RecyclerView?, dx: Int, dy: Int) {
                if (swipeRefresh?.isRefreshing == true) rv?.stopScroll()
                super.onScrolled(rv, dx, dy)
                if (swipeRefresh?.isRefreshing == true) rv?.stopScroll()

                recyclerView?.let {
                    if (recyclerView.computeVerticalScrollOffset()<=0) {
                        swipeRefresh.bringToFront()
                    } else {
//                    if (!swipeRefresh.isRefreshing)
                        header.bringToFront()
                    }
                }
            }
        })
        headerTextFrame.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun setRecycleViewAdapter() {
        recyclerView?.adapter = PromotionsAdapter(app.promotions)
    }

    private fun refreshItems() {
        recyclerView?.stopScroll()
        setRecycleViewAdapter()             // Load items
        swipeRefresh?.isRefreshing = false  // Load complete
    }

    private inner class ParallaxStikkyAnimator : HeaderStikkyAnimator() {
        override fun getAnimatorBuilder(): AnimatorBuilder {
            return AnimatorBuilder.create().applyVerticalParallax(headerImage)
        }
    }
}
