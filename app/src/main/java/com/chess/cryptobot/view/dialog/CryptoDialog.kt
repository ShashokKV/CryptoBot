package com.chess.cryptobot.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chess.cryptobot.R

abstract class CryptoDialog : DialogFragment() {
    private var dialogListener: DialogListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = try {
            context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement DialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(this.context)
        var editText = EditText(this.context)
        editText.setTextColor(resources.getColor(R.color.colorAccent, null))
        editText = enrichEditText(editText)
        var titleView = TextView(this.context)
        titleView.setTextColor(resources.getColor(R.color.colorSecondaryDark, null))
        titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE)
        titleView.textSize = 20f
        titleView = enrichTitle(titleView)
        val alertDialog = builder.setCustomTitle(titleView)
                .setView(editText)
                .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int -> dialogListener!!.onDialogPositiveClick(instance) }
                .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> dialogListener!!.onDialogNegativeClick(instance) }
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorSecondaryDark, null))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorSecondaryDark, null))
        }
        return alertDialog
    }

    abstract fun enrichEditText(editText: EditText): EditText
    abstract fun enrichTitle(title: TextView): TextView
    abstract val instance: CryptoDialog?
}