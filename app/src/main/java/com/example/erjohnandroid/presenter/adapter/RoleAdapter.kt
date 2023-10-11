package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.erjohnandroid.database.Model.CompanyRolesTable
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.databinding.AdapterRolesBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import java.text.DecimalFormat
import java.util.*

class RoleAdapter(private val activity: Activity): RecyclerView.Adapter<RoleAdapter.ViewHolder>(){

    private var products:List<EmployeesTable> = listOf()

    private var checkedPosition = RecyclerView.NO_POSITION



    class ViewHolder(view: AdapterRolesBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterRolesBinding=
            AdapterRolesBinding.inflate(LayoutInflater.from(activity),parent,false)
        return RoleAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.name}"


        holder.itemView.setOnClickListener {

                if(activity is DispatchActivity){
               activity.COnductor(role)
               }

        }

//        holder.itemView.setOnClickListener {
//            if(activity is DispatchActivity){
//               activity.COnductor(role)
//            }
//        }
    }

    fun showCOnductor(list: List<EmployeesTable>) {
        products = list
        notifyDataSetChanged()
    }




}