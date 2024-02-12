package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.WitholdingTypeTable
import com.example.erjohnandroid.databinding.AdapterExpensesBinding
import com.example.erjohnandroid.databinding.AdapterWitholdingBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.ExpensesActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ShowWitholdingActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.WitholdingActivity

class WitholdingAdapter(private val activity: Activity): RecyclerView.Adapter<WitholdingAdapter.ViewHolder>() {

    private var products:List<WitholdingTypeTable> = listOf()

    class ViewHolder(view: AdapterWitholdingBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterWitholdingBinding=
            AdapterWitholdingBinding.inflate(LayoutInflater.from(activity),parent,false)
        return WitholdingAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.type}"

        holder.itemView.setOnClickListener {

            if(activity is ShowWitholdingActivity){
                activity.witholding(role)
            }

        }
    }

    fun showwitholding(list: List<WitholdingTypeTable>) {
        products = list
        notifyDataSetChanged()
    }
}