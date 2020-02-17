package io.github.omisie11.spacexfollower.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.DashboardRepository
import kotlinx.android.synthetic.main.bottom_sheet_launches_chart_year_change.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesChartYearBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: DashboardViewModel by sharedViewModel()
    private lateinit var yearToShowInChart: DashboardRepository.YearInterval

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_launches_chart_year_change, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLaunchesChartYear().observe(viewLifecycleOwner, Observer { yearToShow ->
            yearToShowInChart = yearToShow

            when (yearToShowInChart) {
                DashboardRepository.YearInterval.YEAR_2020 -> radio_group_launches_year.check(
                    radio_button_2020.id
                )
                DashboardRepository.YearInterval.YEAR_2019 -> radio_group_launches_year.check(
                    radio_button_2019.id
                )
                DashboardRepository.YearInterval.YEAR_2018 -> radio_group_launches_year.check(
                    radio_button_2018.id
                )
                DashboardRepository.YearInterval.YEAR_2017 -> radio_group_launches_year.check(
                    radio_button_2017.id
                )
                DashboardRepository.YearInterval.YEAR_2016 -> radio_group_launches_year.check(
                    radio_button_2016.id
                )
                DashboardRepository.YearInterval.YEAR_2015 -> radio_group_launches_year.check(
                    radio_button_2015.id
                )
                DashboardRepository.YearInterval.YEAR_2014 -> radio_group_launches_year.check(
                    radio_button_2014.id
                )
            }
        })

        radio_group_launches_year.check(radio_button_2019.id)
        radio_group_launches_year.setOnCheckedChangeListener { group, checkedId ->
            when (group.checkedRadioButtonId) {
                radio_button_2020.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2020)
                radio_button_2019.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2019)
                radio_button_2018.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2018)
                radio_button_2017.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2017)
                radio_button_2016.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2016)
                radio_button_2015.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2015)
                radio_button_2014.id ->
                    viewModel.setLaunchesChartYear(DashboardRepository.YearInterval.YEAR_2014)
            }
            if (isVisible) dismiss()
        }
    }

    companion object {
        const val TAG = "bottom_sheet_launches_chart_year_change"
    }
}