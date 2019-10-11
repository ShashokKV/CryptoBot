package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.BittrexException;
import com.chess.cryptobot.exceptions.LivecoinException;

public interface Market {

    Double getAmount(String coinName) throws BittrexException, LivecoinException;
}
