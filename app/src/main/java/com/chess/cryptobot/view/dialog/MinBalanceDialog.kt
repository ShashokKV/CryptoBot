package com.chess.cryptobot.view.dialog

import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalanceHolder
import java.util.*

class MinBalanceDialog(val balanceHolder: BalanceHolder, val coinName: String, private val minBalance: Double) : CryptoDialog() {

    override fun enrichEditText(editText: EditText): EditText {
        editText.id = R.id.min_balance_edit_text
        editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        editText.setText(String.format(Locale.US, "%.8f", minBalance))
        return editText
    }

    override fun enrichTitle(title: TextView): TextView {
        title.id = R.id.min_balance_title
        title.text = getString(R.string.set_min_balance)
        return title
    }

    override val instance: CryptoDialog
        get() = this@MinBalanceDialog

}