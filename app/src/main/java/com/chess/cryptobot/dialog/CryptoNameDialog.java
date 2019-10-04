package com.chess.cryptobot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.chess.cryptobot.R;

public class CryptoNameDialog extends DialogFragment {


    public interface CoinNameDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    CoinNameDialogListener coinNameDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            coinNameDialogListener = (CoinNameDialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
            + " must implement CoinNameDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        final EditText editText = new EditText(this.getContext());
        editText.setId(R.id.name_dialog_edit_text);
        editText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.setTitle(R.string.coin_name_dialog)
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, which) -> coinNameDialogListener.onDialogPositiveClick(CryptoNameDialog.this))
                .setNegativeButton(R.string.cancel, (dialog, which) -> coinNameDialogListener.onDialogNegativeClick(CryptoNameDialog.this))
                .create();
    }
}
