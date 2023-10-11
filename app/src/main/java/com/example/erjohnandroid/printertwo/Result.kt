package com.example.erjohnandroid.printertwo

import com.example.erjohnandroid.R

object Result {

    fun msg(code: Int): String {
        val s: String
        when (code) {
            SdkResult.SDK_SENT_ERR -> s = Utils.getApp().getString(R.string.result_sent_err)
            SdkResult.SDK_RECV_ERR -> s = Utils.getApp().getString(R.string.result_recv_err)
            SdkResult.SDK_TIMEOUT -> s = Utils.getApp().getString(R.string.result_timeout)
            SdkResult.SDK_PARAM_ERR -> s = Utils.getApp().getString(R.string.result_params_err)
            SdkResult.SDK_UNKNOWN_ERR -> s = Utils.getApp().getString(R.string.result_unknown_err)
            SdkResult.DEVICE_NOT_CONNECT -> s = Utils.getApp().getString(R.string.result_device_not_conn)
            SdkResult.DEVICE_DISCONNECT -> s = Utils.getApp().getString(R.string.result_device_disconnect)
            SdkResult.DEVICE_CONN_ERR -> s = Utils.getApp().getString(R.string.result_conn_err)
            SdkResult.DEVICE_CONNECTED -> s = Utils.getApp().getString(R.string.result_device_connected)
            SdkResult.DEVICE_NOT_SUPPORT -> s = Utils.getApp().getString(R.string.result_device_not_support)
            SdkResult.DEVICE_NOT_FOUND -> s = Utils.getApp().getString(R.string.result_device_not_found)
            SdkResult.DEVICE_OPEN_ERR -> s = Utils.getApp().getString(R.string.result_device_open_err)
            SdkResult.DEVICE_NO_PERMISSION -> s = Utils.getApp().getString(R.string.result_device_no_permission)
            SdkResult.BT_NOT_OPEN -> s = Utils.getApp().getString(R.string.result_bt_not_open)
            SdkResult.BT_NO_LOCATION -> s = Utils.getApp().getString(R.string.result_bt_no_location)
            SdkResult.BT_NO_BONDED_DEVICE -> s = Utils.getApp().getString(R.string.result_bt_no_bonded)
            SdkResult.BT_SCAN_TIMEOUT -> s = Utils.getApp().getString(R.string.result_bt_scan_timeout)
            SdkResult.BT_SCAN_ERR -> s = Utils.getApp().getString(R.string.result_bt_scan_err)
            SdkResult.BT_SCAN_STOP -> s = Utils.getApp().getString(R.string.result_bt_scan_stop)
            SdkResult.PRN_COVER_OPEN -> s = Utils.getApp().getString(R.string.result_prn_cover_open)
            SdkResult.PRN_PARAM_ERR -> s = Utils.getApp().getString(R.string.result_prn_params_err)
            SdkResult.PRN_NO_PAPER -> s = Utils.getApp().getString(R.string.result_prn_no_paper)
            SdkResult.PRN_OVERHEAT -> s = Utils.getApp().getString(R.string.result_prn_overheat)
            SdkResult.PRN_UNKNOWN_ERR -> s = Utils.getApp().getString(R.string.result_prn_unknown_err)
            SdkResult.PRN_PRINTING -> s = Utils.getApp().getString(R.string.result_prn_printing)
            SdkResult.PRN_NO_NFC -> s = Utils.getApp().getString(R.string.result_prn_no_nfc)
            SdkResult.PRN_NFC_NO_PAPER -> s = Utils.getApp().getString(R.string.result_nfc_no_paper)
            SdkResult.PRN_LOW_BATTERY -> s = Utils.getApp().getString(R.string.result_prn_low_battery)
            SdkResult.PRN_LBL_LOCATE_ERR -> s = Utils.getApp().getString(R.string.result_prn_locate_err)
            SdkResult.PRN_LBL_DETECT_ERR -> s = Utils.getApp().getString(R.string.result_prn_detect_err)
            SdkResult.PRN_LBL_NO_DETECT -> s = Utils.getApp().getString(R.string.result_prn_no_detect)
            SdkResult.PRN_UNKNOWN_CMD, SdkResult.SDK_UNKNOWN_CMD -> s = Utils.getApp().getString(R.string.result_unknown_cmd)
            else -> s = code.toString()
        }
        return s
    }
}
