package com.chess.cryptobot.view.dialog;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;

public class CryptoNameDialog extends CryptoDialog {
    private final BalanceHolder balanceHolder;

    public CryptoNameDialog(BalanceHolder balanceHolder) {
        this.balanceHolder = balanceHolder;
    }

    @Override
    EditText enrichEditText(EditText editText) {
        editText.setId(R.id.name_dialog_edit_text);
        editText.setTextColor(getResources().getColor(R.color.colorAccent, null));
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
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

    public BalanceHolder getBalanceHolder() {
        return balanceHolder;
    }
}