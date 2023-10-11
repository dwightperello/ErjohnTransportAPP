package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.databinding.AdapterDriverBinding
import com.example.erjohnandroid.databinding.AdapterRolesBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity

class DriverAdapter(private val activity: Activity): RecyclerView.Adapter<DriverAdapter.ViewHolder>(){

    private var products:List<EmployeesTable> = listOf()
    private var checkedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: AdapterDriverBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterDriverBinding=
            AdapterDriverBinding.inflate(LayoutInflater.from(activity),parent,false)
        return DriverAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.name}"


        holder.itemView.setOnClickListener {

                if(activity is DispatchActivity){
                    activity.Driver(role)
                }

        }


    }

    fun showDriver(list: List<EmployeesTable>) {
        products = list
        notifyDataSetChanged()
    }
}