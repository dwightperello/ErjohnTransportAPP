package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.LogReport
import com.example.erjohnandroid.database.viewmodel.RoomViewModel
import com.example.erjohnandroid.databinding.ActivityLoginBinding
import com.example.erjohnandroid.databinding.ActivityMainBinding
import com.example.erjohnandroid.domain.model.request.request_login
import com.example.erjohnandroid.domain.model.response.response_login
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.showCustomToast
import com.example.erjohnandroid.util.startActivityWithAnimation
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : BasedActivity() {
    private val networkViewModel:networkViewModel by viewModels()
    private  var _binding:ActivityLoginBinding?=null
    private val dbViewmodel: RoomViewModel by viewModels()
    //private val networkViewModel: networkViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        _binding!!.btnLogin.setOnClickListener {
            val method = request_login(
                email = _binding!!.txtUsername.text.toString(),
                password = _binding!!.txtPassword.text.toString()
            )
            networkViewModel.login(method)


        }

        _binding!!.btnExit.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onStart() {
        super.onStart()
        networkViewModel.login.observe(this, Observer {
                state-> ProcessLogin(state)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    private fun getdate():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
    private fun ProcessLogin(state: ResultState<response_login>?){
        when(state){
            is ResultState.Loading ->{
                showCustomProgressDialog()
            }
            is ResultState.Success->{
                val formattedDateTime = getdate()


                GlobalVariable.token= state.data.token
                hideProgressDialog()
                startActivityWithAnimation<GetSynchingActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
            }
            is ResultState.Error->{
                hideProgressDialog()
                Toast(this).showCustomToast(state.exception.toString(),this)
                val formattedDateTime = getdate()


            }
            else -> {}
        }

    }
}