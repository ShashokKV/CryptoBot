package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.history.HistoryHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class HistoryPagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_pager_fragment, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new PagerAdapter(Objects.requireNonNull(this.getActivity()));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabs = view.findViewById(R.id.pager_header);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "History" : "Orders")
        ).attach();

        return view;
    }

    static class PagerAdapter extends FragmentStateAdapter {

        PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HistoryFragment(HistoryHolder.State.HISTORY);
                case 1:
                    return new HistoryFragment(HistoryHolder.State.ORDERS);
                default:
                    throw new IllegalArgumentException("Unknown viewPager Position");
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}