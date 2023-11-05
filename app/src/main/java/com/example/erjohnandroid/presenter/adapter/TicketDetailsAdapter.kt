package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.databinding.AdapterRolesBinding
import com.example.erjohnandroid.databinding.AdapterTripticketdetailsBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ReverseActivity

class TicketDetailsAdapter(private val activity: Activity): RecyclerView.Adapter<TicketDetailsAdapter.ViewHolder>() {
    private var products:List<TripTicketTable> = listOf()

    class ViewHolder(view: AdapterTripticketdetailsBinding) : RecyclerView.ViewHolder(view.root) {
        val id = view.txtid
        val amount= view.txtAmount
        val origin=view.txtOrigin
        val destination=view.txtDestination

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterTripticketdetailsBinding=
            AdapterTripticketdetailsBinding.inflate(LayoutInflater.from(activity),parent,false)
        return TicketDetailsAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.id.text="000${role.titcketNumber}"
        holder.amount.text="${role.amount}"
        holder.origin.text="${role.origin}"
        holder.destination.text="${role.destination}"



        holder.itemView.setOnClickListener {

            if(activity is ReverseActivity){
               // activity.COnductor(role)
            }

        }
    }

    fun showdetails(list: List<TripTicketTable>) {
        products = list
        notifyDataSetChanged()
    }
}