package com.mitash.fragmentnavcontroller.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mitash.fragmentnavcontroller.FragmentNavigator;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
public abstract class BackHandledFragment extends Fragment {

    private BackHandlerInterface mBackHandlerInterface = null;

    public abstract Boolean onBackPressed();

    protected Boolean handleNavigationClick() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            mBackHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Mark this fragment as the selected Fragment.
        mBackHandlerInterface.setSelectedFragment(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && FragmentNavigator.provideController().isFragmentHideStrategy()) {
            // Mark this fragment as the selected Fragment.
            mBackHandlerInterface.setSelectedFragment(this);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!FragmentNavigator.provideController().isFragmentHideStrategy()) {
            // Mark this fragment as the selected Fragment.
            mBackHandlerInterface.setSelectedFragment(this);
        }
    }

    public Boolean onTraverseBack() {
        return mBackHandlerInterface.popBackStack();
    }

    public void onQueueTransaction(Fragment newFragment) {
        mBackHandlerInterface.triggerStackTransaction(newFragment);
    }

    interface BackHandlerInterface {

        void setSelectedFragment(BackHandledFragment backHandledFragment);

        Boolean popBackStack();

        void triggerStackTransaction(Fragment newFragment);
    }
}
