package com.redcat.expensemonitor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.redcat.expensemonitor.databinding.ExpenseEachItemBinding
import com.redcat.expensemonitor.model.Expense

class ExpensePagingAdapter(private val onExpenseClick: ExpenseViewAdapter.OnExpenseClickListener) :
    PagingDataAdapter<Expense, ExpensePagingAdapter.PageViewHolderThis>(differCallback) {

    inner class PageViewHolderThis(private val binding: ExpenseEachItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense?) {
            binding.apply {
                expense?.let {
                    tvTitle.text = expense.title
                    tvCat.text = "${expense.category}"
                    tvAmount.text = "₹ ${expense.amount}"
                    tvDate.text = "${expense.date}-${expense.month}-${expense.year}"
                    root.setOnClickListener {
                        onExpenseClick.onItemClick(expense)
                    }
                }
            }
        }
    }

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean =
                oldItem.id == newItem.id
//                        && oldItem.title.equals(
//                    newItem.title,
//                    false
//                ) && oldItem.amount == newItem.amount


            override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean =
                oldItem == newItem


        }
    }

    override fun onBindViewHolder(holder: PageViewHolderThis, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolderThis {
        return PageViewHolderThis(
            ExpenseEachItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}