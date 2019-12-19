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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class GraphPagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_pager_layout, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.graph_pager);
        FragmentStateAdapter pagerAdapter = new GraphPagerFragment.PagerAdapter(Objects.requireNonNull(this.getActivity()));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);

        TabLayout tabs = view.findViewById(R.id.graph_pager_header);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Balance" : "Pairs")
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
                    return new BalanceGraphFragment();
                case 1:
                    return new PairsGraphFragment();
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
