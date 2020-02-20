package com.chess.cryptobot.model.response

interface CurrenciesResponse {
    val currencyName: String?
    val isActive: Boolean?
    val fee: Double?
}