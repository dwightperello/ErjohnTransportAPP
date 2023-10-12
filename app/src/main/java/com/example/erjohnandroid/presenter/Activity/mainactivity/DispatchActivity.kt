package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.mPadAssignmentsTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.presenter.adapter.BusAdapter
import com.example.erjohnandroid.presenter.adapter.DriverAdapter
import com.example.erjohnandroid.presenter.adapter.LineAdapter
import com.example.erjohnandroid.presenter.adapter.RoleAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@AndroidEntryPoint
class DispatchActivity : AppCompatActivity() {
    lateinit var _binding:ActivityDispatchBinding
    private val dbViewmodel: RoomViewModel by viewModels()

    private  lateinit var rolesasapter:RoleAdapter
    private  lateinit var driverAdapter: DriverAdapter
    private  lateinit var busAdapter: BusAdapter
    private  lateinit var lineAdapter: LineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityDispatchBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
       // _binding.txtdispatcherName.text ="Welcome "+ GlobalVariable.employeeName
        bindService()

        dbViewmodel.selectConductor(2)
        dbViewmodel.selectDriver(1)
        dbViewmodel.getBusinfo(2)
        dbViewmodel.getAllLines()
        initsearch()
        initCheckbox()

//        _binding.btnprint.setOnClickListener {
//            printText("GUMANA KNA PLS LNG")
//        }


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
                mPadAssignmentId = 0
            )
            dispatch?.add(method)
            try {
                dbViewmodel.insertmPadAssignmentBulk(dispatch!!)
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)


                GlobalVariable.isDispatched=true
                finish()
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

            var headerprint="Erjohn & Almark Transit Corp "

            printText(headerprint)

        }
    }

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
        GlobalVariable.driver= role.name +" " + role.lastName
        _binding!!.txtDriver.text=("\n DRIVER NAME: ${GlobalVariable.driver}")
        Toast(this).showCustomToast("${GlobalVariable.driver}",this)
    }

    fun Liness(role: LinesTable) {
        GlobalVariable.line= role.name
        GlobalVariable.lineid= role.id
        if(role.remarks.equals("SB")){
            _binding.cbNorth.isEnabled=false
            _binding.cbSouth.isEnabled=true
        }
        else{
            _binding.cbNorth.isEnabled=true
            _binding.cbSouth.isEnabled=false
        }
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
               val filtered = conductorList?.filter { it.name?.toLowerCase(Locale.getDefault())!!.contains(newText!!) }
               rolesasapter.showCOnductor(filtered!!)
               return false
           }
       })


       _binding.sDriver.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               val filtered = driverList?.filter { it.name?.toLowerCase(Locale.getDefault())!!.contains(newText!!) }
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
            if(_binding.cbSouth.isChecked) _binding.cbSouth.isChecked=false
            GlobalVariable.direction="North"
            _binding!!.txtDirection.text=("\nDIRECTION: ${GlobalVariable.direction}")
        }
        _binding.cbSouth.setOnClickListener {
            if(_binding.cbNorth.isChecked)_binding.cbNorth.isChecked=false
            GlobalVariable.direction="South"
            _binding!!.txtDirection.text=("\nDIRECTION: ${GlobalVariable.direction}")
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

    private fun printText(text: String) {
        singleThreadExecutor.submit {
            try {
                val textFormat = PrintTextFormat()
                textFormat.textSize=26
                // textFormat.setUnderline(true);
                textFormat.ali=1
                textFormat.style=1
                try {
                    var ret = printerService!!.printText(text, textFormat)
                    textFormat.textSize=24
                    ret = printerService!!.printText("DISPATCH",textFormat)
                    textFormat.style=0
                    ret = printerService!!.printText("------------------------------",textFormat)
                    textFormat.ali=0


                    ret = printerService!!.printText("Line Dispatched: ${GlobalVariable.line}"   ,textFormat)
                    ret = printerService!!.printText("Direction: ${GlobalVariable.direction}",textFormat)
                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
                    ret = printerService!!.printText("Bus Driver: ${GlobalVariable.driver}",textFormat)
                    ret = printerService!!.printText("Bus Conductor: ${GlobalVariable.conductor}",textFormat)
                    textFormat.topPadding=10
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

}