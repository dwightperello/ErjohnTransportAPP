package net.nyx.printerservice.print

import android.os.Parcel
import android.os.Parcelable

class PrintTextFormat() : Parcelable {
    var textSize: Int = 24 // 字符串大小, px
    var underline: Boolean = false // 下划线
    var textScaleX: Float = 1.0f // 字体的横向缩放 参数值0-1表示字体缩小 1表示正常 大于1表示放大
    var textScaleY: Float = 1.0f
    var letterSpacing: Float = 0.0f // 列间距
    var lineSpacing: Float = 0.0f // 行间距
    var topPadding: Int = 0
    var leftPadding: Int = 0
    var ali: Int = 0 // 对齐方式, 默认0. 0--LEFT, 1--CENTER, 2--RIGHT
    var style: Int = 0 // 字体样式, 默认0. 0--NORMAL, 1--BOLD, 2--ITALIC, 3--BOLD_ITALIC
    var font: Int = 0 // 字体, 默认0. 0--DEFAULT, 1--DEFAULT_BOLD, 2--SANS_SERIF, 3--SERIF, 4--MONOSPACE, 5--CUSTOM
    var path: String? = null // 自定义字库文件路径

    constructor(parcel: Parcel) : this() {
        textSize = parcel.readInt()
        underline = parcel.readByte() != 0.toByte()
        textScaleX = parcel.readFloat()
        textScaleY = parcel.readFloat()
        letterSpacing = parcel.readFloat()
        lineSpacing = parcel.readFloat()
        topPadding = parcel.readInt()
        leftPadding = parcel.readInt()
        ali = parcel.readInt()
        style = parcel.readInt()
        font = parcel.readInt()
        path = parcel.readString()
    }

    fun PrintTextFormat() {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(textSize)
        parcel.writeByte(if (underline) 1 else 0)
        parcel.writeFloat(textScaleX)
        parcel.writeFloat(textScaleY)
        parcel.writeFloat(letterSpacing)
        parcel.writeFloat(lineSpacing)
        parcel.writeInt(topPadding)
        parcel.writeInt(leftPadding)
        parcel.writeInt(ali)
        parcel.writeInt(style)
        parcel.writeInt(font)
        parcel.writeString(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrintTextFormat> {
        override fun createFromParcel(parcel: Parcel): PrintTextFormat {
            return PrintTextFormat(parcel)
        }

        override fun newArray(size: Int): Array<PrintTextFormat?> {
            return arrayOfNulls(size)
        }
    }
}