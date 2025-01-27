package com.app.digitdetect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.digitdetect.fragments.AppInfoFragment;
import com.app.digitdetect.fragments.DrawFragment;
import com.app.digitdetect.fragments.GenerateFragment;
import com.app.digitdetect.fragments.PredictFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on the position
        if (position == 0) {
            return new DrawFragment();
        } else if (position == 1) {
            return new PredictFragment();
        } else if (position == 2) {
            return new GenerateFragment();
        } else if (position == 3) {
            return new AppInfoFragment();
        }  /*else if (position == 4) {
            return new CameraFragment();
        }*/
        return new Fragment(); // Default empty fragment
    }

    @Override
    public int getItemCount() {
        return 4; // Number of fragments
    }
}
