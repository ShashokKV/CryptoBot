package com.chess.cryptobot.view.dialog

import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalanceHolder

class CryptoNameDialog(val balanceHolder: BalanceHolder) : CryptoDialog() {

    override fun enrichEditText(editText: EditText): EditText {
        val adapter = ArrayAdapter(this.requireContext(),
                android.R.layout.simple_dropdown_item_1line, balanceHolder.availableCoins.toTypedArray())
        val autoCompleteTextView = AutoCompleteTextView(this.context)
        autoCompleteTextView.setTextColor(editText.currentTextColor)
        autoCompleteTextView.id = R.id.name_dialog_edit_text
        autoCompleteTextView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
        return autoCompleteTextView
    }

    override fun enrichTitle(title: TextView): TextView {
        title.id = R.id.name_dialog_title
        title.text = getString(R.string.coin_name_dialog)
        return title
    }

    override val instance: CryptoDialog
        get() = this@CryptoNameDialog

}