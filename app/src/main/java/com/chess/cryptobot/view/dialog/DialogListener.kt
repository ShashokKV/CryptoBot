package com.chess.cryptobot.view.dialog

interface DialogListener {
    fun onDialogPositiveClick(dialog: CryptoDialog?)
    fun onDialogNegativeClick(dialog: CryptoDialog?)
}