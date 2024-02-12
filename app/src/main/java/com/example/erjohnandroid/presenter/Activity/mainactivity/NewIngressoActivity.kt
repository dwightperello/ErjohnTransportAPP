package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.RemoteException
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.databinding.ActivityIngressoBinding
import com.example.erjohnandroid.databinding.ActivityNewIngressoBinding
import com.example.erjohnandroid.presenter.adapter.TerminalAdapter
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.GlobalVariable.getCurrentDateInFormat
import com.example.erjohnandroid.util.IngressoFunctions.computeBonus
import com.example.erjohnandroid.util.IngressoFunctions.computeCommissions
import com.example.erjohnandroid.util.IngressoFunctions.convertDecimal
import com.example.erjohnandroid.util.IngressoFunctions.model.Bonus
import com.example.erjohnandroid.util.IngressoFunctions.model.ComputeCommisionEntity
import com.example.erjohnandroid.util.showCustomToast
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class NewIngressoActivity : AppCompatActivity() {
    lateinit var _binding: ActivityNewIngressoBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private val externalViewModel: externalViewModel by viewModels()
    private lateinit var terminalAdapter: TerminalAdapter

    var EXPENSES_ACTIVITY=1
    var WITHOLD=2
    var CANCELLED=0
    var isFInished:Boolean=false
    var infault:String?= ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityNewIngressoBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        initPrinter()

        dbViewmodel.getAllTerminal()
        dbViewmodel.terminals.observe(this, androidx.lifecycle.Observer {
            state->ProcessTerminals(state)
        })

        _binding.txtdrivername.text= GlobalVariable.driver
        _binding.txtconductorname.text= GlobalVariable.conductor

        _binding.btnExpenses.setOnClickListener {
            val intent = Intent(this, ShowExpensesActivity::class.java)
            startActivityForResult(intent,EXPENSES_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding.btnWitholding.setOnClickListener {
            val intent = Intent(this, ShowWitholdingActivity::class.java)
            startActivityForResult(intent,WITHOLD)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding.btnReset.setOnClickListener {
            mainreset()
            dbViewmodel.getTotalAmountTrip()
        }

        _binding.btnAddmanual.setOnClickListener {
            val decimalVat = DecimalFormat("#.00")

           val manualticket= _binding.etmanualticket.text.toString()
            if(manualticket.isNullOrEmpty()){
                Toast(this).showCustomToast("Opppss...no amount",this)
                return@setOnClickListener
            }

            val stringWithoutSpaces = manualticket!!.replace(" ", "")


            val gross= _binding.txttotalgross.text.toString()
            val amount =  gross.toDouble() + stringWithoutSpaces.toDouble()

            val ans= decimalVat.format(amount)

            _binding.txttotalgross.text= ans
           // reset()
            recomputecomputeALL(ans.toDouble())

            var deductcommision = ans.toDouble() - commisionReturned.totalcommision
            if(checkBonusInitialized() && ans.toDouble()>=14000)
                deductcommision  = deductcommision - bonusreturned.totalBonus

            deductcommision= deductcommision- partial
            deductcommision = deductcommision - costAllTrip
            deductcommision = deductcommision + witholdingAllTrip

            val totalnet= decimalVat.format(deductcommision)
            deductcommision= totalnet.toDouble()
            _binding.txtnetcollection.text= deductcommision.toString()
        }

        _binding.btnCancelledticket.setOnClickListener {
            val cancelled= _binding.etCancelledticket.text.toString()

            if(cancelled.isEmpty()){
                Toast(this).showCustomToast("Opppss...no amount",this)
                return@setOnClickListener
            }
            val stringWithoutSpaces = cancelled.replace(" ", "")
            val gross= _binding.txttotalgross.text.toString()
            val amount =  gross.toDouble() - stringWithoutSpaces.toDouble()

            val ans= decimalVat.format(amount)
            _binding.txttotalgross.text= ans
            //reset()
            recomputecomputeALL(ans.toDouble())

            var deductcommision = ans.toDouble() - commisionReturned.totalcommision
            if(checkBonusInitialized() && ans.toDouble()>=14000)
                deductcommision  = deductcommision - bonusreturned.totalBonus

            deductcommision= deductcommision- partial
            deductcommision = deductcommision - costAllTrip
            deductcommision = deductcommision + witholdingAllTrip

            val totalnet= decimalVat.format(deductcommision)
            deductcommision= totalnet.toDouble()
            _binding.txtnetcollection.text= deductcommision.toString()
        }

        _binding.btncomputefinalremit.setOnClickListener {
            val finalremit = _binding.etfinalremit.text.toString()
            if(finalremit.isNullOrEmpty()){
                Toast(this).showCustomToast("Ooopps, no amount",this)
                return@setOnClickListener
            }

            var partialremit= _binding.txtpartialremit.text.toString()
            if(partialremit.isNullOrEmpty()) partialremit="0.0"
            val net= _binding.txtnetcollection.text.toString()
            val compute= net.toDouble()- finalremit.toDouble()
            if(compute>0) _binding.viewshort.isVisible=true
            else _binding.viewshort.isVisible=false
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(compute)
            _binding.etshortover.text= ans.toString()
        }

        _binding.btnigresso.setOnClickListener {
            if (GlobalVariable.terminal.isNullOrEmpty()){
                Toast(this).showCustomToast("Ooopps, no terminal",this)
                return@setOnClickListener
            }

            if(_binding.etshortover.text.toString().isNullOrEmpty()) {
                Toast(this).showCustomToast("Oooppss, compute final remit",this)
                return@setOnClickListener
            }

            dbViewmodel.insertLogreportbulk(GlobalVariable.arrayLogReport)

            val formattedDateTime = getCurrentDateInFormat()

            var manualticket= _binding.etmanualticket.text.toString()
            if(manualticket.isNullOrEmpty()) manualticket=0.0.toString()

            var cancelledTicket = _binding.etCancelledticket.text.toString()
            if(cancelledTicket.isNullOrEmpty()) cancelledTicket=0.0.toString()

            var method = IngressoTable(
                Id = 0,
                TotalCollection =  convertDecimal(_binding.txttotalcollection.text.toString()),
                ManualTicket = manualticket.toDouble(),
                CancelledTicket = cancelledTicket.toDouble(),
                TotalExpenses = costAllTrip,
                TotalWitholding = witholdingAllTrip,
                DriverName = GlobalVariable.driver,
                DriverCommission = convertDecimal(_binding.txtdrivercommision.text.toString()),
                ConductorName = GlobalVariable.conductor,
                ConductorCommission = convertDecimal(_binding.txtconductorcommision.text.toString()),
                Net = convertDecimal(_binding.txtnetcollection.text.toString()),
                PartialRemit = convertDecimal(_binding.txtpartialremit.text.toString()),
                FinalRemit = convertDecimal(_binding.etfinalremit.text.toString()),
                ShororOver = convertDecimal(_binding.etshortover.text.toString()),
                InFault = infault,
                DateTimeStamp = formattedDateTime.toString(),
                ingressoRefId = GlobalVariable.ingressoRefId,
                DriverBonus = convertDecimal(_binding.txtdriverbonus.text.toString()),
                ConductorBonus = convertDecimal(_binding.txtconductorbonus.text.toString()),
                terminal = GlobalVariable.terminal
            )
            isFInished=true
            try {
                dbViewmodel.insertIngersso(method)
                externalViewModel.updateIsDispathced(false)
                dbViewmodel.getTripticket()
                dbViewmodel.tripticket.observe(this, Observer {
                        state->ProcessTriptickets(state)
                })
                dbViewmodel.getAllTripReverse()
                dbViewmodel.AllTripReverse.observe(this,Observer{
                        state -> ProcessAllTripReverse(state)
                })
                dbViewmodel.get_logReport()
                dbViewmodel.Alllogreports.observe(this, Observer {
                        state -> ProcessLogReports(state)
                })
                Processpartialremit(AllPartialRemit)
                ProcessTripcost(GlobalVariable.AllTripCost)
                Processtripwitholding(GlobalVariable.AllWitholding)
            }
            catch (e:java.lang.Exception){
                Log.e("erro",e.message.toString())
                GlobalVariable.saveLogreport("error on ingresso, ${e.message}")

            }

            _binding.btnigressoreprint.setOnClickListener {
                printText()
            }

            _binding.btnclose.setOnClickListener {
                showSimpleDialog(this,"FINISH INGRESSO?","YOU SURE YOU WANT TO PROCEED? ALL DATA WILL BE DELETED")
            }
        }

        _binding.cbshortdriver.setOnClickListener {
            if(_binding.cbshortconductor.isChecked) _binding.cbshortconductor.isChecked=false
            _binding.cbshortdriver.isChecked=true
            infault=GlobalVariable.driver
        }

        _binding.cbshortconductor.setOnClickListener {
            if(_binding.cbshortdriver.isChecked) _binding.cbshortdriver.isChecked=false
            _binding.cbshortconductor.isChecked=true
            infault=GlobalVariable.conductor
        }



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

    val resetALl={


//        var bonusArraylist:ArrayList<TripCostTable> = arrayListOf()
//
//        var ticketnumid:Int?= 1
//        var ingressoRefId:Int=0
//
//        var isFromDispatch:Boolean=false
//
//        var priorWitholdingAmount:Double=0.0
//
//        var witholds: java.util.ArrayList<TripWitholdingTable> = arrayListOf()
//        var expenses:ArrayList<TripCostTable> = arrayListOf()
//
//
//        var discountAmount:Double=0.0
//        var basefair:Double=0.0
//        var exceedAmount:Double=0.0
//        var specialexceedAmount=0.0
//
//        var arrayLogReport:ArrayList<LogReport> = arrayListOf()



        GlobalVariable.employeeName=null
        GlobalVariable.inspectorname =null
        GlobalVariable.cashiername =null
        GlobalVariable.line =null
        GlobalVariable.lineid =null
        GlobalVariable.direction =null
        GlobalVariable.conductor =null
        GlobalVariable.driver =null
        GlobalVariable.bus =null
        GlobalVariable.bonusArraylist = arrayListOf()
        GlobalVariable.witholds= arrayListOf()
        GlobalVariable.expenses= arrayListOf()
        GlobalVariable.tripreverse =1
        partial =0.0

        GlobalVariable.AllWitholding= arrayListOf()
        GlobalVariable.AllTripCost= arrayListOf()
        GlobalVariable.arrayLogReport= arrayListOf()

        val sharedPrefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        //GlobalVariable.ingressoRefId +=1
        externalViewModel.updateTicketnumber(GlobalVariable.ticketnumber,GlobalVariable.ingressoRefId,GlobalVariable.ticketnumid!!)
        externalViewModel.updateReverseonly(GlobalVariable.tripreverse!!)



//        editor.putInt("ticketnumber", GlobalVariable.ticketnumber)
//        editor.putInt("ingressoRefId", GlobalVariable.ingressoRefId)
//        ticketnumber= sharedPrefs.getInt("ticketnumber",0)
//        GlobalVariable.originalTicketnum= GlobalVariable.ticketnumber

        GlobalVariable.isDispatched=false

        editor.apply()
        GlobalVariable.linesegment = arrayListOf()
        GlobalVariable.remainingPass = null
        GlobalVariable.ticketcounter =GlobalVariable.ticketnumber
        GlobalVariable.destinationcounter =1
        GlobalVariable.origincounter =0


        _binding.btnAddmanual.isEnabled=true
        _binding.btnCancelledticket.isEnabled=true
        _binding.btnExpenses.isEnabled=true
        _binding.btnWitholding.isEnabled=true
        _binding.btncomputefinalremit.isEnabled=true

    }

    override fun onBackPressed() {
        if(isFInished)  showCustomToast(this,"Use close button")
        else {
            finish()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }
    }

   private fun mainreset(){
        _binding.etmanualticket.text?.clear()
        _binding.etCancelledticket.text?.clear()
        commisionReturned.totalcommision=0.0
        commisionReturned.drivercommision=0.0
        commisionReturned.conductorcommision=0.0
       if(checkBonusInitialized()){
           bonusreturned.totalBonus=0.0
           bonusreturned.driverbonus=0.0
           bonusreturned.conductorbonus=0.0
       }

    }

    private fun ProcessTerminals(state: List<TerminalTable>?){
        if(!state.isNullOrEmpty()){
            terminalAdapter = TerminalAdapter(this)
            _binding.rvTerminal.adapter= terminalAdapter
            _binding.rvTerminal.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            terminalAdapter.showterminal(state)
            dbViewmodel.getTotalAmountTrip()
            dbViewmodel.totaltripamount.observe(this, androidx.lifecycle.Observer {
                    state-> ProcessTotal(state)
            })
        }
    }

    var totalamount:Double=0.0
    var totalAmountGross:Double=0.0
    val decimalVat = DecimalFormat("#.00")

    val ProcessTotal:(state: TicketTotal?) ->Unit={
        totalamount=0.0
        totalAmountGross=0.0
        if(it!=null) {
            totalamount=it.total
            totalAmountGross= it.total

           totalamount = decimalVat.format(totalamount).toDouble()
           totalAmountGross= decimalVat.format(totalAmountGross).toDouble()

            _binding.txttotalcollection.text="${totalamount}"
            _binding.txttotalgross.text="${totalAmountGross}"

            dbViewmodel.getPartialRemit()
            dbViewmodel.partialremit.observeOnce(this, Observer {
                    state->ProcessPartialremit(state)
            })
        }
    }

    var partial:Double=0.0
    var AllPartialRemit:ArrayList<PartialRemitTable> = arrayListOf()

    val ProcessPartialremit:(state:List<PartialRemitTable>) ->Unit={
        partial=0.0
        AllPartialRemit= arrayListOf()
        val decimalVat = DecimalFormat("#.00")

        if(it!=null) {
            it.forEach {
                partial+= it.AmountRemited!!
                AllPartialRemit.add(it)
            }

            computeALL(totalamount)

            val totalpartial = decimalVat.format(partial)

            _binding.txtpartialremit.text="${totalpartial}"

            totalamount -=partial
            totalamount -= commisionReturned.totalcommision
            if(checkBonusInitialized())totalamount -= bonusreturned.totalBonus

            val remit = decimalVat.format(totalamount)
            _binding.txtnetcollection.text="${remit}"
        }
    }

    private lateinit var commisionReturned: ComputeCommisionEntity
    private lateinit var bonusreturned:Bonus

    private  val computeALL:(Double)-> Unit ={total->
         costAllTrip=0.0
         witholdingAllTrip=0.0
         commisionReturned= computeCommissions(total)
        _binding.txttotalcommision.text= commisionReturned.totalcommision.toString()
        _binding.txtdrivercommision.text= commisionReturned.drivercommision.toString()
        _binding.txtconductorcommision.text=commisionReturned.conductorcommision.toString()
        _binding.txtnetcollection.text=total.toString()

        if(total >= 14000){
             bonusreturned= computeBonus(totalamount)
            _binding.txtdriverbonus.text = bonusreturned.driverbonus.toString()
            _binding.txtconductorbonus.text= bonusreturned.conductorbonus.toString()
        }
        _binding.viewconductorbonus.isVisible = total>=14000
        _binding.viewdriverbonus.isVisible= total>=14000
    }

    private var costAllTrip:Double =0.0
    private var witholdingAllTrip:Double=0.0

    private  val recomputecomputeALL:(Double)-> Unit ={total->
        costAllTrip=0.0
        witholdingAllTrip=0.0
        commisionReturned= computeCommissions(total)
        _binding.txttotalcommision.text= commisionReturned.totalcommision.toString()
        _binding.txtdrivercommision.text= commisionReturned.drivercommision.toString()
        _binding.txtconductorcommision.text=commisionReturned.conductorcommision.toString()
       // _binding.txtnetcollection.text=commisionReturned.netcollection.toString()

        costAllTrip= dbViewmodel.getTripcosttwo()
        witholdingAllTrip = dbViewmodel.witholdingTotalTriptwo()


        if(total >= 14000){
            bonusreturned= computeBonus(total)
            _binding.txtdriverbonus.text = bonusreturned.driverbonus.toString()
            _binding.txtconductorbonus.text= bonusreturned.conductorbonus.toString()
        }

        _binding.viewconductorbonus.isVisible = total>=14000
        _binding.viewdriverbonus.isVisible= total>=14000
    }

    fun checkBonusInitialized(): Boolean {
        return ::bonusreturned.isInitialized
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EXPENSES_ACTIVITY) {

            val gross= _binding.txttotalgross.text.toString()
            val ans= decimalVat.format(gross.toDouble())
            _binding.txttotalgross.text= ans
            recomputecomputeALL(gross.toDouble())

            var deductcommision = ans.toDouble() - commisionReturned.totalcommision
            if(checkBonusInitialized() && ans.toDouble()>=14000)
                deductcommision  = deductcommision - bonusreturned.totalBonus

            if(costAllTrip==null) costAllTrip=0.0
            deductcommision=deductcommision-costAllTrip
            if(witholdingAllTrip==null) witholdingAllTrip=0.0
            deductcommision=deductcommision + witholdingAllTrip
            deductcommision= deductcommision- partial


            val totalnet= decimalVat.format(deductcommision)
            deductcommision= totalnet.toDouble()
            _binding.txtnetcollection.text= deductcommision.toString()



        }
        else if(requestCode==WITHOLD){
            val gross= _binding.txttotalgross.text.toString()
            val ans= decimalVat.format(gross.toDouble())
            _binding.txttotalgross.text= ans
            recomputecomputeALL(gross.toDouble())

            var deductcommision = ans.toDouble() - commisionReturned.totalcommision
            if(checkBonusInitialized() && ans.toDouble()>=14000)
                deductcommision  = deductcommision - bonusreturned.totalBonus

            if(costAllTrip==null) costAllTrip=0.0
            deductcommision=deductcommision-costAllTrip
            if(witholdingAllTrip==null) witholdingAllTrip=0.0
            deductcommision=deductcommision + witholdingAllTrip
            deductcommision= deductcommision- partial


            val totalnet= decimalVat.format(deductcommision)
            deductcommision= totalnet.toDouble()
            _binding.txtnetcollection.text= deductcommision.toString()
        }
        else if(requestCode==CANCELLED){
            Log.d("canceled","canceled")
        }





    }






    //region SYNCHING
    private var triptickets:ArrayList<Sycn_TripticketTable> = arrayListOf()
    private var inspectionreport:ArrayList<Sycnh_InspectionReportTable> = arrayListOf()
    private var mpadassignment:ArrayList<Synch_mpadAssignmentsTable> = arrayListOf()
    private var partialremit:ArrayList<Synch_partialremitTable> = arrayListOf()
    private var tripcost:ArrayList<Synch_TripCostTable> = arrayListOf()
    private var withodling:ArrayList<Synch_TripwitholdingTable> = arrayListOf()
    private var TripReversesynching:ArrayList<Synch_TripReverseTable> = arrayListOf()
    private var LogReportsynching:ArrayList<Synch_LogReport> = arrayListOf()



    var alltickets:kotlin.collections.List<TripTicketTable> = arrayListOf()
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
                    ingressoRefId = GlobalVariable.ingressoRefId,
                    reverse = it.reverse

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
    val ProcessAllTripReverse:(state:List<TripReverseTable>) ->Unit={
        var partial:Double=0.0
        val decimalVat = DecimalFormat("#.00")

        if(it!=null) {

            it.forEach {
                var method= Synch_TripReverseTable(
                    Id = 0,
                    amount = it.amount!!,
                    dateTimeStamp = it.dateTimeStamp,
                    deviceName = it.deviceName,
                    direction= it.direction,
                    reverseId = it.reverseId,
                    terminal = it.terminal,
                    ingressoRefId = it.ingressoRefId!!

                )
                TripReversesynching.add(method)
            }

            dbViewmodel.insert_synch_TripReverse(TripReversesynching)
        }
    }
    val ProcessLogReports:(state:List<LogReport>) ->Unit={


        if(it!=null) {

            it.forEach {
                var method= Synch_LogReport(
                    Id = 0,
                    description= it.description,
                    dateTimeStamp = it.dateTimeStamp,
                    deviceName = it.deviceName,
                    ingressoRefId = it.ingressoRefId!!
                )
                LogReportsynching.add(method)
            }

            dbViewmodel.insert_synch_LogReport(LogReportsynching)
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
                    ingressoRefId = GlobalVariable.ingressoRefId,
                    terminal = it.terminal
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
                    ingressoRefId = GlobalVariable.ingressoRefId,
                    terminal = it.terminal

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
                printText()
                dbViewmodel.insert_synch_witholding(withodling)
//               dbViewmodel.truncatetables()
                // resetALl()
                _binding.btnigressoreprint.isEnabled=true
                _binding.btnigresso.isVisible=false
                _binding.btnigresso.isEnabled=false
                _binding.btnclose.isVisible=true
                _binding.btnclose.isEnabled=true
                _binding.btnAddmanual.isEnabled=false
                _binding.btnCancelledticket.isEnabled=false
                _binding.btnExpenses.isEnabled=false
                _binding.btnWitholding.isEnabled=false
                _binding.btncomputefinalremit.isEnabled=false
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

        }
    }





    fun <T> LiveData<T>.observeOnce(observer1: NewIngressoActivity, observer: Observer<T>) {
        observeForever(object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

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
                    this@NewIngressoActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@NewIngressoActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@NewIngressoActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@NewIngressoActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@NewIngressoActivity,
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
                    this@NewIngressoActivity,
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
                return
            }
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
            override fun onRunResult(isSuccess: Boolean) {}

            @Throws(RemoteException::class)
            override fun onReturnString(value: String) {}
        }



        val intent = Intent()
        intent.setPackage("com.iposprinter.iposprinterservice")
        intent.action = "com.iposprinter.iposprinterservice.IPosPrintService"
        bindService(intent, connectService, BIND_AUTO_CREATE)
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
        try {
            printerStatus = mIPosPrinterService!!.printerStatus
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return printerStatus
    }

    fun printerInit() {
        ThreadPoolManager.getInstance().executeTask(Runnable {
            try {
                mIPosPrinterService!!.printerInit(callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        })
    }



    fun printText() {
        ThreadPoolManager.getInstance().executeTask {

            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Machine: ${GlobalVariable.machineName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Per: ${GlobalVariable.permitNumber}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Serial: ${GlobalVariable.serialNumber}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Ingresso Details\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Line: ${GlobalVariable.line}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Bus #: ${GlobalVariable.bus}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("mPAD: ${GlobalVariable.deviceName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Cashier: ${GlobalVariable.cashiername}\n", "ST", 24, 1,callback)
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
                mIPosPrinterService!!.printSpecifiedTypeText("Gross: ${_binding.txttotalgross.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Net: ${_binding.txtnetcollection.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Cancelled: ${_binding.etCancelledticket.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Partial Remit: ${_binding.txtpartialremit.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Expenses: ${costAllTrip}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Bonus Expenses: ${GlobalVariable.bonus}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Witholding: ${witholdingAllTrip}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Short: ${_binding.etshortover.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Charge to: ${infault}\n", "ST", 24, callback)

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Benefits\n\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Total Salary: ${_binding.txttotalcommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Driver Salary: ${_binding.txtdrivercommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor Salary: ${_binding.txtconductorcommision.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Driver bonus: ${_binding.txtdriverbonus.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor bonus: ${_binding.txtconductorbonus.text}\n", "ST", 24, callback)

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Expenses\n\n", "ST", 24, 1,callback)

                GlobalVariable.AllTripCost.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("Expenses type: ${it.costType}\n", "ST", 24, callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Amount: ${it.amount}\n", "ST", 24, callback)
                }

                GlobalVariable.bonusArraylist.forEach {
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

                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            finally {
                runOnUiThread {}
            }
        }
    }



    fun bitmapToInputStream(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray().inputStream()
    }

    fun ingressoTerminals(role: TerminalTable) {
        GlobalVariable.terminal= role.name
        Toast(this).showCustomToast("${GlobalVariable.terminal}",this)
    }

    //endregion
}