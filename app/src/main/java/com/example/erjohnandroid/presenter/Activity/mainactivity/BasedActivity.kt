package com.example.erjohnandroid.presenter.Activity.mainactivity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import com.example.erjohnandroid.R
import com.example.erjohnandroid.util.GlobalVariable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BasedActivity : AppCompatActivity(){

    private var mProgressDialog: Dialog? = null


    fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }
    fun hideProgressDialog() {
        mProgressDialog?.let {
            it.dismiss()
        }
    }
}