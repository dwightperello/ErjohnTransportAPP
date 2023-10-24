package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.InspectionReportTable
import com.example.erjohnandroid.database.Model.PartialRemitTable
import com.example.erjohnandroid.database.Model.convertions.TripTicketGroupCount
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityPartialRemitBinding
import com.example.erjohnandroid.databinding.ActivityReverseBinding
import com.example.erjohnandroid.presenter.adapter.ReverseAdapter
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
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
import java.util.function.DoubleUnaryOperator

@AndroidEntryPoint
class PartialRemitActivity : AppCompatActivity() {
    lateinit var _binding: ActivityPartialRemitBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var reverseAdapter: ReverseAdapter
    var image:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityPartialRemitBinding.inflate(layoutInflater)
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

        dbViewmodel.getReverse()
       // bindService()
        initPrinter()
        _binding.btnSaveremit.setOnClickListener {

            val signatureBitmap = _binding.inspectionsignature.isSignaturePresent()
             val text= _binding.etCashremited.text.toString()
            val total=_binding.txttotalcash.text.toString()
            val formattedDateTime = getdate()
            if(!signatureBitmap){
                Toast(this).showCustomToast("Please sign",this)
                return@setOnClickListener
            }
            image=   _binding.inspectionsignature.drawToBitmap()
            if(text.isNullOrEmpty() || total.isNullOrEmpty()) {
                Toast(this).showCustomToast("Enter amount", this)
                return@setOnClickListener
            }
            val stringWithoutSpaces = text.replace(" ", "")
            val stringcount = total.replace(" ", "")

            var method =  PartialRemitTable(
                PartialremitId = 0,
                CashierName = GlobalVariable.cashiername,
                Amount = stringcount.toDouble(),
                AmountRemited = stringWithoutSpaces.toDouble(),
                Line = GlobalVariable.line,
                DateTimeStamp = formattedDateTime,
                ingressoRefId = GlobalVariable.ingressoRefId
            )
            try {
                dbViewmodel.insertPartialremit(method)
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

           // printText("Erjohn & Almark Transit Corp ")
            printText()
//            finish()
//            overridePendingTransition(
//                R.anim.screenslideleft, R.anim.screen_slide_out_right,
//            );
        }

        _binding.btnclose.setOnClickListener {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.tripticketjson.observe(this, Observer {
                state-> ProcesJson(state)
        })


    }

    private fun ProcesJson(state: List<TripTicketGroupCount>?){
        var amount :Double=0.0
        if(!state.isNullOrEmpty()){
//            val gson = Gson()
//            val jsonResult = gson.toJson(state)


            reverseAdapter = ReverseAdapter(this)
            _binding.rvReverse.adapter= reverseAdapter
            _binding.rvReverse.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            reverseAdapter.showreverse(state)

            state.forEach {
              amount +=  it.sumamount
            }
            val decimalVat = DecimalFormat("#.00")
            val ans = decimalVat.format(amount)
            _binding.txttotalcash.text=ans.toString()
        }
    }

    fun showmodaltickets(role: TripTicketGroupCount) {
        val intent = Intent(this, ticketdetailsActivity::class.java)
        intent.putExtra("key", role.tripReverse)
        startActivity(intent)
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }


    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
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
                    this@PartialRemitActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@PartialRemitActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@PartialRemitActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@PartialRemitActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@PartialRemitActivity,
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
                    this@PartialRemitActivity,
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
        runOnUiThread {

        }

        ThreadPoolManager.getInstance().executeTask {
            val mBitmap: Bitmap = BitmapFactory.decodeStream(bitmapToInputStream(image!!))
            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Partial Remit\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Cashier Name: ${GlobalVariable.cashiername}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.PrintSpecFormatText("Amount Remited ${_binding.etCashremited.text}\n", "ST", 24, 1,callback)

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )

               mIPosPrinterService!!.printBlankLines(1, 8, callback)
               mIPosPrinterService!!.printBitmap(0, 4,mBitmap , callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)


                mIPosPrinterService!!.printerPerformPrint(160, callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

//    fun loopPrint(flag: Int) {
//        when (flag) {
//            MULTI_THREAD_LOOP_PRINT -> multiThreadLoopPrint()
//            DEMO_LOOP_PRINT -> demoLoopPrint()
//            INPUT_CONTENT_LOOP_PRINT -> bigDataPrintTest(127, loopContent)
//            PRINT_DRIVER_ERROR_TEST -> printDriverTest()
//            else -> {}
//        }
//    }

    fun bitmapToInputStream(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray().inputStream()
    }

    //endregion


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
//        singleThreadExecutor.submit {
//            try {
//                val textFormat = PrintTextFormat()
//                textFormat.textSize=26
//                // textFormat.setUnderline(true);
//                textFormat.ali=1
//                textFormat.style=1
//                try {
//                    var ret = printerService!!.printText(text, textFormat)
//                    textFormat.textSize=24
//                    ret = printerService!!.printText("PARTIAL REMIT",textFormat)
//                    ret = printerService!!.printText("Cashier: ${GlobalVariable.cashiername}",textFormat)
//                    ret = printerService!!.printText("Amount Remited: ${_binding.etCashremited.text}",textFormat)
//                    textFormat.style=0
//                    ret = printerService!!.printText("------------------------------",textFormat)
////                    textFormat.textSize=22
////                    textFormat.ali=0
////                    textFormat.style=0
////                    textFormat.topPadding=15
////                    ret = printerService!!.printText("Km Check: ${_binding.etInspectiondestination.text.toString()}",textFormat)
////                    textFormat.topPadding=0
////
////
////                    ret = printerService!!.printText("Count: ${_binding.etActualcount.text}",textFormat)
////                    ret = printerService!!.printText("Diff: ${_binding.txtinspectiondifference.text.toString()}",textFormat)
//
//                    ret = printerService!!.printBitmap(
 //                       BitmapFactory.decodeStream(bitmapToInputStream(image!!)
//
//                        ), 1, 1
//                    )
//
//                    if (ret == 0) {
//                        paperOut()
//                    }
//
//
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
////    fun printBitmap(bitmap: Bitmap, scaleWidth: Int, scaleHeight: Int) {
////        singleThreadExecutor.submit {
////            try {
////                val ret = printerService!!.printBitmap(bitmap, scaleWidth, scaleHeight)
////               // showLog("Print bitmap: " + msg(ret))
////                if (ret == 0) {
////                    paperOut()
////                }
////            } catch (e: Exception) {
////                e.printStackTrace()
////            }
////        }
////    }
//
//    private fun printBitmap(bitmap: Bitmap) {
//        singleThreadExecutor.submit {
//            try {
//                val ret = printerService!!.printBitmap(
//                    BitmapFactory.decodeStream(bitmapToInputStream(bitmap)
//
//                    ), 1, 1
//                )
//                // showLog("Print bitmap: " + msg(ret))
//
//
//                if (ret == 0) {
//                    paperOut()
//                }
//
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//    fun bitmapToInputStream(bitmap: Bitmap): InputStream {
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//        return outputStream.toByteArray().inputStream()
//    }
//
//
  //endregion
}