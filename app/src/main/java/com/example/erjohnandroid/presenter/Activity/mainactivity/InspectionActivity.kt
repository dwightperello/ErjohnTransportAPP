package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.InspectionReportTable
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityInspectionBinding
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.SignatureView
import com.example.erjohnandroid.util.showCustomToast
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@AndroidEntryPoint
class InspectionActivity : AppCompatActivity() {
    lateinit var _binding:ActivityInspectionBinding
    private var origin: LineSegmentTable?= null
    private val dbViewmodel: RoomViewModel by viewModels()
    var countint:Int?=null
    var remint:Int?=null
    var ans:Int?=null
    var image:Bitmap?=null

    var tripticket:kotlin.collections.List< TripTicketTable>?= null

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityInspectionBinding.inflate(layoutInflater)
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


        initPrinter()
        if(GlobalVariable.linesegment.isNullOrEmpty()){
            Toast(this).showCustomToast("NO TICKET YET",this)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()
        }else
        {
            if(GlobalVariable.direction.equals("South")) {
                GlobalVariable.linesegment
                origin= GlobalVariable.linesegment?.get(1)
                _binding.txtinspectionorigin.text= "Default KM ${origin?.kmPoint.toString()}"
            }
            else{
                GlobalVariable.linesegment?.reversed()
                origin= GlobalVariable.linesegment?.get(1)
                _binding.txtinspectionorigin.text="Default KM ${origin?.kmPoint.toString()}"
            }
        }

        _binding.btnInspectiondestinationsearch.setOnClickListener {
            var originss= _binding.etInspectiondestination.text.toString()
            if(!TextUtils.isDigitsOnly(originss) || originss.isNullOrEmpty()) return@setOnClickListener
            var s_origin=  GlobalVariable.linesegment?.find {

                it.kmPoint== originss.toInt()
            }

            val index=  if(s_origin!= null){
                GlobalVariable.linesegment?.indexOf(s_origin)!!
            } else {
                -1

            }
            when(index){
                -1 ->{
                   // Toast.makeText(this,"NO LINESEGMENT FOUND", Toast.LENGTH_LONG).show()
                    Toast(this).showCustomToast("NO LINESEGMENT FOUND",this)
                    return@setOnClickListener
                }
            }


            origin= GlobalVariable.linesegment?.get(index)
            _binding.txtinspectionorigin.text= origin?.kmPoint.toString()

            if(GlobalVariable.direction.equals("South")){
                dbViewmodel.getRemSouth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
                dbViewmodel.getTripTicketafterInspection(originss.toInt(),GlobalVariable.tripreverse!!)
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
                dbViewmodel.getTripTicketafterInspectionNorth(originss.toInt(),GlobalVariable.tripreverse!!)
            }


            hideSoftKeyboard()
        }

        _binding.btnClear.setOnClickListener {
          _binding.inspectionsignature.clear()
        }


        _binding.btnclose.setOnClickListener {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()
        }


        // To clear the signature
       //

        _binding.btngetdiff.setOnClickListener {

            var rem = _binding.txtrem.text.toString()
            var count= _binding.etActualcount.text.toString()
            if(rem.isNullOrEmpty() || count.isNullOrEmpty()){
                Toast(this).showCustomToast("NO INPUTS",this)
                return@setOnClickListener
            }
            val stringWithoutSpaces = rem.replace(" ", "")
            val stringcount = count.replace(" ", "")
            remint= stringWithoutSpaces.toInt()
            countint=stringcount.toInt()
          //  ans= countint!!.minus(remint!!)
            ans=remint?.minus(countint!!)
            _binding.txtinspectiondifference.text=ans.toString()
            hideSoftKeyboard()
        }

        _binding.btninspectionsave.setOnClickListener {

            val signatureBitmap = _binding.inspectionsignature.isSignaturePresent()
            val formattedDateTime = getdate()

        image=   _binding.inspectionsignature.drawToBitmap()

            if(!signatureBitmap){
                Toast(this).showCustomToast("Please sign",this)
                return@setOnClickListener
            }
            if(ans==null){
                Toast(this).showCustomToast("Please compute difference",this)
                return@setOnClickListener
            }

            var method =  InspectionReportTable(
                actualPassengerCount = countint,
                dateTimeStamp = formattedDateTime,
                difference = ans,
                direction = GlobalVariable.direction,
                line = GlobalVariable.line,
                lineSegment = origin?.name,
                mPadUnit = GlobalVariable.deviceName,
                qty = remint,
                InspectionReportId = 0,
                inspectorName = GlobalVariable.inspectorname

            )
            try {
              //  printText("INSPECTION")
                printText()
                dbViewmodel.insertInspectionReportBulk(method)
//                onBackPressed()
//                overridePendingTransition(
//                    R.anim.screenslideleft, R.anim.screen_slide_out_right,
//                );
               // finish()
            }catch (e:java.lang.Exception){
                Toast(this).showCustomToast("Error saving--"+e.localizedMessage,this)
            }



            val signatureView = findViewById<SignatureView>(R.id.inspectionsignature)
            val signatureBitmaps = Bitmap.createBitmap(
                signatureView.width,
                signatureView.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(signatureBitmaps)
            signatureView.draw(canvas)



          //  printBitmap(ewan)
        }

    }

    val processRemsouth:(state: List<TripTicketTable>?) ->Unit={
        var rem:Int=0
        if(it?.size!=null){
            it.forEach {
                rem += it.qty
            }


            _binding.txtrem.text=" ${rem.toString()}"
        }else{

            _binding.txtrem.text="${0.toString()}"
        }
    }

    val processRemnorth:(state: List<TripTicketTable>?) ->Unit={
        var rem:Int=0
        if(it?.size!=null){
            it.forEach {
                rem += it.qty
            }
             rem
            _binding.txtrem.text="${rem.toString()}"
        }else{

            _binding.txtrem.text="${0.toString()}"
        }

    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.remnorth.observe(this, Observer {
                state ->processRemnorth(state)


        })

        dbViewmodel.remsouth.observe(this, Observer {
                state -> processRemsouth(state)
        })

        dbViewmodel.tripticketafterinspection.observe(this, Observer{
            state-> ProcessTicketsAfterInspection(state)
        })
    }
    val ProcessTicketsAfterInspection:(state: List<TripTicketTable>?) ->Unit={

        if(it?.size!=null){
            tripticket=it

        }else{

            Toast(this).showCustomToast("NO TICKET YET",this)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }

    private fun hideSoftKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
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
                    this@InspectionActivity,
                    "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@InspectionActivity,
                        "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@InspectionActivity,
                    "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@InspectionActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@InspectionActivity,
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
                    this@InspectionActivity,
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
            val mBitmap: Bitmap = BitmapFactory.decodeStream(bitmapToInputStream(image!!))
            try {
                val formattedDateTime = getCurrentDateInFormat()
                mIPosPrinterService!!.PrintSpecFormatText("Erjohn & Almark Transit Corp \n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Inspection Report\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Date: ${formattedDateTime}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.PrintSpecFormatText("Inspector: ${GlobalVariable.inspectorname}\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "KM Checked ${_binding.etInspectiondestination.text.toString()}\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printSpecifiedTypeText("Driver: ${GlobalVariable.driver}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Count: ${_binding.etActualcount.text}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Negative: ${_binding.txtinspectiondifference.text.toString()}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                tripticket?.forEach {
                    mIPosPrinterService!!.printSpecifiedTypeText("From: ${it.KMOrigin} - To: ${it.KmDestination} \n", "ST", 24,  callback)
                }
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************",
                    "ST",
                    24,
                    callback
                )

                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printBitmap(0, 4, mBitmap, callback)

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









}