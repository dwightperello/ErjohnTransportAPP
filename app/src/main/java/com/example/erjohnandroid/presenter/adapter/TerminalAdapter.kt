package com.example.erjohnandroid.presenter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.TerminalTable
import com.example.erjohnandroid.databinding.AdapterLineBinding
import com.example.erjohnandroid.databinding.AdapterTerminalsBinding
import com.example.erjohnandroid.presenter.Activity.mainactivity.DispatchActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.IngressoActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.PartialRemitActivity
import com.example.erjohnandroid.presenter.Activity.mainactivity.ReverseActivity

class TerminalAdapter(private val activity: Activity): RecyclerView.Adapter<TerminalAdapter.ViewHolder>() {
    private var terminals:List<TerminalTable> = listOf()

    class ViewHolder(view: AdapterTerminalsBinding) : RecyclerView.ViewHolder(view.root) {
        val name = view.txtterminal

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:AdapterTerminalsBinding=
            AdapterTerminalsBinding.inflate(LayoutInflater.from(activity),parent,false)
        return TerminalAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return terminals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role= terminals[position]
        holder.name.text="${role.name}"



        holder.itemView.setOnClickListener {

            if(activity is DispatchActivity){
                activity.terminals(role)
            }
            else if(activity is PartialRemitActivity){
                activity.partialTerminals(role)
            }
            else if(activity is ReverseActivity){
                activity.reverseTerminals(role)
            }
            else if(activity is IngressoActivity){
                activity.ingressoTerminals(role)
            }

        }
    }

    fun showterminal(list: List<TerminalTable>) {
        terminals = list
        notifyDataSetChanged()
    }
}