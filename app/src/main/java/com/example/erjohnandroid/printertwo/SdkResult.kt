package com.example.erjohnandroid.printertwo
object SdkResult {
    const val SDK_OK = 0
    const val SDK_BASE_ERR = -1000
    const val SDK_SENT_ERR = -1001
    const val SDK_PARAM_ERR = -1002
    const val SDK_TIMEOUT = -1003
    const val SDK_RECV_ERR = -1004
    const val SDK_UNKNOWN_ERR = -1005
    const val SDK_CMD_ERR = -1006
    const val SDK_UNKNOWN_CMD = -1015
    const val SDK_FEATURE_NOT_SUPPORT = -1099

    const val DEVICE_NOT_CONNECT = -1100
    const val DEVICE_DISCONNECT = -1101
    const val DEVICE_CONNECTED = -1102
    const val DEVICE_CONN_ERR = -1103
    const val DEVICE_NOT_SUPPORT = -1104
    const val DEVICE_NOT_FOUND = -1105
    const val DEVICE_OPEN_ERR = -1106
    const val DEVICE_NO_PERMISSION = -1107

    const val BT_NOT_SUPPORT = -1108
    const val BT_NOT_OPEN = -1109
    const val BT_NO_LOCATION = -1110
    const val BT_NO_BONDED_DEVICE = -1111
    const val BT_SCAN_TIMEOUT = -1112
    const val BT_SCAN_ERR = -1113
    const val BT_SCAN_STOP = -1114

    const val PRN_BASE_ERR = -1200
    const val PRN_COVER_OPEN = PRN_BASE_ERR - 1
    const val PRN_PARAM_ERR = PRN_BASE_ERR - 2
    const val PRN_NO_PAPER = PRN_BASE_ERR - 3
    const val PRN_OVERHEAT = PRN_BASE_ERR - 4
    const val PRN_UNKNOWN_ERR = PRN_BASE_ERR - 5
    const val PRN_PRINTING = PRN_BASE_ERR - 6
    const val PRN_NO_NFC = PRN_BASE_ERR - 7
    const val PRN_NFC_NO_PAPER = PRN_BASE_ERR - 8
    const val PRN_LOW_BATTERY = PRN_BASE_ERR - 9
    const val PRN_UNKNOWN_CMD = PRN_BASE_ERR - 15
    const val PRN_LBL_LOCATE_ERR = PRN_BASE_ERR - 90
    const val PRN_LBL_DETECT_ERR = PRN_BASE_ERR - 91
    const val PRN_LBL_NO_DETECT = PRN_BASE_ERR - 92

    const val FW_BASE_ERR = -1300
    const val FW_BIN_ERR = FW_BASE_ERR - 1
    const val FW_CRC_ERR = FW_BASE_ERR - 2
}
