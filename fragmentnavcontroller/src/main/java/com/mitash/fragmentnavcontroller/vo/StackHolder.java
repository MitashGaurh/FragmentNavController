package com.mitash.fragmentnavcontroller.vo;

import androidx.fragment.app.Fragment;

import java.util.Stack;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
public class StackHolder {

    public Fragment rootFragment;

    public Stack<Fragment> fragmentStack = new Stack<>();

    public StackHolder(Fragment rootFragment) {
        this.rootFragment = rootFragment;

    }
}
