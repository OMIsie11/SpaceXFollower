package io.github.omisie11.spacexfollower.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.util.animateNumber
import io.github.omisie11.spacexfollower.util.charts_utils.ChartMarkerView
import io.github.omisie11.spacexfollower.util.charts_utils.MonthsValueFormatter
import io.github.omisie11.spacexfollower.util.charts_utils.RoundNumbersValueFormatter
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

        styleLaunchesStatsChart()
        styleStatusPieChart(chart_capsules_status)
        styleStatusPieChart(chart_cores_status)

        viewModel.getLaunchesStats().observe(viewLifecycleOwner, Observer { launchesStats ->
            setLaunchesStatsDataToChart(launchesStats)
        })

        viewModel.getLaunchesChartYear().observe(viewLifecycleOwner, Observer { yearToShowInChart ->
            text_launches_chart_title.text =
                getString(
                    R.string.launches_in_template,
                    getYearValueFromUnixTime(yearToShowInChart.startUnix)
                )
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

        viewModel.getCapsulesStatusStats().observe(viewLifecycleOwner, Observer { capsulesStats ->
            setDataToPieChart(data = capsulesStats, chart = chart_capsules_status)
        })

        viewModel.getCoresStatusStats().observe(viewLifecycleOwner, Observer { coresStats ->
            setDataToPieChart(data = coresStats, chart = chart_cores_status)
        })

        button_change_chart_year.setOnClickListener {
            val launchesYearChangeBottomSheetDialog = LaunchesChartYearBottomSheetFragment()

            launchesYearChangeBottomSheetDialog.show(
                childFragmentManager,
                LaunchesChartYearBottomSheetFragment.TAG
            )
        }

        text_label_capsules.setOnClickListener {
            findNavController()
                .navigate(DashboardFragmentDirections.actionDashboardDestToCapsulesDest())
        }

        text_label_launches.setOnClickListener {
            findNavController()
                .navigate(DashboardFragmentDirections.actionDashboardDestToLaunchesDest())
        }

        text_label_cores.setOnClickListener {
            findNavController()
                .navigate(DashboardFragmentDirections.actionDashboardDestToCoresDest())
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

    private fun setLaunchesStatsDataToChart(data: List<Entry>, dataSetLabel: String = "") {
        val dataSet = LineDataSet(data, dataSetLabel)
        setupLaunchesStatsDataSetStyle(dataSet)
        val lineData = LineData(dataSet)
        launches_chart.data = lineData
        launches_chart.animateY(1000, Easing.Linear)
        launches_chart.invalidate()
    }

    private fun setDataToPieChart(
        data: List<PieEntry>,
        chart: PieChart,
        dataSetLabel: String = ""
    ) {
        val dataSet = PieDataSet(data, dataSetLabel)
        setupPieStatsDataSetStyle(dataSet)
        val pieData = PieData(dataSet)
        chart.data = pieData
        chart.invalidate()
    }

    private fun setupLaunchesStatsDataSetStyle(dataSet: LineDataSet) = dataSet.apply {
        color = ContextCompat.getColor(context!!, R.color.colorSecondary)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        fillColor = ContextCompat.getColor(context!!, R.color.colorSecondary)
        setCircleColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
        valueTextColor = ContextCompat.getColor(context!!, R.color.colorOnBackground)
        valueTextSize = 12f
        valueFormatter = RoundNumbersValueFormatter()
    }

    private fun setupPieStatsDataSetStyle(dataSet: PieDataSet) = dataSet.apply {
        colors = getPieChartColorsPalette()
        valueTextColor = ContextCompat.getColor(context!!, R.color.pieChartTextColor)
        valueTextSize = 16f
        valueFormatter = RoundNumbersValueFormatter()
    }

    private fun styleLaunchesStatsChart() {
        launches_chart.xAxis.apply {
            valueFormatter =
                MonthsValueFormatter(
                    getMonthsList()
                )
            position = XAxis.XAxisPosition.BOTTOM
            textColor = ContextCompat.getColor(context!!, R.color.colorOnBackground)
            labelCount = 11
            enableGridDashedLine(10f, 10f, 10f)
        }

        launches_chart.axisLeft.isEnabled = false
        launches_chart.axisRight.isEnabled = false

        launches_chart.apply {
            marker = ChartMarkerView(context, R.layout.marker_view)
            description.isEnabled = false
            legend.isEnabled = false
            setPinchZoom(false)
            setNoDataTextColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
            legend.textColor = ContextCompat.getColor(context!!, R.color.colorOnBackground)
        }
        launches_chart.invalidate()
    }

    private fun styleStatusPieChart(chart: PieChart) = chart.apply {
        setDrawEntryLabels(false)
        description.isEnabled = false
        legend.apply {
            isEnabled = true
            textSize = 14f
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            xEntrySpace = 15f
        }
        isDrawHoleEnabled = true
        setTouchEnabled(false)
        isHighlightPerTapEnabled = false
        setNoDataTextColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
        setEntryLabelColor(ContextCompat.getColor(context!!, R.color.pieChartTextColor))
        setHoleColor(ContextCompat.getColor(context!!, R.color.colorBackground))
        setNoDataTextColor(ContextCompat.getColor(context!!, R.color.colorSecondary))
        legend.textColor =
            ContextCompat.getColor(context!!, R.color.colorOnBackground)
        animateXY(500, 500, Easing.Linear)
        // Refresh chart
        invalidate()
    }

    private fun getMonthsList(): List<String> = listOf(
        getString(R.string.month_short_january), getString(R.string.month_short_february),
        getString(R.string.month_short_march), getString(R.string.month_short_april),
        getString(R.string.month_may), getString(R.string.month_short_june),
        getString(R.string.month_short_july), getString(R.string.month_short_august),
        getString(R.string.month_short_september), getString(R.string.month_short_october),
        getString(R.string.month_short_november), getString(R.string.month_short_december)
    )

    private fun getPieChartColorsPalette(): List<Int> = listOf(
        ContextCompat.getColor(context!!, R.color.pieChartColor1),
        ContextCompat.getColor(context!!, R.color.pieChartColor2),
        ContextCompat.getColor(context!!, R.color.pieChartColor3),
        ContextCompat.getColor(context!!, R.color.pieChartColor4),
        ContextCompat.getColor(context!!, R.color.pieChartColor5)
    )
}
