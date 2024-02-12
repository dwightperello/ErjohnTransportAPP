package com.example.erjohnandroid.util.IngressoFunctions

import android.widget.TextView
import com.example.erjohnandroid.database.Model.FareAndKmdiff
import com.example.erjohnandroid.util.IngressoFunctions.model.ComputeCommisionEntity
import java.text.DecimalFormat

val computeCommissions:(Double)-> ComputeCommisionEntity={totalamount ->
    val decimalVat = DecimalFormat("#.00")
    val driver= totalamount * 0.09
    val conductor=totalamount* 0.07

    val amount= driver + conductor
    val sum_total_amount = decimalVat.format(amount)

    val drivercommision = decimalVat.format(driver)

    val conductorcommision=decimalVat.format(conductor)

    val netcollection = decimalVat.format(totalamount)

    ComputeCommisionEntity(
        totalcommision = sum_total_amount.toDouble(),
        drivercommision = drivercommision.toDouble(),
        conductorcommision =conductorcommision.toDouble()

    )
}