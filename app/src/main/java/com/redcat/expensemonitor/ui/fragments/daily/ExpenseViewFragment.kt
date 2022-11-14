package com.redcat.expensemonitor.ui.fragments.daily

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redcat.expensemonitor.R
import com.redcat.expensemonitor.adapters.ExpensePagingAdapter
import com.redcat.expensemonitor.adapters.ExpenseViewAdapter
import com.redcat.expensemonitor.databinding.FragmentExpenseViewBinding
import com.redcat.expensemonitor.db.ExpenseMonitorDatabase
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.ui.MainActivity
import com.redcat.expensemonitor.ui.MainViewModel
import com.redcat.expensemonitor.utility.Constants
import com.redcat.expensemonitor.utility.NotifyUtil
import com.redcat.expensemonitor.utility.UtilExtension
import com.redcat.expensemonitor.utility.UtilExtension.Companion.showSnackBar
import com.redcat.expensemonitor.utility.UtilExtension.Companion.showToast
import com.redcat.expensemonitor.utility.VMFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class ExpenseViewFragment : Fragment(R.layout.fragment_expense_view),
    ExpenseViewAdapter.OnExpenseClickListener {
    private var _binding: FragmentExpenseViewBinding? = null
    private val binding get() = _binding!!
    lateinit var vm: MainViewModel
    private val date = Constants.getTodayDate()
    private val month = Constants.getCurrentMonth()
    private val year = Constants.getCurrentYear()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExpenseViewBinding.bind(view)
        (activity as MainActivity).updateSubtitle("Today")
        setHasOptionsMenu(true)
        val expenseDao = ExpenseMonitorDatabase.getDatabase(requireContext()).getExpenseDao()
        val vmProvider = VMFactory(expenseDao)
        vm = ViewModelProvider(this, vmProvider).get(MainViewModel::class.java)
        binding.tvChart.setOnClickListener {
            showCharts()
        }
        val adapter = ExpensePagingAdapter(this)

        binding.fab.setOnClickListener {
            findNavController().navigate(
                ExpenseViewFragmentDirections.actionExpenseViewFragmentToAddUpdateExpenseBottomSheet(
                    null
                )
            )
        }

        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMain.setHasFixedSize(true)

        lifecycleScope.launch {
            vm.getExpenseOfDate(
                date, month, year
            ).collectLatest { data ->
                adapter.submitData(data)
            }
        }

        lifecycleScope.launch {
            vm.totalForToday(
                date, month, year
            ).collectLatest {
                if (it != null) {
                    binding.tvTotal.text = "Rs $it"
                } else {
                    binding.tvTotal.text = "Rs 0"
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

    override fun onItemClick(expense: Expense) {
        findNavController().navigate(
            ExpenseViewFragmentDirections.actionExpenseViewFragmentToAddUpdateExpenseBottomSheet(
                expense
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export -> {
                if (!UtilExtension.checkPermission(requireContext())) {
                    activity?.let { UtilExtension.askPermission(it) }
                }
                if (UtilExtension.checkPermission(requireContext())) {
                    view?.showSnackBar("Exporting to excel..")
                    val file =
                        "${Constants.getTodayDate()}_${Constants.getCurrentMonth()}_${Constants.getCurrentYear()}.xls"
                    val launch = lifecycleScope.launch {
                        val expenseListOfDate = vm.getExpenseListOfDate(
                            date, month, year
                        )
                        UtilExtension.saveExcel(
                            expenseListOfDate,
                            file,
                            requireContext()
                        )
                    }

                    launch.invokeOnCompletion {
                        Log.d(
                            TAG,
                            "onOptionsItemSelected: is completed " + launch.isCompleted + " is success"
                        )
                        if (launch.isCompleted) {
                            NotifyUtil.notify(requireContext(), file)
                        }
                    }
                }
                true
            }
            R.id.menu_selectByMonth -> {
                findNavController().navigate(
                    ExpenseViewFragmentDirections.actionExpenseViewFragmentToItemListDialogFragment(
                        "month"
                    )
                )
                true
            }
            R.id.menu_selectByYear -> {
                findNavController().navigate(
                    ExpenseViewFragmentDirections.actionExpenseViewFragmentToItemListDialogFragment(
                        "year"
                    )
                )
                true
            }
            R.id.menu_showByDate -> {
                showDateChooser()
                true
            }
            R.id.menu_showChart -> {
                showCharts()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun showCharts() {
        lifecycleScope.launch {
            val catDMY = vm.getCatDMY(date, month, year)
            if (catDMY.isEmpty()) {
                requireContext().showToast("Nothing to show in chart")
            } else {
                findNavController().navigate(
                    ExpenseViewFragmentDirections.actionExpenseViewFragmentToGraphFragment(
                        "Expense Chart for Today",
                        catDMY.toTypedArray()
                    )
                )
            }
        }
    }

    private fun showDateChooser() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                requireContext().showToast("Showing expense of $dayOfMonth/${Constants.MONTHS[month]}/$year")
                findNavController().navigate(
                    ExpenseViewFragmentDirections.actionExpenseViewFragmentToOtherExpenseFragment(
                        dayOfMonth, Constants.MONTHS[month], year
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        private const val TAG = "ExpenseViewFragment"
    }
}