package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.presenter.adapter.BusAdapter
import com.example.erjohnandroid.presenter.adapter.DriverAdapter
import com.example.erjohnandroid.presenter.adapter.LineAdapter
import com.example.erjohnandroid.presenter.adapter.RoleAdapter
import com.example.erjohnandroid.printer.ThreadPoolManager
import com.example.erjohnandroid.printer.printerUtils.HandlerUtils
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import com.google.android.gms.common.internal.GmsLogger
import com.iposprinter.iposprinterservice.IPosPrinterCallback
import com.iposprinter.iposprinterservice.IPosPrinterService
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DispatchActivity : AppCompatActivity() {
    lateinit var _binding:ActivityDispatchBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private val externalViewModel:externalViewModel by viewModels()

    private  lateinit var rolesasapter:RoleAdapter
    private  lateinit var driverAdapter: DriverAdapter
    private  lateinit var busAdapter: BusAdapter
    private  lateinit var lineAdapter: LineAdapter
    private  var isNorthAllowed:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityDispatchBinding.inflate(layoutInflater)
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
        dbViewmodel.selectConductor(2)
        dbViewmodel.selectDriver(1)
        dbViewmodel.getBusinfo(2)
        dbViewmodel.getAllLines()
        externalViewModel.getTicketnumber()
        initsearch()
        initCheckbox()

        _binding.btnSave.setOnClickListener {

            if(GlobalVariable.bus.isNullOrEmpty() ||GlobalVariable.conductor.isNullOrEmpty() ||GlobalVariable.employeeName.isNullOrEmpty() ||GlobalVariable.driver.isNullOrEmpty() ||GlobalVariable.direction.isNullOrEmpty() ||GlobalVariable.line.isNullOrEmpty() ){
               // Toast.makeText(this ,"PLEASE CHECK ENTRY",Toast.LENGTH_LONG).show()
                Toast(this).showCustomToast("PLEASE CHECK ENTRY",this)
                return@setOnClickListener
            }
            var dispatch:ArrayList<mPadAssignmentsTable>?= ArrayList<mPadAssignmentsTable>()
            GlobalVariable.deviceName=getDeviceName()
            val formattedDateTime = getCurrentDateInFormat()
            var method= mPadAssignmentsTable(
                busNumber = GlobalVariable.bus,
                conductorName = GlobalVariable.conductor,
                dataTimeStamp = formattedDateTime.toString(),
                dispatcherName = GlobalVariable.employeeName,
                driverName = GlobalVariable.driver,
                line = GlobalVariable.line,
                mPadUnit = GlobalVariable.deviceName,
                mPadAssignmentId = 0,
                ingressoRefId = GlobalVariable.ingressoRefId
            )
            dispatch?.add(method)
            try {
                externalViewModel.updateSavedDispatched(GlobalVariable.bus!!,GlobalVariable.conductor!!,true,GlobalVariable.employeeName!!,GlobalVariable.driver!!,GlobalVariable.line!!,GlobalVariable.lineid!!,GlobalVariable.deviceName!!,GlobalVariable.tripreverse!!,GlobalVariable.originalTicketnum,GlobalVariable.direction!!,GlobalVariable.ingressoRefId)
                dbViewmodel.insertmPadAssignmentBulk(dispatch!!)
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)


                GlobalVariable.isDispatched=true
                GlobalVariable.isFromDispatch=true
                finish()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }



           printText()



        }

        _binding.btnclose.setOnClickListener {
            finish()
        }
    }

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.capitalize()
        } else {
            manufacturer.capitalize() + " " + model
        }
    }

    override fun onStart() {
        super.onStart()

        externalViewModel.ticketnumberstart.observe(this, Observer {
            state -> ProcessTicketnumbers(state)
        })

        dbViewmodel.selectCOnductor?.observe(this, Observer {
            state->ProcessRoles(state)
        })

        dbViewmodel.selectDriver?.observe(this, Observer {
            state -> ProcessDrivers(state)
        })

        dbViewmodel.businfo.observe(this, Observer {
            state-> ProcessBus(state)
        })

        dbViewmodel.allLines.observe(this, Observer {
            state -> ProcessLines(state)
        })
    }

    private var conductorList:List<EmployeesTable>?= null
    private var driverList:List<EmployeesTable>?= null
    private var busList:List<BusInfoTableItem>?= null
    private var linelist:List<LinesTable>?= null


    private fun ProcessTicketnumbers(state: TicketCounterTable){
            GlobalVariable.ticketnumid=state.Id
            GlobalVariable.ticketnumber=state.ticketnumber
            GlobalVariable.ticketcounter = GlobalVariable.ticketnumber
            GlobalVariable.ingressoRefId=state.ingressoRefId + 1
            GlobalVariable.originalTicketnum= GlobalVariable.ticketnumber
    }

    private fun ProcessRoles(state: List<EmployeesTable>?){
       if(!state.isNullOrEmpty()){
           conductorList=state
           rolesasapter = RoleAdapter(this)
           _binding.rvConductor.adapter= rolesasapter
           _binding.rvConductor.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
           rolesasapter.showCOnductor(conductorList!!)
       }
    }

    private fun ProcessDrivers(state: List<EmployeesTable>?){
        if(!state.isNullOrEmpty()){
            driverList=state
            driverAdapter = DriverAdapter(this)
            _binding.rvDriver.adapter= driverAdapter
            _binding.rvDriver.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            driverAdapter.showDriver(driverList!!)
        }
    }

    private fun ProcessBus(state: List<BusInfoTableItem>?){
        if(!state.isNullOrEmpty()){
            busList=state
            busAdapter = BusAdapter(this)
            _binding.rvBus.adapter= busAdapter
            _binding.rvBus.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            busAdapter.showNumber(busList!!)
        }
    }

    private fun ProcessLines(state: List<LinesTable>?){
        if(!state.isNullOrEmpty()){
            linelist=state
            lineAdapter = LineAdapter(this)
            _binding.rvLine.adapter= lineAdapter
            _binding.rvLine.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            lineAdapter.showLines(linelist!!)
        }
    }



    fun Bus(role: BusInfoTableItem) {
        GlobalVariable.bus= role.busNumber.toString()
       // _binding!!.txtConfirmdispatch.text="BUS #: ${GlobalVariable.bus}"
        _binding!!.txtBus.text=("\nBUS #: ${GlobalVariable.bus}")
        Toast(this).showCustomToast("${GlobalVariable.bus}",this)
    }

    fun Driver(role: EmployeesTable) {
        GlobalVariable.driver= role.name
        _binding!!.txtDriver.text=("\n DRIVER NAME: ${GlobalVariable.driver}")
        Toast(this).showCustomToast("${GlobalVariable.driver}",this)
    }

    fun Liness(role: LinesTable) {
        GlobalVariable.line= role.name
        GlobalVariable.lineid= role.id
        if(role.remarks.equals("SB")){
            _binding.cbNorth.isEnabled=false
            _binding.cbSouth.isEnabled=false
            _binding.cbNorth.isChecked=false
            isNorthAllowed=false
            _binding.cbSouth.isChecked=true
            GlobalVariable.direction="South"
        }
        else{
            isNorthAllowed=true
            _binding.cbSouth.isChecked=false
            _binding.cbNorth.isEnabled=false
            _binding.cbSouth.isEnabled=false
            _binding.cbNorth.isChecked=true
            GlobalVariable.direction="North"
        }
        _binding!!.txtDirection.text=("\nDIRECTION: ${GlobalVariable.direction}")
        _binding!!.txtLine.text=("\nLINE NAME: ${GlobalVariable.line}")
        Toast(this).showCustomToast("${GlobalVariable.line}",this)
    }

    fun COnductor(role: EmployeesTable) {
        GlobalVariable.conductor= role.name
        _binding!!.txtConductor.text=("\nCONDUCTOR NAME: ${GlobalVariable.conductor}")
        Toast(this).showCustomToast("${GlobalVariable.conductor}",this)
    }

    val initsearch={

     _binding.sConductor.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               val filtered = conductorList?.filter {
                  // it.name?.toLowerCase(Locale.getDefault())!!.contains(newText!!)
                   it.name?.toLowerCase(Locale.getDefault())?.startsWith(newText!!.toLowerCase(Locale.getDefault())) == true
               }
               rolesasapter.showCOnductor(filtered!!)
               return false
           }
       })


       _binding.sDriver.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               val filtered = driverList?.filter {
                  // it.name?.toLowerCase(Locale.getDefault())!!.contains(newText!!)
                   it.name?.toLowerCase(Locale.getDefault())?.startsWith(newText!!.toLowerCase(Locale.getDefault())) == true
               }
               driverAdapter.showDriver(filtered!!)
               return false
           }
       })

       _binding.sBus.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               val filtered = busList?.filter { it.busNumber?.toString()?.toLowerCase(Locale.getDefault())!!.contains(newText!!) }
               busAdapter.showNumber(filtered!!)
               return false
           }
       })

       _binding.sLine.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               val filtered = linelist?.filter { it.name?.toLowerCase(Locale.getDefault())!!.contains(newText!!) }
               lineAdapter.showLines(filtered!!)
               return false
           }
       })

   }

    val initCheckbox={
        _binding.cbNorth.setOnClickListener {

            if(!isNorthAllowed){
                Toast(this).showCustomToast("North Bound is not allowed",this)
                _binding.cbNorth.isChecked=false
                return@setOnClickListener
            }

//            else{
//                Toast(this).showCustomToast("South Bound is not allowed",this)
//                _binding.cbSouth.isChecked=false
//                return@setOnClickListener
//            }

            if(_binding.cbSouth.isChecked) _binding.cbSouth.isChecked=false
            GlobalVariable.direction="North"
            _binding!!.txtDirection.text=("\nDIRECTION: ${GlobalVariable.direction}")
        }
        _binding.cbSouth.setOnClickListener {
            if(isNorthAllowed){
                Toast(this).showCustomToast("South Bound is not allowed",this)
                _binding.cbSouth.isChecked=false
                return@setOnClickListener
            }

            if(_binding.cbNorth.isChecked)_binding.cbNorth.isChecked=false
            GlobalVariable.direction="South"
            _binding!!.txtDirection.text=("\nDIRECTION: ${GlobalVariable.direction}")
        }



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
                    this@DispatchActivity,
                   "BUSY",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_PAPER_LESS -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@DispatchActivity,
                      "NO PAPER",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                MSG_PAPER_EXISTS -> Toast.makeText(
                    this@DispatchActivity,
                   "paper present",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_THP_HIGH_TEMP -> Toast.makeText(
                    this@DispatchActivity,
                    "high temp",
                    Toast.LENGTH_SHORT
                ).show()
                MSG_MOTOR_HIGH_TEMP -> {
                    loopPrintFlag = DEFAULT_LOOP_PRINT
                    Toast.makeText(
                        this@DispatchActivity,
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
                    this@DispatchActivity,
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
                mIPosPrinterService!!.PrintSpecFormatText("Dispatch\n", "ST", 24, 1,callback)
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "Line Dispatch ${GlobalVariable.line}\n",
                    "ST",
                    24,
                    callback
                )
                mIPosPrinterService!!.printBlankLines(1, 8, callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "Date: ${formattedDateTime}\n",
                    "ST",
                    24,
                    callback
                )
               // mIPosPrinterService!!.printBlankLines(1, 16, callback)
              //  mIPosPrinterService!!.printBitmap(1, 12, mBitmap, callback)
              //  mIPosPrinterService!!.printBlankLines(1, 16, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Direction: ${GlobalVariable.direction}\n", "ST", 24, callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Bus Number: ${GlobalVariable.bus}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Dispatcher: ${GlobalVariable.employeeName}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Driver: ${GlobalVariable.driver}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText("Conductor: ${GlobalVariable.conductor}\n", "ST", 24,  callback)
                mIPosPrinterService!!.printSpecifiedTypeText(
                    "********************************",
                    "ST",
                    24,
                    callback
                )
//                mIPosPrinterService!!.printSpecifiedTypeText("这是一行16号字体\n", "ST", 16, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText("这是一行24号字体\n", "ST", 24, callback)
//                mIPosPrinterService!!.PrintSpecFormatText("这是一行24号字体\n", "ST", 24, 2, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText("这是一行32号字体\n", "ST", 32, callback)
//                mIPosPrinterService!!.PrintSpecFormatText("这是一行32号字体\n", "ST", 32, 2, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText("这是一行48号字体\n", "ST", 48, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234\n",
//                    "ST",
//                    16,
//                    callback
//                )
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "abcdefghijklmnopqrstuvwxyz56789\n",
//                    "ST",
//                    24,
//                    callback
//                )
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "κρχκμνκλρκνκνμρτυφ\n",
//                    "ST",
//                    24,
//                    callback
//                )
//                mIPosPrinterService!!.setPrinterPrintAlignment(0, callback)
//                mIPosPrinterService!!.printQRCode("http://www.baidu.com\n", 10, 1, callback)
//                mIPosPrinterService!!.printBlankLines(1, 16, callback)
//                mIPosPrinterService!!.printBlankLines(1, 16, callback)
//                for (i in 0..11) {
//                    mIPosPrinterService!!.printRawData(BytesUtil.initLine1(384, i), callback)
//                }
//                mIPosPrinterService!!.PrintSpecFormatText("打印测试完成\n", "ST", 32, 1, callback)
//                mIPosPrinterService!!.printSpecifiedTypeText(
//                    "**********END***********\n\n",
//                    "ST",
//                    32,
//                    callback
//                )
              //  bitmapRecycle(mBitmap)
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(IPosPrinterStatusListener)
        unbindService(connectService)
        handler!!.removeCallbacksAndMessages(null)
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
//          //  showLog("printer service disconnected, try reconnect")
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
//                    ret = printerService!!.printText("DISPATCH",textFormat)
//                    textFormat.style=0
//                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.ali=0
//
//
//                    ret = printerService!!.printText("Line Dispatched: ${GlobalVariable.line}"   ,textFormat)
//                    ret = printerService!!.printText("Direction: ${GlobalVariable.direction}",textFormat)
//                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
//                    ret = printerService!!.printText("Bus Driver: ${GlobalVariable.driver}",textFormat)
//                    ret = printerService!!.printText("Bus Conductor: ${GlobalVariable.conductor}",textFormat)
//                    textFormat.topPadding=10
//                    ret = printerService!!.printText("",textFormat)
//                    ret = printerService!!.printText("",textFormat)
//                    ret = printerService!!.printText("",textFormat)
//                    if (ret == 0) {
//                        paperOut()
//                    }
//
//                }catch (e:java.lang.Exception){
//                    Log.e("tae",e.localizedMessage)
//                }
//
//               // showLog("Print text: " + msg(ret))
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