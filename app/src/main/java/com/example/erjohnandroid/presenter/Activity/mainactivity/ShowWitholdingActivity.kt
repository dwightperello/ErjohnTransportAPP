package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.database.Model.WitholdingTypeTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityShowExpensesBinding
import com.example.erjohnandroid.databinding.ActivityShowWitholdingBinding
import com.example.erjohnandroid.presenter.adapter.WitholdingAdapter
import com.example.erjohnandroid.presenter.adapter.showAllExpensesAdapter
import com.example.erjohnandroid.presenter.adapter.showAllWitholdingAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowWitholdingActivity : AppCompatActivity() {
    lateinit var _binding: ActivityShowWitholdingBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var witholdingAdapter: WitholdingAdapter
    private  lateinit var showWitholdingAdapter: showAllWitholdingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityShowWitholdingBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        dbViewmodel.getwitholdingtype()
        dbViewmodel.witholdingtype.observe(this, Observer {
                state->Processwitholding(state)
        })

        dbViewmodel.allTripWitholding.observe(this, Observer {
                state -> showWitholding(state)
        })

        _binding.btnSave.setOnClickListener {
            val withold = _binding.etWithold.text.toString()
            if(!TextUtils.isDigitsOnly(withold) || withold.isNullOrEmpty() || witholdingtype.isNullOrEmpty()) {
                Toast(this).showCustomToast("Ooopss,no amount or witholding type",this)
                return@setOnClickListener
            }
            val date= GlobalVariable.getCurrentDateInFormat()
            val stringWithoutSpaces = withold.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")

            var method= TripWitholdingTable(
                TripwitholdingId = 0,
                amount = stringcount.toDouble(),
                dateTimeStamp = date,
                mPadUnit = GlobalVariable.deviceName,
                name = GlobalVariable.conductor +" " +GlobalVariable.driver,
                witholdingType = witholdingtype,
                ingressoRefId = GlobalVariable.ingressoRefId
            )
            dbViewmodel.insertTripwitholdingbulk(method)
            dbViewmodel.allTripWitholding.observe(this, Observer {
                    state -> showWitholding(state)
            })
        }
    }

    private fun Processwitholding(state: List<WitholdingTypeTable>?){
        if(!state.isNullOrEmpty()){
            witholdingAdapter = WitholdingAdapter(this)
            _binding.rvWithold.adapter= witholdingAdapter
            _binding.rvWithold.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            witholdingAdapter.showwitholding(state)
        }
    }

    private fun showWitholding(state: List<TripWitholdingTable>?){
        GlobalVariable.AllWitholding= arrayListOf()
        if(!state.isNullOrEmpty()){
            showWitholdingAdapter = showAllWitholdingAdapter(this)
            _binding.rvWitholdingTypes.adapter= showWitholdingAdapter
            _binding.rvWitholdingTypes.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            showWitholdingAdapter.showwitholding(state)
            GlobalVariable.AllWitholding.addAll(state)
        }
        else _binding.rvWitholdingTypes.adapter= null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
        finish()
    }

    var witholdingtype:String?= null
    fun witholding(role: WitholdingTypeTable) {
        witholdingtype= role.type
        _binding.txtWitholdingtype.text= witholdingtype
    }

    fun removeItem(role: TripWitholdingTable) {
        dbViewmodel.deleteTripWitholdingItem(role.TripwitholdingId)
        dbViewmodel.allTripWitholding.observe(this, Observer {
                state -> showWitholding(state)
        })
    }
}