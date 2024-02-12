package com.example.erjohnandroid.util.IngressoFunctions

import com.example.erjohnandroid.util.GlobalVariable
import com.example.erjohnandroid.util.IngressoFunctions.model.Bonus
import java.text.DecimalFormat

val computeBonus:(Double) -> Bonus ={total->
    val decimalformat = DecimalFormat("#.00")
    val bonus = 100.00

    val aboveThresholdAmount = (total - 14000)
    val bonuscount= (aboveThresholdAmount/1000).toInt()
    val driverbonus= bonus +(bonuscount *50)
    val conductorbonus=bonus +(bonuscount * 50)

    val format_driver_bonus = decimalformat.format(driverbonus)
    val format_conductor_bonus = decimalformat.format(conductorbonus)

    var totalbonus = format_driver_bonus.toDouble() + format_conductor_bonus.toDouble()
    totalbonus= decimalformat.format(totalbonus).toDouble()
    GlobalVariable.bonus=totalbonus

    Bonus(
        driverbonus= format_driver_bonus.toDouble(),
        conductorbonus = format_conductor_bonus.toDouble(),
        totalBonus = totalbonus.toDouble()
    )
}