package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.marginStart
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.PrimaryKey
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.TerminalTable
import com.example.erjohnandroid.database.Model.TripReverseTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.convertions.TripTicketGroupCount
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.externalViewModel

import com.example.erjohnandroid.databinding.ActivityReverseBinding
import com.example.erjohnandroid.presenter.adapter.ReverseAdapter
import com.example.erjohnandroid.presenter.adapter.RoleAdapter
import com.example.erjohnandroid.presenter.adapter.TerminalAdapter
import com.example.erjohnandroid.presenter.adapter.TicketDetailsAdapter
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.showCustomToast
import com.example.erjohnandroid.util.startActivityWithAnimation
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ReverseActivity : AppCompatActivity() {
    lateinit var _binding: ActivityReverseBinding
    private val dbViewmodel: RoomViewModel by viewModels()
    private  val externalViewModel:externalViewModel by viewModels()
    private  lateinit var reverseAdapter: ReverseAdapter
    private lateinit var terminalAdapter: TerminalAdapter
    private var tripreverse:ArrayList<TripReverseTable> = arrayListOf()
    private var totalamount:Double=0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityReverseBinding.inflate(layoutInflater)
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

        dbViewmodel.getReverse()
        GlobalVariable.terminal= null

        _binding.btnReverse.setOnClickListener {
            val formattedDateTime = getdate()
            if(GlobalVariable.terminal.isNullOrEmpty()){
                Toast(this).showCustomToast("Select Terminal",this)
                return@setOnClickListener
            }
            var method= TripReverseTable(
                Id=0,
                amount = totalamount,
                dateTimeStamp = formattedDateTime,
                deviceName = GlobalVariable.deviceName!!,
                direction = GlobalVariable.direction!!,
                reverseId = GlobalVariable.tripreverse!!,
                terminal = GlobalVariable.terminal!!,
                ingressoRefId = GlobalVariable.ingressoRefId


            )
            tripreverse.add(method)

            dbViewmodel.insertTripReverse(tripreverse)

            try {
                if(GlobalVariable.direction.equals("South"))
                    GlobalVariable.direction="North"
                else  GlobalVariable.direction="South"


                GlobalVariable.remainingPass=0
                GlobalVariable.destinationcounter=1
                GlobalVariable.origincounter=0
                GlobalVariable.tripreverse = GlobalVariable.tripreverse?.plus(1)
                externalViewModel.updateSavedDispatchedReverse(GlobalVariable.direction!!,GlobalVariable.tripreverse!!)
                GlobalVariable.ReverseTotalAmount=0.0
                Toast(this).showCustomToast("Reverse Success",this)
                finish()
                overridePendingTransition(
                    R.anim.screenslideleft, R.anim.screen_slide_out_right,
                );
            }catch (e:Exception){
                Toast(this).showCustomToast("${e}",this)
            }

        }

        _binding.btnclose.setOnClickListener {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
            finish()
        }

        _binding.btnChangeroute.setOnClickListener {
            if(GlobalVariable.terminal.isNullOrEmpty()){
                Toast(this).showCustomToast("Select Terminal",this)
                return@setOnClickListener
            }
            showchangeroutedialog()
        }
    }

    override fun onStart() {
        super.onStart()
        dbViewmodel.tripticketjson.observe(this, Observer {
                state-> ProcesJson(state)
        })

        dbViewmodel.terminals.observe(this,Observer{
                state -> ProcessTerminals(state)
        })

    }

    private fun ProcesJson(state: List<TripTicketGroupCount>?){
        if(!state.isNullOrEmpty()){
            reverseAdapter = ReverseAdapter(this)
            _binding.rvReverse.adapter= reverseAdapter
            _binding.rvReverse.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            reverseAdapter.showreverse(state)
            state.forEach {
               totalamount += it.sumamount
            }
            dbViewmodel.getAllTerminal()
        }
    }

    private fun ProcessTerminals(state: List<TerminalTable>?){
        if(!state.isNullOrEmpty()){
            // linelist=state
            // GlobalVariable.terminalList=state
            terminalAdapter = TerminalAdapter(this)
            _binding.rvTerminal.adapter= terminalAdapter
            _binding.rvTerminal.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            terminalAdapter.showterminal(state)
        }
    }

    fun showtickets(role: TripTicketGroupCount) {
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

    fun showchangeroutedialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("ARE YOU SURE YOU WANT TO CHANGE ROUTE?")

// Set up the input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.gravity = Gravity.CENTER
        input.textSize= 30f
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            val text = input.text.toString()
            if(text=="77777"){
                GlobalVariable.ReverseTotalAmount=totalamount
                startActivityWithAnimation<ChangeRouteActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                finish()
            }else{
                Toast.makeText(this,"PLEASE ENTER CORRECT PIN",Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun reverseTerminals(role: TerminalTable) {
        GlobalVariable.terminal= role.name

        // _binding!!.txtTerminal.text=("\nTerminal: ${GlobalVariable.terminal}")
        // _binding!!.txtLine.text=("\nLINE NAME: ${GlobalVariable.line}")
        Toast(this).showCustomToast("${GlobalVariable.terminal}",this)
    }

    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

}