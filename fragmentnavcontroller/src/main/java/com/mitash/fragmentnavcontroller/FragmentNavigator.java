package com.mitash.fragmentnavcontroller;

import com.mitash.fragmentnavcontroller.controller.FragmentNavController;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
public class FragmentNavigator {

    public static FragmentNavController provideController() {
        return FragmentNavController.getInstance();
    }
}
