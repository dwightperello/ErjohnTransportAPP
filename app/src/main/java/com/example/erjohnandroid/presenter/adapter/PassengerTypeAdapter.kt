package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.PassengerTypeTable

import com.example.erjohnandroid.databinding.AdapterPassengertypeBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.TIcketingActivity

class PassengerTypeAdapter(private val activity: Activity): RecyclerView.Adapter<PassengerTypeAdapter.ViewHolder>() {

    private var products:List<PassengerTypeTable> = listOf()
    private var checkedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: AdapterPassengertypeBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtrolename
        val checkbox= view.checkbox

    }

    interface OnCheckboxClickListener {
        fun onCheckboxClick(position: Int)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterPassengertypeBinding=
            AdapterPassengertypeBinding.inflate(LayoutInflater.from(activity),parent,false)
        return PassengerTypeAdapter.ViewHolder(binding)
    }



    override fun getItemCount(): Int {
        return products.size
    }

    private var preposition:Int?= null
    private var selectedPosition = -1
    var checkboxClickListener: OnCheckboxClickListener? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= products[position]
        holder.name.text="${role.name}"

        holder.checkbox.isChecked = position == checkedPosition
        holder.checkbox.setOnClickListener {
            if (position == checkedPosition) {
                holder.checkbox.isChecked = true
            } else {
                notifyItemChanged(checkedPosition)
                checkedPosition = position
                if(activity is TIcketingActivity){
                   // activity.getPasstype(role)
                }
            }
        }


    }

    fun showRoles(list: List<PassengerTypeTable>) {
        products = list
        notifyDataSetChanged()
    }

    fun clearSelection() {
        checkedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}