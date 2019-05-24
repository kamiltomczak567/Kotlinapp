package kamil.restau

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kamil.restau.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        binding.cart = app.cart
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (!app.userManager.Auth()) {
            (App.activity as? MainActivity)?.supportFragmentManager?.popBackStack()
            (App.activity as? MainActivity)?.addFragment(LoginFragment())
            return
        }

        activity?.let {
            it.toolbar.title = "Zamówienia"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_orders)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        orderButton.setOnClickListener { order() }

        val newLayout = LinearLayoutManager(activity)
        newLayout.orientation = LinearLayout.VERTICAL
        newRecyclerView.layoutManager = newLayout

        val acceptedLayout = LinearLayoutManager(activity)
        acceptedLayout.orientation = LinearLayout.VERTICAL
        acceptedRecyclerView.layoutManager = acceptedLayout

        val duringLayout = LinearLayoutManager(activity)
        duringLayout.orientation = LinearLayout.VERTICAL
        duringRecyclerView.layoutManager = duringLayout

        val completedLayout = LinearLayoutManager(activity)
        completedLayout.orientation = LinearLayout.VERTICAL
        completedRecyclerView.layoutManager = completedLayout

        val canceledLayout = LinearLayoutManager(activity)
        canceledLayout.orientation = LinearLayout.VERTICAL
        canceledRecyclerView.layoutManager = canceledLayout

        setRecycleViewAdapters()

        swipeRefresh.setOnRefreshListener{ refreshItems() }
    }

    private fun setRecycleViewAdapters() {
        val new = app.orders.filter { it.status=="new" }.sortedBy { it.createdAt }
        newRecyclerView?.adapter = OrdersAdapter(new)
        if (new.isEmpty()) {
            newText.visibility = View.GONE
            newRecyclerView.visibility = View.GONE
        } else {
            newText.visibility = View.VISIBLE
            newRecyclerView.visibility = View.VISIBLE
        }

        val accepted = app.orders.filter { it.status=="accepted" }.sortedBy { it.createdAt }
        acceptedRecyclerView?.adapter = OrdersAdapter(accepted)
        if (accepted.isEmpty()) {
            acceptedText.visibility = View.GONE
            acceptedRecyclerView.visibility = View.GONE
        } else {
            acceptedText.visibility = View.VISIBLE
            acceptedRecyclerView.visibility = View.VISIBLE
        }

        val during = app.orders.filter { it.status=="during" }.sortedBy { it.createdAt }
        duringRecyclerView?.adapter = OrdersAdapter(during)
        if (during.isEmpty()) {
            duringText.visibility = View.GONE
            duringRecyclerView.visibility = View.GONE
        } else {
            duringText.visibility = View.VISIBLE
            duringRecyclerView.visibility = View.VISIBLE
        }

        val completed = app.orders.filter { it.status=="completed" }.sortedBy { it.createdAt }
        completedRecyclerView?.adapter = OrdersAdapter(completed)
        if (completed.isEmpty()) {
            completedText.visibility = View.GONE
            completedRecyclerView.visibility = View.GONE
        } else {
            completedText.visibility = View.VISIBLE
            completedRecyclerView.visibility = View.VISIBLE
        }

        val canceled = app.orders.filter { it.status=="canceled" }.sortedBy { it.createdAt }
        canceledRecyclerView?.adapter = OrdersAdapter(canceled)
        if (canceled.isEmpty()) {
            canceledText.visibility = View.GONE
            canceledRecyclerView.visibility = View.GONE
        } else {
            canceledText.visibility = View.VISIBLE
            canceledRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun refreshItems() {
        setRecycleViewAdapters()
        swipeRefresh?.isRefreshing = false
    }

    override fun refreshOrders() {
        setRecycleViewAdapters()
    }
}
