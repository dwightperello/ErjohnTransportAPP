package com.example.erjohnandroid.printertwo.aop
import android.util.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
class SingleClickAspect {

    private val TAG = "SingleClickAspect"

    /**
     * 最近一次点击的时间
     */
    private var mLastTime: Long = 0

    /**
     * 最近一次点击的标记
     */
    private var mLastTag: String? = null

    /**
     * 方法切入点
     */
    @Pointcut("execution(@net.nyx.printerclient.aop.SingleClick * *(..))")
    fun method() {
    }

    /**
     * 在连接点进行方法替换
     */
    @Around("method() && @annotation(singleClick)")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint, singleClick: SingleClick) {
        Log.e(TAG, "SingleClick")
        val codeSignature = joinPoint.signature as Signature
        // 方法所在类
        val className = codeSignature.declaringType.name
        // 方法名
        val methodName = codeSignature.name
        // 构建方法 TAG
        val builder = StringBuilder("$className.$methodName")
        builder.append("(")
        val parameterValues = joinPoint.args
        for (i in parameterValues.indices) {
            val arg = parameterValues[i]
            if (i == 0) {
                builder.append(arg)
            } else {
                builder.append(", ").append(arg)
            }
        }
        builder.append(")")

        val tag = builder.toString()
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - mLastTime < singleClick.value && tag == mLastTag) {
            Log.d(TAG, String.format("%s 毫秒内发生快速点击：%s", singleClick.value, tag))
            return
        }
        mLastTime = currentTimeMillis
        mLastTag = tag
        // 执行原方法
        joinPoint.proceed()
    }
}
