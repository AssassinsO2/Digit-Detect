package com.app.digitdetect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.app.digitdetect.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Create an adapter for the ViewPager2
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.draw) {
                viewPager.setCurrentItem(0); // Show first fragment
                return true;
            } else if (itemId == R.id.image) {
                viewPager.setCurrentItem(1); // Show third fragment
                return true;
            } else if (itemId == R.id.generate) {
                viewPager.setCurrentItem(2); // Show second fragment
                return true;
            } else if (itemId == R.id.app_info) {
                viewPager.setCurrentItem(3); // Show second fragment
                return true;
            }  /*else if (itemId == R.id.camera) {
                viewPager.setCurrentItem(3); // Show forth fragment
                return true;
            }*/ else {
                return false;
            }
        });

        // Sync the ViewPager2 swipe with the BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.draw);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.image);
                } else if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.generate);
                } else if (position == 3) {
                    bottomNavigationView.setSelectedItemId(R.id.app_info);
                } /*else if (position == 3) {
                    bottomNavigationView.setSelectedItemId(R.id.camera);
                }*/
            }
        });
    }
}
