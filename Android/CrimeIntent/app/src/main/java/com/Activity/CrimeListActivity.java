package com.Activity;

import android.app.Fragment;

import fragment.CrimeListFragment;

/**
 * Created by lhy on 10/21/15.
 */
public class CrimeListActivity extends  singleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
