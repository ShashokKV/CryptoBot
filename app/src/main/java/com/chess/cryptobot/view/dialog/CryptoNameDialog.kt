package com.chess.cryptobot.view.dialog

import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalanceHolder

class CryptoNameDialog(val balanceHolder: BalanceHolder) : CryptoDialog() {

    override fun enrichEditText(editText: EditText): EditText {
        editText.id = R.id.name_dialog_edit_text
        editText.setTextColor(resources.getColor(R.color.colorAccent, null))
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        return editText
    }

    override fun enrichTitle(title: TextView): TextView {
        title.id = R.id.name_dialog_title
        title.text = getString(R.string.coin_name_dialog)
        return title
    }

    override val instance: CryptoDialog
        get() = this@CryptoNameDialog

}