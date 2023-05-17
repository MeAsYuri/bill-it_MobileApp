package com.example.billit_all;

import androidx.viewpager2.widget.ViewPager2;

import java.util.TimerTask;

public class AutoSwipeTask extends TimerTask {
    private ViewPager2 viewPager;

    public AutoSwipeTask(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void run() {
        viewPager.post(() -> {
            int currentItem = viewPager.getCurrentItem();
            int nextItem = (currentItem + 1) % viewPager.getAdapter().getItemCount();
            viewPager.setCurrentItem(nextItem);
        });
    }
}