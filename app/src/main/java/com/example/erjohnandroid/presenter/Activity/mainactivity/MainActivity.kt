package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.database.viewmodel.sd_viewmodel
import com.example.erjohnandroid.databinding.ActivityMainBinding
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.startActivityWithAnimation
import com.google.android.gms.common.internal.GmsLogger
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding:ActivityMainBinding?= null
    private val networkViewModel:networkViewModel by viewModels()
    private val roomviewmodel: RoomViewModel by viewModels()
    private val sdViewmodel:sd_viewmodel by viewModels()

    val DISPATCH_ACTIVITY = 1
    val INSPECTION_ACTIVITY=2
    val SYNCHING_ACTIVITY=3
    val INGRESSO_ACTIVITY=4
    val INSPECTION=9
    val PARTIAL_ACTIVITY=5
    val SETTINGS_ACTIVITY=10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //IMPLEMENT WHEN DEPLOYED
//        val sharedPreferences = this.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
//        GlobalVariable.API_BASE_URL  = sharedPreferences.getString("URL", "default_value").toString()

        checkifAlreadySynch()
        initiButtons()
        initibuttondisable()

        val sharedPrefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        GlobalVariable.ticketnumber = sharedPrefs.getInt("ticketnumber", 0)


    }
    val initibuttondisable={
        _binding?.btnDispatch?.isVisible=true
        _binding?.btnTicketing?.isEnabled=false
        _binding?.btnIngresso?.isEnabled=false
        _binding?.btnInspection?.isEnabled=false
        _binding?.btnPartialremit?.isEnabled=false
        _binding?.btnReverse?.isEnabled=false
        _binding?.btnSettings?.isEnabled=false
        _binding?.btnSynch?.isEnabled=true
    }
    val enablebutton={
        _binding?.btnDispatch?.isVisible=false
        _binding?.txtdispatch?.isVisible=false
        _binding?.btnTicketing?.isEnabled=true
        _binding?.btnIngresso?.isEnabled=true
        _binding?.btnInspection?.isEnabled=true
        _binding?.btnPartialremit?.isEnabled=true
        _binding?.btnReverse?.isEnabled=true
        _binding?.btnSettings?.isEnabled=true
        _binding?.btnSynch?.isEnabled=true
    }

    val checkifAlreadySynch ={
        roomviewmodel.getAllLines()
       // sdViewmodel.getTripticket()
    }

    override fun onStart() {
        super.onStart()
        roomviewmodel.allLines.observe(this, Observer {
            state ->  ProcessAllLinesResponse(state)
        })
        sdViewmodel.sdtripticket.observe(this, Observer {
            state ->
            var items=state
        })
    }

    private fun ProcessAllLinesResponse(state: List<LinesTable>){
      if( !state.isNullOrEmpty()){
            Log.d("ays","ASYA")
      }else{
          startActivityWithAnimation<LoginActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            finish()
      }
    }

    val initiButtons={
        _binding!!.btnDispatch.setOnClickListener {
            //startActivityWithAnimation<SharedLoginActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            val intent = Intent(this, SharedLoginActivity::class.java)
            startActivityForResult(intent,DISPATCH_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
        }

        _binding!!.btnTicketing.setOnClickListener {
            startActivityWithAnimation<TIcketingActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnInspection.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity",INSPECTION)
            startActivityForResult(intent,INSPECTION_ACTIVITY)
            overridePendingTransition(
                R.anim.screenslideright, R.anim.screen_slide_out_left
            );
            //startActivityWithAnimation<InspectionActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnIngresso.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", INGRESSO_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }

        _binding!!.btnPartialremit.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", PARTIAL_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }
        _binding!!.btnReverse.setOnClickListener {
            startActivityWithAnimation<ReverseActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnSynch.setOnClickListener {
            startActivityWithAnimation<SycnhLogin>(R.anim.screenslideright, R.anim.screen_slide_out_left)
        }

        _binding!!.btnSettings.setOnClickListener {
            val intent = Intent(this, SharedLoginActivity::class.java)
            intent.putExtra("activity", SETTINGS_ACTIVITY)
            startActivity(intent)
            overridePendingTransition(
                R.anim.screenslideleft, R.anim.screen_slide_out_right,
            );
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DISPATCH_ACTIVITY) {
            if(GlobalVariable.isDispatched) enablebutton()
//            if (resultCode == Activity.RESULT_OK) {
//
//            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(GlobalVariable.isDispatched){
            enablebutton()
        }else{
            initibuttondisable()
        }
    }

}