package com.example.erjohnandroid.presenter.Activity.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.erjohnandroid.R
import com.example.erjohnandroid.databinding.ActivityLoginBinding
import com.example.erjohnandroid.databinding.ActivitySycnhLoginBinding
import com.example.erjohnandroid.domain.model.request.request_login
import com.example.erjohnandroid.domain.model.response.response_login
import com.example.erjohnandroid.presenter.viewmodel.networkViewModel
import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.ResultState
import com.example.erjohnandroid.util.showCustomToast
import com.example.erjohnandroid.util.startActivityWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SycnhLogin : BasedActivity() {
    lateinit var _binding:ActivitySycnhLoginBinding
    private val networkViewModel: networkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySycnhLoginBinding.inflate(layoutInflater)
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
           onBackPressed()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        networkViewModel.login.observe(this, Observer {
                state-> ProcessLogin(state)
        })
    }

    private fun ProcessLogin(state: ResultState<response_login>?){
        when(state){
            is ResultState.Loading ->{
                showCustomProgressDialog()
            }
            is ResultState.Success->{

                GlobalVariable.token= state.data.token
                hideProgressDialog()

               // GlobalVariable.saveLogreport("Login for synching success")
                startActivityWithAnimation<SynchActivity>(R.anim.screenslideright, R.anim.screen_slide_out_left)
                finish()
            }
            is ResultState.Error->{
                hideProgressDialog()
                Toast(this).showCustomToast(state.exception.toString(),this)

              // GlobalVariable.saveLogreport("error login, ${state.exception.message} ")
            }
            else -> {}
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}