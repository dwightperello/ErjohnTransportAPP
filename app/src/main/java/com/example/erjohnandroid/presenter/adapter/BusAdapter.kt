package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.databinding.AdapterBusBinding
import com.example.erjohnandroid.databinding.AdapterDriverBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.SettingsActivity

class BusAdapter(private val activity: Activity): RecyclerView.Adapter<BusAdapter.ViewHolder>() {
    private var products:List<BusInfoTableItem> = listOf()
    private var checkedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: AdapterBusBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterBusBinding=
            AdapterBusBinding.inflate(LayoutInflater.from(activity),parent,false)
        return BusAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.busNumber}"

        holder.itemView.setOnClickListener {

                if(activity is DispatchActivity){
                    activity.Bus(role)
                }else if(activity is SettingsActivity){
                    activity.changebus(role)
                }
            }



    }
    fun showNumber(list: List<BusInfoTableItem>) {
        products = list
        notifyDataSetChanged()
    }
}