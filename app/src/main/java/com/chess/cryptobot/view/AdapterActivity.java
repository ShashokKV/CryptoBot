package com.chess.cryptobot.view;

import com.chess.cryptobot.model.ViewItem;

public interface AdapterActivity {

    void addItem(ViewItem item);

    void updateItem(ViewItem item);

    void deleteItemByPosition(int position);

    String itemNameByPosition(int position);

    void makeToast(String message);
}
