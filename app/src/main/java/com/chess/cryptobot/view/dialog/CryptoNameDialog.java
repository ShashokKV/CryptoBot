package com.chess.cryptobot.view.dialog;

import android.widget.EditText;
import android.widget.TextView;

import com.chess.cryptobot.R;

public class CryptoNameDialog extends CryptoDialog {

    @Override
    EditText enrichEditText(EditText editText) {
        editText.setId(R.id.name_dialog_edit_text);
        editText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        return editText;
    }

    @Override
    TextView enrichTitle(TextView title) {
        title.setId(R.id.name_dialog_title);
        title.setText(getString(R.string.coin_name_dialog));
        return title;
    }

    @Override
    CryptoDialog getInstance() {
        return CryptoNameDialog.this;
    }
}