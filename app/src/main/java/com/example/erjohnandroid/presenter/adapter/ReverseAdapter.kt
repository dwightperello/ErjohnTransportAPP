package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.database.Model.convertions.TripTicketGroupCount
import com.example.erjohnandroid.databinding.AdapterDriverBinding
import com.example.erjohnandroid.databinding.AdapterReverseBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.PartialRemitActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ReverseActivity
import java.text.DecimalFormat

class ReverseAdapter(private val activity: Activity): RecyclerView.Adapter<ReverseAdapter.ViewHolder>() {

    private var products:List<TripTicketGroupCount> = listOf()

    class ViewHolder(view: AdapterReverseBinding) : RecyclerView.ViewHolder(view.root) {
        val reverse = view.txtReverse
        val count =view.txtReverseticketcount
        val sum= view.txtSum
        val button = view.btnReverseview

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterReverseBinding=
            AdapterReverseBinding.inflate(LayoutInflater.from(activity),parent,false)
        return ReverseAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.reverse.text="${role.tripReverse}"
        holder.count.text="Tickets: ${role.group_count.toString()}"
        val decimalVat = DecimalFormat("#.00")
        val ans = decimalVat.format(role.sumamount)
        holder.sum.text="Total amount ${ans}"

        holder.button.setOnClickListener {
            if(activity is ReverseActivity){
               activity.showtickets(role)
            }else if (activity is PartialRemitActivity){
               activity.showmodaltickets(role)
            }
        }
    }

    fun showreverse(list: List<TripTicketGroupCount>) {
        products = list
        notifyDataSetChanged()
    }
}