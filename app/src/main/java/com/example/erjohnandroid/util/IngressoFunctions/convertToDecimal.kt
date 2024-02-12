package com.example.erjohnandroid.util.IngressoFunctions

import java.text.DecimalFormat

val convertDecimal:(String?)-> Double={
    var item= it?.toDouble()
    val decimalVat = DecimalFormat("#.00")
    var ans = decimalVat.format(item)
    ans.toDouble()

}