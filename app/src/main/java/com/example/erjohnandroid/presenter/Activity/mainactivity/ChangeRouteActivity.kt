package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.PrimaryKey
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.TripReverseTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.databinding.ActivityChangeRouteBinding
import com.example.erjohnandroid.presenter.adapter.ChangeRouteAdapter
import com.example.erjohnandroid.presenter.adapter.LineAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChangeRouteActivity : AppCompatActivity() {
    private var _binding:ActivityChangeRouteBinding?= null
    private  lateinit var changeRouteAdapter: ChangeRouteAdapter
    private val dbViewmodel: RoomViewModel by viewModels()
    private val externalViewModel:externalViewModel by viewModels()
    private var tripreverse:ArrayList<TripReverseTable> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityChangeRouteBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val window = window
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

        dbViewmodel.getAllLines()
        initCheckbox()

        _binding!!.btnsavechangeroute.setOnClickListener {
//            if(_binding?.cbNorth?.isChecked==false || _binding?.cbSouth?.isChecked==false){
//                Toast(this).showCustomToast("Select Direction",this)
//                return@setOnClickListener
//            }
            val formattedDateTime = getdate()
            var method= TripReverseTable(
                Id = 0,
                amount = GlobalVariable.ReverseTotalAmount,
                dateTimeStamp = formattedDateTime,
                deviceName = GlobalVariable.deviceName!!,
                direction = GlobalVariable.direction!!,
                reverseId = GlobalVariable.tripreverse!!,
                terminal = GlobalVariable.terminal!!,
                ingressoRefId = GlobalVariable.ingressoRefId
            )
            tripreverse.add(method)
            dbViewmodel.insertTripReverse(tripreverse)

            GlobalVariable.remainingPass=0
            GlobalVariable.destinationcounter=1
            GlobalVariable.origincounter=0
            GlobalVariable.tripreverse = GlobalVariable.tripreverse?.plus(1)

            externalViewModel.updateSavedDispatched(GlobalVariable.bus!!,GlobalVariable.conductor!!,true,GlobalVariable.employeeName!!,GlobalVariable.driver!!,GlobalVariable.line!!,GlobalVariable.lineid!!,GlobalVariable.deviceName!!,GlobalVariable.tripreverse!!,GlobalVariable.originalTicketnum,GlobalVariable.direction!!,GlobalVariable.ingressoRefId,GlobalVariable.machineName!!,GlobalVariable.permitNumber!!,GlobalVariable.serialNumber!!)
            GlobalVariable.ReverseTotalAmount=0.0
            finish()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }

        _binding!!.btnclose.setOnClickListener {
            this.finish()
        }
    }

    fun Liness(role: LinesTable) {
        GlobalVariable.line= role.name
        GlobalVariable.lineid= role.id
        if(role.remarks.equals("SB")) GlobalVariable.direction= "South"
        else GlobalVariable.direction= "North"
        Toast(this).showCustomToast("${GlobalVariable.line}",this)
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.allLines.observe(this, Observer {
                state -> ProcessLines(state)
        })
    }

    private fun ProcessLines(state: List<LinesTable>?){
        _binding?.txtlinenow!!.text="Current Line ${GlobalVariable.line}"
        if(!state.isNullOrEmpty()){

            changeRouteAdapter = ChangeRouteAdapter(this)
            _binding?.rvChangeroute?.adapter= changeRouteAdapter
            _binding?.rvChangeroute?.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            changeRouteAdapter.showLines(state!!)
        }
    }

    val initCheckbox={
        _binding?.cbNorth?.setOnClickListener {

//            if(!isNorthAllowed){
//                Toast(this).showCustomToast("North Bound is not allowed",this)
//                _binding.cbNorth.isChecked=false
//                return@setOnClickListener
//            }
            if(_binding?.cbSouth!!.isChecked) _binding?.cbSouth?.isChecked=false
            GlobalVariable.direction="North"

        }
        _binding?.cbSouth?.setOnClickListener {
            if(_binding?.cbNorth!!.isChecked)_binding?.cbNorth?.isChecked=false
            GlobalVariable.direction="South"

        }



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }
    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}