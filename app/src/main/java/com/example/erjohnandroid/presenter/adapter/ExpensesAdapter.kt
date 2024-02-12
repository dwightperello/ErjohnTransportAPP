package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.databinding.AdapterExpensesBinding
import com.example.erjohnandroid.databinding.AdapterLineBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ExpensesActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ShowExpensesActivity

class ExpensesAdapter(private val activity: Activity): RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    private var products:List<ExpensesTypeTable> = listOf()

    class ViewHolder(view: AdapterExpensesBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterExpensesBinding=
            AdapterExpensesBinding.inflate(LayoutInflater.from(activity),parent,false)
        return ExpensesAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.name}"

        holder.itemView.setOnClickListener {

            if(activity is ShowExpensesActivity){
               activity.expenses(role)
            }

        }
    }
    fun showexpenses(list: List<ExpensesTypeTable>) {
        products = list
        notifyDataSetChanged()
    }
}