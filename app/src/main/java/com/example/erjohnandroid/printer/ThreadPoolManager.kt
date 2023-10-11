package com.example.erjohnandroid.printer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadPoolManager private constructor() {
    private val service: ExecutorService

    init {
        val num = Runtime.getRuntime().availableProcessors() * 20
        service = Executors.newFixedThreadPool(num)
    }

    companion object {
        private val manager = ThreadPoolManager()

        @JvmStatic
        fun getInstance(): ThreadPoolManager {
            return manager
        }
    }

    fun executeTask(runnable: Runnable) {
        service.execute(runnable)
    }
}
