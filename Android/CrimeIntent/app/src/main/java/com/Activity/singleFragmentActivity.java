package com.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crimeintent.R;

public abstract class singleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framentContainer);
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().
                    add(R.id.framentContainer, fragment).
                    commit();
        }
    }
}
