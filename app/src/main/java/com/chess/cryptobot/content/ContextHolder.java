package com.chess.cryptobot.content;

import android.content.Context;

import com.chess.cryptobot.exceptions.ItemNotFoundException;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.view.AdapterActivity;
import com.chess.cryptobot.view.BalanceActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ContextHolder {
    private WeakReference<Context> context;
    private List<ViewItem> viewItems;
    private Preferences prefs;

    public ContextHolder(Context context) {
        this.context = new WeakReference<>(context);
        viewItems = new ArrayList<>();
        prefs = initPrefs(context);
        Set<String> coinNames = prefs.getCoinNames();
        initViewItems(coinNames);
    }

    public abstract Preferences initPrefs(Context context);

    public abstract void initViewItems(Set<String> coinNames);

    public void add(ViewItem viewItem) {
        if (viewItems.contains(viewItem)) return;
        viewItems.add(viewItem);
        AdapterActivity activity = getAdapterActivityOrNull();
        if (activity != null) activity.addItem(viewItem);
    }

    public void remove(ViewItem item) {
        if (item==null) return;
        AdapterActivity activity = getAdapterActivityOrNull();
        if (activity!=null) {
            activity.deleteItemByPosition(viewItems.indexOf(item));
        }
        this.viewItems.remove(item);
    }

    public void setItem(ViewItem updatedItem) {
        for(ViewItem item: viewItems) {
            if (item.equals(updatedItem)) {
                viewItems.set(viewItems.indexOf(item), updatedItem);
                break;
            }
        }

        AdapterActivity activity =  getAdapterActivityOrNull();
        if (activity!=null) activity.updateItem(updatedItem);
    }

    public void updateAllItems() {
        viewItems.forEach(this::updateItem);
    }

    public abstract void updateItem(ViewItem item);

    public List<ViewItem> getViewItems() {
        return this.viewItems;
    }

    public WeakReference<Context> getContext() {
        return this.context;
    }

    public Preferences getPrefs() {
        return this.prefs;
    }

    protected AdapterActivity getAdapterActivityOrNull() {
        Context context = this.getContext().get();
        if (context instanceof BalanceActivity) {
            return (AdapterActivity) context;
        }
        return null;
    }

    protected ViewItem getItemByName(String itemName) throws ItemNotFoundException {
        for(ViewItem item: viewItems) {
            if (item.getName().equals(itemName)) return item;
        }
        throw new ItemNotFoundException(itemName);
    }
}