package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.WindowManager
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
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
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

        dbViewmodel.getReverse()
        bindService()

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
                DateTimeStamp = formattedDateTime
            )
            try {
                dbViewmodel.insertPartialremit(method)
            }catch (e:java.lang.Exception){
                Log.e("error",e.localizedMessage)
            }

            printText("Erjohn & Almark Transit Corp ")

//            finish()
//            overridePendingTransition(
//                R.anim.screenslideleft, R.anim.screen_slide_out_right,
//            );
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
                    ret = printerService!!.printText("PARTIAL REMIT",textFormat)
                    ret = printerService!!.printText("Cashier: ${GlobalVariable.cashiername}",textFormat)
                    ret = printerService!!.printText("Amount Remited: ${_binding.etCashremited.text}",textFormat)
                    textFormat.style=0
                    ret = printerService!!.printText("------------------------------",textFormat)
//                    textFormat.textSize=22
//                    textFormat.ali=0
//                    textFormat.style=0
//                    textFormat.topPadding=15
//                    ret = printerService!!.printText("Km Check: ${_binding.etInspectiondestination.text.toString()}",textFormat)
//                    textFormat.topPadding=0
//
//
//                    ret = printerService!!.printText("Count: ${_binding.etActualcount.text}",textFormat)
//                    ret = printerService!!.printText("Diff: ${_binding.txtinspectiondifference.text.toString()}",textFormat)

                    ret = printerService!!.printBitmap(
                        BitmapFactory.decodeStream(bitmapToInputStream(image!!)

                        ), 1, 1
                    )

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

//    fun printBitmap(bitmap: Bitmap, scaleWidth: Int, scaleHeight: Int) {
//        singleThreadExecutor.submit {
//            try {
//                val ret = printerService!!.printBitmap(bitmap, scaleWidth, scaleHeight)
//               // showLog("Print bitmap: " + msg(ret))
//                if (ret == 0) {
//                    paperOut()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun printBitmap(bitmap: Bitmap) {
        singleThreadExecutor.submit {
            try {
                val ret = printerService!!.printBitmap(
                    BitmapFactory.decodeStream(bitmapToInputStream(bitmap)

                    ), 1, 1
                )
                // showLog("Print bitmap: " + msg(ret))


                if (ret == 0) {
                    paperOut()
                }


            } catch (e: java.lang.Exception) {
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