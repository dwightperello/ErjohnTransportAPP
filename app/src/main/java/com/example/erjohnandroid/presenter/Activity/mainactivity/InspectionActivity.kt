package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
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
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.SignatureView
import com.example.erjohnandroid.util.showCustomToast
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

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityInspectionBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        bindService()

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
            }
            else{
                dbViewmodel.getRemNorth(origin?.kmPoint!!,GlobalVariable.tripreverse!!)
            }
            hideSoftKeyboard()
        }

        _binding.btnClear.setOnClickListener {
          _binding.inspectionsignature.clear()
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
                printText("INSPECTION")
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
                    textFormat.textSize=22
                    textFormat.ali=0
                    textFormat.style=0
                    textFormat.topPadding=15
                    ret = printerService!!.printText("Km Check: ${_binding.etInspectiondestination.text.toString()}",textFormat)
                    textFormat.topPadding=0
                    ret = printerService!!.printText("Count: ${_binding.etActualcount.text}",textFormat)
                    ret = printerService!!.printText("Diff: ${_binding.txtinspectiondifference.text.toString()}",textFormat)
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

    private fun printBitmap(bitmap:Bitmap) {
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