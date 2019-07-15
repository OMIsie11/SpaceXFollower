package io.github.omisie11.spacexfollower.ui.company


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.util.shortenNumberAddPrefix
import kotlinx.android.synthetic.main.bottom_sheet_attribution.view.*
import kotlinx.android.synthetic.main.fragment_company.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CompanyFragment : Fragment() {

    private val viewModel: CompanyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_company, container, false)

        val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCompanyInfo().observe(viewLifecycleOwner, Observer<Company> { companyInfo ->
            if (companyInfo != null) {
                text_summary.text = companyInfo.summary
                text_employees.text = companyInfo.employees.toString()
                text_vehicles.text = companyInfo.vehicles.toString()
                text_launch_sites.text = companyInfo.launchSites.toString()
                text_test_sites.text = companyInfo.testSites.toString()
                text_valuation.text = resources.getString(
                    R.string.company_valuation,
                    shortenNumberAddPrefix(companyInfo.valuation)
                )
                text_address.text = companyInfo.headquarters.address
                text_city.text = companyInfo.headquarters.city
                text_state.text = companyInfo.headquarters.state
            }
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getCompanyInfoLoadingStatus().observe(viewLifecycleOwner,
            Observer<Boolean> { isCompanyInfoRefreshing ->
                swipeRefreshLayout.isRefreshing = isCompanyInfoRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(viewLifecycleOwner, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshCompanyInfo()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CompanyInfoFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCompanyInfo()
        }

        val attributionBottomSheetDialog = BottomSheetDialog(activity!!)
        val sheetView = activity!!.layoutInflater.inflate(R.layout.bottom_sheet_attribution, null)
        attributionBottomSheetDialog.setContentView(sheetView)

        image_logo.setOnClickListener { attributionBottomSheetDialog.show() }
        sheetView.text_attribution.setOnClickListener { openWebUrl(getString(R.string.lottie_files_url_dongdona)) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshIfCompanyDataOld()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshCompanyInfo()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteCompanyInfo()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress)))
    }
}
