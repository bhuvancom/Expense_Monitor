package com.redcat.expensemonitor.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.redcat.expensemonitor.R
import com.redcat.expensemonitor.adapters.ExpensePagingAdapter
import com.redcat.expensemonitor.adapters.ExpenseViewAdapter
import com.redcat.expensemonitor.databinding.FragmentOtherBinding
import com.redcat.expensemonitor.db.ExpenseMonitorDatabase
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.model.ExpenseWithCatGroup
import com.redcat.expensemonitor.ui.MainActivity
import com.redcat.expensemonitor.ui.MainViewModel
import com.redcat.expensemonitor.utility.Constants
import com.redcat.expensemonitor.utility.NotifyUtil
import com.redcat.expensemonitor.utility.UtilExtension
import com.redcat.expensemonitor.utility.UtilExtension.Companion.showSnackBar
import com.redcat.expensemonitor.utility.UtilExtension.Companion.showToast
import com.redcat.expensemonitor.utility.VMFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OtherExpenseFragment : Fragment(R.layout.fragment_other),
    ExpenseViewAdapter.OnExpenseClickListener {
    private var _binding: FragmentOtherBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private val args by navArgs<OtherExpenseFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOtherBinding.bind(view)
        setHasOptionsMenu(true)
        binding.tvChart.setOnClickListener {
            showCharts()
        }
        val dao = ExpenseMonitorDatabase.getDatabase(requireContext()).getExpenseDao()
        mainViewModel = ViewModelProvider(this, VMFactory(dao)).get(MainViewModel::class.java)

        val adapter = ExpensePagingAdapter(this)
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMain.setHasFixedSize(true)
        when {
            args.date != -1 -> {
                //we have date, month, year wise view
                (activity as MainActivity).updateSubtitle("${args.date}-${args.month}-${args.year}")
                lifecycleScope.launch {
                    mainViewModel.getExpenseOfDate(args.date, args.month, args.year)
                        .collectLatest { data ->
                            adapter.submitData(data)
                        }
                }

                lifecycleScope.launch {
                    mainViewModel.totalForToday(args.date, args.month, args.year).collectLatest {
                        if (it != null) {
                            binding.tvTotal.text = "Rs $it"
                            mainViewModel.updateHide(false)
                        } else {
                            binding.tvTotal.text = "Nothing found"
                            mainViewModel.updateHide(true)
                        }
                    }
                }
            }

            args.month.isNotBlank() -> {
                //we have month and year vise view
                (activity as MainActivity).updateSubtitle("${args.month}-${args.year}")
                lifecycleScope.launch {
                    mainViewModel.getExpensesOfMonthYear(args.month, args.year)
                        .collectLatest { data ->
                            adapter.submitData(data)
                        }
                }

                lifecycleScope.launch {
                    mainViewModel.getTotalOfGivenMonthAndYear(args.month, args.year).collectLatest {
                        if (it != null) {
                            binding.tvTotal.text = "${args.month} ${args.year} - Rs $it"
                            mainViewModel.updateHide(false)
                        } else {
                            binding.tvTotal.text = "Nothing found"
                            mainViewModel.updateHide(true)
                        }
                    }
                }
            }

            args.year != -1 -> {
                //we have only year view
                (activity as MainActivity).updateSubtitle("${args.year}")
                lifecycleScope.launch {
                    mainViewModel.getExpenseOfYear(args.year)
                        .collectLatest { data ->
                            adapter.submitData(data)
                        }
                }

                lifecycleScope.launch {
                    mainViewModel.getTotalOfYear(args.year).collectLatest {
                        if (it != null) {
                            binding.tvTotal.text = "${args.year} - Rs $it"
                            mainViewModel.updateHide(false)
                        } else {
                            binding.tvTotal.text = "Nothing found"
                            mainViewModel.updateHide(true)
                        }
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG = "OtherExpenseFragment"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_export_graph, menu)
        if (args.date == -1) menu.removeItem(R.id.menu_add)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        mainViewModel.shouldHideExportAndGraph.observe(this) {
            it?.let {
                menu.findItem(R.id.menu_showChart).isVisible = !it
                menu.findItem(R.id.menu_export).isVisible = !it
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_showChart -> {
                showCharts()
                //requireContext().showToast("show chart")
            }
            R.id.menu_export -> {
                exportToExcel()
                requireContext().showToast("exporting")
            }
            R.id.menu_add -> {
                val expense =
                    Expense(null, "", 0.0, args.date, args.month, args.year, Constants.CAT_LIST[0])
                findNavController().navigate(
                    OtherExpenseFragmentDirections.actionOtherExpenseFragmentToAddUpdateExpenseBottomSheet(
                        expense
                    )
                )
                requireContext().showToast("Add to date ${args.date} ${args.month} ${args.year}")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCharts() {
        val date = args.date
        val month = args.month
        val year = args.year
        var catList: List<ExpenseWithCatGroup>? = null

        val launch = lifecycleScope.launch {
            catList = when {
                date != -1 -> mainViewModel.getCatDMY(date, month, year)
                month.isNotBlank() -> mainViewModel.getCATMY(month, year)
                year != -1 -> mainViewModel.getCatY(year)
                else -> mainViewModel.getCAT()
            }
        }

        launch.invokeOnCompletion {
            if (launch.isCompleted) {
                catList?.let {
                    if (it.isEmpty()) {
                        requireContext().showToast("No data to show in charts")
                        return@invokeOnCompletion
                    } else {
                        val sb = StringBuilder()
                        sb.append("Chart for ")
                        if (date != -1) sb.append("$date-")
                        if (month.isNotEmpty()) sb.append("$month-")
                        if (year != -1) sb.append("$year")

                        findNavController().navigate(
                            OtherExpenseFragmentDirections.actionOtherExpenseFragmentToGraphFragment(
                                sb.toString(), catList!!.toTypedArray()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun exportToExcel() {
        val date = args.date
        val month = args.month
        val year = args.year
        if (!UtilExtension.checkPermission(requireContext())) {
            activity?.let { UtilExtension.askPermission(it) }
        }

        if (UtilExtension.checkPermission(requireContext())) {
            view?.showSnackBar("Exporting to excel..")
            val sb = StringBuilder()
            var list: List<Expense>
            val launch: Job

            when {
                date != -1 -> {
                    sb.append("${date}_${month}_${year}.xls")
                    launch = lifecycleScope.launch {
                        list = mainViewModel.getExpenseListOfDate(date, month, year)
                        UtilExtension.saveExcel(list, sb.toString(), requireContext())
                    }
                }

                args.month.isNotBlank() && year != -1 -> {
                    sb.append("${month}_$year.xls")
                    launch = lifecycleScope.launch {
                        list = mainViewModel.getExpenseListOfGivenMonthYear(month, year)
                        UtilExtension.saveExcel(list, sb.toString(), requireContext())
                    }
                }

                args.year != -1 -> {
                    sb.append("$year.xls")
                    launch = lifecycleScope.launch {
                        list = mainViewModel.getExpenseListOfGivenYear(year)
                        UtilExtension.saveExcel(list, sb.toString(), requireContext())
                    }
                }
                else -> {
                    sb.append("all.xls")
                    launch = lifecycleScope.launch {
                        list = mainViewModel.getAllExpenseList()
                        UtilExtension.saveExcel(list, sb.toString(), requireContext())
                    }
                }
            }

            launch.invokeOnCompletion {
                if (launch.isCompleted) {
                    NotifyUtil.notify(requireContext(), sb.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // _binding = null
    }

    override fun onItemClick(expense: Expense) {
        findNavController().navigate(
            OtherExpenseFragmentDirections.actionOtherExpenseFragmentToAddUpdateExpenseBottomSheet(
                expense
            )
        )
    }
}