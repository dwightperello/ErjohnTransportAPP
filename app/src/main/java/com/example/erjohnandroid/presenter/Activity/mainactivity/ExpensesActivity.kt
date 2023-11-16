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
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityExpensesBinding

import com.example.erjohnandroid.presenter.adapter.BusAdapter
import com.example.erjohnandroid.presenter.adapter.ExpensesAdapter
import com.example.erjohnandroid.presenter.adapter.LineAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.GlobalVariable.AllTripCost
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ExpensesActivity : AppCompatActivity() {
    lateinit var _binding:ActivityExpensesBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var expensesAdapter: ExpensesAdapter

    private var expensestype:String?= null

    private var expenses:ArrayList<TripCostTable> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        dbViewmodel.getExpensesType()

        _binding.btnsaveexpenses.setOnClickListener {
            var withold=_binding.etExpensesamount.text.toString()
            if(!TextUtils.isDigitsOnly(withold) || withold.isNullOrEmpty() || expensestype.isNullOrEmpty()) {
               // Toast.makeText(this,"NO AMOUNT", Toast.LENGTH_SHORT).show()
                Toast(this).showCustomToast("NO AMOUNT",this)
                return@setOnClickListener
            }
            val formattedDateTime = getCurrentDateInFormat()
            var manual= _binding.etExpensesamount.text.toString()
            val stringWithoutSpaces = manual.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")

            var method= TripCostTable(
                amount = stringcount.toDouble(),
                dateTimeStamp = formattedDateTime,
                costType = expensestype,
                driverConductorName = GlobalVariable.conductor +" " +GlobalVariable.driver,
                line = GlobalVariable.line,
                TripCostId = 0,
                ingressoRefId = GlobalVariable.ingressoRefId
            )

            expenses.add(method)
            dbViewmodel.inserTirpcostBUlk(expenses)

            dbViewmodel.getTripcost()
            dbViewmodel.tripcost.distinctUntilChanged().observe(this, Observer {
                    state-> ProcessExpenses(state)
            })


        }

    }
    val ProcessExpenses:(state:List<TripCostTable>) ->Unit={
       GlobalVariable.AllTripCost= arrayListOf()

        if(!it.isNullOrEmpty()) {



          GlobalVariable.AllTripCost.addAll(it)
        }
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }

    override fun onStart() {
        super.onStart()

        dbViewmodel.expensestype.observe(this   , Observer {
            state-> Processexpenses(state)
        })
    }

    private fun Processexpenses(state: List<ExpensesTypeTable>?){
        if(!state.isNullOrEmpty()){
            expensesAdapter = ExpensesAdapter(this)
            _binding.rvexpenses.adapter= expensesAdapter
            _binding.rvexpenses.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            expensesAdapter.showexpenses(state)
        }
    }

    fun expenses(role: ExpensesTypeTable) {
        expensestype= role.name
        _binding.txtexpensestype.text= expensestype
    }

    fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}