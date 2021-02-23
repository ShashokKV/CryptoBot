package com.chess.cryptobot.model.response.poloniex

import com.chess.cryptobot.model.response.CurrenciesResponse

class PoloniexCurrenciesResponse(override val currencyName: String?,
                                 override val isActive: Boolean?,
                                 override val fee: Double?) : CurrenciesResponse {

}