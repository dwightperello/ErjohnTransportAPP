package com.example.erjohnandroid.printer.printerUtils
import android.annotation.SuppressLint
import android.graphics.Bitmap
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.Random
import kotlin.experimental.inv

object BytesUtil {
    private const val TAG = "BytesUtil"
    private const val MATRIX_DATA_ROW = 384
    private const val BYTE_BIT = 8
    private const val BYTE_PER_LINE = 48
    private val random = Random()

    fun randomDotData(lines: Int): ByteArray {
        val printData = ByteArray(lines * BYTE_PER_LINE)
        for (i in 0 until lines) {
            val lineData = ByteArray(BYTE_PER_LINE)
            val randData = random.nextInt(BYTE_PER_LINE)
            lineData[randData] = 0x01
            System.arraycopy(lineData, 0, printData, i * BYTE_PER_LINE, BYTE_PER_LINE)
        }
        return printData
    }

    fun initBlackBlock(w: Int): ByteArray {
        val ww = w / 8
        val n = ww / 12
        val hh = n * 24
        val data = ByteArray(hh * ww)

        var k = 0
        for (i in 0 until n) {
            for (j in 0 until 24) {
                for (m in 0 until ww) {
                    data[k++] = if (m / 12 == i) 0xFF.toByte() else 0
                }
            }
        }

        return data
    }

    fun initBlackBlock(h: Int, w: Int): ByteArray {
        val hh = h
        val ww = w / 8
        val data = ByteArray(hh * ww)

        var k = 0
        for (i in 0 until hh) {
            for (j in 0 until ww) {
                data[k++] = 0xFF.toByte()
            }
        }

        return data
    }

    fun blackBlockData(lines: Int): ByteArray {
        val printData = ByteArray(lines * BYTE_PER_LINE)
        for (i in 0 until lines * BYTE_PER_LINE) {
            printData[i] = 0xFF.toByte()
        }
        return printData
    }

    fun initGrayBlock(h: Int, w: Int): ByteArray {
        val hh = h
        val ww = w / 8
        val data = ByteArray(hh * ww)

        var k = 0
        var m: Byte = 0xAA.toByte()
        for (i in 0 until hh) {
            m = m.inv()
            for (j in 0 until ww) {
                data[k++] = m
            }
        }

        return data
    }

    fun getBitmapFromData(pixels: IntArray, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun getLineBitmapFromData(size: Int, width: Int): Bitmap {
        val pixels = createLineData(size, width)
        return getBitmapFromData(pixels, width, size + 6)
    }

    fun getHexStringFromBytes(data: ByteArray?): String {
        if (data == null || data.isEmpty()) {
            return ""
        }
        val hexString = "0123456789ABCDEF"
        val size = data.size * 2
        val sb = StringBuilder(size)
        for (i in data.indices) {
            sb.append(hexString[data[i].toInt() shr 4 and 0x0F])
            sb.append(hexString[data[i].toInt() and 0x0F])
        }
        return sb.toString()
    }

    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    fun getBytesFromHexString(hexstring: String): ByteArray? {
        if (hexstring.isEmpty()) {
            return null
        }
        var hexstring = hexstring.replace(" ", "")
        hexstring = hexstring.toUpperCase()
        val size = hexstring.length / 2
        val hexarray = hexstring.toCharArray()
        val rv = ByteArray(size)
        var i = 0
        while (i < size) {
            val pos = i * 2
            rv[i] =
                (charToByte(hexarray[pos]).toInt() shl 4 or charToByte(hexarray[pos + 1]).toInt()).toByte()
            i++
        }
        return rv
    }

    private fun createLineData(size: Int, width: Int): IntArray {
        val pixels = IntArray(width * (size + 6))
        var k = 0
        for (j in 0 until 3) {
            for (i in 0 until width) {
                pixels[k++] = -0x1
            }
        }
        for (j in 0 until size) {
            for (i in 0 until width) {
                pixels[k++] = -0x1000000
            }
        }
        for (j in 0 until 3) {
            for (i in 0 until width) {
                pixels[k++] = -0x1
            }
        }
        return pixels
    }

    fun initLine1(w: Int, type: Int): ByteArray {
        val kk = arrayOf(
            byteArrayOf(0x00, 0x00, 0x7c, 0x7c, 0x7c, 0x00, 0x00),
            byteArrayOf(0x00, 0x00, 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0x00, 0x00),
            byteArrayOf(0x00, 0x44, 0x44, 0xff.toByte(), 0x44, 0x44, 0x00),
            byteArrayOf(0x00, 0x22, 0x55, 0x88.toByte(), 0x55, 0x22, 0x00),
            byteArrayOf(0x08, 0x08, 0x1c, 0x7f.toByte(), 0x1c, 0x08, 0x08),
            byteArrayOf(0x08, 0x14, 0x22, 0x41, 0x22, 0x14, 0x08),
            byteArrayOf(0x08, 0x14, 0x2a, 0x55, 0x2a, 0x14, 0x08),
            byteArrayOf(0x08, 0x1c, 0x3e, 0x7f.toByte(), 0x3e, 0x1c, 0x08),
            byteArrayOf(0x49, 0x22, 0x14, 0x49, 0x14, 0x22, 0x49),
            byteArrayOf(0x63.toByte(), 0x77.toByte(), 0x3e, 0x1c, 0x3e, 0x77.toByte(), 0x63.toByte()),
            byteArrayOf(0x70.toByte(), 0x20, 0xaf.toByte(), 0xaa.toByte(), 0xfa.toByte(), 0x02, 0x07),
            byteArrayOf(0xef.toByte(), 0x28, 0xee.toByte(), 0xaa.toByte(), 0xee.toByte(), 0x82.toByte(), 0xfe.toByte())
        )

        val ww = w / 8
        val data = ByteArray(13 * ww)

        var k = 0
        for (i in 0 until 3 * ww) {
            data[k++] = 0
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][0]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][1]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][2]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][3]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][4]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][5]
        }
        for (i in 0 until ww) {
            data[k++] = kk[type][6]
        }
        for (i in 0 until 3 * ww) {
            data[k++] = 0
        }
        return data
    }

    fun initLine2(w: Int): ByteArray {
        val ww = (w + 7) / 8
        val data = ByteArray(12 * ww + 8)
        data[0] = 0x1D
        data[1] = 0x76
        data[2] = 0x30
        data[3] = 0x00
        data[4] = ww.toByte()
        data[5] = (ww shr 8).toByte()
        data[6] = 12
        data[7] = 0
        var k = 8
        for (i in 0 until 5 * ww) {
            data[k++] = 0
        }
        for (i in 0 until ww) {
            data[k++] = 0x7f
        }
        for (i in 0 until ww) {
            data[k++] = 0x7f
        }
        for (i in 0 until 5 * ww) {
            data[k++] = 0
        }
        return data
    }

    fun byte2hex(buffer: ByteArray): String {
        val hexString = StringBuilder()
        for (aBuffer in buffer) {
            var temp = Integer.toHexString(aBuffer.toInt() and 0xFF)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            hexString.append(" $temp")
        }
        return hexString.toString()
    }
}
