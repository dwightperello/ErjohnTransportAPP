package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.DoubleUnaryOperator

@AndroidEntryPoint
class PartialRemitActivity : AppCompatActivity() {
    lateinit var _binding: ActivityPartialRemitBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var reverseAdapter: ReverseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityPartialRemitBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        dbViewmodel.getReverse()

        _binding.btnSaveremit.setOnClickListener {

            val signatureBitmap = _binding.inspectionsignature.isSignaturePresent()
             val text= _binding.etCashremited.text.toString()
            val total=_binding.txttotalcash.text.toString()
            val formattedDateTime = getdate()
            if(!signatureBitmap){
                Toast(this).showCustomToast("Please sign",this)
                return@setOnClickListener
            }
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


            finish()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
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
}