package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityDispatchBinding
import com.example.erjohnandroid.databinding.ActivitySharedLoginBinding
import com.example.erjohnandroid.databinding.ActivityTicketdetailsBinding
import com.example.erjohnandroid.presenter.adapter.TicketDetailsAdapter
import com.example.erjohnandroid.util.GlobalVariable
import dagger.hilt.android.AndroidEntryPoint
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.util.concurrent.Executors

@AndroidEntryPoint
class ticketdetailsActivity : AppCompatActivity() {
    lateinit var _binding: ActivityTicketdetailsBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var TicketDetailsAdapter: TicketDetailsAdapter
    var tripticketTable: List<TripTicketTable> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTicketdetailsBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        computeWindowSizeClasses()
        val receivedData = intent.getIntExtra("key",0)
        dbViewmodel.getTripticketdetails(receivedData!!)

        bindService()

        _binding.btnprintticketdetails.setOnClickListener {
            printText("Erjohn & Almark Transit Corp ")
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
//            val gson = Gson()
//            val jsonResult = gson.toJson(state)
            tripticketTable= state

            TicketDetailsAdapter = TicketDetailsAdapter(this)
            _binding.rvTicketdetails.adapter= TicketDetailsAdapter
            _binding.rvTicketdetails.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            TicketDetailsAdapter.showdetails(state)
        }
    }


    //region PRINTER
    private val TAG: String? = "TicketDetails"
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
                    ret = printerService!!.printText("TICKET DETAILS",textFormat)
                    textFormat.style=0
                    ret = printerService!!.printText("------------------------------",textFormat)
                    textFormat.ali=0
                    textFormat.topPadding=10
                    tripticketTable.forEach {
                        ret = printerService!!.printText("${it.origin}" + " -- "+ "${it.destination}"   ,textFormat)
                        ret = printerService!!.printText("${it.amount}"   ,textFormat)
                        textFormat.topPadding=5
                    }
//                    ret = printerService!!.printText("Line Dispatched: ${GlobalVariable.line}"   ,textFormat)
//                    ret = printerService!!.printText("Direction: ${GlobalVariable.direction}",textFormat)
//                    ret = printerService!!.printText("Bus Number: ${GlobalVariable.bus}",textFormat)
//                    ret = printerService!!.printText("Bus Driver: ${GlobalVariable.driver}",textFormat)
//                    ret = printerService!!.printText("Bus Conductor: ${GlobalVariable.conductor}",textFormat)
//                    textFormat.topPadding=10
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