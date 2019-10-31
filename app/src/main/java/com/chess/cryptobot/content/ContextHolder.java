package com.chess.cryptobot.content;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.chess.cryptobot.exceptions.ItemNotFoundException;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.view.MainActivity;
import com.chess.cryptobot.view.MainFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ContextHolder {
    private Fragment fr;
    private List<ViewItem> viewItems;
    private Preferences prefs;

    public ContextHolder(Fragment fr) {
        this.fr = fr;
        initFields();
        prefs = initPrefs(fr.getContext());
        Set<String> itemsSet = prefs.getItems();
        initViewItems(itemsSet);
    }

    public void initFields() {
        viewItems = new ArrayList<>();
    }

    public abstract Preferences initPrefs(Context context);

    public abstract void initViewItems(Set<String> itemsSet);

    public synchronized void add(ViewItem viewItem) {
        addItemToList(viewItem);
        getMainFragment().addItem();
        Preferences preferences = getPrefs();
        preferences.addItem(viewItem.getName());
    }

    protected synchronized void addItemToList(ViewItem viewItem) {
        if (viewItems.contains(viewItem)) return;
        viewItems.add(viewItem);
    }

    public synchronized void remove(ViewItem item) {
        if (item == null) return;
        getMainFragment().deleteItemByPosition(viewItems.indexOf(item));
        this.viewItems.remove(item);
        getPrefs().removeItem(item.getName());
    }

    public synchronized void setItem(ViewItem updatedItem) {
        for (ViewItem item : viewItems) {
            if (item.equals(updatedItem)) {
                viewItems.set(viewItems.indexOf(item), updatedItem);
                break;
            }
        }

       getMainFragment().updateItem(updatedItem);
    }

    public void updateAllItems() {
        viewItems.forEach(this::updateItem);
    }

    public abstract void updateItem(ViewItem item);

    protected ViewItem getItemByName(String itemName) throws ItemNotFoundException {
        for (ViewItem item : viewItems) {
            if (item.getName().equals(itemName)) return item;
        }
        throw new ItemNotFoundException(itemName);
    }

    public List<ViewItem> getViewItems() {
        return this.viewItems;
    }

    public Context getContext() {
        return this.fr.getContext();
    }

    public Preferences getPrefs() {
        return this.prefs;
    }

    protected MainFragment getMainFragment() {
        return (MainFragment) this.fr;
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) this.fr.getActivity();
    }

    public void makeToast(String message) {
        this.getMainFragment().makeToast(message);
    }

    public void showSpinner() {
        this.getMainFragment().showSpinner();
    }

    public void hideSpinner() {
        this.getMainFragment().hideSpinner();
    }
}