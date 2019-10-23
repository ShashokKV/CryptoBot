package com.chess.cryptobot.view.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.model.ViewItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private List<ViewItem> items;
    private ContextHolder holder;

    RecyclerViewAdapter(ContextHolder holder) {
        this.holder = holder;
        this.items = new ArrayList<>();
        initViewItems();
    }

    private void initViewItems() {
        holder.getViewItems().forEach(item -> this.items.add(copyItem(item)));
    }

    public abstract ViewItem copyItem(ViewItem item);

    public String itemNameByPosition(int position) {
        return getItemByPosition(position).getName();
    }

    ViewItem getItemByPosition(int position) {
        return this.items.get(position);
    }

    public void addItem(ViewItem item) {
        this.items.add(copyItem(item));
        this.notifyItemInserted(getItemCount());
    }

    public void deleteItem(int position) {
        items.remove(position);
        this.notifyItemRemoved(position);
    }

    public void updateItem(ViewItem item) {
        int index = this.items.indexOf(item);
        if (index >= 0) {
            this.items.set(index, copyItem(item));
            this.notifyItemChanged(index);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
