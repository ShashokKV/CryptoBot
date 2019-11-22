package com.chess.cryptobot.view.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.model.ViewItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private List<ViewItem> items;
    private final ContextHolder holder;

    RecyclerViewAdapter(ContextHolder holder) {
        this.setHasStableIds(true);
        this.holder = holder;
        this.items = new ArrayList<>();
        initViewItems();
    }

    private void initViewItems() {
        this.items = holder.getViewItems();
    }

    public String itemNameByPosition(int position) {
        return getItemByPosition(position).getName();
    }

    ViewItem getItemByPosition(int position) {
        return this.items.get(position);
    }

    public void notifyItemInserted() {
        this.notifyItemInserted(getItemCount());
    }

    public void deleteItem(int position) {
        this.notifyItemRemoved(position);
    }

    public void updateItem(ViewItem item) {
        int index = this.items.indexOf(item);
        if (index >= 0) {
            this.notifyItemChanged(index);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        ViewItem viewItem = items.get(position);
        return viewItem.getName().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
