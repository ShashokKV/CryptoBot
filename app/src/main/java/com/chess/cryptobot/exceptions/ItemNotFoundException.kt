package com.chess.cryptobot.exceptions

class ItemNotFoundException(message: String) : Exception("$message not found in adapter items")