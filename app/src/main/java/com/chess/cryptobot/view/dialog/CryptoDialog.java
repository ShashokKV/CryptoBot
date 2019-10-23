package com.chess.cryptobot.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chess.cryptobot.R;

public abstract class CryptoDialog extends DialogFragment {
    private DialogListener dialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            dialogListener = (DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        EditText editText = new EditText(this.getContext());
        editText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        editText = enrichEditText(editText);

        TextView titleView = new TextView(this.getContext());
        titleView.setTextColor(getResources().getColor(R.color.colorSecondaryDark, null));
        titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        titleView.setTextSize(20);
        titleView = enrichTitle(titleView);

        AlertDialog alertDialog = builder.setCustomTitle(titleView)
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialogListener.onDialogPositiveClick(getInstance()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(getInstance()))
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorSecondaryDark, null));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorSecondaryDark, null));
        });

        return alertDialog;
    }

    abstract EditText enrichEditText(EditText editText);

    abstract TextView enrichTitle(TextView title);

    abstract CryptoDialog getInstance();
}