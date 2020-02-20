package com.chess.cryptobot.model.response

interface CurrenciesListResponse {
    fun getCurrencies(): List<CurrenciesResponse>
}