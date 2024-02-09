package com.example.erjohnandroid.util

import android.content.Context
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.erjohnandroid.R
import com.example.erjohnandroid.database.Model.FareAndKmdiff
import com.google.android.gms.common.internal.GmsLogger
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

val computePitxFare: (Int, Int,Double,String, Button) -> FareAndKmdiff = { destination,origin,basefare,passengertype, printBtn ->
    var total:Double=0.0
    val df = DecimalFormat("#.##")
    var discountedFare:Double?=0.0
    var kmDifference=0
    when (GlobalVariable.direction){
        "South" ->{
            kmDifference = destination - origin
            if (kmDifference<=0) printBtn.isVisible = false
            else printBtn.isVisible=true
            if(kmDifference<=10 && origin<10){
                total= if(passengertype in listOf("Senior", "Student", "PWD")){
                    discountedFare= basefare * GlobalVariable.discountAmount
                    basefare - discountedFare
                }else{
                    basefare
                } * 1 // always 1 quantity as required
            }
            else if(kmDifference >10 && origin <=10){
                val fare= kmDifference * GlobalVariable.specialexceedAmount
                if (passengertype in listOf("Senior", "Student", "PWD")){
                    val discount = fare * GlobalVariable.discountAmount
                    val amountafterDiscount= fare - discount
                    total = amountafterDiscount * 1 + 2 // 2 here is the ANOMALY of bus company and 1 is quantity
                    val roundedNumber = df.format(total).toDouble()
                    total= roundedNumber
                    //THIS IS STUPID.
                    //WHY STATIC INSTEAD OF ASKING ANOMALY FARE FORMULA FROM BUS CLIENT
                    if(total ==31.68) total --
                    else if(total == 46.52)total--
                    else if(total ==48.64)total --
                    else if(total == 50.76) total --
                    else if(origin==15 && destination==30) total --
                    else if(origin==3 && destination==27) total --
                    else if(origin==2 && destination==26) total --
                    else if(origin==1 && destination==25) total --
                    else if(origin==0 && destination==24) total --
                    else if(origin==3 && destination==18) total --
                    else if(origin==2 && destination==17) total --
                    else if(origin==1 && destination==16) total --
                    else if(origin==0 && destination==15) total --
                    else if(origin==10 && destination==25) total --
                    else if(origin==9 && destination==24) total --
                    else if(origin==8 && destination==23) total --
                    else if(origin==7 && destination==22) total --
                }else total = fare * 1 + 2
            }
            else if(kmDifference <= 5 && origin>=10){
                if (passengertype in listOf("Senior", "Student", "PWD"))total = 12.0 // 12 is static amount for discount, NO IDEA WHY
                else  total = 15.0.toDouble() * 1
            }
            else if (origin>= 10 && kmDifference>5 && kmDifference<=9){
                if (passengertype in listOf("Senior", "Student", "PWD")) total = 21.0
                else  total = 26.0.toDouble()
            }
            else if(origin>=10 && kmDifference>9){
                val basefare = kmDifference * GlobalVariable.specialexceedAmount
                if (passengertype in listOf("Senior", "Student", "PWD")){
                    val discount = basefare * GlobalVariable.discountAmount
                    val amountAfterDiscount = basefare - discount
                    total = amountAfterDiscount * 1 + 2
                    val roundedNumber = df.format(total).toDouble()
                    total= roundedNumber
                    if(total ==31.68) total --
                    else if(total == 46.51)total--
                    else if(total ==48.64)total --
                    else if(total == 50.76) total --
                    else if(origin==15 && destination==30) total --
                    else if(origin==16 && destination==29) total --
                    else if(origin==14 && destination==29) total --
                    else if(origin==13 && destination==29) total --
                    else if(origin==2 && destination==29) total ++
                    else if(origin==15 && destination==28) total --
                    else if(origin==13 && destination==28) total --
                    else if(origin==12 && destination==28) total --
                    else if(origin==1 && destination==28) total ++
                    else if(origin==14 && destination==27) total --
                    else if(origin==12 && destination==27) total --
                    else if(origin==13 && destination==26) total --
                    else if(origin==11 && destination==26) total --
                    else if(origin==12 && destination==25) total --
                    else if(origin==10 && destination==25) total = total-1
                    else if(origin==11 && destination==24) total --
                    else if(origin==9 && destination==24) total = total-1
                    else if(origin==8 && destination==23) total = total-1
                    else if(origin==7 && destination==23) total = total-1
                    else if(origin==7 && destination==22) total = total-1
                    else if(origin==9 && destination==22) total = total-1
                    else if(origin==8 && destination==21) total = total-1

                    else if(origin==11 && destination==27) total --
                    else if(origin==3 && destination==27) total --
                    else if(origin==0 && destination==27) total ++
                    else if(origin==2 && destination==26) total --
                    else if(origin==9 && destination==25) total --
                    else if(origin==1 && destination==25) total --
                    else if(origin==8 && destination==24) total --
                    else if(origin==0 && destination==24) total --
                    else if(origin==7 && destination==20) total --
                    else if(origin==3 && destination==19) total --
                    else if(origin==3 && destination==18) total --
                    else if(origin==2 && destination==18) total --
                    else if(origin==2 && destination==17) total --
                    else if(origin==1 && destination==17) total --
                    else if(origin==3 && destination==16) total --
                    else if(origin==1 && destination==16) total --
                    else if(origin==0 && destination==16) total --
                    else if(origin==2 && destination==15) total --
                    else if(origin==0 && destination==15) total --
                    else if(origin==1 && destination==14) total --
                    else if(origin==0 && destination==13) total --

                } else total = basefare * 1 + 2
            }
        }
        "North" -> {
            kmDifference = origin - destination
            if (kmDifference<=0) printBtn.isVisible = false
            else printBtn.isVisible=true
            if(kmDifference <= 5 && !(origin<= 10)){
                if (passengertype.equals("Senior") || passengertype.equals("Student") || passengertype.equals("PWD")) total= 12.0
                else total = 15.0
            }
            else if(kmDifference > 5 && kmDifference <9 && !(origin<=10)){
                if(passengertype in listOf("Senior", "Student", "PWD"))total = 21.0
                else total = 26.0
            }
            else if(kmDifference==9 && !(origin<=10)){
                if(passengertype in listOf("Senior", "Student", "PWD"))total = 21.0
                else total= 26.0
            }
            else if(kmDifference > 9){
                if(passengertype in listOf("Senior", "Student", "PWD")){
                    var getExceedAmount= kmDifference * GlobalVariable.specialexceedAmount
                    val discount =getExceedAmount * GlobalVariable.discountAmount
                    val amountAfterDiscount = getExceedAmount - discount
                    total= amountAfterDiscount
                    total= total * 1 + 2
                    val roundedNumber = df.format(total).toDouble()
                    total = roundedNumber
                    if(total ==31.68) total --
                    else if(total == 46.52)total--
                    else if(total ==48.64)total --
                    else if(total == 50.76) total --
                    else if(origin==30 && destination==15) total --
                    else if(origin==29 && destination==16) total --
                    else if(origin==29 && destination==14) total --
                    else if(origin==29 && destination==13) total --
                    else if(origin==29 && destination==2) total ++
                    else if(origin==28 && destination==15) total --
                    else if(origin==28 && destination==13) total --
                    else if(origin==28 && destination==12) total --
                    else if(origin==28 && destination==1) total ++
                    else if(origin==27 && destination==14) total --
                    else if(origin==27 && destination==12) total --
                    else if(origin==26 && destination==13) total --
                    else if(origin==26 && destination==11) total --
                    else if(origin==25 && destination==12) total --
                    else if(origin==25 && destination==10) total --
                    else if(origin==24 && destination==11) total --
                    else if(origin==24 && destination==9) total --
                    else if(origin==23 && destination==8) total --
                    else if(origin==23 && destination==7) total --
                    else if(origin==22 && destination==7) total --
                    else if(origin==22 && destination==9) total --
                    else if(origin==21 && destination==8) total --
                    else if(origin==27 && destination==11) total --
                    else if(origin==27 && destination==3) total --
                    else if(origin==27 && destination==0) total ++
                    else if(origin==26 && destination==2) total --
                    else if(origin==25 && destination==9) total --
                    else if(origin==25 && destination==1) total --
                    else if(origin==25 && destination==1) total --
                    else if(origin==24 && destination==8) total --
                    else if(origin==24 && destination==0) total --
                    else if(origin==20 && destination==7) total --
                    else if(origin==19 && destination==3) total --
                    else if(origin==18 && destination==3) total --
                    else if(origin==18 && destination==2) total --
                    else if(origin==17 && destination==2) total --
                    else if(origin==17 && destination==1) total --
                    else if(origin==16 && destination==3) total --
                    else if(origin==16 && destination==1) total --
                    else if(origin==16 && destination==0) total --
                    else if(origin==15 && destination==2) total --
                    else if(origin==15 && destination==0) total --
                    else if(origin==14 && destination==1) total --
                    else if(origin==13 && destination==0) total --
                }
                else{
                   val getExceedAmount = kmDifference * GlobalVariable.specialexceedAmount
                    total = getExceedAmount
                    total = total * 1 +2
                }

            }
            else if(origin<=10 && kmDifference<10){
                if (passengertype.equals("Senior") || passengertype.equals("Student") || passengertype.equals("PWD"))
                {
                    //discount = (discountamount / 100) * fare
                   val discount = GlobalVariable.basefair * GlobalVariable.discountAmount
                   val amountafterdiscount = GlobalVariable.basefair - discount
                    total = amountafterdiscount * 1
                } else {
                    total=30.0
                }
            }
        }

    }
    FareAndKmdiff(total,kmDifference)
}