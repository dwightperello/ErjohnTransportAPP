package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.databinding.AdapterShowallexpensesBinding
import com.example.erjohnandroid.databinding.AdapterShowallwitholdingBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.ShowWitholdingActivity

class showAllWitholdingAdapter (private val activity: Activity): RecyclerView.Adapter<showAllWitholdingAdapter.ViewHolder>(){
    private var withold:List<TripWitholdingTable> = listOf()

    class ViewHolder(view: AdapterShowallwitholdingBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtWitholdingType
        val amount = view.txtWitholdingamount
        val remove = view.icRemove

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterShowallwitholdingBinding=
            AdapterShowallwitholdingBinding.inflate(LayoutInflater.from(activity),parent,false)
        return showAllWitholdingAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return withold.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= withold[position]
        holder.name.text="${role.witholdingType}"
        holder.amount.text = "${role.amount}"
        holder.remove.setOnClickListener {
            if (activity is ShowWitholdingActivity){
                activity.removeItem(role)
            }
        }
    }

    fun showwitholding(list: List<TripWitholdingTable>) {
        withold = list
        notifyDataSetChanged()
    }
}