package com.example.erjohnandroid.printer
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

object MemInfo {

    // Get available memory
    fun getmem_UNUSED(mContext: Context): Long {
        val am = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem / 1048576
    }

    // Get total memory
    fun getmem_TOTAL(): Long {
        val path = "/proc/meminfo"
        var content: String? = null
        var br: BufferedReader? = null

        try {
            br = BufferedReader(FileReader(path), 8)
            var line: String?
            if (br.readLine().also { line = it } != null) {
                content = line
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val begin = content?.indexOf(':') ?: -1
        val end = content?.indexOf('k') ?: -1

        content = content?.substring(begin + 1, end)?.trim()
        return content?.toLongOrNull() ?: 0
    }

    // Optimize bitmap recycling to reduce memory usage
    fun bitmapRecycle(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}
