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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityShowExpensesBinding
import com.example.erjohnandroid.presenter.adapter.ExpensesAdapter
import com.example.erjohnandroid.presenter.adapter.showAllExpensesAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowExpensesActivity : AppCompatActivity() {
    lateinit var _binding: ActivityShowExpensesBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  lateinit var expensesAdapter: ExpensesAdapter
    private  lateinit var showExpensesAdapter:showAllExpensesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityShowExpensesBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dbViewmodel.getExpensesType()
        dbViewmodel.expensestype.observe(this   , Observer {
                state-> Processexpenses(state)
        })

        dbViewmodel.allTripcost.observe(this, Observer {
                state -> showCosts(state)
        })

        _binding.btnSave.setOnClickListener {
            val expenses = _binding.etExpenses.text.toString()
            if(!TextUtils.isDigitsOnly(expenses) || expenses.isNullOrEmpty() || expensestype.isNullOrEmpty()) {
                Toast(this).showCustomToast("Ooopss, no amount or expenses type",this)
                return@setOnClickListener
            }
            val date= GlobalVariable.getCurrentDateInFormat()
            val stringWithoutSpaces = expenses.replace(" ", "")
            val stringcount = stringWithoutSpaces.replace(" ", "")

            var method = TripCostTable(
                amount = stringcount.toDouble(),
                dateTimeStamp = date,
                costType = expensestype,
                driverConductorName = GlobalVariable.conductor +" " +GlobalVariable.driver,
                line = GlobalVariable.line,
                TripCostId = 0,
                ingressoRefId = GlobalVariable.ingressoRefId
            )

            dbViewmodel.inserTirpcostBUlk(method)
            dbViewmodel.allTripcost.observe(this, Observer {
                state -> showCosts(state)
            })

        }

    }

    private fun Processexpenses(state: List<ExpensesTypeTable>?){
        if(!state.isNullOrEmpty()){
            expensesAdapter = ExpensesAdapter(this)
            _binding.rvExpenses.adapter= expensesAdapter
            _binding.rvExpenses.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            expensesAdapter.showexpenses(state)
        }
    }

    private fun showCosts(state: List<TripCostTable>?){
        GlobalVariable.AllTripCost= arrayListOf()
        if(!state.isNullOrEmpty()){
            showExpensesAdapter = showAllExpensesAdapter(this)
            _binding.rvExpensesTypes.adapter= showExpensesAdapter
            _binding.rvExpensesTypes.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            showExpensesAdapter.showexpenses(state)
            GlobalVariable.AllTripCost.addAll(state)
        }
        else _binding.rvExpensesTypes.adapter= null
    }

    private var expensestype:String?= null
    fun expenses(role: ExpensesTypeTable) {
        expensestype= role.name
        _binding.txtExpensestype.text= expensestype
    }

    fun removeItem(role: TripCostTable) {
        dbViewmodel.deleteTripcostItem(role.TripCostId)
        dbViewmodel.allTripcost.observe(this, Observer {
                state -> showCosts(state)
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(
//            R.anim.screenslideleft, R.anim.screen_slide_out_right,
//        );
//        finish()
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        overridePendingTransition(
            R.anim.screenslideleft, R.anim.screen_slide_out_right,
        );
    }


}