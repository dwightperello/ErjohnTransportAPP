package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.databinding.AdapterExpensesBinding
import com.example.erjohnandroid.databinding.AdapterShowallexpensesBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.ShowExpensesActivity

class showAllExpensesAdapter(private val activity: Activity): RecyclerView.Adapter<showAllExpensesAdapter.ViewHolder>() {
    private var expenses:List<TripCostTable> = listOf()

    class ViewHolder(view: AdapterShowallexpensesBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtExpensesType
        val amount = view.txtExpensesamount
        val remove = view.icRemove

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterShowallexpensesBinding=
            AdapterShowallexpensesBinding.inflate(LayoutInflater.from(activity),parent,false)
        return showAllExpensesAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= expenses[position]
        holder.name.text="${role.costType}"
        holder.amount.text = "${role.amount}"

        holder.remove.setOnClickListener {
            if (activity is ShowExpensesActivity){
                activity.removeItem(role)
            }
        }
    }

    fun showexpenses(list: List<TripCostTable>) {
        expenses = list
        notifyDataSetChanged()
    }

}