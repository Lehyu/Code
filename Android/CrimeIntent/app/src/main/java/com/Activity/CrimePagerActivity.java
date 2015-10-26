package com.Activity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.MotionEvent;

import com.crimeintent.R;

import java.util.ArrayList;
import java.util.UUID;

import fragment.CrimeFragment;
import model.Crime;
import model.CrimeLab;

/**
 * Created by lhy on 10/21/15.
 */
public class CrimePagerActivity extends FragmentActivity {
    private final String TAG="CrimePagerActivity";
    private mViewPager pager;
    private ArrayList<Crime> crimeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pager = new mViewPager(this);
        pager.setId(R.id.viewPager);
        setContentView(pager);

        crimeList = CrimeLab.get(this).getCrimeList();

        Log.v(TAG, "wtf a");
        FragmentManager fm = getSupportFragmentManager();
        pager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                Crime crime = crimeList.get(position);
                return CrimeFragment.newInstance(crime.getCrimeId());
            }

            @Override
            public int getCount() {
                return crimeList.size();
            }
        });

        Log.v(TAG, "wtf");

        UUID uuid = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME);
        for(int i = 0; i < crimeList.size(); i++){
            if(uuid.equals(crimeList.get(i).getCrimeId())){
                pager.setCurrentItem(i);
                break;
            }
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Crime crime = crimeList.get(position);
                if(crime.getTitle() != null){
                    setTitle(crime.getTitle());
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private class mViewPager extends ViewPager{

        public mViewPager(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        }
    }
}
