package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.externalDispatch.TotalAmountAndTicketNumbersPerReverse
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.databinding.ActivitySharedLoginBinding
import com.example.erjohnandroid.databinding.ActivityTicketdetailsBinding
import com.example.erjohnandroid.presenter.adapter.TicketDetailsAdapter
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
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
class ticketdetailsActivity : AppCompatActivity() {
    lateinit var _binding: ActivityTicketdetailsBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var TicketDetailsAdapter: TicketDetailsAdapter
    var tripticketTable: List<TripTicketTable> = arrayListOf()

    var amount:Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTicketdetailsBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        computeWindowSizeClasses()
        val receivedData = intent.getIntExtra("key",0)
        dbViewmodel.getTripticketdetails(receivedData!!)

        initPrinter()

        _binding.btnprintticketdetails.setOnClickListener {
            GlobalVariable.saveLogreport("Ticket detials printed")
            printText()

        }
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.tripticketdetails.observe(this, Observer {
                state-> Processdetails(state)
        })
    }

    private fun computeWindowSizeClasses() {
        try {
            val layout: LinearLayout = findViewById(R.id.mainLinear)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            var width = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels
            val autoScreenSize= when{
                width > 900f -> {
                    val params: ViewGroup.LayoutParams = layout.layoutParams
                    params.width = 1900
                    layout.layoutParams = params
                }
                else -> {  val params: ViewGroup.LayoutParams = layout.layoutParams
                    params.width = 1900

                    layout.layoutParams = params}
            }
        }catch (e:java.lang.Exception){
            Log.d("ta",e.localizedMessage)
        }
    }

    private fun Processdetails(state: List<TripTicketTable>?){
        if(!state.isNullOrEmpty()){
            var total:Double=0.0
            tripticketTable= state

            tripticketTable.forEach {
                total += it.amount!!
            }
            amount=total

            TicketDetailsAdapter = TicketDetailsAdapter(this)
            _binding.rvTicketdetails.adapter= TicketDetailsAdapter
            _binding.rvTicketdetails.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            TicketDetailsAdapter.showdetails(state)
            dbViewmodel.getAllTripTicketForReverse()
            dbViewmodel.alltripticketforreverse.observe(this, Observer {
                state -> ProcessTripTicketReverse(state)
            })
        }
    }
    var ticketforReverse:ArrayList<TotalAmountAndTicketNumbersPerReverse> = arrayListOf()
    private fun ProcessTripTicketReverse(state: List<TotalAmountAndTicketNumbersPerReverse>?){
        if(!state.isNullOrEmpty()){
            ticketforReverse = arrayListOf()
            state.forEach {
                ticketforReverse.add(it)
            }

        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(IPosPrinterStatusListener)
        unbindService(connectService)
        handler!!.removeCallbacksAndMessages(null)
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
                    this@ticketdetailsActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@ticketdetailsActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@ticketdetailsActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@ticketdetailsActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@ticketdetailsActivity,
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
                    this@ticketdetailsActivity,
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

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun printText() {
        ThreadPoolManager.getInstance().executeTask {
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(amount)
            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Ticket Details - Reverse\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Reverse: ${GlobalVariable.inspectorname}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Driver: ${GlobalVariable.driver}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Trip #: ${GlobalVariable.tripreverse}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Bus #: ${GlobalVariable.bus}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.PrintSpecFormatText("Total Amount: ${ans}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.PrintSpecFormatText("DETAILS PER REVERSE: \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)

                ticketforReverse?.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("Reverse: ${it.tripReverse}\n", "ST", 24,  callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Total: ${it.totalAmount}\n", "ST", 24,  callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Start #: 000${it.firstTicketNumber}\n", "ST", 24,  callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("End #: 000${it.lastTicketNumber}\n", "ST", 24,  callback)
                    mIPosPrinterService!!.printSpecifiedTypeText("Ticket Count: ${it.ticketCount}\n", "ST", 24,  callback)
                    mIPosPrinterService!!.printBlankLines(1, 8, callback)
                }


                mIPosPrinterService!!.printerPerformPrint(100, callback)
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




    //region PRINTER
//    private val TAG: String? = "TicketDetails"
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
//        val decimalVat = DecimalFormat("#.00")
//        val ans = decimalVat.format(amount)
//        singleThreadExecutor.submit {
//            try {
//
//                val textFormat = PrintTextFormat()
//                textFormat.textSize=26
//                // textFormat.setUnderline(true);
//                textFormat.ali=1
//                textFormat.style=1
//                try {
//                    var ret = printerService!!.printText(text, textFormat)
//                    textFormat.textSize=24
//                    ret = printerService!!.printText("TICKET DETAILS",textFormat)
//                    ret = printerService!!.printText("Total Amount: ${ans}",textFormat)
//                    textFormat.style=0
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.ali=0
//                    textFormat.topPadding=10
//                    tripticketTable.forEach {
//                        ret = printerService!!.printText("${it.origin}" + " -- "+ "${it.destination}"   ,textFormat)
//                        ret = printerService!!.printText("${it.amount}"   ,textFormat)
//                        textFormat.topPadding=5
//                    }
////                    ret = printerService!!.printText("Line Dispatched: ${GlobalVariable.line}"   ,textFormat)
////                    ret = printerService!!.printText("Direction: ${GlobalVariable.direction}",textFormat)
////                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
////                    ret = printerService!!.printText("Bus Driver: ${GlobalVariable.driver}",textFormat)
////                    ret = printerService!!.printText("Bus Conductor: ${GlobalVariable.conductor}",textFormat)
////                    textFormat.topPadding=10
//                    ret = printerService!!.printText("",textFormat)
//                    amount=0.0
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

}