package com.chess.cryptobot.market.sockets.bittrex

class SocketResponse {
    var Success: Boolean? = null
    var ErrorCode: String? = null

    fun SocketResponse(success: Boolean?, error: String?) {
        Success = success
        ErrorCode = error
    }
}