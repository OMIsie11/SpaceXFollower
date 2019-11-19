package io.github.omisie11.spacexfollower.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.charts_formatters.MonthsValueFormatter
import io.github.omisie11.spacexfollower.data.charts_formatters.RoundNumbersValueFormatter
import io.github.omisie11.spacexfollower.util.animateNumber
import io.github.omisie11.spacexfollower.util.getYearValueFromUnixTime
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launches_chart.xAxis.apply {
            valueFormatter = MonthsValueFormatter(getMonthsList())
            position = XAxis.XAxisPosition.BOTTOM
            textColor = ContextCompat.getColor(context!!, R.color.colorOnBackground)
            labelCount = 11
            enableGridDashedLine(10f, 10f, 10f)
        }

        launches_chart.axisLeft.isEnabled = false
        launches_chart.axisRight.isEnabled = false

        launches_chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setPinchZoom(false)
            setNoDataTextColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
            launches_chart.legend.textColor =
                ContextCompat.getColor(context!!, R.color.colorOnBackground)
        }
        launches_chart.invalidate()

        viewModel.getLaunchesStats().observe(viewLifecycleOwner, Observer { launchesStats ->
            setNewDataToChart(launchesStats)
        })

        viewModel.getNumberOfLaunches().observe(viewLifecycleOwner, Observer { numberOfLaunches ->
            text_number_of_launches.animateNumber(finalValue = numberOfLaunches)
        })

        viewModel.getNumberOfCapsules().observe(viewLifecycleOwner, Observer { numberOfCapsules ->
            text_number_of_capsules.animateNumber(finalValue = numberOfCapsules)
        })

        viewModel.getNumberOfCores().observe(viewLifecycleOwner, Observer { numberOfCores ->
            text_number_of_cores.animateNumber(finalValue = numberOfCores)
        })

        viewModel.getLaunchesChartYear().observe(viewLifecycleOwner, Observer { yearToShowInChart ->
            text_launches_chart_title.text = getString(
                R.string.launches_in_template,
                getYearValueFromUnixTime(yearToShowInChart.startUnix)
            )
        })

        button_change_chart_year.setOnClickListener {
            val launchesYearChangeBottomSheetDialog = LaunchesChartYearBottomSheetFragment()

            launchesYearChangeBottomSheetDialog.show(
                childFragmentManager,
                LaunchesChartYearBottomSheetFragment.TAG
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfDataIsOld()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setNewDataToChart(data: List<Entry>, dataSetLabel: String = "") {
        val dataSet = LineDataSet(data, dataSetLabel)
        setupDataSetStyle(dataSet)
        val lineData = LineData(dataSet)
        launches_chart.data = lineData
        launches_chart.animateY(1000, Easing.Linear)
        launches_chart.invalidate()
    }

    private fun setupDataSetStyle(dataSet: LineDataSet) {
        dataSet.apply {
            color = ContextCompat.getColor(context!!, R.color.colorSecondary)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(context!!, R.color.colorSecondary)
            setCircleColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
            valueTextColor = ContextCompat.getColor(context!!, R.color.colorOnBackground)
            valueTextSize = 12f
            valueFormatter = RoundNumbersValueFormatter()
        }
    }

    private fun getMonthsList(): List<String> = listOf(
        getString(R.string.month_short_january), getString(R.string.month_short_february),
        getString(R.string.month_short_march), getString(R.string.month_short_april),
        getString(R.string.month_may), getString(R.string.month_short_june),
        getString(R.string.month_short_july), getString(R.string.month_short_august),
        getString(R.string.september), getString(R.string.month_short_october),
        getString(R.string.month_short_november), getString(R.string.month_short_december)
    )
}
