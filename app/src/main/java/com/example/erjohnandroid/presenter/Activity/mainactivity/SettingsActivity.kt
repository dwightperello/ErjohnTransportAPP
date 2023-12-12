package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.DatePickerDialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.Sycn_TripticketTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.sd_viewmodel
import com.example.erjohnandroid.databinding.ActivitySettingsBinding
import com.example.erjohnandroid.domain.model.request.TripTIcket
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.presenter.adapter.BusAdapter
import com.example.erjohnandroid.util.GlobalVariable

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    lateinit var _binding:ActivitySettingsBinding
    private val sdviemodel: sd_viewmodel by viewModels()
    private val dbViewmodel:RoomViewModel by viewModels()
    private  lateinit var busAdapter: BusAdapter
    private val externalViewModel: externalViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dbViewmodel.getBusinfo(2)


        _binding.btnReloadtickets.setOnClickListener {
            _binding!!.txtReload.text="PLEASE WAIT WHILE SYSTEM FETCH DATA FROM SD-CARD DATABASE \n FETCH TRIP TICKETS...."
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }.time

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate)


                    sdviemodel.selectTicket_bydate(formattedDate)
                    sdviemodel.sdtripticketdate.observe(this, androidx.lifecycle.Observer {
                        state->Processreload(state)
                    })
                    Toast.makeText(this, "Selected Date: $formattedDate", Toast.LENGTH_SHORT).show()
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }


        _binding.btnSaveurl.setOnClickListener {
            if(_binding.etChangeurl.text.toString().isNullOrEmpty()){
                Toast(this).showCustomToast("Please enter new URL",this)
                return@setOnClickListener
            }
            val sharedPreferences = this.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("URL", _binding.etChangeurl.text.toString())
            Toast(this).showCustomToast("Success. PLEASE RESTART DEVICE",this)
            editor.apply()

        }

        _binding.btnChangebus.setOnClickListener {
            externalViewModel.updateSavedDispatched(GlobalVariable.bus!!,GlobalVariable.conductor!!,true,GlobalVariable.employeeName!!,GlobalVariable.driver!!,GlobalVariable.line!!,GlobalVariable.lineid!!,GlobalVariable.deviceName!!,GlobalVariable.tripreverse!!,GlobalVariable.originalTicketnum,GlobalVariable.direction!!,GlobalVariable.ingressoRefId,GlobalVariable.machineName!!,GlobalVariable.permitNumber!!,GlobalVariable.serialNumber!!)

            Toast(this).showCustomToast("Success on bus change",this)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.businfo.observe(this, androidx.lifecycle.Observer {
            state -> ProcessBus(state)
        })
    }
    private var busList:List<BusInfoTableItem>?= null
    private fun ProcessBus(state: List<BusInfoTableItem>?){
        if(!state.isNullOrEmpty()){
            busList=state
            busAdapter = BusAdapter(this)
            _binding.rvChangeBUs.adapter= busAdapter
            _binding.rvChangeBUs.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            busAdapter.showNumber(busList!!)
        }
    }
    var tripticket:ArrayList<TripTicketTable> = arrayListOf()
    private fun Processreload(state: List<TripTicketTable>?){
        if(!state.isNullOrEmpty()){

        try {
            state.forEach {
                var method= TripTicketTable(
                    mPadUnit = it.mPadUnit!!,
                    titcketNumber = it.titcketNumber!!,
                    line = it.line!!,
                    origin = it.origin!!,
                    destination = it.destination!!,
                    passengerType = it.passengerType!!,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp!!,
                    conductorName = it.conductorName!!,
                    driverName = it.driverName!!,
                    qty = it.qty,
                    TripTicketId = 0,
                    KMOrigin = it.KMOrigin,
                    KmDestination = it.KmDestination,
                    tripReverse = it.tripReverse,
                    ingressoRefId = GlobalVariable.ingressoRefId,
                    time = it.time,
//                    totalQty = null,
//                    totalAmount = null,
                    reverse = it.reverse

                )
                tripticket?.add(method)
            }
            dbViewmodel.insertTripticketBulkTwo(tripticket)
            _binding!!.txtReload.append("SUCCESS. Please contact  AZ SOLUTIONS PH for any concers")
        }
        catch (e:java.lang.Exception){
            Log.e("backuperror",e.localizedMessage)
            _binding!!.txtReload.append("Failed. Please contact  AZ SOLUTIONS PH")
        }

        }
        else{
            Toast(this).showCustomToast("NO TRIP TICKETS FOUND",this)
            return
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }

    fun changebus(role: BusInfoTableItem) {
        GlobalVariable.bus= role.busNumber.toString()
        // _binding!!.txtConfirmdispatch.text="BUS #: ${GlobalVariable.bus}"
      //  _binding!!.txtBus.text=("\nBUS #: ${GlobalVariable.bus}")
        Toast(this).showCustomToast("${GlobalVariable.bus}",this)
    }


}