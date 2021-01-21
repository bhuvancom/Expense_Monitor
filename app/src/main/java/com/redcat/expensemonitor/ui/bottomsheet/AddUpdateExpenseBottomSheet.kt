package com.redcat.expensemonitor.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.redcat.expensemonitor.R
import com.redcat.expensemonitor.databinding.AddUpdateExpenseBottomSheetBinding
import com.redcat.expensemonitor.db.ExpenseMonitorDatabase
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.ui.MainViewModel
import com.redcat.expensemonitor.utility.Constants
import com.redcat.expensemonitor.utility.UtilExtension.Companion.showToast
import com.redcat.expensemonitor.utility.VMFactory

class AddUpdateExpenseBottomSheet : BottomSheetDialogFragment() {
    private var _binding: AddUpdateExpenseBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var expenseObj: Expense? = null
    private lateinit var mainViewModel: MainViewModel
    private val args by navArgs<AddUpdateExpenseBottomSheetArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.add_update_expense_bottom_sheet,
            container,
            false
        ).rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = AddUpdateExpenseBottomSheetBinding.bind(view)

        val dao =
            ExpenseMonitorDatabase.getDatabase(requireContext().applicationContext).getExpenseDao()
        mainViewModel = ViewModelProvider(this, VMFactory(dao)).get(MainViewModel::class.java)

        val arrayAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Constants.CAT_LIST
            )
        binding.spinnerCat.adapter = arrayAdapter

        args.expense?.let {
            val expense = it
            expenseObj = expense
            binding.etExpenseTitle.editText?.setText(expense.title)
            binding.etExpenseAmount.editText?.setText(expense.amount.toString())

            for ((index, str) in Constants.CAT_LIST.withIndex()) {
                if (str.equals(expense.category, true)) {
                    binding.spinnerCat.setSelection(index)
                    break
                }
            }
        }

        binding.btnDeleteExpense.isVisible = args.expense != null && args.expense!!.id != null

        args.expense?.let { thisExpense ->
            thisExpense.id?.let {
                binding.btnDeleteExpense.setOnClickListener {
                    mainViewModel.deleteExpense(thisExpense)
                    dismiss()
                }
            }
        }

        binding.btnSaveExpense.setOnClickListener {
            try {
                val title = binding.etExpenseTitle.editText?.text.toString()
                val amount = binding.etExpenseAmount.editText?.text.toString()
                val category = binding.spinnerCat.selectedItem.toString()

                if (title.isBlank() || amount.isBlank()) {
                    requireContext().showToast("Enter title and amount")
                    return@setOnClickListener
                }

                mainViewModel.saveExpense(
                    Expense(
                        expenseObj?.id,
                        title,
                        amount.toDouble(),
                        expenseObj?.date ?: Constants.getTodayDate(),
                        expenseObj?.month ?: Constants.getCurrentMonth(),
                        expenseObj?.year ?: Constants.getCurrentYear(),
                        category
                    )
                )
                dismiss()
            } catch (exception: NumberFormatException) {
                requireContext().showToast("Unable to understand amount")
            }
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        Log.d(TAG, "onViewCreated: args ${args.expense} ")
    }

    companion object {
        private const val TAG = "AddUpdateExpenseBottomS"
        fun newInstance(): AddUpdateExpenseBottomSheet =
            AddUpdateExpenseBottomSheet()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}