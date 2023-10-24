package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.database.Model.WitholdingTypeTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityWitholdingBinding
import com.example.erjohnandroid.presenter.adapter.ExpensesAdapter
import com.example.erjohnandroid.presenter.adapter.WitholdingAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WitholdingActivity : AppCompatActivity() {

    lateinit var _binding:ActivityWitholdingBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var witholdingAdapter: WitholdingAdapter

    private var witholds:ArrayList<TripWitholdingTable> = arrayListOf()
    var witholdingtype:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityWitholdingBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        dbViewmodel.getwitholdingtype()

        _binding.btnsavwitholding.setOnClickListener {

            var withold=_binding.etWitholdingamount.text.toString()
            if(!TextUtils.isDigitsOnly(withold) || withold.isNullOrEmpty()|| witholdingtype.isNullOrEmpty()){
               // Toast.makeText(this,"NO AMOUNT",Toast.LENGTH_SHORT).show()
                Toast(this).showCustomToast("NO AMOUNT",this)
                return@setOnClickListener
            }
            val formattedDateTime = getCurrentDateInFormat()
            var manual= _binding.etWitholdingamount.text.toString()
            val stringWithoutSpaces = manual.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")

            var method= TripWitholdingTable(
                TripwitholdingId = 0,
                amount = stringcount.toDouble(),
                dateTimeStamp = formattedDateTime,
                mPadUnit = GlobalVariable.deviceName,
                name = "",
                witholdingType = witholdingtype,
                ingressoRefId = GlobalVariable.ingressoRefId
            )

            witholds.add(method)
            dbViewmodel.insertTripwitholdingbulk(witholds)

            dbViewmodel.getTripwitholding()
            dbViewmodel.tripwitholding.distinctUntilChanged().observe(this, Observer {
                    state->ProcesswitholdingResult(state)
            })




        }
    }

    override fun onStart() {
        super.onStart()

        dbViewmodel.witholdingtype.observe(this, Observer {
            state->Processwitholding(state)
        })
    }


    private fun Processwitholding(state: List<WitholdingTypeTable>?){
        if(!state.isNullOrEmpty()){
            witholdingAdapter = WitholdingAdapter(this)
            _binding.rvwitholding.adapter= witholdingAdapter
            _binding.rvwitholding.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            witholdingAdapter.showwitholding(state)
        }
    }

    val ProcesswitholdingResult:(state:List<TripWitholdingTable>) ->Unit={
        GlobalVariable.AllWitholding= arrayListOf()
        if(!it.isNullOrEmpty()) {
            GlobalVariable.AllWitholding.addAll(it)
        }
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }

    fun witholding(role: WitholdingTypeTable) {
        witholdingtype= role.type
        _binding.txtwitholdingtype.text=witholdingtype
    }

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}