package com.chess.cryptobot.view.notification

import java.util.concurrent.atomic.AtomicInteger

object NotificationID {
    private val c = AtomicInteger(1)
    val id: Int
        get() = c.incrementAndGet()
}