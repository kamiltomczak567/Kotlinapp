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
import kotlinx.android.synthetic.main.fragment_home.*
import kamil.restau.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.user = app.user
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.toolbar.title = getString(R.string.app_name)
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zbierajButton.setOnClickListener {
            if (app.userManager.Auth()) {
//                (activity as? MainActivity)?.addFragment(QRCodeFragment("USER|"+app.user.id))
                (activity as? MainActivity)?.addFragment(KeyboardFragment())
            } else {
                (activity as? MainActivity)?.addFragment(LoginFragment())
            }
        }
        nagrodyButton.setOnClickListener { (activity as? MainActivity)?.addFragment(CouponListFragment()) }
        menuButton.setOnClickListener { (activity as? MainActivity)?.addFragment(ProductsListFragment()) }

        val layout = LinearLayoutManager(activity)
        layout.orientation = LinearLayout.HORIZONTAL
        newsPager.layoutManager = layout
        setRecycleViewAdapter()

        if (!app.userManager.Auth()) pointsText.visibility = View.GONE

        if (app.promotions.size!=0) {
            promocjeButton.setOnClickListener { (activity as? MainActivity)?.addFragment( PromotionsListFragment()) }
            promocjeFrame.visibility = View.VISIBLE
        } else {
            promocjeFrame.visibility = View.GONE

        }
    }

    private fun setRecycleViewAdapter() {
        newsPager?.adapter = NewsAdapter(newsPager, app.news)
    }
}
