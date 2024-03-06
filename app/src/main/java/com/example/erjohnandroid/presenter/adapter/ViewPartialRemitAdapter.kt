package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.PartialRemitTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.databinding.AdapterTripticketdetailsBinding
import com.example.erjohnandroid.databinding.AdapterViewPartialremitBinding

class ViewPartialRemitAdapter(private val activity: Activity): RecyclerView.Adapter<ViewPartialRemitAdapter.ViewHolder>() {
    private var remits:List<PartialRemitTable> = listOf()

    class ViewHolder(view: AdapterViewPartialremitBinding) : RecyclerView.ViewHolder(view.root) {
        val name= view.txtname
        val amount=view.txtremitedamount
        val terminal=view.txtterminal
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterViewPartialremitBinding=
            AdapterViewPartialremitBinding.inflate(LayoutInflater.from(activity),parent,false)
        return ViewPartialRemitAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return remits.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v= remits[position]
        holder.name.text=v.CashierName
        holder.amount.text= v.AmountRemited.toString()
        holder.terminal.text=v.terminal

    }

    fun showdremits(list: List<PartialRemitTable>) {
        remits = list
        notifyDataSetChanged()
    }
}