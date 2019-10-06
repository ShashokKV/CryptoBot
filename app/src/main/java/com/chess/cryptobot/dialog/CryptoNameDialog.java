package com.chess.cryptobot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chess.cryptobot.R;

public class CryptoNameDialog extends DialogFragment {


    public interface CoinNameDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private CoinNameDialogListener coinNameDialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//, R.style.WhiteAlertDialog);

        return builder.setTitle(R.string.coin_name_dialog)
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, which) -> coinNameDialogListener.onDialogPositiveClick(CryptoNameDialog.this))
                .setNegativeButton(R.string.cancel, (dialog, which) -> coinNameDialogListener.onDialogNegativeClick(CryptoNameDialog.this))
                .create();
    }
}
