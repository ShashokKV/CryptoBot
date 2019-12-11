package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.history.HistoryHolder;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class HistoryPagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_pager_fragment, container, false);

        ViewPager viewPager = view.findViewById(R.id.pager);
        FragmentPagerAdapter pagerAdapter = new PagerAdapter(Objects.requireNonNull(getFragmentManager()), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabs = view.findViewById(R.id.pager_header);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
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
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "History";
                case 1:
                    return "Orders";
                default:
                    throw new IllegalArgumentException("Unknown viewPager Position");
            }
        }
    }
}
