package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.PassengerTypeTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.convertions.TicketConvertions
import com.example.erjohnandroid.database.Model.convertions.TripAmountPerReverse
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.sd_viewmodel
import com.example.erjohnandroid.databinding.ActivityTicketingBinding
import com.example.erjohnandroid.presenter.adapter.PassengerTypeAdapter
import com.example.erjohnandroid.util.BatteryReceiver
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.GlobalVariable.destinationcounter
import com.example.erjohnandroid.util.GlobalVariable.linesegment
import com.example.erjohnandroid.util.GlobalVariable.origincounter
import com.example.erjohnandroid.util.GlobalVariable.ticketcounter
import com.example.erjohnandroid.util.GlobalVariable.ticketnumber

import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


@AndroidEntryPoint
class TIcketingActivity : AppCompatActivity() {
    lateinit var _binding:ActivityTicketingBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private val sdViewmodel:sd_viewmodel by viewModels()

    private var batteryReceiver: BatteryReceiver? = null

    //private var linesegment:List<LineSegmentTable>?= null

    //private  lateinit var passengerTypeAdapter: PassengerTypeAdapter

    private var passtype:String?=null

    private var origin:LineSegmentTable?= null
    private var destination:LineSegmentTable?= null

    var postTripticket:TripTicketTable?= null

    private var qty:Int=1
    val fare:Double=15.00
    var discountamount:Double=0.20
    val pesoSign = '\u20B1'
    var totalfare:Double=0.0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTicketingBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        batteryReceiver = BatteryReceiver { batteryLevel ->
           if(batteryLevel<5) showAlertDialog()

            _binding.txtbat.text = "BAT: $batteryLevel%"
        }

        if(linesegment.isNullOrEmpty()) {
           // dbViewmodel.getLinesegment(GlobalVariable.lineid!!)
        }
       // dbViewmodel.getPassengerType()
        dbViewmodel.getLinesegment(GlobalVariable.lineid!!)
        initView()
       // passengerTypeAdapter = PassengerTypeAdapter(this)

        initCheckbox()

        initBUttons()

        _binding.txtpesosign.text= pesoSign.toString()

        bindService()



    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.linesegment.observe(this, Observer {
                state->processLine(state)

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

            var o= _binding.txtoriginKM.text.toString()
            var d = _binding.txtDestination.text.toString()


            if(destinationcounter == origincounter || _binding.txtamount.text.toString().equals("0.0") || _binding.txtDestination.text.toString()==_binding.txtoriginKM.text.toString()){
                Toast(this).showCustomToast("AMOUNT IS 0, SELECT DESTINATION",this)
                _binding.txtamount.text ="0.0"
                return@setOnClickListener
            }

            if(GlobalVariable.direction.equals("South")){

                if(o.toInt()>= d.toInt() ){
                    Toast(this).showCustomToast("SOUTH BOUND, PLEASE CHECK KM",this)
                    _binding.txtamount.text ="0.0"
                    return@setOnClickListener
                }
            }else if(GlobalVariable.direction.equals("North"))
            {
//                var s= _binding.txtoriginKM.text.toString()
//                var d = _binding.txtDestination.text.toString()
                if(d.toInt()>= o.toInt() ){
                    Toast(this).showCustomToast("North BOUND, PLEASE CHECK KM",this)
                    _binding.txtamount.text ="0.0"
                    return@setOnClickListener
                }
            }

            if(!passtype.isNullOrEmpty()) {
                ticketnumber +=1
                ticketcounter +=1
                _binding.txtticketcount.text="Next ticket: ${ticketcounter.toString()}"
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

            printText("Erjohn & Almark Transit Corp")

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
//        var postTripticket:ArrayList<TripTicketTable>?= arrayListOf()
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

      try {
          if(passtype.equals("Baggage")){
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
              if(GlobalVariable.direction.equals("South")) KMdiff= destination?.kmPoint!! - origin?.kmPoint!!
              else KMdiff= origin?.kmPoint!! - destination?.kmPoint!!

              if(KMdiff > 5){

                  if(passtype.equals("Senior") || passtype.equals("Student")|| passtype.equals("PWD")) {
                      discount= fare.toDouble() *   discountamount
                      amountafterdiscount= fare - discount
                      getkmdiff = KMdiff - 5
                      getExceedAmount = getkmdiff * 2.12
                      total = getExceedAmount + amountafterdiscount
                      total = total * qty
                  }else{
                      getkmdiff = KMdiff -5
                      getExceedAmount= getkmdiff * 2.65
                      total = getExceedAmount + fare
                      total= total * qty
                  }

              }
              else{
                  if(passtype.equals("Senior") || passtype.equals("Student") || passtype.equals("PWD")){
                      discount= fare *  discountamount
                      amountafterdiscount= fare - discount
                      total= amountafterdiscount * qty
                  }else{
                      total= fare.toDouble() * qty
                  }

              }
//        if(_binding.cbBaggage.isChecked){
//            var bag= _binding.etbaggaeamount.text.toString()
//            if(bag.isNullOrEmpty()) bag="0.0"
//            baggageamount += bag.toDouble()
//            total += baggageamount
//        }


          }
      }catch (e:Exception){
          Toast(this).showCustomToast("ERROR on fare Computation: ${e.message}",this)
      }

        val decimalVat = DecimalFormat("#.00")
        val ans = decimalVat.format(total)
        amount= ans.toString()
        _binding.txtamount.text=amount



        try {
            postTripticket=TicketConvertions.convertTripTickets(amount!!.toDouble(),GlobalVariable.conductor!!,destination?.name!!,GlobalVariable.driver!!,
                GlobalVariable.line!!,GlobalVariable.deviceName!!,origin?.name!!,passtype!!,ticketnumber.toString(),GlobalVariable.tripreverse!!,destination?.kmPoint!!,origin?.kmPoint!!,qty)

            totalfare=postTripticket?.amount!!
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



//    override fun onResume() {
//        super.onResume()
////        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
////        destinationcounter = preferences.getInt("destinationcounter", 1)
////        origincounter= preferences.getInt("origincounter",0)
////        ticketnumber= preferences.getInt("ticketnumber",0)
//
//        // Your existing code to set destination and update UI
//        if(!linesegment.isNullOrEmpty()) {
////            if (GlobalVariable.direction.equals("South")) {
////
////               // destinationcounter += 1
////            } else {
////                linesegment = linesegment?.reversed()
////               // destinationcounter += 1
////            }
//
//            destination = linesegment?.get(destinationcounter)
//            _binding.txtDestination.text = destination?.kmPoint.toString()
//            _binding.etDestination.setText(destination?.name)
//
//            origin = linesegment?.get(origincounter)
//            _binding.txtoriginKM.text = origin?.kmPoint.toString()
//            _binding.etOrigin.setText(origin?.name)
//
//            if (GlobalVariable.remainingPass == null) GlobalVariable.remainingPass = 0
//            _binding.txtrem.text = "Passenger: ${GlobalVariable.remainingPass.toString()}"
//            if (GlobalVariable.ticketcounter == null) GlobalVariable.ticketcounter = 0
//            _binding.txtticketcount.text = "Next ticket: ${ticketcounter.toString()}"
//            computeAmount()
//        }
//
//
//
//    }

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

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }


//    private fun ProcessPassType(state: List<PassengerTypeTable>?){
//        if(!state.isNullOrEmpty()){
//            _binding.rvPassTYpe.adapter= passengerTypeAdapter
//            _binding.rvPassTYpe.layoutManager= GridLayoutManager(this, 3)
//            passengerTypeAdapter.showRoles(state!!)
//        }
//    }


//    fun getPasstype(role: PassengerTypeTable) {
//        passengerTypeAdapter.notifyItemChanged(0)
//        passtype=role.name!!
//        fare= role.tag!!
//        discountamount=role.discount!!
//    }

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


    //region PRINTER
    private val TAG: String? = "MainActivity"
    var PRN_TEXT: String? = "THIS IS A TEsT PRINT"
    var version = arrayOfNulls<String>(1)

  //  private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    private val handler = Handler()

    private fun bindService() {
        try {
            val intent = Intent()
            intent.setPackage("net.nyx.printerservice")
            intent.action = "net.nyx.printerservice.IPrinterService"
            bindService(intent, connService, BIND_AUTO_CREATE)
        }catch (e:Exception){
            Log.e("ere",e.localizedMessage)
        }

    }

    private var printerService: IPrinterService? = null

    private val connService = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            //  showLog("printer service disconnected, try reconnect")
            printerService = null
            // 尝试重新bind
            handler.postDelayed({ bindService() }, 5000)
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("onServiceConnected: $name")
            printerService = IPrinterService.Stub.asInterface(service)
            getVersion()
        }
    }


    private fun getVersion() {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor.submit(Runnable {
            try {
                val ret = printerService!!.getPrinterVersion(version)
                //showLog("Version: " + msg(ret) + "  " + version.get(0))
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            finally {
                singleThreadExecutor.shutdown()
            }
        })
    }
    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun printText(text: String) {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor.submit {
            try {
                val textFormat = PrintTextFormat()
                textFormat.textSize=26
                // textFormat.setUnderline(true);
                textFormat.ali=1
                textFormat.style=1
                try {
                    val date= getCurrentDateInFormat()
                    var ret = printerService!!.printText(text, textFormat)
                    textFormat.topPadding=5
                    textFormat.textSize=22
                    textFormat.style=0
                    ret = printerService!!.printText("TIN # 207 904 409 000",textFormat)
                    ret = printerService!!.printText("${date.toString()}",textFormat)
                    textFormat.topPadding=20
                    textFormat.textSize=22
                    textFormat.ali=1
                    ret = printerService!!.printText("Passenger type: ${passtype}",textFormat)
                    //textFormat.ali=0
                    textFormat.topPadding=0
                    textFormat.style=1
                    textFormat.textSize=26
                    ret = printerService!!.printText("Fare: ${totalfare.toString()}${pesoSign}"   ,textFormat)
                    textFormat.textSize=22
                    textFormat.style=0
                    textFormat.ali=1
                    ret = printerService!!.printText("Qty: ${qty.toString()}",textFormat)
                    textFormat.topPadding=20
                    textFormat.style=0
                    textFormat.ali=0
                    textFormat.textSize=22
                    ret = printerService!!.printText("Line: ${GlobalVariable.line}",textFormat)
                    textFormat.topPadding=0
                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
                    ret = printerService!!.printText("Origin: ${_binding.etOrigin.text.toString()}",textFormat)
                    ret = printerService!!.printText("Destination: ${_binding.etDestination.text.toString()}",textFormat)
//                    textFormat.topPadding=10
//                    ret = printerService!!.printBarcode("${_binding.etOrigin.text} - ${_binding.etDestination.text}",300,160,1,1 )
                    textFormat.topPadding=10
                    ret = printerService!!.printQrCode("${_binding.etOrigin.text} - ${_binding.etDestination.text}",300,160,1 )
                    textFormat.topPadding=10
                    textFormat.textSize=18
                    textFormat.ali=1
                    ret = printerService!!.printText("Powered by mPad Solutions",textFormat)


                    if (ret == 0) {
                        paperOut()
                    }


                }catch (e:java.lang.Exception){
                    Log.e("tae",e.localizedMessage)
                }
                finally {
                    singleThreadExecutor.shutdown()
                    passtype="Regular"
                    _binding.txtqty.text="1"
                    qty=1
                    computeAmount()
                }

//                _binding.txtqty.text="1"
//                qty=1
//
//
//                if(passtype.equals("Student") || passtype.equals("Senior") || passtype.equals("PWD") || passtype.equals("Baggage")) {
//                    passtype="Regular"
//                }
//
//                computeAmount()

            } catch (e: RemoteException) {
                e.printStackTrace()
            }


        }
    }




    private fun paperOut() {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()

        singleThreadExecutor.submit {
            try {
                printerService!!.paperOut(80)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            finally {
                singleThreadExecutor.shutdown()
            }
        }
    }

    //endregion
}