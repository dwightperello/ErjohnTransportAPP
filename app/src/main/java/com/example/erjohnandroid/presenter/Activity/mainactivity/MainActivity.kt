package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import android.os.*
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable

import com.example.erjohnandroid.util.startActivityWithAnimation
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
    val TRIP_REPORT_ACTIVITY=11


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
        initPrinter()

        checkifAlreadySynch()
        initiButtons()
        initibuttondisable()

        val sharedPrefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        GlobalVariable.ticketnumber = sharedPrefs.getInt("ticketnumber", 0)



        val window = window

// Hide the navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

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
//            startActivityWithAnimation<ReverseActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            showReverseDialog()
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
            showTripTicketDialog()

//            roomviewmodel.getTripticket()
//            roomviewmodel.tripticket.observe(this, Observer {
//                    state->ProcessTriptickets(state)
//            })
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

           // printText("Erjohn & Almark Transit Corp",ans)
            printText(ans)

        }
    }


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
//    private fun printText(text: String,amount:String) {
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
//                    ret = printerService!!.printText("TRIP REPORT",textFormat)
//                    ret = printerService!!.printText("Line:  ${GlobalVariable.line}",textFormat)
//                    ret = printerService!!.printText("Bus #:  ${GlobalVariable.bus}",textFormat)
//                    ret = printerService!!.printText("mPad #:  ${GlobalVariable.deviceName}",textFormat)
//                    ret = printerService!!.printText("Dispatcher:  ${GlobalVariable.employeeName}",textFormat)
//                    ret = printerService!!.printText("Driver:  ${GlobalVariable.driver}",textFormat)
//                    ret = printerService!!.printText("Conductor:  ${GlobalVariable.conductor}",textFormat)
 //                   ret = printerService!!.printText("TOTAL:  ${amount}",textFormat)
//                    ret = printerService!!.printText("Date:  ${formattedDate}",textFormat)
//                    textFormat.style=0
//                    textFormat.ali=1
//                    ret = printerService!!.printText("------------------------------",textFormat)
//
//                    textFormat.ali=0
//
//
//                    alltickets.forEach {
//                        ret = printerService!!.printText("Segment: ${it.origin}"+"--"+"${it.destination}",textFormat)
//                        ret = printerService!!.printText("Amount: ${it.amount}"+"--"+"${it.passengerType}",textFormat)
//                    }
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

  fun showTripTicketDialog(){
      val builder: AlertDialog.Builder = AlertDialog.Builder(this)
      builder.setTitle("Enter Pin")

// Set up the input
      val input = EditText(this)
      input.inputType = InputType.TYPE_CLASS_NUMBER
      input.gravity = Gravity.CENTER
      builder.setView(input)

// Set up the buttons
      builder.setPositiveButton("OK") { dialog, which ->
          val text = input.text.toString()
          if(text=="99999"){
              roomviewmodel.getTripticket()
              roomviewmodel.tripticket.observe(this, Observer {
                      state->ProcessTriptickets(state)
              })
          }else{
              Toast.makeText(this,"PLEASE ENTER CORRECT PIN",Toast.LENGTH_SHORT).show()
          }
      }

      builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

      builder.show()

  }

    fun showReverseDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Pin")

// Set up the input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.gravity = Gravity.CENTER
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            val text = input.text.toString()
            if(text=="88888"){
                startActivityWithAnimation<ReverseActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            }else{
                Toast.makeText(this,"PLEASE ENTER CORRECT PIN",Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

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
                    this@MainActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@MainActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@MainActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@MainActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@MainActivity,
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
                    this@MainActivity,
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

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun printText(amount:String) {
        ThreadPoolManager.getInstance().executeTask {

            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Trip Report\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Line: ${GlobalVariable.line}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Bus #: ${GlobalVariable.bus}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("mPAD: ${GlobalVariable.deviceName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Dispatcher: ${GlobalVariable.employeeName}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Driver: ${GlobalVariable.driver}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Total Amount: ${amount}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

                alltickets.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("Segment: ${it.origin} - ${it.destination}\n", "ST", 24, callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Amount: ${it.amount}\n", "ST", 24, callback)
                }


                mIPosPrinterService!!.printerPerformPrint(160, callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }



    fun bitmapToInputStream(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray().inputStream()
    }

    //endregion



}