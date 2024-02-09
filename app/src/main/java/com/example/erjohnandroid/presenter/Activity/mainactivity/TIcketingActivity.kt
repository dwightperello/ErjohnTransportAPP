package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.Model.convertions.TicketConvertions
import com.example.erjohnandroid.database.Model.convertions.TripAmountPerReverse
import com.example.erjohnandroid.database.Model.convertions.TripGross
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.database.viewmodel.sd_viewmodel
import com.example.erjohnandroid.databinding.ActivityTicketingBinding
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.BatteryReceiver
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.GlobalVariable.destinationcounter
import com.example.erjohnandroid.util.GlobalVariable.linesegment
import com.example.erjohnandroid.util.GlobalVariable.origincounter
import com.example.erjohnandroid.util.GlobalVariable.ticketcounter
import com.example.erjohnandroid.util.GlobalVariable.ticketnumber
import com.example.erjohnandroid.util.showCustomToast
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt


@AndroidEntryPoint
class TIcketingActivity : AppCompatActivity() {
    lateinit var _binding:ActivityTicketingBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private val sdViewmodel:sd_viewmodel by viewModels()
    private val externalViewModel:externalViewModel by viewModels()

    private var batteryReceiver: BatteryReceiver? = null

    //private var linesegment:List<LineSegmentTable>?= null

    //private  lateinit var passengerTypeAdapter: PassengerTypeAdapter

    private var passtype:String?=null

    private var origin:LineSegmentTable?= null
    private var destination:LineSegmentTable?= null

    var postTripticket:TripTicketTable?= null

    private var qty:Int=1

//    var discountamount:Double=0.20
    val pesoSign = '\u20B1'
    var totalfare:String="0.0"
    var kmdiffprint:Int=0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTicketingBinding.inflate(layoutInflater)
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

        batteryReceiver = BatteryReceiver { batteryLevel ->
           if(batteryLevel<5) showAlertDialog()

            _binding.txtbat.text = "BAT: $batteryLevel%"
            GlobalVariable.batLevel="${batteryLevel}%"
        }

        if(linesegment.isNullOrEmpty()) {
           // dbViewmodel.getLinesegment(GlobalVariable.lineid!!)
        }
       // dbViewmodel.getPassengerType()
        dbViewmodel.getLinesegment(GlobalVariable.lineid!!)
        dbViewmodel.getAllfarebykm()
       // dbViewmodel.getGross()
        initView()
       // passengerTypeAdapter = PassengerTypeAdapter(this)

        initPrinter()

        initCheckbox()

        initBUttons()

        _binding.txtpesosign.text= pesoSign.toString()

        _binding.btnclose.setOnClickListener {
            val formattedDateTime = getCurrentDateInFormat()
            GlobalVariable.saveLogreport("Ticketing screen closed")

            super.onBackPressed()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }
        val formattedDateTime = getCurrentDateInFormat()
        GlobalVariable.saveLogreport("start ticketing")



    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.linesegment.observe(this, Observer {
                state->processLine(state)

        })

        dbViewmodel.hotspots.observe(this,Observer{
            state -> ProcessHotspot(state)
        })

//        dbViewmodel.passengertype.observe(this, Observer {
//            state->ProcessPassType(state)
//        })

        dbViewmodel.remnorth.observe(this, Observer {
                state ->processRemnorth(state)


        })

        dbViewmodel.remsouth.observe(this, Observer {
                state -> processRemsouth(state)
        })

        dbViewmodel.tripamountperreverse.observe(this,Observer{
            state-> ProcessAmountPerReverse(state)
        })

        dbViewmodel.tripgross.observe(this,Observer{
            state -> ProcessGross(state)
        })

        dbViewmodel.fare.observe(this, Observer {
                state -> ProcessFare(state)
        })
        dbViewmodel.allfarebykm.observe(this,Observer{
            state -> ProcessFarebykm(state)
        })
    }

    private fun ProcessFare(state: FareTable){
        GlobalVariable.discountAmount= state.discountAmount
        GlobalVariable.basefair=state.baseAmount
        GlobalVariable.exceedAmount=state.exceedAmount
        GlobalVariable.specialexceedAmount=state.specialExceedAmount
    }

    private  var dbFarebykm:ArrayList<FareByKm>?= arrayListOf()
    val ProcessFarebykm:(state:kotlin.collections.List<FareByKm>?) ->Unit={

        if(it!=null){
           dbFarebykm?.addAll(it)
        }
    }

    val ProcessAmountPerReverse:(state: TripAmountPerReverse?) ->Unit={

        if(it?.sumamount!=null){
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(it.sumamount)
           _binding.txtonhand.text = "CASH: ${ans}"
            _binding.txtticketcountperreverse.text= "TICKET COUNT: ${it.ticket_count.toString()}"
        }else{
            _binding.txtonhand.text = "CASH: 0.0"
            _binding.txtticketcountperreverse.text= " TICKET COUNT: 0"
        }
    }

    val ProcessGross:(state: TripGross?) ->Unit={

        if(it?.sumamount!=null){
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(it.sumamount)
            _binding.txtgross.text = "GROSS: ${ans}"
           // _binding.txtticketcountperreverse.text= "TICKET COUNT: ${it.ticket_count.toString()}"
        }else{
            _binding.txtgross.text = "GROSS: 0.0"

        }
    }

    val initView={
        _binding.txtLine.text=GlobalVariable.line?.toUpperCase()
        _binding.cbRegular.isChecked=true
        passtype="Regular"
    }

    val initCheckbox:()->Unit={

        _binding.cbRegular.setOnClickListener {
            if(_binding.cbSenior.isChecked || _binding.cbStudent.isChecked || _binding.cbPwd.isChecked || _binding.cbBaggage.isChecked){
                _binding.cbSenior.isChecked=false
                _binding.cbStudent.isChecked=false
                _binding.cbPwd.isChecked=false


                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.setText("")

            }
            _binding.cbRegular.isChecked=true
            passtype="Regular"
            computeAmount()
        }

        _binding.cbSenior.setOnClickListener {
            if(_binding.cbRegular.isChecked || _binding.cbStudent.isChecked || _binding.cbPwd.isChecked || _binding.cbBaggage.isChecked){
                _binding.cbRegular.isChecked=false
                _binding.cbStudent.isChecked=false
                _binding.cbPwd.isChecked=false
                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.setText("")
            }
            _binding.cbSenior.isChecked=true
            passtype="Senior"
            computeAmount()
        }

        _binding.cbPwd.setOnClickListener {
            if(_binding.cbRegular.isChecked || _binding.cbStudent.isChecked || _binding.cbSenior.isChecked || _binding.cbBaggage.isChecked){
                _binding.cbRegular.isChecked=false
                _binding.cbStudent.isChecked=false
                _binding.cbSenior.isChecked=false
                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.setText("")
            }
            _binding.cbPwd.isChecked=true
            passtype="PWD"
            computeAmount()
        }

        _binding.cbStudent.setOnClickListener {
            if(_binding.cbRegular.isChecked || _binding.cbSenior.isChecked || _binding.cbPwd.isChecked|| _binding.cbBaggage.isChecked){
                _binding.cbRegular.isChecked=false
                _binding.cbSenior.isChecked=false
                _binding.cbPwd.isChecked=false
                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.setText("")
            }
            _binding.cbStudent.isChecked=true
            passtype="Student"
            computeAmount()
        }

        _binding.cbBaggage.setOnClickListener {
            if(_binding.cbBaggage.isChecked){
                _binding.etbaggaeamount.isEnabled=true
                _binding.btnaddbaggage.isEnabled=true
                _binding.cbRegular.isChecked=false
                _binding.cbPwd.isChecked=false
                _binding.cbStudent.isChecked=false
                _binding.cbSenior.isChecked=false
                _binding.txtamount.text="0.0"
                passtype="Baggage"
               // computeAmount()

            }else{
                _binding.cbRegular.isChecked=true
                _binding.cbPwd.isChecked=false
                _binding.cbStudent.isChecked=false
                _binding.cbSenior.isChecked=false
                passtype="Regular"
                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.setText("")
                computeAmount()
            }
        }

        _binding.btnaddbaggage.setOnClickListener {
            //computeAmount()
            _binding.txtamount.text = _binding.etbaggaeamount.text.toString()
            hideSoftKeyboard()
            computeAmount()

        }
    }

    val processLine:(state: List<LineSegmentTable>?) ->Unit={
        if(!it.isNullOrEmpty()){
            if(GlobalVariable.direction.equals("South")) {
                linesegment = it!!
                //  destinationcounter += 1
            }
            else{
                linesegment= it?.reversed()
                //  destinationcounter +=1
            }
            origin= linesegment?.get(origincounter)
            destination=linesegment?.get(destinationcounter)

            _binding.txtoriginKM.text= origin?.kmPoint.toString()
            _binding.txtDestination.text= destination?.kmPoint.toString()

            _binding.etOrigin.setText(origin?.name)
            _binding.etDestination.setText(destination?.name)

        }else
        {
            Toast.makeText(this,"NO LINE SEGMENT",Toast.LENGTH_LONG).show()
            _binding.btnDestinationBack.isEnabled=false
            _binding.btnDestinationForward.isEnabled=false
            _binding.btnOriginBack.isEnabled=false
            _binding.btnOriginForward.isEnabled=false
        }

        dbViewmodel.gethotspots(GlobalVariable.lineid!!)

    }

    val ProcessHotspot:(state: List<HotSpotsTable>?) ->Unit={
        if(!it.isNullOrEmpty()){
            GlobalVariable.hotspot= it

        }else
        {
           // Toast(this@TIcketingActivity).showCustomToast("NO HOTSPOT FOUND",this@TIcketingActivity)
        }



    }

    val processRemsouth:(state: List<TripTicketTable>?) ->Unit={
        var rem:Int=0
        if(it?.size!=null){
            it.forEach {
                rem += it.qty
            }

            GlobalVariable.remainingPass= rem
            _binding.txtrem.text="Passenger: ${GlobalVariable.remainingPass.toString()}"
        }else{
            GlobalVariable.remainingPass= 0
            _binding.txtrem.text="Passenger: ${GlobalVariable.remainingPass.toString()}"
        }
    }

    val processRemnorth:(state: List<TripTicketTable>?) ->Unit={
        var rem:Int=0
        if(it?.size!=null){
            it.forEach {
                rem += it.qty
            }
            GlobalVariable.remainingPass= rem
            _binding.txtrem.text="Passenger: ${GlobalVariable.remainingPass.toString()}"
        }else{
            GlobalVariable.remainingPass= 0
            _binding.txtrem.text="Passenger: ${GlobalVariable.remainingPass.toString()}"
        }

    }




    val initBUttons={
        _binding.txtqty.text=qty.toString()

        _binding.btnOriginBack.setOnClickListener {
            origincounter -= 1

           // if(origincounter<=0) return@setOnClickListener
            if(origincounter<0){
                origincounter += 1
               // return@setOnClickListener
            }


            origin=linesegment?.get(origincounter)
            _binding.txtoriginKM.text= origin?.kmPoint.toString()
            _binding.etOrigin.setText(origin?.name)
            if(GlobalVariable.direction.equals("South")){
                dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            clearAllSearchFields()


            computeAmount()
        }

        _binding.btnOriginForward.setOnClickListener {
            origincounter += 1
           // if(origincounter >= linesegment!!.size) return@setOnClickListener
            if(origincounter >= linesegment!!.size){
                origincounter -= 1
               // return@setOnClickListener
            }

            origin=linesegment?.get(origincounter)
            _binding.txtoriginKM.text= origin?.kmPoint.toString()
            _binding.etOrigin.setText(origin?.name)

            if(origincounter==destinationcounter){
                destinationcounter+=1
                if(destinationcounter >= linesegment!!.size){
                    destinationcounter -=1
                }
                destination=linesegment?.get(destinationcounter)
                _binding.txtDestination.text= destination?.kmPoint.toString()
                _binding.etDestination.setText(destination?.name)
            }


            if(GlobalVariable.direction.equals("South")){
                dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            clearAllSearchFields()
            computeAmount()
        }

        _binding.btnDestinationBack.setOnClickListener {
            destinationcounter -= 1
            if(destinationcounter==origincounter) destinationcounter +=1
//           if(destinationcounter>origincounter){
//               destinationcounter -= 1
//               if(destinationcounter==origincounter)
//                   destinationcounter +=1
//           }
                destination=linesegment?.get(destinationcounter)
                _binding.txtDestination.text= destination?.kmPoint.toString()
                _binding.etDestination.setText(destination?.name)
            clearAllSearchFields()
            computeAmount()
        }

        _binding.btnDestinationForward.setOnClickListener {
            destinationcounter +=1
            if(destinationcounter >=linesegment!!.size){
                destinationcounter -=1
            }
//            if(destinationcounter >= linesegment!!.size) return@setOnClickListener
//            destinationcounter += 1
            destination=linesegment?.get(destinationcounter)
            _binding.txtDestination.text= destination?.kmPoint.toString()
            _binding.etDestination.setText(destination?.name)
            clearAllSearchFields()
            computeAmount()
        }

        _binding.etOrigin.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    _binding.etOrigin.text.clear()
                }
                return false // Return false to allow other touch events to be handled
            }
        })

        _binding.etDestination.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    _binding.etDestination.text.clear()
                }
                return false // Return false to allow other touch events to be handled
            }
        })

      _binding.etOriginsearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                var originss= _binding.etOriginsearch.text.toString()
                if(!TextUtils.isDigitsOnly(originss) || originss.isNullOrEmpty()){
                    //Toast(this@TIcketingActivity).showCustomToast("Search field should only contain numeric or field is empty",this@TIcketingActivity)
                   // hideSoftKeyboard()
                    hideKeyboard(_binding.etOriginsearch)
                    return
                }

                var s_origin=  linesegment?.find {
                    it.kmPoint== originss.toInt()
                }

                val index=  if(s_origin!= null){
                    linesegment?.indexOf(s_origin)!!
                } else {
                    -1

                }
                when(index){
                    -1 ->{
                        Toast(this@TIcketingActivity).showCustomToast("Route not found",this@TIcketingActivity)
                        return
                    }
                }


                origin= linesegment?.get(index)
                origincounter = index
                _binding.txtoriginKM.text= origin?.kmPoint.toString()
                _binding.etOrigin.setText(origin?.name)

                computeAmount()
                if(GlobalVariable.direction.equals("South")){
                    dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
                }
                else{
                    dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
                }
            }
        })

        _binding.btnOriginsearch.setOnClickListener {
            var originss= _binding.etOrigin.text.toString()
            if(!TextUtils.isDigitsOnly(originss) || originss.isNullOrEmpty()){
                Toast(this).showCustomToast("Search field should only contain numeric or field is empty",this)
                return@setOnClickListener
            }
            var s_origin=  linesegment?.find {
                it.kmPoint== originss.toInt()
            }

            val index=  if(s_origin!= null){
                linesegment?.indexOf(s_origin)!!
            } else {
                -1

            }
            when(index){
                -1 ->{
                    Toast(this).showCustomToast("Route not found",this)
                    return@setOnClickListener
                }
            }


            origin= linesegment?.get(index)
            _binding.txtoriginKM.text= origin?.kmPoint.toString()
            _binding.etOrigin.setText(origin?.name)

            computeAmount()
            if(GlobalVariable.direction.equals("South")){
                dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }

            hideSoftKeyboard()
        }

       _binding.etDestinationsearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                var destinationss= _binding.etDestinationsearch.text.toString()
                if(!TextUtils.isDigitsOnly(destinationss)||destinationss.isNullOrEmpty()){
                   // Toast(this@TIcketingActivity).showCustomToast("Search field should only contain numeric or field is empty",this@TIcketingActivity)
                    hideKeyboard(_binding.etDestinationsearch)
                    return
                }

                var s_desitnation=  linesegment?.find {
                    it.kmPoint== destinationss.toInt()
                }

                val index= if(s_desitnation!= null){
                    linesegment?.indexOf(s_desitnation)!!

                } else {
                    -1

                }

                when(index){
                    -1 ->{
                        Toast(this@TIcketingActivity).showCustomToast("No route found",this@TIcketingActivity)
                        return
                    }

                }

                destination=linesegment?.get(index)
                destinationcounter =index
                _binding.txtDestination.text= destination?.kmPoint.toString()
                _binding.etDestination.setText(destination?.name)

                computeAmount()
//            _binding.txtDestination.text= s_desitnation?.kmPoint.toString()
//            _binding.etDestination.setText(s_desitnation?.name)
               // hideSoftKeyboard()
            }
        })


        _binding.btnDestinationsearch.setOnClickListener {
            var destinationss= _binding.etDestination.text.toString()
            if(!TextUtils.isDigitsOnly(destinationss)||destinationss.isNullOrEmpty()){
                Toast(this).showCustomToast("Search field should only contain numeric or field is empty",this)
                return@setOnClickListener
            }

            var s_desitnation=  linesegment?.find {
                it.kmPoint== destinationss.toInt()
            }

            val index= if(s_desitnation!= null){
                linesegment?.indexOf(s_desitnation)!!

            } else {
                -1

            }
            when(index){
                -1 ->{
                    Toast(this).showCustomToast("No route found",this)
                    return@setOnClickListener
                }
            }

            destination=linesegment?.get(index)
            _binding.txtDestination.text= destination?.kmPoint.toString()
            _binding.etDestination.setText(destination?.name)

            computeAmount()
//            _binding.txtDestination.text= s_desitnation?.kmPoint.toString()
//            _binding.etDestination.setText(s_desitnation?.name)
            hideSoftKeyboard()
        }

        _binding.btnMinusqty.setOnClickListener {
            if(qty>1){
                qty-=1
                _binding.txtqty.text=qty.toString()
                computeAmount()
            }
            else{
                _binding.txtqty.text="1"
                computeAmount()
            }
        }

        _binding.btnAddquantity.setOnClickListener {
            qty+=1
            _binding.txtqty.text=qty.toString()
            computeAmount()
        }

        _binding.btnPrintticke.setOnClickListener {
            clearAllSearchFields()
            _binding.btnPrintticke.isEnabled=false
            var o= _binding.txtoriginKM.text.toString()
            var d = _binding.txtDestination.text.toString()


            if(destinationcounter == origincounter || _binding.txtamount.text.toString().equals("0.0") || _binding.txtDestination.text.toString()==_binding.txtoriginKM.text.toString()){
                Toast(this).showCustomToast("AMOUNT IS 0, SELECT DESTINATION",this)
                _binding.txtamount.text ="0.0"
                _binding.btnPrintticke.isEnabled=true
                return@setOnClickListener
            }

            if(GlobalVariable.direction.equals("South")){

                if(o.toInt()>= d.toInt() ){
                    Toast(this).showCustomToast("SOUTH BOUND, PLEASE CHECK KM",this)
                    _binding.txtamount.text ="0.0"
                    _binding.btnPrintticke.isEnabled=true
                    return@setOnClickListener
                }
            }else if(GlobalVariable.direction.equals("North"))
            {
//                var s= _binding.txtoriginKM.text.toString()
//                var d = _binding.txtDestination.text.toString()
                if(d.toInt()>= o.toInt() ){
                    Toast(this).showCustomToast("North BOUND, PLEASE CHECK KM",this)
                    _binding.txtamount.text ="0.0"
                    _binding.btnPrintticke.isEnabled=true
                    return@setOnClickListener
                }
            }

            if(!passtype.isNullOrEmpty()) {
//                ticketnumber +=1
                ticketcounter +=1
                _binding.txtticketcount.text="Next ticket: 00${ticketcounter.toString()}"
                 // var amounttotal = computeAmount()


                postTripticket?.titcketNumber= ticketnumber.toString()
                dbViewmodel.insertTripTicketBulk(postTripticket!!)
                sdViewmodel.insertTripTicketBulk(postTripticket!!)



            }
            else{
                Toast(this).showCustomToast("Select passenger type",this)
                return@setOnClickListener
            }

            if(GlobalVariable.direction.equals("South")){
                dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }

                dbViewmodel.getTripAmountPerReverse(GlobalVariable.tripreverse!!)
                dbViewmodel.getGross()

           // printText("Erjohn & Almark Transit Corp")
                printText()
            if(_binding.cbBaggage.isChecked){
                _binding.cbBaggage.isChecked=false
                _binding.etbaggaeamount.isEnabled=false
                _binding.btnaddbaggage.isEnabled=false

            }

//            if(passtype.equals("Student") || passtype.equals("Senior")) {
//                //passengerTypeAdapter.clearSelection()
//                //passtype=null
//                resetCheckbox()
//            }
           // computeAmount()


            if(passtype.equals("Student") || passtype.equals("Senior") || passtype.equals("PWD") || passtype.equals("Baggage")) {
                //passengerTypeAdapter.clearSelection()
                //passtype=null
                resetCheckbox()
            }
            //else computeAmount()

        }



    }



    val resetCheckbox={
        _binding.cbRegular.isChecked=true
        // passtype="Regular"
        _binding.cbSenior.isChecked=false
        _binding.cbStudent.isChecked=false
        _binding.cbPwd.isChecked=false
        _binding.cbBaggage.isChecked=false
        _binding.etbaggaeamount.setText("")
        //computeAmount()


    }

    val computeAmount:()->String ={
        val discountamount: Double = GlobalVariable.discountAmount
        val fare:Double=GlobalVariable.basefair
        val specialExceedamount:Double= GlobalVariable.specialexceedAmount
        val exceedAmount:Double=GlobalVariable.exceedAmount
        var baggageamount:Double=0.0
        var KMdiff:Int=0
        var amount:String?=null
        var getkmdiff:Int?= null
        var getExceedAmount:Double?=0.0
        var total:Double?=0.0
        var discount:Double?=0.0
        var qty:Int?=0
        var amountafterdiscount:Double?= null
        var z= _binding.txtqty.text.toString()
        qty= z.toInt()

        if(GlobalVariable.line!!.equals("GMA-PITX") || GlobalVariable.line.equals("PITX-GMA")){
            try {
                if (passtype == "Baggage" && _binding.cbBaggage.isChecked) {
                    if(GlobalVariable.direction.equals("South")) KMdiff = destination?.kmPoint!! - origin?.kmPoint!!
                    else KMdiff = origin?.kmPoint!! - destination?.kmPoint!!
                    val bag = _binding.etbaggaeamount.text.toString().toDoubleOrNull() ?: 0.0
                    baggageamount = bag
                    total = baggageamount
                }

                else{
                    val amountfromHspot= checkhotspotPITX(destination?.kmPoint!!,origin?.kmPoint!!)
                    if (amountfromHspot!=null){
                        if(GlobalVariable.direction.equals("South")) KMdiff = destination?.kmPoint!! - origin?.kmPoint!!
                        else KMdiff = origin?.kmPoint!! - destination?.kmPoint!!
                        total= amountfromHspot
                        _binding.btnPrintticke.isVisible=true
                    }
                    else
                    {
                        if(GlobalVariable.direction.equals("South")) {
                            KMdiff = destination?.kmPoint!! - origin?.kmPoint!!
                            kmdiffprint = KMdiff

                            _binding.btnPrintticke.isVisible = KMdiff > 0

                            if (KMdiff <= 10 && origin?.kmPoint!! < 10) {
//                                total = if (passtype in listOf("Senior", "Student", "PWD")) {
//                                    discount = (discountamount / 100) * fare
//                                    fare - discount

                                total = if (passtype in listOf("Senior", "Student", "PWD")) {
                                    discount = fare * discountamount
                                    fare - discount

//                                   if(passtype in listOf("Senior", "Student", "PWD"))
//                                    val discount = fare * discountamount
//                                    val amountAfterDiscount = fare - discount
//                                    total = amountAfterDiscount * qty


                                } else {
                                    fare.toDouble()
                                } * qty
                            }

                            else if (KMdiff > 10 && origin?.kmPoint!! <= 10) {
                                val baseFare = KMdiff!! * specialExceedamount
                                if (passtype in listOf("Senior", "Student", "PWD")) {
                                    val discount = baseFare * discountamount
                                    val amountAfterDiscount = baseFare - discount
                                    total = amountAfterDiscount * qty + 2
                                    val df = DecimalFormat("#.##")
                                    val roundedNumber = df.format(total).toDouble()
                                    total= roundedNumber
                                    if(total ==31.68) total --
                                    else if(total == 46.52)total--
                                    else if(total ==48.64)total --
                                    else if(total == 50.76) total --
                                    else if(origin!!.kmPoint==15 && destination!!.kmPoint==30) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==27) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==26) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==25) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==24) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==18) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==17) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==16) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==15) total --
                                    else if(origin!!.kmPoint==10 && destination!!.kmPoint==25) total --
                                    else if(origin!!.kmPoint==9 && destination!!.kmPoint==24) total --
                                    else if(origin!!.kmPoint==8 && destination!!.kmPoint==23) total --
                                    else if(origin!!.kmPoint==7 && destination!!.kmPoint==22) total --
                                } else {
                                    total = baseFare * qty + 2
                                }
                            }

                            else if (KMdiff <= 5 && origin?.kmPoint!! >= 10) {
                                if (passtype in listOf("Senior", "Student", "PWD"))total = 12.0
                                else  total = 15.0.toDouble() * qty
                            }

                            else if (origin?.kmPoint!! >= 10  && KMdiff > 5 && KMdiff<=9) {
                                if (passtype in listOf("Senior", "Student", "PWD")) total = 21.0
                                else  total = 26.0.toDouble()
                            }

                            else if (origin?.kmPoint!! >= 10 &&  KMdiff > 9) {
                                val baseFare = KMdiff!! * specialExceedamount
                                if (passtype in listOf("Senior", "Student", "PWD"))
                                {   val df = DecimalFormat("#.##")
                                    val discount = baseFare * discountamount
                                    val amountAfterDiscount = baseFare - discount
                                    total = amountAfterDiscount * qty + 2
                                    val roundedNumber = df.format(total).toDouble()
                                    total= roundedNumber
                                    if(total ==31.68) total --
                                    else if(total == 46.51)total--
                                    else if(total ==48.64)total --
                                    else if(total == 50.76) total --
                                    else if(origin!!.kmPoint==15 && destination!!.kmPoint==30) total --
                                    else if(origin!!.kmPoint==16 && destination!!.kmPoint==29) total --
                                    else if(origin!!.kmPoint==14 && destination!!.kmPoint==29) total --
                                    else if(origin!!.kmPoint==13 && destination!!.kmPoint==29) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==29) total ++
                                    else if(origin!!.kmPoint==15 && destination!!.kmPoint==28) total --
                                    else if(origin!!.kmPoint==13 && destination!!.kmPoint==28) total --
                                    else if(origin!!.kmPoint==12 && destination!!.kmPoint==28) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==28) total ++
                                    else if(origin!!.kmPoint==14 && destination!!.kmPoint==27) total --
                                    else if(origin!!.kmPoint==12 && destination!!.kmPoint==27) total --
                                    else if(origin!!.kmPoint==13 && destination!!.kmPoint==26) total --
                                    else if(origin!!.kmPoint==11 && destination!!.kmPoint==26) total --
                                    else if(origin!!.kmPoint==12 && destination!!.kmPoint==25) total --
                                    else if(origin!!.kmPoint==10 && destination!!.kmPoint==25) total = total-1
                                    else if(origin!!.kmPoint==11 && destination!!.kmPoint==24) total --
                                    else if(origin!!.kmPoint==9 && destination!!.kmPoint==24) total = total-1
                                    else if(origin!!.kmPoint==8 && destination!!.kmPoint==23) total = total-1
                                    else if(origin!!.kmPoint==7 && destination!!.kmPoint==23) total = total-1
                                    else if(origin!!.kmPoint==7 && destination!!.kmPoint==22) total = total-1
                                    else if(origin!!.kmPoint==9 && destination!!.kmPoint==22) total = total-1
                                    else if(origin!!.kmPoint==8 && destination!!.kmPoint==21) total = total-1

                                    else if(origin!!.kmPoint==11 && destination!!.kmPoint==27) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==27) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==27) total ++
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==26) total --
                                    else if(origin!!.kmPoint==9 && destination!!.kmPoint==25) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==25) total --
                                    else if(origin!!.kmPoint==8 && destination!!.kmPoint==24) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==24) total --
                                    else if(origin!!.kmPoint==7 && destination!!.kmPoint==20) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==19) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==18) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==18) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==17) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==17) total --
                                    else if(origin!!.kmPoint==3 && destination!!.kmPoint==16) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==16) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==16) total --
                                    else if(origin!!.kmPoint==2 && destination!!.kmPoint==15) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==15) total --
                                    else if(origin!!.kmPoint==1 && destination!!.kmPoint==14) total --
                                    else if(origin!!.kmPoint==0 && destination!!.kmPoint==13) total --


                                } else {
                                    total = baseFare * qty + 2
                                }
                            }
                        }

                        //PITEX NORTH
                        else{
                            KMdiff= origin?.kmPoint!! - destination?.kmPoint!!
                            kmdiffprint= KMdiff
                            if (KMdiff <= 0) {
                                // throw IllegalArgumentException()
                                _binding.btnPrintticke.isVisible = false
                            } else {
                                _binding.btnPrintticke.isVisible = true
                            }

                            if (KMdiff <= 5 && !(origin?.kmPoint!! <= 10)) {

                                if (passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD"))
                                { total=12.0}
                                else {total = 15.0.toDouble() * qty}


                            } else if (KMdiff > 5 && KMdiff <9 && !(origin?.kmPoint!!<=10)) {

                                if (passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD"))
                                { total=21.0 }
                                else { total= 26.0}
                            }
                            else if(KMdiff==9 && !(origin?.kmPoint!!<=10)){
                                if (passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD"))
                                { total=21.0 }
                                else { total= 26.0}
                            }

                            else if (KMdiff >9) {

                                if (passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD"))
                                {
                                    val df = DecimalFormat("#.##")

                                    getkmdiff = KMdiff
                                    getExceedAmount = getkmdiff * specialExceedamount
                                    discount = getExceedAmount * discountamount
                                    amountafterdiscount = getExceedAmount - discount
                                    total = amountafterdiscount
                                    total = total * qty + 2
                                    val roundedNumber = df.format(total).toDouble()
                                    total= roundedNumber
                                    if(total ==31.68) total --
                                    else if(total == 46.52)total--
                                    else if(total ==48.64)total --
                                    else if(total == 50.76) total --
                                    else if(origin!!.kmPoint==30 && destination!!.kmPoint==15) total --
                                    else if(origin!!.kmPoint==29 && destination!!.kmPoint==16) total --
                                    else if(origin!!.kmPoint==29 && destination!!.kmPoint==14) total --
                                    else if(origin!!.kmPoint==29 && destination!!.kmPoint==13) total --
                                    else if(origin!!.kmPoint==29 && destination!!.kmPoint==2) total ++
                                    else if(origin!!.kmPoint==28 && destination!!.kmPoint==15) total --
                                    else if(origin!!.kmPoint==28 && destination!!.kmPoint==13) total --
                                    else if(origin!!.kmPoint==28 && destination!!.kmPoint==12) total --
                                    else if(origin!!.kmPoint==28 && destination!!.kmPoint==1) total ++
                                    else if(origin!!.kmPoint==27 && destination!!.kmPoint==14) total --
                                    else if(origin!!.kmPoint==27 && destination!!.kmPoint==12) total --
                                    else if(origin!!.kmPoint==26 && destination!!.kmPoint==13) total --
                                    else if(origin!!.kmPoint==26 && destination!!.kmPoint==11) total --
                                    else if(origin!!.kmPoint==25 && destination!!.kmPoint==12) total --
                                    else if(origin!!.kmPoint==25 && destination!!.kmPoint==10) total --
                                    else if(origin!!.kmPoint==24 && destination!!.kmPoint==11) total --
                                    else if(origin!!.kmPoint==24 && destination!!.kmPoint==9) total --
                                    else if(origin!!.kmPoint==23 && destination!!.kmPoint==8) total --
                                    else if(origin!!.kmPoint==23 && destination!!.kmPoint==7) total --
                                    else if(origin!!.kmPoint==22 && destination!!.kmPoint==7) total --
                                    else if(origin!!.kmPoint==22 && destination!!.kmPoint==9) total --
                                    else if(origin!!.kmPoint==21 && destination!!.kmPoint==8) total --

                                    else if(origin!!.kmPoint==27 && destination!!.kmPoint==11) total --
                                    else if(origin!!.kmPoint==27 && destination!!.kmPoint==3) total --
                                    else if(origin!!.kmPoint==27 && destination!!.kmPoint==0) total ++
                                    else if(origin!!.kmPoint==26 && destination!!.kmPoint==2) total --
                                    else if(origin!!.kmPoint==25 && destination!!.kmPoint==9) total --
                                    else if(origin!!.kmPoint==25 && destination!!.kmPoint==1) total --
                                    else if(origin!!.kmPoint==25 && destination!!.kmPoint==1) total --
                                    else if(origin!!.kmPoint==24 && destination!!.kmPoint==8) total --
                                    else if(origin!!.kmPoint==24 && destination!!.kmPoint==0) total --
                                    else if(origin!!.kmPoint==20 && destination!!.kmPoint==7) total --
                                    else if(origin!!.kmPoint==19 && destination!!.kmPoint==3) total --
                                    else if(origin!!.kmPoint==18 && destination!!.kmPoint==3) total --
                                    else if(origin!!.kmPoint==18 && destination!!.kmPoint==2) total --
                                    else if(origin!!.kmPoint==17 && destination!!.kmPoint==2) total --
                                    else if(origin!!.kmPoint==17 && destination!!.kmPoint==1) total --
                                    else if(origin!!.kmPoint==16 && destination!!.kmPoint==3) total --
                                    else if(origin!!.kmPoint==16 && destination!!.kmPoint==1) total --
                                    else if(origin!!.kmPoint==16 && destination!!.kmPoint==0) total --
                                    else if(origin!!.kmPoint==15 && destination!!.kmPoint==2) total --
                                    else if(origin!!.kmPoint==15 && destination!!.kmPoint==0) total --
                                    else if(origin!!.kmPoint==14 && destination!!.kmPoint==1) total --
                                    else if(origin!!.kmPoint==13 && destination!!.kmPoint==0) total --




                                } else {
                                    getExceedAmount = KMdiff!! * specialExceedamount
                                    total = getExceedAmount
                                    total = total * qty + 2
                                }

                            }
                            else if( origin?.kmPoint!!<=10 && KMdiff<10){
                                //val discountamount: Double = 20.0
                                if (passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD"))
                                {
                                    //discount = (discountamount / 100) * fare
                                    discount = fare * discountamount
                                    amountafterdiscount = fare - discount
                                    total = amountafterdiscount * qty
                                } else {
                                    total=30.0
                                }
                            }


                        }

                    }

                }
            }catch (e:Exception){
                Toast(this).showCustomToast("ERROR on fare Computation: ${e.message}",this)
            }
        }
        //NOT PITX
        else
        {
            try {
                if(passtype.equals("Baggage")){
                    if(GlobalVariable.direction.equals("South")) KMdiff = destination?.kmPoint!! - origin?.kmPoint!!
                    else KMdiff = origin?.kmPoint!! - destination?.kmPoint!!
                    if(_binding.cbBaggage.isChecked){
                        var bag= _binding.etbaggaeamount.text.toString()
                        if(bag.isNullOrEmpty()){
                            bag="0.0"
                            baggageamount = bag.toDouble()
                            total = baggageamount
                        }else{
                            baggageamount = bag.toDouble()
                            total = baggageamount
                        }

                    }
                }
                else{
                    val amountfromHspot= checkhotspot(destination?.kmPoint!!,origin?.kmPoint!!)
                    if (amountfromHspot!=null){
                        if(GlobalVariable.direction.equals("South")) KMdiff = destination?.kmPoint!! - origin?.kmPoint!!
                        else KMdiff = origin?.kmPoint!! - destination?.kmPoint!!
                        total= amountfromHspot
                       _binding.btnPrintticke.isVisible=true

                    }
                    else
                    {
                        if(GlobalVariable.direction.equals("South")){
                            KMdiff= destination?.kmPoint!! - origin?.kmPoint!!
                            kmdiffprint= KMdiff
                            if(KMdiff<=0){
                                _binding.btnPrintticke.isVisible=false
                            }else{
                                _binding.btnPrintticke.isVisible=true
                            }
                            if(KMdiff <= 13){
//                                val discountamount:Double=20.0

                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    discount= fare *  discountamount
                                    amountafterdiscount= fare - discount



                                  //  discount=  (discountamount / 100) * fare
                                  //  amountafterdiscount= fare - discount
                                    total= amountafterdiscount * qty
                                }else{
                                    total= fare.toDouble() * qty
                                }
                            }
                            else if(KMdiff > 13 && destination?.kmPoint!!<=35){
//                                val discountamount:Double=0.20
                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    getkmdiff = KMdiff
                                    getExceedAmount = getkmdiff * exceedAmount
                                    discount= getExceedAmount *   discountamount
                                    amountafterdiscount= getExceedAmount - discount
                                    total = amountafterdiscount
                                    total = total * qty + 2
                                }
                                else
                                {
                                    getExceedAmount= KMdiff!! * exceedAmount
                                    total = getExceedAmount
                                    total= total * qty + 2
                                }
                            }
                            else if(KMdiff>13 && destination?.kmPoint!!>35){

                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    getkmdiff = KMdiff
                                    getExceedAmount = getkmdiff * exceedAmount
                                    discount= getExceedAmount *   discountamount
                                    amountafterdiscount= getExceedAmount - discount
                                    total = amountafterdiscount
                                    total = total * qty
                                }
                                else
                                {
                                    getExceedAmount= KMdiff!! * exceedAmount
                                   // getExceedAmount= KMdiff!! * 150.00
                                    total = getExceedAmount
                                    total= total * qty
                                }
                            }
                        }
                        else{
                            KMdiff= origin?.kmPoint!! - destination?.kmPoint!!
                            kmdiffprint= KMdiff
                            if(KMdiff<=0){
                                //throw IllegalArgumentException()
                                _binding.btnPrintticke.isVisible=false
                            }else _binding.btnPrintticke.isVisible= true
                            if(KMdiff!! <= 13){

                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    discount= fare *  discountamount
                                    amountafterdiscount= fare - discount
                                    total= amountafterdiscount * qty
                                }else{
                                    total= fare.toDouble() * qty
                                }
                            }
                            else if(KMdiff > 13 && origin?.kmPoint!!<=35){

                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    getkmdiff = KMdiff
                                    getExceedAmount = getkmdiff * exceedAmount
                                    discount= getExceedAmount *   discountamount
                                    amountafterdiscount= getExceedAmount - discount
                                    total = amountafterdiscount
                                    total = total * qty + 2
                                }
                                else
                                {
                                    getExceedAmount= KMdiff!! * exceedAmount
                                    total = getExceedAmount
                                    total= total * qty + 2
                                }
                            }
                            else if(KMdiff>13 && origin?.kmPoint!!>35){

                                if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                                    getkmdiff = KMdiff
                                    getExceedAmount = getkmdiff * exceedAmount
                                    discount= getExceedAmount *   discountamount
                                    amountafterdiscount= getExceedAmount - discount
                                    total = amountafterdiscount
                                    total = total * qty
                                }
                                else
                                {
                                    getExceedAmount= KMdiff!! * exceedAmount
                                    total = getExceedAmount
                                    total= total * qty
                                }
                            }
                        }
                    }
                }
            }catch (e:Exception){
                Toast(this).showCustomToast("ERROR on fare Computation: ${e.message}",this)
            }
        }


        if(GlobalVariable.lineid==7 && KMdiff>12 && passtype in listOf("Senior", "Student", "PWD") || GlobalVariable.lineid==18 && KMdiff>12 && passtype in listOf("Senior", "Student", "PWD")){
            val filteredList =dbFarebykm?.find { it.totalkm == KMdiff}
            total= filteredList?.discountrate?.toDouble()
        }

        val decimalVat = DecimalFormat("#.00")
        val decimalPart = total!! - floor(total)
        val roundedNumber = if (decimalPart >= 0.5) {
            kotlin.math.ceil(total).toInt()
        } else {
            kotlin.math.floor(total).toInt()
        }

       // val roundedFare = ceil(total!!).toInt()



        val ans = decimalVat.format(roundedNumber)
        amount = ans.toString()
        _binding.txtamount.text = amount
        _binding.txtkm.text="~${KMdiff}km"




        try {
            postTripticket=TicketConvertions.convertTripTickets(amount!!.toDouble(),GlobalVariable.conductor!!,destination?.name!!,GlobalVariable.driver!!,
                GlobalVariable.line!!,GlobalVariable.deviceName!!,origin?.name!!,passtype!!,ticketnumber.toString(),GlobalVariable.tripreverse!!,destination?.kmPoint!!,origin?.kmPoint!!,qty,GlobalVariable.ingressoRefId)

            //totalfare=postTripticket?.amount!!
            totalfare=amount
            qty=postTripticket?.qty


//            dbViewmodel.insertTripTicketBulk(postTripticket!!)
//            sdViewmodel.insertTripTicketBulk(postTripticket!!)
        }catch (e:java.lang.Exception){
            Log.e("errorsd",e.localizedMessage)
        }


        amount!!

    }

    private fun hideSoftKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }



    override fun onResume() {
        super.onResume()
        if(GlobalVariable.direction.equals("South")){
            _binding.btnMinusqty.setBackgroundColor(Color.GREEN)
            _binding.btnAddquantity.setBackgroundColor(Color.DKGRAY)
        }
        else{
            _binding.btnAddquantity.setBackgroundColor(Color.GREEN)
            _binding.btnMinusqty.setBackgroundColor(Color.DKGRAY)
        }
        dbViewmodel.getTripAmountPerReverse(GlobalVariable.tripreverse!!)
        dbViewmodel.getGross()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
        computeAmount()
        GlobalVariable.saveLogreport("ticketing resumed")

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }



    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)


        alertDialogBuilder.setTitle("ALERT!")
        alertDialogBuilder.setMessage("LOW BATTERY. PLS CHARGE")
        alertDialogBuilder.setPositiveButton("OK") { dialog, which ->

            dialog.dismiss()
        }

        // Create and show the dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    val clearAllSearchFields={
        _binding.etDestinationsearch.setText("")
        _binding.etOriginsearch.setText("")
        _binding.etDestinationsearch.clearFocus()
        _binding.etOriginsearch.clearFocus()
    }

    val checkhotspot:(Int,Int)-> Double? ={ from, to ->
        var hotspotamount:Double?= null
        var discount:Double?=0.0
        var amountafterdiscount:Double?= null
        val discountamounthotspot=20.0
        val filteredList =GlobalVariable.hotspot?.filter { it.pointfrom == from && it.pointto == to || it.pointfrom == to && it.pointto == from }
        if(filteredList?.size != 0){
            hotspotamount = filteredList?.firstOrNull()?.fare
            if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                discount=  (discountamounthotspot / 100) * hotspotamount!!
                amountafterdiscount= hotspotamount - discount
                hotspotamount= amountafterdiscount * qty
            }
        }

        hotspotamount
    }

    val checkhotspotPITX:(Int,Int)-> Double? ={ from, to ->
        var hotspotamount:Double?= null
        var discount:Double?=0.0
        var amountafterdiscount:Double?= null
        val discountamounthotspot=21.62
        val filteredList =GlobalVariable.hotspot?.filter { it.pointfrom == from && it.pointto == to || it.pointfrom == to && it.pointto == from }
        if(filteredList?.size != 0){
            hotspotamount = filteredList?.firstOrNull()?.fare
            if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                //discount=  (discountamounthotspot / 100) * hotspotamount!!
                if(from==30 && to==17 ||from==17 && to==30 ) hotspotamount = hotspotamount!! - 1
                if(from ==30&& to ==14 || from==14 && to==30)hotspotamount = hotspotamount!! - 1
                if(from ==30 && to == 3 || from==3 && to==30) hotspotamount = hotspotamount!! + 1
                else if(from==30 && to==15) hotspotamount!! -1
                else if(from==29 && to==16 || from== 16 && to==29){
                    hotspotamount = hotspotamount!!-1
                }
                else if(from==29 && to==14|| from== 14 && to==29) hotspotamount = hotspotamount!!-1
                else if(from==29 && to==13|| from== 13 && to==29) hotspotamount = hotspotamount!!-1
                else if(from==29 && to==2|| from== 2 && to==29)  hotspotamount = hotspotamount!!+1
                else if(from==28 && to==15|| from== 15 && to==28) hotspotamount = hotspotamount!!-1
                else if(from==28 && to==13|| from== 13 && to==28) hotspotamount = hotspotamount!!-1
                else if(from==28 && to==12|| from== 12 && to==28) hotspotamount = hotspotamount!!-1
                else if(from==28 && to==1|| from== 1 && to==28) hotspotamount = hotspotamount!!+1
                else if(from==27 && to==14|| from== 14 && to==27) hotspotamount = hotspotamount!!-1
                else if(from==27 && to==12|| from== 12 && to==27) hotspotamount = hotspotamount!!-1
                else if(from==26 && to==13|| from== 13 && to==26) hotspotamount = hotspotamount!!-1
                else if(from==26 && to==11|| from== 11 && to==26) hotspotamount = hotspotamount!!-1
                else if(from==25 && to==12|| from== 12 && to==25) hotspotamount = hotspotamount!!-1
                else if(from==25 && to==10|| from== 10 && to==25) hotspotamount = hotspotamount!!-1
                else if(from==24 && to==11|| from== 11 && to==24) hotspotamount = hotspotamount!!-1
                else if(from==24 && to==9|| from== 9 && to==24) hotspotamount = hotspotamount!!-1
                else if(from==23 && to==8|| from== 8 && to==23) hotspotamount = hotspotamount!!-1
                else if(from==23 && to==7|| from== 7 && to==23) hotspotamount = hotspotamount!!-1
                else if(from==22 && to==7|| from== 7 && to==22) hotspotamount = hotspotamount!!-1
                else if(from==22 && to==9|| from== 9 && to==22) hotspotamount = hotspotamount!!-1
                else if(from==21 && to==8|| from== 8 && to==21) hotspotamount = hotspotamount!!-1


                else if(from==27 && to==11 ||from==11 && to==27) hotspotamount = hotspotamount!!-1
                else if(from==27 && to==3||from==13 && to==27) hotspotamount = hotspotamount!!-1
                else if(from==27 && to==0||from==0 && to==27) hotspotamount = hotspotamount!! +1
                else if(from==26 && to==2||from==2 && to==26) hotspotamount = hotspotamount!!-1
                else if(from==25 && to==9||from==9 && to==25) hotspotamount = hotspotamount!!-1
                else if(from==25 && to==1||from==1 && to==25) hotspotamount = hotspotamount!!-1
                else if(from==24 && to==8||from==8 && to==24) hotspotamount = hotspotamount!!-1
                else if(from==24 && to==0||from==0 && to==24) hotspotamount = hotspotamount!!-1
                else if(from==20 && to==7||from==7 && to==20) hotspotamount = hotspotamount!!-1
                else if(from==19 && to==3||from==3 && to==19) hotspotamount = hotspotamount!!-1
                else if(from==18 && to==3||from==3 && to==18) hotspotamount = hotspotamount!!-1
                else if(from==18 && to==2||from==2 && to==18) hotspotamount = hotspotamount!!-1
                else if(from==17 && to==2||from==2 && to==17) hotspotamount = hotspotamount!!-1
                else if(from==17 && to==1||from==1 && to==17) hotspotamount = hotspotamount!!-1
                else if(from==16 && to==3||from==3 && to==16) hotspotamount = hotspotamount!!-1
                else if(from==16 && to==1||from==1 && to==16) hotspotamount = hotspotamount!!-1
                else if(from==16 && to==0||from==0 && to==16) hotspotamount = hotspotamount!!-1
                else if(from==15 && to==2||from==2 && to==15) hotspotamount = hotspotamount!!-1
                else if(from==15 && to==0||from==0 && to==15) hotspotamount = hotspotamount!!-1
                else if(from==14 && to==1||from==1 && to==14) hotspotamount = hotspotamount!!-1
                else if(from==13 && to==0||from==0 && to==13) hotspotamount = hotspotamount!!-1

                    discount=  GlobalVariable.discountAmount * hotspotamount!!
                    amountafterdiscount= hotspotamount - discount
                    hotspotamount= amountafterdiscount * qty


            }

        }

        hotspotamount
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
    //private val PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION"
    private val PRINTER_BUSY_ACTION = ""
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
                MSG_IS_BUSY -> Log.d("printing","PRINTING")
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@TIcketingActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@TIcketingActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@TIcketingActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@TIcketingActivity,
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
                    this@TIcketingActivity,
                    "COmpleted",
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


    fun printText() {
        ThreadPoolManager.getInstance().executeTask {
            //   val mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.test)
            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("TIN # 207 904 409 000\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Machine: ${GlobalVariable.machineName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Per: ${GlobalVariable.permitNumber}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Serial: ${GlobalVariable.serialNumber}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
               // mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Line: ${GlobalVariable.line}\n", "ST", 24,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Bus Number: ${GlobalVariable.bus}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "Date: ${formattedDateTime}\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printSpecifiedTypeText("Driver: ${GlobalVariable.driver}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24, callback)
               // mIPosPrinterService!!.printSpecifiedTypeText("Device: ${GlobalVariable.deviceName}\n", "ST", 24, callback)
              //  mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
               // mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("FARE: ${totalfare.toString()} ${pesoSign}\n", "ST", 24, 1,callback)

              //  mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

               // mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Ticket #: 000${GlobalVariable.ticketnumber}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Passenger Type: ${passtype}\n", "ST", 24,callback)
               // mIPosPrinterService!!.printSpecifiedTypeText("QTY: ${qty}\n", "ST", 24,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Distance KM: ${kmdiffprint}\n", "ST", 24,callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Fm: ${_binding.txtoriginKM.text.toString()}km~${_binding.etOrigin.text.toString()}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("To: ${_binding.txtDestination.text.toString()}km~${_binding.etDestination.text.toString()}\n", "ST", 24,  callback)
               // mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
//                mIPosPrinterService!!.printBlankLines(1, 8, callback)
//               mIPosPrinterService!!.setPrinterPrintAlignment(1, callback)
////                mIPosPrinterService!!.printQRCode("${_binding.etOrigin.text.toString()+ "-" +_binding.etDestination.text.toString() }\n", 6, 1, callback)
              //  mIPosPrinterService!!.printQRCode("000${GlobalVariable.ticketnumber .toString()+ "-" + _binding.txtoriginKM.text.toString()+ "-" +_binding.txtDestination.text.toString() }\n", 6, 1, callback)
////                mIPosPrinterService!!.printBarCode("${_binding.etOrigin.text.toString()} - ${_binding.etDestination.text.toString()}", 8, 2, 5, 0, callback)
//                mIPosPrinterService!!.printBlankLines(1, 8, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "********************************\n",
//                    "ST",
//                    24,
//                    callback
//                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Powered by mPAD\n", "ST", 24, 1,callback)


                mIPosPrinterService!!.printerPerformPrint(100, callback)
                runOnUiThread {
                   // ticketnumber +=1
                    passtype = "Regular"
                    _binding.txtqty.text = "1"
                    qty = 1
                    _binding.btnPrintticke.isEnabled=true
                    computeAmount()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            finally {
                runOnUiThread {
                    ticketnumber +=1
                    passtype = "Regular"
                    _binding.txtqty.text = "1"
                    qty = 1
                    _binding.btnPrintticke.isEnabled=true
                    externalViewModel.updateTicketnumber(GlobalVariable.ticketnumber,GlobalVariable.ingressoRefId,GlobalVariable.ticketnumid!!)
                    computeAmount()
                }
            }
        }
    }


        fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }


    //endregion


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(IPosPrinterStatusListener)
        unbindService(connectService)
        handler!!.removeCallbacksAndMessages(null)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    //region PRINTER
//    private val TAG: String? = "MainActivity"
//    var PRN_TEXT: String? = "THIS IS A TEsT PRINT"
//    var version = arrayOfNulls<String>(1)
//
//  //  private val singleThreadExecutor = Executors.newSingleThreadExecutor()
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
//        val singleThreadExecutor = Executors.newSingleThreadExecutor()
//        singleThreadExecutor.submit(Runnable {
//            try {
//                val ret = printerService!!.getPrinterVersion(version)
//                //showLog("Version: " + msg(ret) + "  " + version.get(0))
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//            finally {
//                singleThreadExecutor.shutdown()
//            }
//        })
//    }
//    fun getCurrentDateInFormat(): String {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val currentDate = Date()
//        return dateFormat.format(currentDate)
//    }
//
//    private fun printText(text: String) {
//        val singleThreadExecutor = Executors.newSingleThreadExecutor()
//        singleThreadExecutor.submit {
//            try {
//                val textFormat = PrintTextFormat()
//                textFormat.textSize=26
//                // textFormat.setUnderline(true);
//                textFormat.ali=1
//                textFormat.style=1
//                try {
//                    val date= getCurrentDateInFormat()
//                    var ret = printerService!!.printText(text, textFormat)
//                    textFormat.topPadding=5
//                    textFormat.textSize=22
//                    textFormat.style=0
//                    ret = printerService!!.printText("TIN # 207 904 409 000",textFormat)
//                    ret = printerService!!.printText("${date.toString()}",textFormat)
//                    textFormat.topPadding=20
//                    textFormat.textSize=22
//                    textFormat.ali=1
//                    ret = printerService!!.printText("Passenger type: ${passtype}",textFormat)
//                    //textFormat.ali=0
//                    textFormat.topPadding=0
//                    textFormat.style=1
//                    textFormat.textSize=26
//                    ret = printerService!!.printText("Fare: ${totalfare.toString()}${pesoSign}"   ,textFormat)
//                    textFormat.textSize=22
//                    textFormat.style=0
//                    textFormat.ali=1
//                    ret = printerService!!.printText("Qty: ${qty.toString()}",textFormat)
//                    textFormat.topPadding=20
//                    textFormat.style=0
//                    textFormat.ali=0
//                    textFormat.textSize=22
//                    ret = printerService!!.printText("Line: ${GlobalVariable.line}",textFormat)
//                    textFormat.topPadding=0
//                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
//                    ret = printerService!!.printText("Origin: ${_binding.etOrigin.text.toString()}",textFormat)
//                    ret = printerService!!.printText("Destination: ${_binding.etDestination.text.toString()}",textFormat)
////                    textFormat.topPadding=10
////                    ret = printerService!!.printBarcode("${_binding.etOrigin.text} - ${_binding.etDestination.text}",300,160,1,1 )
//                    textFormat.topPadding=10
//                    ret = printerService!!.printQrCode("${_binding.etOrigin.text} - ${_binding.etDestination.text}",300,160,1 )
//                    textFormat.topPadding=10
//                    textFormat.textSize=18
//                    textFormat.ali=1
//                    ret = printerService!!.printText("Powered by mPad Solutions",textFormat)
//
//
//                    if (ret == 0) {
//                        paperOut()
//                    }
//
//
//                }catch (e:java.lang.Exception){
//                    Log.e("tae",e.localizedMessage)
//                }
//                finally {
//                    singleThreadExecutor.shutdown()
//                    passtype="Regular"
//                    _binding.txtqty.text="1"
//                    qty=1
//                    computeAmount()
//                }
//
////                _binding.txtqty.text="1"
////                qty=1
////
////
////                if(passtype.equals("Student") || passtype.equals("Senior") || passtype.equals("PWD") || passtype.equals("Baggage")) {
////                    passtype="Regular"
////                }
////
////                computeAmount()
//
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//
//
//        }
//    }
//
//
//
//
//    private fun paperOut() {
//        val singleThreadExecutor = Executors.newSingleThreadExecutor()
//
//        singleThreadExecutor.submit {
//            try {
//                printerService!!.paperOut(80)
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//            finally {
//                singleThreadExecutor.shutdown()
//            }
//        }
//    }
//
   //endregion
}