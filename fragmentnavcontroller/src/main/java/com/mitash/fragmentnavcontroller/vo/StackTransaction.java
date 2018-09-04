package com.mitash.fragmentnavcontroller.vo;

import android.support.v4.app.Fragment;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
public class StackTransaction {

    public Fragment newFragment;

    public Integer stackIndex;

    public StackTransaction(Fragment newFragment, Integer stackIndex) {
        this.newFragment = newFragment;
        this.stackIndex = stackIndex;
    }
}
