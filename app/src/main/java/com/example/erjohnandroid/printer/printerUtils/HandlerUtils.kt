package com.example.erjohnandroid.printer.printerUtils
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.SoftReference

object HandlerUtils {

    private const val serialVersionUID = 0L

    /**
     * 在使用handler的地方继承此接口，然后把实例化的引用给实例化的handler
     */
    interface IHandlerIntent {
        fun handlerIntent(message: Message?)
    }

    class MyHandler : Handler {
        private var owner: SoftReference<IHandlerIntent>

        constructor(t: IHandlerIntent) {
            owner = SoftReference(t)
        }

        constructor(looper: Looper?, t: IHandlerIntent) : super(looper!!) {
            owner = SoftReference(t)
        }

        override fun handleMessage(msg: Message) {
            val t = owner.get()
            t?.handlerIntent(msg)
        }
    }
}
