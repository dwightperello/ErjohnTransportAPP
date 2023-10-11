package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.CompanyRolesTable
import com.example.erjohnandroid.database.Model.EmployeesTable
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityMainBinding
import com.example.erjohnandroid.databinding.ActivitySharedLoginBinding
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.startActivityWithAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedLoginActivity : AppCompatActivity() {
    lateinit var _binding:ActivitySharedLoginBinding
    private val dbViewmodel: RoomViewModel by viewModels()

    private var activity:Int?= null
    var pin:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySharedLoginBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        computeWindowSizeClasses()
        activity = intent.getIntExtra("activity",0)

        _binding.btnPin.setOnClickListener {
            pin = _binding.etPin.text.toString()
            dbViewmodel.selectEmployee(pin!!.toInt())
        }

    }



    override fun onStart() {
        super.onStart()
        dbViewmodel.selectemployees?.observe(this, Observer {
            state->ProcessRoles(state)
        })
    }
    val DISPATCH_ACTIVITY = 1
    private fun ProcessRoles(state: EmployeesTable?){
        try {
            if( state!=null){
                if(state.companyRolesId==4){
                    if(GlobalVariable.isDispatched)return
                    GlobalVariable.employeeName= state.name +" "+ state.lastName
                   // startActivityWithAnimation<DispatchActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                    val intent = Intent(this, DispatchActivity::class.java)
                    startActivityForResult(intent,DISPATCH_ACTIVITY)
                    overridePendingTransition(
                        R.anim.screenslideright, R.anim.screen_slide_out_left
                    );
                    finish()
                }
                else if(state.companyRolesId==3){
                    if(!GlobalVariable.isDispatched){
                        Toast.makeText(this,"Please Dispatch", Toast.LENGTH_LONG).show()
                        return
                    }
                    if (activity!!.equals(9)){
                        GlobalVariable.inspectorname=state.name + " " +state.lastName
                        startActivityWithAnimation<InspectionActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                        finish()
                    }

                }
                else if(state.companyRolesId==5){
                    if(!GlobalVariable.isDispatched){
                        Toast.makeText(this,"Please Dispatch", Toast.LENGTH_LONG).show()
                        return
                    }

                   if(activity!!.equals(4)){
                       GlobalVariable.cashiername=state.name + " " +state.lastName
                       val intent = Intent(this, IngressoActivity::class.java)
                       startActivityForResult(intent,3)
                       activity=null
                       finish()
                       overridePendingTransition(
                           R.anim.screenslideright, R.anim.screen_slide_out_left
                       );
                       //startActivityWithAnimation<IngressoActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)

                   }else if(activity!!.equals(5))
                   {
                       GlobalVariable.cashiername=state.name + " " +state.lastName
                       startActivityWithAnimation<PartialRemitActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                       finish()
                   }


                }


                else Toast.makeText(this,"LOGIN USING CORRECT PIN",Toast.LENGTH_LONG).show()

            }else{

                if(pin.equals("30300")){
                    startActivityWithAnimation<SettingsActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                    finish()
                }else{
                    _binding.etPin.setText("")
                    Toast.makeText(this,"NO DATA FOUND", Toast.LENGTH_LONG).show()
                }

            }
        }catch (e:java.lang.Exception){
            Toast.makeText(this,"NO EMPLOYEE",Toast.LENGTH_LONG).show()
        }

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
                    params.width = 600
                    layout.layoutParams = params
                }
                else -> {  val params: ViewGroup.LayoutParams = layout.layoutParams
                    params.width = 600

                    layout.layoutParams = params}
            }
        }catch (e:java.lang.Exception){
            Log.d("ta",e.localizedMessage)
        }
    }



}