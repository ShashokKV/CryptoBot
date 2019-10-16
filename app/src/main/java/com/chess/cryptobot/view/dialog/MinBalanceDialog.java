package com.chess.cryptobot.view.dialog;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import com.chess.cryptobot.R;

public class MinBalanceDialog extends CryptoDialog {
private String coinName;
private Double minBalance;

    public MinBalanceDialog(String coinName, Double minBalance) {
        this.coinName = coinName;
        this.minBalance = minBalance;
    }

    @Override
    EditText enrichEditText(EditText editText) {
        editText.setId(R.id.min_balance_edit_text);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setText(String.valueOf(minBalance));
        return editText;
    }

    @Override
    TextView enrichTitle(TextView title) {
        title.setId(R.id.min_balance_title);
        title.setText(getString(R.string.set_min_balance).concat(coinName));
        return title;
    }

    @Override
    CryptoDialog getInstance() {
        return MinBalanceDialog.this;
    }

    public String getCoinName() {
        return coinName;
    }
}
