package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivitySynchBinding
import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.domain.model.response.LinesItem
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.showCustomToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SynchActivity : AppCompatActivity() {
    lateinit var _binding:ActivitySynchBinding
    private val viewModel: networkViewModel by viewModels()
    private val dbViewmodel:RoomViewModel by viewModels()

    private var listOfRefID:ArrayList<Int> = arrayListOf()
    private var ingresso:Ingresso?= null
    private var inspection:ArrayList< Inspectionreport>?= arrayListOf()
    private var mpadassignment:ArrayList<Mpadassignment>?= arrayListOf()
    private var partialremit:ArrayList<Partialremitsdetail>?= arrayListOf()
    private var tripcost:ArrayList<costtrip>?= arrayListOf()
    private var tripwitholding:ArrayList<Tripwitholding>?= arrayListOf()
    private var triptickets:ArrayList<tickettrip>?= arrayListOf()
    private var tripReverse:ArrayList<TripReverse>?= arrayListOf()
    private var logReport:ArrayList<LogReports>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySynchBinding.inflate(layoutInflater)
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
        _binding.btnPostsynch.isEnabled=false
        _binding.btnPostsynch.setText("PLEASE WAIT!")
        _binding!!.txtPostsynching.append("Please wait while system prepares to synch")


        val delayInSeconds = 5
        val timer = Timer()



        timer.schedule(object : TimerTask() {
            override fun run() {

                dbViewmodel.getAllIngressoRefID()
            }
        }, delayInSeconds * 1000L)

        _binding.btnPostsynch.setOnClickListener {
            dbViewmodel.truncateCopyTables()
            _binding.btnPostsynch.isEnabled=false

            _binding!!.txtPostsynching.text=""
            finish()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );

//            if(!post_ingresso.isNullOrEmpty()){
//                viewModel.postIngresso(GlobalVariable.token!!,post_ingresso!!.toList())
//            }

        }

    }
    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
    private fun ProcessAllIngressorefid(state: List<Int>?){
        _binding!!.txtPostsynching.append("\nChecking data")
        if(!state.isNullOrEmpty()) {
            val formattedDateTime = getdate()
           listOfRefID.addAll(state)

           listOfRefID.forEach {
                dbViewmodel.getAllIngresso(it)
                dbViewmodel.get_synch_inspection(it)
                dbViewmodel.get_synch_mpad(it)
                dbViewmodel.get_synch_partial_remit(it)
                dbViewmodel.get_synch_trip_cost(it)
                dbViewmodel.get_synch_trip_witholding(it)
                dbViewmodel.getTicketsForSynch(it)
                dbViewmodel.getTripReverseForSynch(it)
                dbViewmodel.get_synch_logReport(it)
                var method= postAllItem(
                    totalCollection = ingresso?.totalCollection ?:0.0,
                    manualTicket = ingresso?.manualTicket ?: 0.0,
                    cancelledTicket = ingresso?.cancelledTicket ?:0.0,
                    totalExpenses = ingresso?.totalExpenses ?:0.0,
                    totalWitholding = ingresso?.totalWitholding ?:0.0,
                    driverName = ingresso?.driverName ?:"Driver",
                    driverCommission = ingresso?.driverCommission ?:0.0,
                    conductorName = ingresso?.conductorName?:"Conductor",
                    conductorCommission = ingresso?.conductorCommission ?:0.0,
                    net = ingresso?.net ?:0.0,
                    partialRemit = ingresso?.partialRemit ?:0.0,
                    finalRemit = ingresso?.finalRemit ?:0.0,
                    shororOver = ingresso?.shororOver ?:0.0,
                    inFault =  ingresso?.inFault ?:"none",
                    dateTimeStamp = formattedDateTime,


                    mpadassignments = mpadassignment?.toList()  ,
                    partialremitsdetails = partialremit?.toList() ,
                    inspectionreport = inspection?.toList() ,
                    tripcost = tripcost!!.toList() ,
                    triptickets = triptickets?.toList() ,
                    tripwitholding = tripwitholding?.toList(),
                    terminal =ingresso?.terminal ?:"None Selected",
                    tripreverse = tripReverse?.toList(),
                    logreport =logReport!!.toList()

                )

//            val gson = Gson()
//           val jsonResult = gson.toJson(method)

               viewModel.postIngressoALL(GlobalVariable.token!!,method)
               inspection= arrayListOf()
               mpadassignment= arrayListOf()
               partialremit= arrayListOf()
               tripcost= arrayListOf()
               tripwitholding=arrayListOf()
               triptickets= arrayListOf()
               tripReverse= arrayListOf()
               logReport= arrayListOf()

           }




        }else{
            Toast(this).showCustomToast("ALREADY SYNCH", this)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()

        }
    }
    override fun onStart() {
        super.onStart()
        dbViewmodel.ingressoRefids.observe(this, Observer {
                state-> ProcessAllIngressorefid(state)
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

        dbViewmodel.ticketsycnh.observe(this, Observer {
                state->Processtripticket(state)
        })

        viewModel.postIngressoALL.observe(this, Observer {
            state -> Processadding(state)
        })

        dbViewmodel.tripreversesycnh.observe(this,Observer{
            state->ProcessTripReverseAll(state)
        })

        dbViewmodel.synch_log_report.observe(this,Observer{
                state->ProcessNetworkLogreport(state)
        })


    }


    private fun Processadding(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.append("\nPosting Ingresso to service....")
            }


            is ResultState.Success->{
                Toast(this).showCustomToast("Synching Success", this)
                _binding!!.txtPostsynching.append("\nSYNCHING SUCCESS")
                _binding.btnPostsynch.setText("CLOSE NOW")
                _binding.btnPostsynch.isEnabled=true
            }
            is ResultState.Error->{
                Toast(this).showCustomToast(state.exception.toString(), this)
                _binding!!.txtPostsynching.append("\nFailed")
            }
            else -> {}
        }
    }

    private fun Process_postIngresso(state: ResultState<ResponseBody>){
        when(state){
            is ResultState.Loading ->{
                _binding!!.txtPostsynching.text="Posting Ingresso to service...."
            }


            is ResultState.Success->{
                _binding!!.txtPostsynching.append("\nSuccess")
                  //  viewModel.posttripticketBULK(GlobalVariable.token!!,post_tripticket!!.toList())
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
                  //  viewModel.postmpadAssignments(GlobalVariable.token!!,post_mpadassignment!!.toList())

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


//                if(post_inspection.isNullOrEmpty()){
//                    _binding!!.txtPostsynching.append("\nInspection Report empty, skipping....")
//                }else{
//                    viewModel.postInspection(GlobalVariable.token!!,post_inspection!!.toList())
//                }

//                if(post_partialremit.isNullOrEmpty()){
//                    _binding!!.txtPostsynching.append("\nPartial Remit empty, skipping....")
//                }else{
//                    viewModel.postPartialRemits(GlobalVariable.token!!,post_partialremit!!.toList())
//                }

//                if(post_tripcost.isNullOrEmpty()){
//                    _binding!!.txtPostsynching.append("\nTrip Expenses empty, skipping....")
//                }else{
//                    viewModel.postTripcosts(GlobalVariable.token!!,post_tripcost!!.toList())
//                }

//                if(post_witholding.isNullOrEmpty()){
//                    _binding!!.txtPostsynching.append("\nWitholding empty, skipping....")
//                }else{
//                   // viewModel.postWitholdingsBULK(GlobalVariable.token!!,post_witholding!!.toList())
//                }

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




    private fun ProcessNetworkLogreport(state: List<Synch_LogReport>?){
        if(!state.isNullOrEmpty()){


            state.forEach {
                var method= NetworkLogreportItem(
                    dateTimeStamp = it.dateTimeStamp,
                    deviceName = it.deviceName,
                    description = it.description
                )


                var method2 = LogReports(
                    id = 0,
                    deviceName = method.deviceName,
                    description = method.description,
                    dateTimeStamp = method.dateTimeStamp,
                    ingressoId = 0
                )
                logReport?.add(method2)
                // triptickets?.add(method2)
                _binding!!.txtPostsynching.text="\n\n FETCH TRIP LOGS...."

            }
            // dbViewmodel.getAllIngresso()
        }
        else{
            Toast(this).showCustomToast("NO TRIP REVERSE FOUND",this)
            return
        }
    }

    private fun Processtripticket(state: List<Sycn_TripticketTable>?){
        if(!state.isNullOrEmpty()){


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
                   qty = it.qty,
                   reverse = it.reverse!!
               )
               var method2 = tickettrip(
                 amount = method.amount,
                   conductorName = method.conductorName,
                   dateTimeStamp = method.dateTimeStamp,
                   destination = method.destination,
                   driverName = method.driverName,
                   id = 0,
                   ingreId = 0,
                   line = method.line,
                   mPadUnit = method.mPadUnit,
                   origin = method.origin,
                   passengerType = method.passengerType,
                   qty = method.qty,
                   titcketNumber = method.titcketNumber,
                   reverse = method.reverse



               )

               triptickets?.add(method2)
               _binding!!.txtPostsynching.text="\n\n FETCH TRIP TICKETS...."

           }
           // dbViewmodel.getAllIngresso()
        }
        else{
            Toast(this).showCustomToast("NO TRIP TICKETS FOUND",this)
            return
        }
    }

    private fun ProcessTripReverseAll(state: List<Synch_TripReverseTable>?){
        if(!state.isNullOrEmpty()){


            state.forEach {
                var method= TripReverseItem(
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp,
                    deviceName = it.deviceName,
                    direction = it.direction,
                    reverseId = it.reverseId,
                    terminal = it.terminal
                )


                var method2 = TripReverse(
                   id = 0,
                    deviceName = method.deviceName,
                    amount = method.amount,
                    direction = method.direction,
                    dateTimeStamp = method.dateTimeStamp,
                    reverseId = method.reverseId,
                    terminal = method.terminal,
                    ingId = 0
                )
                tripReverse?.add(method2)
               // triptickets?.add(method2)
                _binding!!.txtPostsynching.text="\n\n FETCH TRIP REVERSE...."

            }
            // dbViewmodel.getAllIngresso()
        }
        else{
            Toast(this).showCustomToast("NO TRIP REVERSE FOUND",this)
            return
        }
    }


    private fun ProcessIngresso(state: List<IngressoTable>?){
        if(!state.isNullOrEmpty()){


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
                  finalRemit = it.FinalRemit!!,
                    terminal = it.terminal!!
                )
            ingresso=method
                _binding!!.txtPostsynching.append("\n\n FETCH INGRESSO DATA....")
            }

        }
        else{
            Toast(this).showCustomToast("NO TRIP TICKETS FOUND",this)
        }
    }


    private fun ProcessInspection(state: List<Sycnh_InspectionReportTable>?){
        if(!state.isNullOrEmpty()){


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
                var method2 = Inspectionreport(
                    actualPassengerCount = method.actualPassengerCount,
                    dateTimeStamp = method.dateTimeStamp,
                    difference = method.difference,
                    direction = method.direction,
                    id = 0,
                    ingId = 0,
                    inspectorName = method.inspectorName,
                    line = method.line,
                    lineSegment = method.lineSegment,
                    mPadUnit = method.mPadUnit,
                    qty = method.qty


                )

                inspection?.add(method2)
                _binding!!.txtPostsynching.append("\n\n FETCH INSPECTION REPORT....")
            }

        }

    }


    private fun ProccessmpadAssignment(state: List<Synch_mpadAssignmentsTable>?){
        if(!state.isNullOrEmpty()){


            state.forEach {
                var method= mPadAssignments(
                    mPadUnit = it.mPadUnit!!,
                    dataTimeStamp = it.dataTimeStamp!!,
                    line = it.line!!,
                    busNumber = it.busNumber!!,
                    dispatcherName = it.dispatcherName!!,
                    conductorName = it.conductorName!!,
                    driverName = it.driverName!!,
                    terminal = it.terminal!!
                )
                var method2= Mpadassignment(
                    busNumber = method.busNumber,
                    conductorName = method.conductorName,
                    dataTimeStamp = method.dataTimeStamp,
                    dispatcherName = method.dispatcherName,
                    driverName = method.driverName,
                    id = 0,
                    ingressoId = 0,
                    line = method.line,
                    mPadUnit = method.mPadUnit,
                    terminal = method.terminal


                )

                mpadassignment?.add(method2)
                _binding!!.txtPostsynching.append("\n\n FETCH mPAD ASSIGNMENT....")
            }

        }

    }


    private fun ProcessPartialRemit(state: List<Synch_partialremitTable>?){
        if(!state.isNullOrEmpty()){


            state.forEach {
               var method= PartialRemit(
                   amount = it.Amount!!,
                   amountRemited = it.AmountRemited!!,
                   cashierName = it.CashierName!!,
                   line = it.Line!!,
                   dateTimeStamp = it.DateTimeStamp!!,
                   terminal = it.terminal!!
               )
                var method2 = Partialremitsdetail(
                    amount = method.amount,
                    amountRemited = method.amountRemited,
                    cashierName = method.cashierName,
                    dateTimeStamp = method.dateTimeStamp,
                    id = 0,
                    ingressId = 0,
                    line = method.line,
                    terminal = method.terminal


                )

                partialremit?.add(method2)
                _binding!!.txtPostsynching.append("\n\n FETCH PARTIAL REMIT....")
            }

        }

    }


    private fun ProcessTripCost(state: List<Synch_TripCostTable>?){
        if(!state.isNullOrEmpty()){


            state.forEach {
                var method=TripCost(
                    costType = it.costType!!,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp!!,
                    line = it.line!!,
                    driverConductorName = it.driverConductorName!!
                )
                var method2= costtrip(
                    amount = method.amount,
                    costType = method.costType,
                    dateTimeStamp = method.dateTimeStamp,
                    driverConductorName = method.driverConductorName,
                    id = 0,
                    ingresId = 0,
                    line = method.line

                )

                tripcost?.add(method2)
                _binding!!.txtPostsynching.append("\n\n FETCH TRIP EXPENSES....")
            }


        }

    }


    private fun ProcessWitholding(state: List<Synch_TripwitholdingTable>?){
        if(!state.isNullOrEmpty()) {


            state.forEach {
                if (it.name.isNullOrEmpty()) it.name = "VERIFICATION"
                var method = TripWitholdings(
                    mPadUnit = it.mPadUnit!!,
                    witholdingType = it.witholdingType!!,
                    name = it.name!!,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp
                )
                var method2 = Tripwitholding(
                    amount = method.amount,
                    dateTimeStamp = method.dateTimeStamp,
                    id = -0,
                    ingrId = 0,
                    mPadUnit = method.mPadUnit,
                    name = method.name,
                    witholdingType = method.witholdingType


                )

                tripwitholding?.add(method2)
                _binding!!.txtPostsynching.append("\n\n FETCH WITHOLDINGS....")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }


    //region TEST IF STILL NEEDED.. PUT IN START
//
//    viewModel.postingresso.observe(this, Observer {
//        state->Process_postIngresso(state)
//    })
//
//    viewModel.posttripticketBULK.observe(this, Observer {
//        state-> Process_triptickets(state)
//    })
//
//    viewModel.postmpadassignments.observe(this, Observer {
//        state->Process_mpadassignments(state)
//    })
//
//    viewModel.postinspection.observe(this,Observer{
//        state->Process_inspection(state)
//    })
//
//    viewModel.postpartialremit.observe(this, Observer {
//        state->Process_partialremit(state)
//    })
//
//    viewModel.posttripcosts.observe(this, Observer {
//        state->Process_tripcost(state)
//    })
//
//    viewModel.postwitholdingBULK.observe(this, Observer {
//        state->Process_witholding(state)
//    })
    //endregion



}