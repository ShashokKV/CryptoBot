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
    private final Fragment fr;
    private List<ViewItem> viewItems;
    private final Preferences prefs;

    protected ContextHolder(Fragment fr) {
        this.fr = fr;
        initFields();
        prefs = initPrefs(fr.getContext());
        Set<String> itemsSet = prefs.getItems();
        initViewItems(itemsSet);
    }

    public void initFields() {
        viewItems = new ArrayList<>();
    }

    protected abstract Preferences initPrefs(Context context);

    protected abstract void initViewItems(Set<String> itemsSet);

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

    protected void retainAll(List<ViewItem> viewItems) {
        List<ViewItem> invalidItems = new ArrayList<>(getViewItems());
        getViewItems().retainAll(viewItems);

        invalidItems.forEach(viewItem -> {
            if (!getViewItems().contains(viewItem)) {
                removeFromPrefs(viewItem);
            }
        });
        getMainFragment().updateAllItems();
    }

    private void removeFromAdapter(ViewItem viewItem) {
        getMainFragment().deleteItemByPosition(viewItems.indexOf(viewItem));
    }

    private void removeFromPrefs(ViewItem viewItem) {
        getPrefs().removeItem(viewItem.getName());
    }

    public synchronized void remove(ViewItem item) {
        if (item == null) return;
        removeFromAdapter(item);
        viewItems.remove(item);
        removeFromPrefs(item);
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

    protected abstract void updateItem(ViewItem item);

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