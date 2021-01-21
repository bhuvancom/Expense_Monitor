package com.redcat.expensemonitor.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.redcat.expensemonitor.R
import com.redcat.expensemonitor.utility.Constants

const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class ItemListDialogFragment : BottomSheetDialogFragment() {
    val args by navArgs<ItemListDialogFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewId = if (args.type.equals(
                "year",
                false
            )
        ) (R.layout.year_selector) else (R.layout.month_year_selector_bottom_sheet)

        return inflater.inflate(viewId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.type.equals("year", false)) {
            view.findViewById<RecyclerView>(R.id.list)?.layoutManager =
                LinearLayoutManager(context)
            view.findViewById<RecyclerView>(R.id.list)?.adapter =
                ItemAdapter(Constants.tillYear())
        } else {
            val month = view.findViewById<Spinner>(R.id.spinner_month)
            val year = view.findViewById<Spinner>(R.id.spinner_year)
            val button = view.findViewById<Button>(R.id.btn_view)
            val arrayAdapterMonth =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    Constants.MONTHS
                )
            month.adapter = arrayAdapterMonth

            val arrayAdapterYear =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    Constants.tillYear()
                )
            year.adapter = arrayAdapterYear

            button.setOnClickListener {
                val selectedM = month.selectedItem.toString()
                val selectedY = year.selectedItem.toString()
                if (selectedM.isBlank() || selectedY.isBlank()) {
                    return@setOnClickListener
                }

                findNavController().navigate(
                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToOtherExpenseFragment(
                        month = selectedM, year = selectedY.toInt()
                    )
                )
            }
        }
    }

    private inner class ViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.year_each,
            parent,
            false
        )
    ) {
        val text: TextView = itemView.findViewById(R.id.text)
    }

    /**
     * @param years
     * called only when year type selection
     */
    private inner class ItemAdapter(private val years: List<Int>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = years[position]
            Log.d(TAG, "onBindViewHolder: $data is about to set")
            holder.text.text = years[position].toString()
            holder.itemView.setOnClickListener {
                findNavController().navigate(
                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToOtherExpenseFragment(
                        year = years[position]
                    )
                )
            }
        }

        override fun getItemCount(): Int {
            return years.size
        }

    }

    companion object {
        private const val TAG = "ItemListDialogFragment"
    }
}
