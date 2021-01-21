package com.redcat.expensemonitor.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.redcat.expensemonitor.R
import com.redcat.expensemonitor.databinding.FragmentGraphBinding


class GraphFragment : BottomSheetDialogFragment() {
    private val args by navArgs<GraphFragmentArgs>()
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "GraphFragment"
    }

    private fun initialize() {
        binding.anychart.setProgressBar(binding.progressBar)
        val pie = AnyChart.
            //funnel()
        pie()

        val dataList = mutableListOf<DataEntry>()
        args.cat.forEach {
            dataList.add(ValueDataEntry(it.category, it.total))
        }

        pie.data(dataList)

        //pie.title(args.expenseType)

        pie.labels().position("outside")

        pie.legend().title().enabled(true)
        pie.legend().title()
            .text(args.expenseType)
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)
        pie.animation(true)

        binding.anychart.setChart(pie)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGraphBinding.bind(view)

        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}