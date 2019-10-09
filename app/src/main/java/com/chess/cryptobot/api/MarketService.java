package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.BalanceResponse;

import java.util.Map;

public interface MarketService {

    BalanceResponse getBalance(Map<String, String> options, Map<String, String> headers);
}
