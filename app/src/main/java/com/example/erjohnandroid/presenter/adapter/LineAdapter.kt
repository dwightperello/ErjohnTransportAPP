package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.databinding.AdapterDriverBinding
import com.example.erjohnandroid.databinding.AdapterLineBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity

class LineAdapter(private val activity: Activity): RecyclerView.Adapter<LineAdapter.ViewHolder>() {
    private var products:List<LinesTable> = listOf()
    private var checkedPosition = RecyclerView.NO_POSITION


    class ViewHolder(view: AdapterLineBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterLineBinding=
            AdapterLineBinding.inflate(LayoutInflater.from(activity),parent,false)
        return LineAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.name}"



        holder.itemView.setOnClickListener {

                if(activity is DispatchActivity){
                    activity.Liness(role)
                }

        }

    }

    fun showLines(list: List<LinesTable>) {
        products = list
        notifyDataSetChanged()
    }
}