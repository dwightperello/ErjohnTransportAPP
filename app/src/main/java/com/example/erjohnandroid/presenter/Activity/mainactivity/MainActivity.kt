package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.sd_viewmodel
import com.example.erjohnandroid.databinding.ActivityMainBinding
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable

import com.example.erjohnandroid.util.startActivityWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding:ActivityMainBinding?= null
    private val networkViewModel:networkViewModel by viewModels()
    private val roomviewmodel: RoomViewModel by viewModels()
    private val sdViewmodel:sd_viewmodel by viewModels()

    var alltickets:kotlin.collections.List<TripTicketTable> = arrayListOf()

    val DISPATCH_ACTIVITY = 1
    val INSPECTION_ACTIVITY=2
    val SYNCHING_ACTIVITY=3
    val INGRESSO_ACTIVITY=4
    val INSPECTION=9
    val PARTIAL_ACTIVITY=5
    val SETTINGS_ACTIVITY=10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //IMPLEMENT WHEN DEPLOYED
//        val sharedPreferences = this.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
//        GlobalVariable.API_BASE_URL  = sharedPreferences.getString("URL", "default_value").toString()
        bindService()
        checkifAlreadySynch()
        initiButtons()
        initibuttondisable()

        val sharedPrefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        GlobalVariable.ticketnumber = sharedPrefs.getInt("ticketnumber", 0)





    }


    val initibuttondisable={
        _binding?.btnDispatch?.isVisible=true
        _binding?.btnTicketing?.isEnabled=false
        _binding?.btnIngresso?.isEnabled=false
        _binding?.btnInspection?.isEnabled=false
        _binding?.btnPartialremit?.isEnabled=false
        _binding?.btnReverse?.isEnabled=false
        _binding?.btnSettings?.isEnabled=false
        _binding?.btnSynch?.isEnabled=true
        _binding?.btnTripreport?.isEnabled=false
    }
    val enablebutton={
        _binding?.btnDispatch?.isVisible=false
        _binding?.txtdispatch?.isVisible=false
        _binding?.btnTicketing?.isEnabled=true
        _binding?.btnIngresso?.isEnabled=true
        _binding?.btnInspection?.isEnabled=true
        _binding?.btnPartialremit?.isEnabled=true
        _binding?.btnReverse?.isEnabled=true
        _binding?.btnSettings?.isEnabled=true
        _binding?.btnSynch?.isEnabled=true
        _binding?.btnTripreport?.isEnabled=true
    }

    val checkifAlreadySynch ={
        roomviewmodel.getAllLines()
       // sdViewmodel.getTripticket()
    }

    override fun onStart() {
        super.onStart()
        roomviewmodel.allLines.observe(this, Observer {
            state ->  ProcessAllLinesResponse(state)
        })
        sdViewmodel.sdtripticket.observe(this, Observer {
            state ->
            var items=state
        })


    }

    private fun ProcessAllLinesResponse(state: List<LinesTable>){
      if( !state.isNullOrEmpty()){
            Log.d("ays","ASYA")
      }else{
          startActivityWithAnimation<LoginActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            finish()
      }
    }

    val initiButtons={
        _binding!!.btnDispatch.setOnClickListener {
            //startActivityWithAnimation<SharedLoginActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            val intent = Intent(this, SharedLoginActivity::class.java)
            startActivityForResult(intent,DISPATCH_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding!!.btnTicketing.setOnClickListener {
            startActivityWithAnimation<TIcketingActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnInspection.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity",INSPECTION)
            startActivityForResult(intent,INSPECTION_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
            //startActivityWithAnimation<InspectionActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnIngresso.setOnClickListener {
           showSimpleDialog(this,"INGRESSO ALERT!", " ARE YOU SURE YOU WAN TO PROCEED TO INGRESSO?, YOU WONT BE ABLE TO CONTINUE TICKETING")
        }

        _binding!!.btnPartialremit.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", PARTIAL_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }

        _binding!!.btnReverse.setOnClickListener {
            startActivityWithAnimation<ReverseActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnSynch.setOnClickListener {
            startActivityWithAnimation<SycnhLogin>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnSettings.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", SETTINGS_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }

        _binding!!.btnTripreport.setOnClickListener {
            roomviewmodel.getTripticket()
            roomviewmodel.tripticket.observe(this, Observer {
                    state->ProcessTriptickets(state)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DISPATCH_ACTIVITY) {
            if(GlobalVariable.isDispatched) enablebutton()
//            if (resultCode == Activity.RESULT_OK) {
//
//            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(GlobalVariable.isDispatched){
            enablebutton()
        }else{
            initibuttondisable()
        }


    }

    val ProcessTriptickets:(state: List<TripTicketTable>) ->Unit={
        var amounttic:Double=0.0

        if(it!=null) {
            alltickets=it

            alltickets.forEach {
                amounttic+=it.amount!!
            }
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(amounttic)

            printText("Erjohn & Almark Transit Corp",ans)

        }
    }


    //region PRINTER
    private val TAG: String? = "MainActivity"
    var PRN_TEXT: String? = "THIS IS A TEsT PRINT"
    var version = arrayOfNulls<String>(1)

    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
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
        singleThreadExecutor.submit(Runnable {
            try {
                val ret = printerService!!.getPrinterVersion(version)
                //showLog("Version: " + msg(ret) + "  " + version.get(0))
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        })
    }

    private fun printText(text: String,amount:String) {

        singleThreadExecutor.submit {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val currentDate = Date()
                val formattedDate = dateFormat.format(currentDate)


                val textFormat = PrintTextFormat()
                textFormat.textSize=26
                // textFormat.setUnderline(true);
                textFormat.ali=1
                textFormat.style=1
                try {
                    var ret = printerService!!.printText(text, textFormat)
                    textFormat.textSize=24
                    ret = printerService!!.printText("TRIP REPORT",textFormat)
                    ret = printerService!!.printText("Line:  ${GlobalVariable.line}",textFormat)
                    ret = printerService!!.printText("Bus #:  ${GlobalVariable.bus}",textFormat)
                    ret = printerService!!.printText("mPad #:  ${GlobalVariable.deviceName}",textFormat)
                    ret = printerService!!.printText("Dispatcher:  ${GlobalVariable.employeeName}",textFormat)
                    ret = printerService!!.printText("Driver:  ${GlobalVariable.driver}",textFormat)
                    ret = printerService!!.printText("Conductor:  ${GlobalVariable.conductor}",textFormat)
                    ret = printerService!!.printText("TOTAL:  ${amount}",textFormat)
                    ret = printerService!!.printText("Date:  ${formattedDate}",textFormat)
                    textFormat.style=0
                    textFormat.ali=1
                    ret = printerService!!.printText("------------------------------",textFormat)

                    textFormat.ali=0


                    alltickets.forEach {
                        ret = printerService!!.printText("Segment: ${it.origin}"+"--"+"${it.destination}",textFormat)
                        ret = printerService!!.printText("Amount: ${it.amount}"+"--"+"${it.passengerType}",textFormat)
                    }


                    ret = printerService!!.printText("",textFormat)
                    ret = printerService!!.printText("",textFormat)

                    if (ret == 0) {
                        paperOut()
                    }

                }catch (e:java.lang.Exception){
                    Log.e("tae",e.localizedMessage)
                }

                // showLog("Print text: " + msg(ret))

            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }




    private fun paperOut() {
        singleThreadExecutor.submit {
            try {
                printerService!!.paperOut(80)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    //endregion

    override fun onBackPressed() {

    }

    fun showSimpleDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(title)

        // Set the alert dialog message
        builder.setMessage(message)

        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK") { dialog, which ->
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", INGRESSO_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            dialog.dismiss()
        }

        builder.setNegativeButton("CANCEL") { dialog, which ->
            // Do something when the negative button is clicked
            dialog.dismiss()
        }

        // Create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



//    override fun onPause() {
//        super.onPause()
//        val activityManager = applicationContext
//            .getSystemService(ACTIVITY_SERVICE) as ActivityManager
//        activityManager.moveTaskToFront(taskId, 0)
//    }


}