package com.redcat.expensemonitor.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.redcat.expensemonitor.databinding.ExpenseEachItemBinding
import com.redcat.expensemonitor.model.Expense

class ExpenseViewAdapter(private val onExpenseClick: OnExpenseClickListener) :
    RecyclerView.Adapter<ExpenseViewAdapter.ExpenseViewHolder>() {
    val TAG = "adapter_debug"
    private val differCallback = object : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            val b =
                (oldItem.id == newItem.id) && oldItem.title.equals(
                    newItem.title,
                    true
                ) && oldItem.amount == newItem.amount
            Log.d(TAG, "areItemsTheSame: for $b, id ${oldItem.id} and ${newItem.id}")
            return b
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class ExpenseViewHolder(private val binding: ExpenseEachItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.apply {
                tvTitle.text = expense.title
                tvCat.text = "On ${expense.category}"
                tvAmount.text = "Rs. ${expense.amount}"
            }
        }

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val expense = differ.currentList[position]
                    expense?.let {
                        onExpenseClick.onItemClick(expense)
                    }
                }
            }
        }
    }

    interface OnExpenseClickListener {
        fun onItemClick(expense: Expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding =
            ExpenseEachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = differ.currentList[position]
        Log.d(TAG, "onBindViewHolder: position $position and $expense")
        expense?.let {
            holder.bind(it)
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}