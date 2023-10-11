package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.databinding.ActivitySharedLoginBinding
import com.example.erjohnandroid.databinding.ActivityTicketdetailsBinding
import com.example.erjohnandroid.presenter.adapter.TicketDetailsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ticketdetailsActivity : AppCompatActivity() {
    lateinit var _binding: ActivityTicketdetailsBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var TicketDetailsAdapter: TicketDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTicketdetailsBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        computeWindowSizeClasses()
        val receivedData = intent.getIntExtra("key",0)
        dbViewmodel.getTripticketdetails(receivedData!!)
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.tripticketdetails.observe(this, Observer {
                state-> Processdetails(state)
        })
    }

    private fun computeWindowSizeClasses() {
        try {
            val layout: LinearLayout = findViewById(R.id.mainLinear)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            var width = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels
            val autoScreenSize= when{
                width > 900f -> {
                    val params: ViewGroup.LayoutParams = layout.layoutParams
                    params.width = 1900
                    layout.layoutParams = params
                }
                else -> {  val params: ViewGroup.LayoutParams = layout.layoutParams
                    params.width = 1900

                    layout.layoutParams = params}
            }
        }catch (e:java.lang.Exception){
            Log.d("ta",e.localizedMessage)
        }
    }

    private fun Processdetails(state: List<TripTicketTable>?){
        if(!state.isNullOrEmpty()){
//            val gson = Gson()
//            val jsonResult = gson.toJson(state)


            TicketDetailsAdapter = TicketDetailsAdapter(this)
            _binding.rvTicketdetails.adapter= TicketDetailsAdapter
            _binding.rvTicketdetails.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            TicketDetailsAdapter.showdetails(state)
        }
    }
}