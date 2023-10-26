package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.Model.convertions.TripCostTotal
import com.example.erjohnandroid.database.Model.convertions.WitholdingTotal
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityIngressoBinding
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
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
import com.example.erjohnandroid.util.showCustomToast
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

@AndroidEntryPoint
class IngressoActivity : AppCompatActivity() {
    lateinit var _binding:ActivityIngressoBinding
    private val dbViewmodel: RoomViewModel by viewModels()

    var alltickets:kotlin.collections.List<TripTicketTable> = arrayListOf()

    var AllTripCost:ArrayList<TripCostTable> = arrayListOf()
    var AllWitholding:ArrayList<TripWitholdingTable> = arrayListOf()
    var AllPartialRemit:ArrayList<PartialRemitTable> = arrayListOf()

    var manualticket:ArrayList<Double> = arrayListOf()
    var cancelticket:ArrayList<Double> = arrayListOf()

    var totalamount:Double=0.0
    var drivercommision:String="0.0"
    var conductorcommision:String="0.0"
    var bonus = 100.0
    var infault:String?= null
    var expensesTotal:String?= "0.0"
    var witholdingTotal:String="0.0"
//    var manualticket:Double=0.0
//    var cancelticket:Double=0.0
//
//

//
//    var totalTripcost:Double=0.0
//    var totalwitholding:Double=0.0
//    var remit:Double=0.0




    var EXPENSES_ACTIVITY=1
    var WITHOLD=2
    var CANCELLED=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityIngressoBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)


            window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val window = window
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                )


        initPrinter()
        _binding.btnigresso.isEnabled=true

        dbViewmodel.getPartialRemit()
        dbViewmodel.getTotalAmountTrip()

        _binding.txtdrivername.text=GlobalVariable.driver
        _binding.txtconductorname.text=GlobalVariable.conductor

        _binding.btnAddmanual.setOnClickListener {
            var manual= _binding.etmanualticket.text.toString()

            if(manual.isEmpty()) return@setOnClickListener

            val stringWithoutSpaces = manual.replace(" ", "")
            manual= stringWithoutSpaces.replace(" ", "")

            manualticket.add(manual.toDouble())

            totalamount += manual.toDouble()

            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)

            _binding.txttotalgross.text="${ans}"

            _binding.txtnetcollection.text="${ans}"
        }

        _binding.btnCancelledticket.setOnClickListener {
            var manual= _binding.etCancelledticket.text.toString()

            if(manual.isEmpty()) return@setOnClickListener

            val stringWithoutSpaces = manual.replace(" ", "")
            manual = stringWithoutSpaces.replace(" ", "")

            if(totalamount<=manual.toDouble()) return@setOnClickListener

            cancelticket.add(manual.toDouble())
            totalamount -= manual.toDouble()
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)

            _binding.txttotalgross.text="${ans}"
            _binding.txtnetcollection.text="${ans}"

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
                Toast(this).showCustomToast("INPUT AMOUNT",this)
                return@setOnClickListener
            }

//            dbViewmodel.getTotalTripcost()
//            dbViewmodel.getTotalwithlding()

            var partialremit= _binding.txtpartialremit.text.toString()
            if(partialremit.isNullOrEmpty()) partialremit="0.0"

          //  var addamount= finalremit.toDouble() + partialremit.toDouble() + totalTripcost +totalwitholding
          //  var addamount= finalremit.toDouble()  + totalTripcost +totalwitholding

            var net= _binding.txtnetcollection.text.toString()

            var compute= net.toDouble()- finalremit.toDouble()
            if(compute>0) _binding.viewshort.isVisible=true
            else _binding.viewshort.isVisible=false
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(compute)
            _binding.etshortover.text= ans.toString()

            if(totalamount>=14000){
                _binding.viewconductorbonus.isVisible=true
                _binding.viewdriverbonus.isVisible=true
                calculateBonus(totalamount)
            }

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
            //_binding.btnigressoreprint.isEnabled=true
            if(_binding.etshortover.text.toString().isNullOrEmpty()) {
                Toast(this).showCustomToast("PLEASE COMPUTE FINAL REMIT",this)
                return@setOnClickListener
            }
            val formattedDateTime = getCurrentDateInFormat()
            var manualticketamount:Double=0.0
            var canceledTickectamount:Double=0.0

            if(manualticket.isNullOrEmpty()){
                manualticketamount=0.0
            }else {
                manualticket.forEach {
                    manualticketamount += it
                }
            }

            if (cancelticket.isNullOrEmpty()){
                canceledTickectamount=0.0
            }else{
                cancelticket.forEach {
                    canceledTickectamount += it
                }
            }

            var method= IngressoTable(
                Id = 0,
                TotalCollection =  convertDecimal(_binding.txttotalcollection.text.toString()),
                ManualTicket = manualticketamount,
                CancelledTicket = canceledTickectamount,
                TotalExpenses = convertDecimal(expensesTotal),
                TotalWitholding = convertDecimal(witholdingTotal),
                DriverName = GlobalVariable.driver,
                DriverCommission = convertDecimal(drivercommision),
                ConductorName = GlobalVariable.conductor,
                ConductorCommission = convertDecimal(conductorcommision),
                Net = convertDecimal(_binding.txtnetcollection.text.toString()),
                PartialRemit = convertDecimal(_binding.txtpartialremit.text.toString()),
                FinalRemit = convertDecimal(_binding.etfinalremit.text.toString()),
                ShororOver = convertDecimal(_binding.etshortover.text.toString()),
                InFault = infault,
                DateTimeStamp = formattedDateTime.toString(),
                ingressoRefId = GlobalVariable.ingressoRefId

            )

            try {
                dbViewmodel.insertIngersso(method)

                dbViewmodel.getTripticket()
                dbViewmodel.tripticket.observe(this, Observer {
                        state->ProcessTriptickets(state)
                })




                Processpartialremit(AllPartialRemit)
                ProcessTripcost(AllTripCost)
                Processtripwitholding(AllWitholding)
//                dbViewmodel.partialremit.observe(this,Observer{
//                        state-> Processpartialremit(state)
//               })

//                dbViewmodel.tripcost.observe(this,Observer{
//                        state->ProcessTripcost(state)
//                })

//                dbViewmodel.tripwitholding.observe(this,Observer{
//                        state->Processtripwitholding(state)
//                })
               // dbViewmodel.truncatetables()
            }catch (e:java.lang.Exception){
                Log.e("erro",e.message.toString())
            }
        }

        _binding.btnigressoreprint.setOnClickListener {
            printText("Erjohn & Almark Transit Corp")
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



//        dbViewmodel.totalTripcost?.observe(this,Observer{
//            state-> ProcessTotaltripcost(state)
//        })
//
//        dbViewmodel.totalwithodling?.observe(this,Observer{
//                state-> ProcessTotalwitholding(state)
//        })


    }





    override fun onBackPressed() {
       showSimpleDialog(this,"FINISH INGRESSO?","YOU SURE YOU WANT TO PROCEED? ALL DATA WILL BE DELETED")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EXPENSES_ACTIVITY) {
            AllTripCost= arrayListOf()
//            dbViewmodel.getTripcost()
//            dbViewmodel.tripcost.distinctUntilChanged().observe(this, Observer {
//                    state-> ProcessExpenses(state)
//            })

            ProcessExpenses(GlobalVariable.AllTripCost)
        }
        else if(requestCode==WITHOLD){
              AllWitholding = arrayListOf()
          //  dbViewmodel.getTripwitholding()
//            dbViewmodel.tripwitholding.distinctUntilChanged().observe(this, Observer {
//                    state->Processwitholding(state)
//            })
            Processwitholding(GlobalVariable.AllWitholding)
        }
        else if(requestCode==CANCELLED){
            Log.d("canceled","canceled")
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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

//    val ProcessTotaltripcost:(state: TripCostTotal?) ->Unit={
//        if(it?.total!=null) {
//          totalTripcost= it?.total!!
//        }else totalTripcost=0.0
//    }

//    val ProcessTotalwitholding:(state: WitholdingTotal?) ->Unit={
//        if(it?.total!=null) {
//            totalwitholding= it?.total!!
//        }else totalwitholding=0.0
//    }

    val ProcessPartialremit:(state:List<PartialRemitTable>) ->Unit={
        var partial:Double=0.0
        val decimalVat = DecimalFormat("#.00")

        if(it!=null) {

            it.forEach {
                partial+= it.AmountRemited!!
                AllPartialRemit.add(it)
            }


            val ans = decimalVat.format(partial)

            _binding.txtpartialremit.text="${ans}"
            totalamount -=partial

            val remit = decimalVat.format(totalamount)
            _binding.txtnetcollection.text="${remit}"
        }
    }

    val ProcessExpenses:(state:List<TripCostTable>) ->Unit={
        var expensesamount:Double=0.0
        var expenses="0.0"
        if(!it.isNullOrEmpty()) {

            it.forEach {expenses->

                if(!AllTripCost.contains(expenses)){
                    AllTripCost.add(expenses)
                }

            }


            totalamount -= AllTripCost.last().amount!!
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(totalamount)
            _binding.txtnetcollection.text="${ans}"

            AllTripCost.forEach {
                expensesamount += it.amount!!
            }

            expenses = decimalVat.format(expensesamount)
            _binding.btnExpenses.text="ENTER / " +"${expenses}"
            expensesTotal = expenses
        }
    }

    val Processwitholding:(state:List<TripWitholdingTable>) ->Unit={
        var withold:Double=0.0
        var witholding:String="0.0"
        if(!it.isNullOrEmpty()) {
            it.forEach { witholding->
                if(!AllWitholding.contains(witholding)){
                    AllWitholding.add(witholding)
                }
            }

           // totalamount -= AllWitholding.last().amount!! // NEED TO CLARIFY IF WITHOLDING IS MINUS TO TOTAL SALES
            val decimalVat = DecimalFormat("#.00")
            _binding.txtnetcollection.text="${decimalVat.format(totalamount)}"

            AllWitholding.forEach {
                withold += it.amount!!
            }

            witholding = decimalVat.format(withold)
            _binding.btnWitholding.text="ENTER / " +"${witholding}"
            witholdingTotal = witholding
        }
    }
    //endregion


    fun calculateBonus(sales: Double) {
       // totalSales += sales
        var total=sales

        if (total >= 14000) {

            val bonusIncreaseCount = (total - 14000) / 1000
            bonus += bonusIncreaseCount * 100

            val decimalVat = DecimalFormat("#.00")
            var ans = decimalVat.format(bonus)

            _binding.txtdriverbonus.text= "${ans}"
            _binding.txtconductorbonus.text= "${ans}"

        }
    }

    //region SYNCHING
    private var triptickets:ArrayList<Sycn_TripticketTable> = arrayListOf()
    private var inspectionreport:ArrayList<Sycnh_InspectionReportTable> = arrayListOf()
    private var mpadassignment:ArrayList<Synch_mpadAssignmentsTable> = arrayListOf()
    private var partialremit:ArrayList<Synch_partialremitTable> = arrayListOf()
    private var tripcost:ArrayList<Synch_TripCostTable> = arrayListOf()
    private var withodling:ArrayList<Synch_TripwitholdingTable> = arrayListOf()

    val ProcessTriptickets:(state: List<TripTicketTable>) ->Unit={
        if(it!=null) {
            alltickets=it
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
                   Id = 0,
                   ingressoRefId = GlobalVariable.ingressoRefId

               )
               triptickets.add(method)
           }
            try {
                dbViewmodel.insertticketsynch(triptickets)
                dbViewmodel.getInspectionReport()
                dbViewmodel.inspectionreport.observe(this,Observer{
                        state->Processinspectionreport(state)
                })
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
                  Id = 0,
                  ingressoRefId = GlobalVariable.ingressoRefId
              )
                inspectionreport.add(method)
            }
            try {
                dbViewmodel.insert_synch_inspection(inspectionreport)
                dbViewmodel.getMpadAssignment()
                dbViewmodel.mpadAssignment.observe(this,Observer{
                        state-> Processmpadassignment(state)
                })
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
                  Id = 0,
                  ingressoRefId = GlobalVariable.ingressoRefId
              )
                mpadassignment.add(method)
            }
            try {
                dbViewmodel.insert_synch_mpad(mpadassignment)
               // dbViewmodel.getPartialRemit() // NEED TO REMOVE
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
                   Id = 0,
                   ingressoRefId = GlobalVariable.ingressoRefId

               )
                partialremit.add(method)
            }
            try {
                dbViewmodel.insert_synch_partial_remit(partialremit)
               // dbViewmodel.getTripcost()
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
                   Id = 0,
                   ingressoRefId = GlobalVariable.ingressoRefId
               )
                tripcost.add(method)
            }
            try {
               dbViewmodel.insert_synch_trip_cost(tripcost)
               // dbViewmodel.getTripwitholding()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }
    var gross:String?= null
    val Processtripwitholding:(state: List<TripWitholdingTable>) ->Unit={
        if(it!=null) {
            it.forEach {
               var method= Synch_TripwitholdingTable(
                   amount = it.amount,
                   dateTimeStamp = it.dateTimeStamp!!,
                   mPadUnit = it.mPadUnit,
                   name = it.name,
                   witholdingType = it.witholdingType,
                   Id = 0,
                   ingressoRefId = GlobalVariable.ingressoRefId
               )
                withodling.add(method)
            }
            try {
                 var a=_binding.txttotalcollection.text.toString()
                val decimalVat = DecimalFormat("#.00")
                val ans = decimalVat.format(a.toDouble())
                gross=ans
                printText("Erjohn & Almark Transit Corp")
               dbViewmodel.insert_synch_witholding(withodling)
//               dbViewmodel.truncatetables()
               // resetALl()
                _binding.btnigressoreprint.isEnabled=true
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
        dbViewmodel.updateTicketnumber(GlobalVariable.ticketnumber,GlobalVariable.ingressoRefId,GlobalVariable.ticketnumid!!)





//        editor.putInt("ticketnumber", GlobalVariable.ticketnumber)
//        editor.putInt("ingressoRefId", GlobalVariable.ingressoRefId)
//        ticketnumber= sharedPrefs.getInt("ticketnumber",0)
//        GlobalVariable.originalTicketnum= GlobalVariable.ticketnumber

        GlobalVariable.isDispatched=false

        editor.apply()
        linesegment= arrayListOf()
        remainingPass= null
        ticketcounter=1
        destinationcounter=1
        origincounter=0


    }

    //endregion

//
    //region PRINTER
//    private val TAG: String? = "MainActivity"
//    var PRN_TEXT: String? = "THIS IS A TEsT PRINT"
//    var version = arrayOfNulls<String>(1)
//
//    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
//    private val handler = Handler()
//
//    private fun bindService() {
//        try {
//            val intent = Intent()
//            intent.setPackage("net.nyx.printerservice")
//            intent.action = "net.nyx.printerservice.IPrinterService"
//            bindService(intent, connService, BIND_AUTO_CREATE)
//        }catch (e:Exception){
//            Log.e("ere",e.localizedMessage)
//        }
//
//    }
//
//    private var printerService: IPrinterService? = null
//
//    private val connService = object : ServiceConnection {
//        override fun onServiceDisconnected(name: ComponentName?) {
//            //  showLog("printer service disconnected, try reconnect")
//            printerService = null
//            // 尝试重新bind
//            handler.postDelayed({ bindService() }, 5000)
//        }
//
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            Timber.d("onServiceConnected: $name")
//            printerService = IPrinterService.Stub.asInterface(service)
//            getVersion()
//        }
//    }
//
//
//    private fun getVersion() {
//        singleThreadExecutor.submit(Runnable {
//            try {
//                val ret = printerService!!.getPrinterVersion(version)
//                //showLog("Version: " + msg(ret) + "  " + version.get(0))
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//        })
//    }
//
//    private fun printText(text: String) {
//
//        singleThreadExecutor.submit {
//            try {
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
//                val currentDate = Date()
//                val formattedDate = dateFormat.format(currentDate)
//
//
//                val textFormat = PrintTextFormat()
//                textFormat.textSize=26
//                // textFormat.setUnderline(true);
//                textFormat.ali=1
//                textFormat.style=1
//                try {
//                    var ret = printerService!!.printText(text, textFormat)
//                    textFormat.textSize=24
//                    ret = printerService!!.printText("INGRESSO",textFormat)
//                    ret = printerService!!.printText("Line:  ${GlobalVariable.line}",textFormat)
//                    ret = printerService!!.printText("Bus #:  ${GlobalVariable.bus}",textFormat)
//                    ret = printerService!!.printText("mPad #:  ${GlobalVariable.deviceName}",textFormat)
//                    ret = printerService!!.printText("Dispatcher:  ${GlobalVariable.employeeName}",textFormat)
//                    ret = printerService!!.printText("Driver:  ${GlobalVariable.driver}",textFormat)
//                    ret = printerService!!.printText("Conductor:  ${GlobalVariable.conductor}",textFormat)
//                    ret = printerService!!.printText("Date:  ${formattedDate}",textFormat)
//                    textFormat.style=0
//                    textFormat.ali=1
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.topPadding=10
//
//                    ret = printerService!!.printText("SALES"   ,textFormat)
//                    textFormat.ali=0
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("GROSS: ${gross}"   ,textFormat)
//                    ret = printerService!!.printText("Net: ${_binding.txtnetcollection.text}",textFormat)
//                    ret = printerService!!.printText("Partial Remit:  ${_binding.txtpartialremit.text}",textFormat)
//                    ret = printerService!!.printText("Expenses: ${expenses}",textFormat)
//                    ret = printerService!!.printText("Witholding: ${witholding}",textFormat)
//                    textFormat.ali=1
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("BENEFITS",textFormat)
//                    textFormat.ali=0
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("Total Commission: ${_binding.txttotalcommision.text}",textFormat)
//                    ret = printerService!!.printText("Driver Commission: ${_binding.txtdrivercommision.text}",textFormat)
//                    ret = printerService!!.printText("Conductor Commission: ${_binding.txtconductorcommision.text}",textFormat)
//                    ret = printerService!!.printText("Driver Bonus: ${_binding.txtdriverbonus.text}",textFormat)
//                    ret = printerService!!.printText("Conductor Bonus: ${_binding.txtconductorbonus.text}",textFormat)
//                    textFormat.ali=1
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("INSPECTION REPORT",textFormat)
//                    textFormat.ali=0
//                    textFormat.topPadding=10
//                    inspectionreport.forEach {
//                        ret = printerService!!.printText("Inspector: ${it.inspectorName}",textFormat)
//                        ret = printerService!!.printText("Count: ${it.actualPassengerCount}"+"--"+"Discrepancy: ${it.difference}",textFormat)
//                        ret = printerService!!.printText("Segment: ${it.lineSegment}",textFormat)
//                        ret = printerService!!.printText("mPad: ${it.mPadUnit}",textFormat)
//                        textFormat.topPadding=10
//                    }
//
//
//
//                    textFormat.ali=1
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("Thank You",textFormat)
//                    textFormat.topPadding=10
//                    textFormat.ali=0
//                    textFormat.topPadding=10
//
////                    alltickets.forEach {
////                        ret = printerService!!.printText("Segment: ${it.origin}"+"--"+"${it.destination}",textFormat)
////                        ret = printerService!!.printText("Amount: ${it.amount}"+"--"+"${it.passengerType}",textFormat)
////                    }
//
//
//                    ret = printerService!!.printText("",textFormat)
//                    ret = printerService!!.printText("",textFormat)
//
//                    if (ret == 0) {
//                        paperOut()
//                    }
//
//                }catch (e:java.lang.Exception){
//                    Log.e("tae",e.localizedMessage)
//                }
//
//                // showLog("Print text: " + msg(ret))
//
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//
//
//    private fun paperOut() {
//        singleThreadExecutor.submit {
//            try {
//                printerService!!.paperOut(80)
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
   //endregion

    //region PRINTER TWO
    private val TAG: String? = "IPosPrinterTestDemo"

    private val PRINTER_NORMAL = 0
    private val PRINTER_PAPERLESS = 1
    private val PRINTER_THP_HIGH_TEMPERATURE = 2
    private val PRINTER_MOTOR_HIGH_TEMPERATURE = 3
    private val PRINTER_IS_BUSY = 4
    private val PRINTER_ERROR_UNKNOWN = 5

    /*打印机当前状态*/
    private var printerStatus = 0

    /*定义状态广播*/
    private val PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservice.NORMAL_ACTION"
    private val PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION"
    private val PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION"
    private val PRINTER_THP_HIGHTEMP_ACTION =
        "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION"
    private val PRINTER_THP_NORMALTEMP_ACTION =
        "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION"
    private val PRINTER_MOTOR_HIGHTEMP_ACTION =
        "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION"
    private val PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION"
    private val PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION =
        "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION"

    /*定义消息*/
    private val MSG_TEST = 1
    private val MSG_IS_NORMAL = 2
    private val MSG_IS_BUSY = 3
    private val MSG_PAPER_LESS = 4
    private val MSG_PAPER_EXISTS = 5
    private val MSG_THP_HIGH_TEMP = 6
    private val MSG_THP_TEMP_NORMAL = 7
    private val MSG_MOTOR_HIGH_TEMP = 8
    private val MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9
    private val MSG_CURRENT_TASK_PRINT_COMPLETE = 10

    /*循环打印类型*/
    private val MULTI_THREAD_LOOP_PRINT = 1
    private val INPUT_CONTENT_LOOP_PRINT = 2
    private val DEMO_LOOP_PRINT = 3
    private val PRINT_DRIVER_ERROR_TEST = 4
    private val DEFAULT_LOOP_PRINT = 0

    //循环打印标志位
    private var loopPrintFlag = DEFAULT_LOOP_PRINT
    private val loopContent: Byte = 0x00
    private val printDriverTestCount = 0


    private val info: TextView? = null
    private var mIPosPrinterService: IPosPrinterService? = null
    private var callback: IPosPrinterCallback? = null

    /* Demo 版本号*/
    private val VERSION = "V1.1.0"

    private val random = Random()
    private var handler: HandlerUtils.MyHandler? = null


    private val iHandlerIntent: HandlerUtils.IHandlerIntent = object : HandlerUtils.IHandlerIntent {


        override fun handlerIntent(message: Message?) {
            when (message?.what) {
                MSG_TEST -> {}
                MSG_IS_NORMAL -> if (getPrinterStatus() == PRINTER_NORMAL) {
                    //  loopPrint(loopPrintFlag)
                }
                MSG_IS_BUSY -> Toast.makeText(
                    this@IngressoActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@IngressoActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@IngressoActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@IngressoActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@IngressoActivity,
                        "motor hight temp",
                        Toast.LENGTH_SHORT
                    ).show()
                    handler!!.sendEmptyMessageDelayed(
                        MSG_MOTOR_HIGH_TEMP_INIT_PRINTER,
                        180000
                    ) //马达高温报警，等待3分钟后复位打印机
                }
                MSG_MOTOR_HIGH_TEMP_INIT_PRINTER -> printerInit()
                MSG_CURRENT_TASK_PRINT_COMPLETE -> Toast.makeText(
                    this@IngressoActivity,
                    "Completed",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {}
            }
        }
    }


    private val IPosPrinterStatusListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == null) {
//                Log.d(
//                    com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
//                    "IPosPrinterStatusListener onReceive action = null"
//                )
                return
            }
            //           Log.d(
//                com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
//                "IPosPrinterStatusListener action = $action"
            //          )
            if (action == PRINTER_NORMAL_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0)
            } else if (action == PRINTER_PAPERLESS_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0)
            } else if (action == PRINTER_BUSY_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_IS_BUSY, 0)
            } else if (action == PRINTER_PAPEREXISTS_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0)
            } else if (action == PRINTER_THP_HIGHTEMP_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0)
            } else if (action == PRINTER_THP_NORMALTEMP_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0)
            } else if (action == PRINTER_MOTOR_HIGHTEMP_ACTION) //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                handler!!.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0)
            } else if (action == PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION) {
                handler!!.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0)
            } else {
                handler!!.sendEmptyMessageDelayed(MSG_TEST, 0)
            }
        }
    }

    private val connectService: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mIPosPrinterService = IPosPrinterService.Stub.asInterface(service)
            // setButtonEnable(true)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mIPosPrinterService = null
        }
    }



    private fun initPrinter(){
        handler = HandlerUtils.MyHandler(iHandlerIntent)
        // innitView()
        callback = object : IPosPrinterCallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRunResult(isSuccess: Boolean) {
//                Log.i(
////                    com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
////                    "result:$isSuccess\n"
//                )
            }

            @Throws(RemoteException::class)
            override fun onReturnString(value: String) {
//                Log.i(
////                    com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
////                    "result:$value\n"
//                )
            }
        }


        //绑定服务
        val intent = Intent()
        intent.setPackage("com.iposprinter.iposprinterservice")
        intent.action = "com.iposprinter.iposprinterservice.IPosPrintService"
        //startService(intent);
        //startService(intent);
        bindService(intent, connectService, BIND_AUTO_CREATE)

        //注册打印机状态接收器

        //注册打印机状态接收器
        val printerStatusFilter = IntentFilter()
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION)
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION)
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION)
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION)
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION)

        registerReceiver(IPosPrinterStatusListener, printerStatusFilter)
    }


    fun getPrinterStatus(): Int {
//        Log.i(
////            com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
////            "***** printerStatus$printerStatus"
//        )
        try {
            printerStatus = mIPosPrinterService!!.printerStatus
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
//        Log.i(
////            com.iposprinter.printertestdemo.IPosPrinterTestDemo.TAG,
////            "#### printerStatus$printerStatus"
//        )
        return printerStatus
    }

    /**
     * 打印机初始化
     */
    fun printerInit() {
        ThreadPoolManager.getInstance().executeTask(Runnable {
            try {
                mIPosPrinterService!!.printerInit(callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        })
    }



    fun printText(amount:String) {
        ThreadPoolManager.getInstance().executeTask {

            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Ingresso Details\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Line: ${GlobalVariable.line}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Bus #: ${GlobalVariable.bus}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("mPAD: ${GlobalVariable.deviceName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Dispatcher: ${GlobalVariable.employeeName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Driver: ${GlobalVariable.driver}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("SALES\n\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Gross: ${gross}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Net: ${_binding.txtnetcollection.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Partial Remit: ${_binding.txtpartialremit.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Expenses: ${expensesTotal}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Witholding: ${witholdingTotal}\n", "ST", 24, callback)

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Benefits\n\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Total Commission: ${_binding.txttotalcommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Driver Commission: ${_binding.txtdrivercommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor Commission: ${_binding.txtconductorcommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Driver bonus: ${_binding.txtdriverbonus.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor bonus: ${_binding.txtconductorbonus.text}\n", "ST", 24, callback)

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
//                mIPosPrinterService!!.printBlankLines(1, 8, callback)
//                mIPosPrinterService!!.PrintSpecFormatText("Inspection Report\n\n", "ST", 24, 1,callback)
//                inspectionreport.forEach {
//                  mIPosPrinterService!!.printSpecifiedTypeText("Inspector: ${it.inspectorName}\n", "ST", 24, callback)
//                  mIPosPrinterService!!.printSpecifiedTypeText("Count: ${it.actualPassengerCount} - Discrepancy: ${it.difference} \n", "ST", 24, callback)
//                  mIPosPrinterService!!.printSpecifiedTypeText("Segment: ${it.lineSegment}\n", "ST", 24, callback)
//                  mIPosPrinterService!!.printSpecifiedTypeText("mPAD: ${it.mPadUnit}\n", "ST", 24, callback)
//              }
//
//                mIPosPrinterService!!.printBlankLines(1, 8, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "********************************\n",
//                    "ST",
//                    24,
//                    callback
//                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Expenses\n\n", "ST", 24, 1,callback)

                GlobalVariable.AllTripCost.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("Expenses type: ${it.costType}\n", "ST", 24, callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Amount: ${it.amount}\n", "ST", 24, callback)
                }

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Witholding\n\n", "ST", 24, 1,callback)
                GlobalVariable.AllWitholding.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("Witholding type: ${it.witholdingType}\n", "ST", 24, callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Amount: ${it.amount}\n", "ST", 24, callback)
                }

                mIPosPrinterService!!.printerPerformPrint(160, callback)
                runOnUiThread {
                   // super.onBackPressed()
//                    resetALl()
//                    val resultIntent = Intent()
//                    setResult(Activity.RESULT_OK, resultIntent)
//
//                    finish()
//
//
//                    overridePendingTransition(
//                        R.anim.screenslideleft, R.anim.screen_slide_out_right,
//                    );
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            finally {
                runOnUiThread {
                   // super.onBackPressed()
//                    resetALl()
//                    val resultIntent = Intent()
//                    setResult(Activity.RESULT_OK, resultIntent)
//
//                    finish()
//
//
//                    overridePendingTransition(
//                        R.anim.screenslideleft, R.anim.screen_slide_out_right,
//                    );
                }
            }
        }
    }



    fun bitmapToInputStream(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray().inputStream()
    }

    //endregion

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(IPosPrinterStatusListener)
        unbindService(connectService)
        handler!!.removeCallbacksAndMessages(null)
    }

    fun showSimpleDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(title)

        // Set the alert dialog message
        builder.setMessage(message)

        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK") { dialog, which ->
            super.onBackPressed()
            dbViewmodel.truncatetables()
            resetALl()
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)

            finish()


            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }

        builder.setNegativeButton("CANCEL") { dialog, which ->

            dialog.dismiss()
        }

        // Create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
