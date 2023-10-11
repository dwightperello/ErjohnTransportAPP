package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.Model.convertions.TripCostTotal
import com.example.erjohnandroid.database.Model.convertions.WitholdingTotal
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityIngressoBinding
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.GlobalVariable.bus
import com.example.erjohnandroid.util.GlobalVariable.cashiername
import com.example.erjohnandroid.util.GlobalVariable.conductor
import com.example.erjohnandroid.util.GlobalVariable.destinationcounter
import com.example.erjohnandroid.util.GlobalVariable.direction
import com.example.erjohnandroid.util.GlobalVariable.driver
import com.example.erjohnandroid.util.GlobalVariable.inspectorname
import com.example.erjohnandroid.util.GlobalVariable.isDispatched
import com.example.erjohnandroid.util.GlobalVariable.line
import com.example.erjohnandroid.util.GlobalVariable.lineid
import com.example.erjohnandroid.util.GlobalVariable.linesegment
import com.example.erjohnandroid.util.GlobalVariable.origincounter
import com.example.erjohnandroid.util.GlobalVariable.remainingPass
import com.example.erjohnandroid.util.GlobalVariable.ticketcounter
import com.example.erjohnandroid.util.GlobalVariable.ticketnumber
import com.example.erjohnandroid.util.GlobalVariable.tripreverse
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class IngressoActivity : AppCompatActivity() {
    lateinit var _binding:ActivityIngressoBinding
    private val dbViewmodel: RoomViewModel by viewModels()

    var totalamount:Double=0.0
    var manualticket:Double=0.0
    var cancelticket:Double=0.0
    var expenses:String?= "0.0"
    var witholding:String="0.0"
    var drivercommision:String="0.0"
    var conductorcommision:String="0.0"
    var infault:String?= null
    var totalTripcost:Double=0.0
    var totalwitholding:Double=0.0



    var EXPENSES_ACTIVITY=1
    var WITHOLD=2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityIngressoBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)


            window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        _binding.btnigresso.isEnabled=true

        dbViewmodel.getTotalAmountTrip()
        dbViewmodel.getPartialRemit()

        _binding.txtdrivername.text=GlobalVariable.driver
        _binding.txtconductorname.text=GlobalVariable.conductor

        _binding.btnAddmanual.setOnClickListener {
            var manual= _binding.etmanualticket.text.toString()
            if(manual.isEmpty()) return@setOnClickListener
            val stringWithoutSpaces = manual.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")
            manualticket += stringcount.toDouble()
            totalamount +=manualticket
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)
            _binding.txttotalgross.text="${ans}"
//            _binding.etmanualticket.setText("")
            _binding.txtnetcollection.text="${ans}"
        }

        _binding.btnCancelledticket.setOnClickListener {
            var manual= _binding.etCancelledticket.text.toString()
            if(manual.isEmpty()) return@setOnClickListener
            val stringWithoutSpaces = manual.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")
            if(totalamount<=stringcount.toDouble()) return@setOnClickListener
            cancelticket += stringcount.toDouble()
            totalamount -= cancelticket
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)
            _binding.txtnetcollection.text="${ans}"
//            _binding.etCancelledticket.setText("")
        }

        _binding.btnExpenses.setOnClickListener {
            val intent = Intent(this, ExpensesActivity::class.java)
            startActivityForResult(intent,EXPENSES_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding.btnWitholding.setOnClickListener {
            val intent = Intent(this, WitholdingActivity::class.java)
            startActivityForResult(intent,WITHOLD)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding.btncomputefinalremit.setOnClickListener {
            var finalremit = _binding.etfinalremit.text.toString()
            if(finalremit.isNullOrEmpty()){
                Toast.makeText(this,"Input amount",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dbViewmodel.getTotalTripcost()
            dbViewmodel.getTotalwithlding()

            var partialremit= _binding.txtpartialremit.text.toString()
            if(partialremit.isNullOrEmpty()) partialremit="0.0"

            var addamount= finalremit.toDouble() + partialremit.toDouble() + totalTripcost +totalwitholding

            var net= _binding.txtnetcollection.text.toString()

            var compute= net.toDouble()- addamount.toDouble()
            if(compute>0) _binding.viewshort.isVisible=true
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(compute)
            _binding.etshortover.text= ans.toString()
        }

        _binding.cbshortdriver.setOnClickListener {
            if(_binding.cbshortconductor.isChecked) _binding.cbshortconductor.isChecked=false
            _binding.cbshortdriver.isChecked=true
            infault="driver"
        }

        _binding.cbshortconductor.setOnClickListener {
            if(_binding.cbshortdriver.isChecked) _binding.cbshortdriver.isChecked=false
            _binding.cbshortconductor.isChecked=true
            infault="conductor"
        }

        _binding.btnigresso.setOnClickListener {
            _binding.btnigressoreprint.isEnabled=true
            if(_binding.etshortover.text.toString().isNullOrEmpty()) {
                Toast.makeText(this,"NO COMPUTATION MADE",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formattedDateTime = getCurrentDateInFormat()

            var method= IngressoTable(
                Id = 0,
                TotalCollection =  convertDecimal(_binding.txttotalcollection.text.toString()),
                ManualTicket = manualticket,
                CancelledTicket = cancelticket,
                TotalExpenses = convertDecimal(expenses),
                TotalWitholding = convertDecimal(witholding),
                DriverName = GlobalVariable.driver,
                DriverCommission = convertDecimal(drivercommision),
                ConductorName = GlobalVariable.conductor,
                ConductorCommission = convertDecimal(conductorcommision),
                Net = convertDecimal(_binding.txtnetcollection.text.toString()),
                PartialRemit = convertDecimal(_binding.txtpartialremit.text.toString()),
                FinalRemit = convertDecimal(_binding.etfinalremit.text.toString()),
                ShororOver = convertDecimal(_binding.etshortover.text.toString()),
                InFault = infault,
                DateTimeStamp = formattedDateTime.toString()

            )

            try {
                dbViewmodel.insertIngersso(method)
                dbViewmodel.getTripticket()
                dbViewmodel.tripticket.observe(this, Observer {
                        state->ProcessTriptickets(state)
                })

                dbViewmodel.inspectionreport.observe(this,Observer{
                        state->Processinspectionreport(state)
                })

                dbViewmodel.mpadAssignment.observe(this,Observer{
                        state-> Processmpadassignment(state)
                })

                dbViewmodel.partialremit.observe(this,Observer{
                        state-> Processpartialremit(state)
                })

                dbViewmodel.tripcost.observe(this,Observer{
                        state->ProcessTripcost(state)
                })

                dbViewmodel.tripwitholding.observe(this,Observer{
                        state->Processtripwitholding(state)
                })
               // dbViewmodel.truncatetables()
            }catch (e:java.lang.Exception){
                Log.e("erro",e.message.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.totaltripamount.observe(this, Observer {
            state-> ProcessTotal(state)
        })

        dbViewmodel.partialremit.observe(this, Observer {
            state->ProcessPartialremit(state)
        })

        dbViewmodel.tripcost.observe(this, Observer {
            state-> ProcessExpenses(state)
        })

        dbViewmodel.tripwitholding.observe(this, Observer {
            state->Processwitholding(state)
        })

        dbViewmodel.totalTripcost?.observe(this,Observer{
            state-> ProcessTotaltripcost(state)
        })

        dbViewmodel.totalwithodling?.observe(this,Observer{
                state-> ProcessTotalwitholding(state)
        })


    }





    override fun onBackPressed() {
        super.onBackPressed()
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)

        finish()


        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EXPENSES_ACTIVITY) {
            dbViewmodel.getTripcost()
        }
        else if(requestCode==WITHOLD){
            dbViewmodel.getTripwitholding()
        }

    }

    val computeCommissions:(Double)-> Unit={
        val driver= totalamount * 0.09
        val conductor=totalamount* 0.07
        val amount= driver + conductor
        val decimalVat = DecimalFormat("#.00")
        val ans = decimalVat.format(amount)
        _binding.txttotalcommision.text=ans.toString()

        drivercommision = decimalVat.format(driver)
        _binding.txtdrivercommision.text=drivercommision.toString()
        conductorcommision=decimalVat.format(conductor)
        _binding.txtconductorcommision.text=conductorcommision.toString()
    }

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    val convertDecimal:(String?)-> Double={
        var item= it?.toDouble()
        val decimalVat = DecimalFormat("#.00")
        var ans = decimalVat.format(item)
        ans.toDouble()

    }

    //region ALL PROCESS
    val ProcessTotal:(state: TicketTotal?) ->Unit={
        if(it!=null) {
            totalamount=it.total
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)
            _binding.txttotalcollection.text="${ans}"
            _binding.txttotalgross.text="${ans}"
            _binding.txtnetcollection.text="${ans}"

            computeCommissions(totalamount)
        }
    }

    val ProcessTotaltripcost:(state: TripCostTotal?) ->Unit={
        if(it?.total!=null) {
          totalTripcost= it?.total!!
        }else totalTripcost=0.0
    }

    val ProcessTotalwitholding:(state: WitholdingTotal?) ->Unit={
        if(it?.total!=null) {
            totalwitholding= it?.total!!
        }else totalwitholding=0.0
    }

    val ProcessPartialremit:(state:List<PartialRemitTable>) ->Unit={
        var partial:Double=0.0
        if(it!=null) {
            it.forEach {
                partial+= it.AmountRemited!!
            }

            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(partial)
            _binding.txtpartialremit.text="${ans}"

        }
    }

    val ProcessExpenses:(state:List<TripCostTable>) ->Unit={
        var expensesamount:Double=0.0
        if(it!=null) {
            it.forEach {
                expensesamount += it.amount!!
            }

            totalamount -= expensesamount
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)


            expenses = decimalVat.format(expensesamount)

            _binding.txtnetcollection.text="${ans}"
            _binding.btnExpenses.text="ENTER / " +"${expenses}"
            // _binding.txttotalgross.text="${ans}"
        }
    }

    val Processwitholding:(state:List<TripWitholdingTable>) ->Unit={
        var withold:Double=0.0
        if(it!=null) {
            it.forEach {
                withold += it.amount!!
            }

            // totalamount -= expensesamount
            val decimalVat = DecimalFormat("#.00")
            witholding = decimalVat.format(withold)


            // val expenses = decimalVat.format(expensesamount)

            // _binding.txtnetcollection.text="${ans}"
            _binding.btnWitholding.text="ENTER / " +"${witholding}"
            // _binding.txttotalgross.text="${ans}"
        }
    }
    //endregion

    //region SYNCHING
    private var triptickets:ArrayList<Sycn_TripticketTable> = arrayListOf()
    private var inspectionreport:ArrayList<Sycnh_InspectionReportTable> = arrayListOf()
    private var mpadassignment:ArrayList<Synch_mpadAssignmentsTable> = arrayListOf()
    private var partialremit:ArrayList<Synch_partialremitTable> = arrayListOf()
    private var tripcost:ArrayList<Synch_TripCostTable> = arrayListOf()
    private var withodling:ArrayList<Synch_TripwitholdingTable> = arrayListOf()

    val ProcessTriptickets:(state: List<TripTicketTable>) ->Unit={
        if(it!=null) {
           it.forEach {
               var method= Sycn_TripticketTable(
                   amount = it.amount,
                   conductorName = it.conductorName,
                   dateTimeStamp = it.dateTimeStamp,
                   destination = it.destination,
                   driverName = it.driverName,
                   line = it.line,
                   mPadUnit = it.mPadUnit,
                   origin = it.origin,
                   passengerType = it.passengerType,
                   titcketNumber = it.titcketNumber!!,
                   qty = it.qty,
                   Id = 0

               )
               triptickets.add(method)
           }
            try {
                dbViewmodel.insertticketsynch(triptickets)
                dbViewmodel.getInspectionReport()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val Processinspectionreport:(state: List<InspectionReportTable>) ->Unit={
        if(it!=null) {
            it.forEach {
              var method= Sycnh_InspectionReportTable(
                  actualPassengerCount = it.actualPassengerCount,
                  dateTimeStamp = it.dateTimeStamp!!,
                  difference = it.difference,
                  direction = it.direction,
                  inspectorName = it.inspectorName,
                  line = it.line,
                  lineSegment = it.lineSegment,
                  mPadUnit = it.mPadUnit,
                  qty = it.qty,
                  Id = 0
              )
                inspectionreport.add(method)
            }
            try {
                dbViewmodel.insert_synch_inspection(inspectionreport)
                dbViewmodel.getMpadAssignment()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val Processmpadassignment:(state: List<mPadAssignmentsTable>) ->Unit={
        if(it!=null) {
            it.forEach {
              var method= Synch_mpadAssignmentsTable(
                  busNumber = it.busNumber,
                  conductorName = it.conductorName,
                  dataTimeStamp = it.dataTimeStamp,
                  dispatcherName = it.dispatcherName,
                  driverName = it.driverName,
                  line = it.line,
                  mPadUnit = it.mPadUnit!!,
                  Id = 0
              )
                mpadassignment.add(method)
            }
            try {
                dbViewmodel.insert_synch_mpad(mpadassignment)
                dbViewmodel.getPartialRemit()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val Processpartialremit:(state: List<PartialRemitTable>) ->Unit={
        if(it!=null) {
            it.forEach {
               var method= Synch_partialremitTable(
                   CashierName = it.CashierName,
                   Amount = it.Amount,
                   AmountRemited = it.AmountRemited,
                   Line = it.Line,
                   DateTimeStamp = it.DateTimeStamp,
                   Id = 0

               )
                partialremit.add(method)
            }
            try {
                dbViewmodel.insert_synch_partial_remit(partialremit)
                dbViewmodel.getTripcost()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val ProcessTripcost:(state: List<TripCostTable>) ->Unit={
        if(it!=null) {
            it.forEach {
               var method= Synch_TripCostTable(
                   amount = it.amount,
                   costType = it.costType,
                   dateTimeStamp = it.dateTimeStamp!!,
                   driverConductorName = it.driverConductorName,
                   line = it.line,
                   Id = 0
               )
                tripcost.add(method)
            }
            try {
               dbViewmodel.insert_synch_trip_cost(tripcost)
                dbViewmodel.getTripwitholding()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val Processtripwitholding:(state: List<TripWitholdingTable>) ->Unit={
        if(it!=null) {
            it.forEach {
               var method= Synch_TripwitholdingTable(
                   amount = it.amount,
                   dateTimeStamp = it.dateTimeStamp!!,
                   mPadUnit = it.mPadUnit,
                   name = it.name,
                   witholdingType = it.witholdingType,
                   Id = 0
               )
                withodling.add(method)
            }
            try {
               dbViewmodel.insert_synch_witholding(withodling)
               dbViewmodel.truncatetables()
                resetALl()
                _binding.btnigresso.isEnabled=false
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }

    val resetALl={
        GlobalVariable.employeeName=null
        inspectorname=null
        cashiername=null
        line=null
        lineid=null
        direction=null
        conductor=null
        driver=null
        bus=null

        tripreverse=1
        val sharedPrefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt("ticketnumber", GlobalVariable.ticketnumber)
        ticketnumber= sharedPrefs.getInt("ticketnumber",0)
        //editor.putBoolean("isdispatch",false)
        GlobalVariable.isDispatched=false

        editor.apply()
        linesegment= arrayListOf()
        remainingPass= null
        ticketcounter=1
        destinationcounter=1
        origincounter=0


    }

    //endregion

}
