package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.databinding.ActivitySynchBinding
import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.presenter.adapter.ExpensesAdapter
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.showCustomToast
import com.google.android.gms.common.internal.GmsLogger
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SynchActivity : AppCompatActivity() {
    lateinit var _binding:ActivitySynchBinding
    private val viewModel: networkViewModel by viewModels()
    private val dbViewmodel:RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySynchBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        _binding.btnPostsynch.isEnabled=false
        _binding.btnPostsynch.setText("PLEASE WAIT WHILE SYSTEM LOADS")


        val delayInSeconds = 5
        val timer = Timer()

        println("Before delay")

        timer.schedule(object : TimerTask() {
            override fun run() {
                dbViewmodel.getTicketsForSynch()
            }
        }, delayInSeconds * 1000L)

        _binding.btnPostsynch.setOnClickListener {
            _binding.btnPostsynch.isEnabled=false
            dbViewmodel.truncateCopyTables()
            _binding!!.txtPostsynching.text=""

            if(!post_ingresso.isNullOrEmpty()){
                viewModel.postIngresso(GlobalVariable.token!!,post_ingresso!!.toList())
            }

        }

    }

    override fun onStart() {
        super.onStart()

        dbViewmodel.ticketsycnh.observe(this, Observer {
            state->Processtripticket(state)
        })

        dbViewmodel.ingresso.observe(this, Observer {
            state-> ProcessIngresso(state)
        })

        dbViewmodel.synch_inspectionreport.observe(this,Observer{
            state->ProcessInspection(state)
        })

        dbViewmodel.synch_mpad.observe(this, Observer {
            state->ProccessmpadAssignment(state)
        })

        dbViewmodel.synch_partial.observe(this,Observer{
            state->ProcessPartialRemit(state)
        })

        dbViewmodel.synch_trip_cost.observe(this, Observer {
            state->ProcessTripCost(state)
        })

        dbViewmodel.synch_trip_witholdingt.observe(this,Observer{
            state->ProcessWitholding(state)
        })

        viewModel.postingresso.observe(this, Observer {
            state->Process_postIngresso(state)
        })

        viewModel.posttripticketBULK.observe(this, Observer {
            state-> Process_triptickets(state)
        })

        viewModel.postmpadassignments.observe(this, Observer {
            state->Process_mpadassignments(state)
        })

        viewModel.postinspection.observe(this,Observer{
            state->Process_inspection(state)
        })

        viewModel.postpartialremit.observe(this, Observer {
            state->Process_partialremit(state)
        })

        viewModel.posttripcosts.observe(this, Observer {
                state->Process_tripcost(state)
        })

        viewModel.postwitholdingBULK.observe(this, Observer {
                state->Process_witholding(state)
        })
    }

    private fun Process_postIngresso(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.text="Posting Ingresso to service...."
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
                    viewModel.posttripticketBULK(GlobalVariable.token!!,post_tripticket!!.toList())
            }
            is ResultState.Error->{
                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }

    private fun Process_triptickets(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Trip Tickets to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
                    viewModel.postmpadAssignments(GlobalVariable.token!!,post_mpadassignment!!.toList())

//                if(post_inspection.isNullOrEmpty()){
//                    _binding!!.txtPostsynching.append("\nInspection Report empty, skipping....")
//                }else{
//                    viewModel.postInspection(GlobalVariable.token!!,post_inspection!!.toList())
//                }


            }
            is ResultState.Error->{
                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }


    private fun Process_mpadassignments(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting mPad Assignment to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")


                if(post_inspection.isNullOrEmpty()){
                    _binding!!.txtPostsynching.append("\nInspection Report empty, skipping....")
                }else{
                    viewModel.postInspection(GlobalVariable.token!!,post_inspection!!.toList())
                }

                if(post_partialremit.isNullOrEmpty()){
                    _binding!!.txtPostsynching.append("\nPartial Remit empty, skipping....")
                }else{
                    viewModel.postPartialRemits(GlobalVariable.token!!,post_partialremit!!.toList())
                }

                if(post_tripcost.isNullOrEmpty()){
                    _binding!!.txtPostsynching.append("\nTrip Expenses empty, skipping....")
                }else{
                    viewModel.postTripcosts(GlobalVariable.token!!,post_tripcost!!.toList())
                }

                if(post_witholding.isNullOrEmpty()){
                    _binding!!.txtPostsynching.append("\nWitholding empty, skipping....")
                }else{
                    viewModel.postWitholdingsBULK(GlobalVariable.token!!,post_witholding!!.toList())
                }

                Toast(this).showCustomToast(" SYCNHING FINISH", this)
            }
            is ResultState.Error->{
                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }


    private fun Process_inspection(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Inspection Report to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
            }
            is ResultState.Error->{

                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }

    private fun Process_partialremit(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Partial Remit to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
            }
            is ResultState.Error->{

                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }

    private fun Process_tripcost(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Trip Expenses to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
            }
            is ResultState.Error->{

                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }

    private fun Process_witholding(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Witholding to service....")
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
            }
            is ResultState.Error->{

                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }





    private var post_tripticket:ArrayList<TripTIcket>?= arrayListOf()
    private fun Processtripticket(state: List<Sycn_TripticketTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.text="PLEASE WAIT WHILE SYSTEM FETCH DATA FROM LOCAL DATABASE \n\n FETCH TRIP TICKETS...."
           state.forEach {
               var method= TripTIcket(
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
                   qty = it.qty
               )
               post_tripticket?.add(method)
           }
            dbViewmodel.getAllIngresso()
        }
        else{
            Toast(this).showCustomToast("NO TRIP TICKETS FOUND",this)
            return
        }
    }

    private var post_ingresso:ArrayList<Ingresso>? = arrayListOf()
    private fun ProcessIngresso(state: List<IngressoTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH INGRESSO DATA....")
            state.forEach {
                if(it.InFault.isNullOrEmpty())it.InFault="none"
                var method= Ingresso(
                  totalCollection = it.TotalCollection!!,
                  manualTicket = it.ManualTicket!!,
                  cancelledTicket = it.CancelledTicket!!,
                  totalExpenses = it.TotalExpenses!!,
                  totalWitholding = it.TotalWitholding!!,
                  driverName = it.DriverName!!,
                  driverCommission = it.DriverCommission!!,
                  conductorName = it.ConductorName!!,
                  conductorCommission = it.ConductorCommission!!,
                  net=it.Net!!,
                  partialRemit = it.PartialRemit!!,
                  shororOver = it.ShororOver!!,
                  inFault = it.InFault!!,
                  dateTimeStamp = it.DateTimeStamp!!,
                  finalRemit = it.FinalRemit!!

                )
                post_ingresso?.add(method)
            }
            dbViewmodel.get_synch_inspection()
        }
        else{
            Toast(this).showCustomToast("NO TRIP TICKETS FOUND",this)
        }
    }

    private var post_inspection:ArrayList<InspectionReports>? = arrayListOf()
    private fun ProcessInspection(state: List<Sycnh_InspectionReportTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH INSPECTION REPORT....")
            state.forEach {
               var method= InspectionReports(
                   inspectorName = it.inspectorName!!,
                   dateTimeStamp = it.dateTimeStamp!!,
                   mPadUnit = it.mPadUnit!!,
                   qty = it.qty!!,
                   line = it.line!!,
                   lineSegment = it.lineSegment!!,
                   direction = it.direction!!,
                   actualPassengerCount = it.actualPassengerCount!!,
                   difference = it.difference!!
               )
                post_inspection?.add(method)
            }
                dbViewmodel.get_synch_mpad()
        }
        else{
            _binding!!.txtPostsynching.append("\n\n NO INSPECTION REPORT FOUND, SKIPPING....")
            dbViewmodel.get_synch_mpad()
        }
    }

    private var post_mpadassignment:ArrayList<mPadAssignments>? = arrayListOf()
    private fun ProccessmpadAssignment(state: List<Synch_mpadAssignmentsTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH mPAD ASSIGNMENT....")
            state.forEach {
                var method= mPadAssignments(
                    mPadUnit = it.mPadUnit!!,
                    dataTimeStamp = it.dataTimeStamp!!,
                    line = it.line!!,
                    busNumber = it.busNumber!!,
                    dispatcherName = it.dispatcherName!!,
                    conductorName = it.conductorName!!,
                    driverName = it.driverName!!
                )
                post_mpadassignment?.add(method)
            }
                dbViewmodel.get_synch_partial_remit()
        }
        else{
            _binding!!.txtPostsynching.append("\n\n NO mPAD ASSIGNMENT FOUND, SKIPPING....")
            dbViewmodel.get_synch_partial_remit()
        }
    }

    private var post_partialremit:ArrayList<PartialRemit>? = arrayListOf()
    private fun ProcessPartialRemit(state: List<Synch_partialremitTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH PARTIAL REMIT....")
            state.forEach {
               var method= PartialRemit(
                   amount = it.Amount!!,
                   amountRemited = it.AmountRemited!!,
                   cashierName = it.CashierName!!,
                   line = it.Line!!,
                   dateTimeStamp = it.DateTimeStamp!!
               )
                post_partialremit?.add(method)
            }
                dbViewmodel.get_synch_trip_cost()
        }
        else{
            _binding!!.txtPostsynching.append("\n\n NO PARTIAL REMIT FOUND, SKIPPING....")
            dbViewmodel.get_synch_trip_cost()
        }
    }

    private var post_tripcost:ArrayList<TripCost>? = arrayListOf()
    private fun ProcessTripCost(state: List<Synch_TripCostTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH TRIP EXPENSES....")
            state.forEach {
                var method=TripCost(
                    costType = it.costType!!,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp!!,
                    line = it.line!!,
                    driverConductorName = it.driverConductorName!!
                )
                post_tripcost?.add(method)
            }
            dbViewmodel.get_synch_trip_witholding()

        }
        else{
            _binding!!.txtPostsynching.append("\n\n NO TRIP EXPENSES FOUND, SKIPPING....")
            dbViewmodel.get_synch_trip_witholding()
        }
    }

    private var post_witholding:ArrayList<TripWitholdings>? = arrayListOf()
    private fun ProcessWitholding(state: List<Synch_TripwitholdingTable>?){
        if(!state.isNullOrEmpty()){

            _binding!!.txtPostsynching.append("\n\n FETCH WITHOLDINGS....")
            state.forEach {
                if(it.name.isNullOrEmpty())it.name="VERIFICATION"
                var method= TripWitholdings(
                    mPadUnit = it.mPadUnit!!,
                    witholdingType = it.witholdingType!!,
                    name = it.name!!,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp
                )
                post_witholding?.add(method)
            }
            _binding.btnPostsynch.isEnabled=true
            _binding.btnPostsynch.setText("SYSTEM READY TO SYNCH")
        }
        else{
            _binding!!.txtPostsynching.append("\n\n NO WITHOLDINGS FOUND, SKIPPING....")
            _binding.btnPostsynch.isEnabled=true
            _binding.btnPostsynch.setText("SYSTEM READY TO SYNCH")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }
}