package kamil.restau

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_navigation.*

class AboutFragment : Fragment() {
    private var mMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        return view
    }

    private fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.let {
            val location = LatLng(51.5826498, 31.2818629)
            val zoom = 18f

            val marker = it.addMarker(
                    MarkerOptions()
                            .position(location)
                            .title( resources.getString(R.string.app_name) )
            )
            marker.showInfoWindow()
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            it.toolbar.title = "O restauracji"
            it.toolbar.subtitle = ""
            it.navigationView.setCheckedItem(R.id.nav_about)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync({ onMapReady(it) })
    }
}
