package com.mitash.fragmentnavcontroller.controller;

import android.util.SparseArray;
import android.view.Menu;

import androidx.annotation.IntDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mitash.fragmentnavcontroller.NavigationExecutors;
import com.mitash.fragmentnavcontroller.util.FragmentNavUtil;
import com.mitash.fragmentnavcontroller.vo.StackHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Stack;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
@SuppressWarnings("WeakerAccess")
public class FragmentNavController {

    private static FragmentNavController sFragmentNavController;

    /**
     * Using attach and detach methods of Fragment transaction to switch between fragments
     */
    public static final int DETACH = 1;

    /**
     * Using show and hide methods of Fragment transaction to switch between fragments
     */
    public static final int HIDE = 2;

    /**
     * Default action for back press when current stack size is 1
     */
    public static final int DEFAULT_BACK_STRATEGY = 10;

    /**
     * Used to show/attach home tab's stack fragment on back press when current stack size is 1
     */
    public static final int HOME = 11;

    /**
     * Default action for tab press when current stack size is 1
     */
    public static final int DEFAULT_TAB_STRATEGY = 20;

    /**
     * Used to show/attach root fragment on tab press when current stack size is 1
     */
    public static final int ROOT = 21;

    private FragmentManager mFragmentManager = null;

    private int mContainerId;

    private SparseArray<StackHolder> mStackHolder = new SparseArray<>();

    private Fragment mActiveFragment = null;

    private int mCurrentStackIndex = 0;

    private SelectTabListener mSelectTabListener;

    @FragmentDetachStrategy
    private int mFragmentDetachStrategy = DETACH;

    @FragmentBackStrategy
    private int mFragmentBackStrategy = DEFAULT_BACK_STRATEGY;

    @FragmentTabStrategy
    private int mFragmentTabStrategy = DEFAULT_TAB_STRATEGY;

    @Transit
    private int mFragmentTransit = FragmentTransaction.TRANSIT_FRAGMENT_FADE;

    public static FragmentNavController getInstance() {
        if (null == sFragmentNavController) {
            sFragmentNavController = new FragmentNavController();
        }
        return sFragmentNavController;
    }

    public void initFragmentManager(FragmentManager childFragmentManager, Integer containerId) {
        mFragmentManager = childFragmentManager;
        mContainerId = containerId;
    }

    public void initialize(Menu menu, List<Fragment> rootFragments) {
        if (menu.size() != rootFragments.size()) {
            throw new RuntimeException("Number of Menu Items and Root Fragments should be same.");
        }
        for (int i = 0; i < menu.size(); i++) {
            mStackHolder.put(menu.getItem(i).getItemId(), new StackHolder(rootFragments.get(i)));
        }
    }

    public void setFragmentDetachStrategy(Integer strategy) {
        mFragmentDetachStrategy = strategy;
    }

    public void setFragmentBackStrategy(Integer strategy) {
        mFragmentBackStrategy = strategy;
    }

    public void setFragmentTabStrategy(Integer strategy) {
        mFragmentTabStrategy = strategy;
    }

    public void setFragmentTransit(Integer transit) {
        mFragmentTransit = transit;
    }

    public void setSelectTabListener(SelectTabListener selectTabListener) {
        mSelectTabListener = selectTabListener;
    }

    public void performSwitchTabTransaction(Integer menuItemId) {
        Integer stackIndex = mCurrentStackIndex;
        mCurrentStackIndex = menuItemId;

        if (!stackIndex.equals(mCurrentStackIndex)) {
            if (null != mStackHolder.get(mCurrentStackIndex).rootFragment) {
                performStackOperation(mStackHolder.get(mCurrentStackIndex).fragmentStack, mStackHolder.get(mCurrentStackIndex).rootFragment
                        , true);
            }
        } else {
            if (mFragmentTabStrategy == ROOT) {
                if (mStackHolder.get(mCurrentStackIndex).fragmentStack.size() > 1) {
                    multiBackStackOperation(mStackHolder.get(mCurrentStackIndex).fragmentStack);
                }
            }
        }
    }

    public Boolean isCurrentStackSameAsNavigationClick(Integer menuItemId) {
        return menuItemId.equals(mCurrentStackIndex);
    }

    public void performStackFragmentsTransaction(Fragment newFragment) {

        Stack<Fragment> stack = mStackHolder.get(mCurrentStackIndex).fragmentStack;

        if (null != stack) {
            performStackOperation(stack, newFragment, false);
        }

    }

    private void performStackOperation(Stack<Fragment> stack, Fragment fragment, Boolean isFromBottomTabs) {
        if (mActiveFragment == fragment) {
            return;
        }
        if (isFromBottomTabs) {
            if (stack.isEmpty()) {
                stack.push(fragment);
                performFragmentTransaction(stack.peek(), false);
            } else {
                performFragmentTransaction(stack.peek(), true);
            }
        } else {
            stack.push(fragment);
            performFragmentTransaction(stack.peek(), false);
        }
    }

    public Boolean popBackStack() {
        if (mCurrentStackIndex == mStackHolder.keyAt(0)) {
            if (mStackHolder.get(mCurrentStackIndex).fragmentStack.size() > 1) {
                backStackOperation(mStackHolder.get(mCurrentStackIndex).fragmentStack);
                return true;
            } else {
                return false;
            }
        } else {
            if (mStackHolder.get(mCurrentStackIndex).fragmentStack.size() > 1) {
                backStackOperation(mStackHolder.get(mCurrentStackIndex).fragmentStack);
            } else {
                if (mFragmentBackStrategy == HOME && null != mSelectTabListener) {
                    returnToHomeTab();
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private void returnToHomeTab() {
        NavigationExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                mFragmentManager.beginTransaction().remove(mStackHolder.get(mCurrentStackIndex).fragmentStack.pop()).commit();
            }
        });
        mCurrentStackIndex = mStackHolder.keyAt(0);
        performFragmentTransaction(mStackHolder.get(mCurrentStackIndex).fragmentStack.peek(), true);
        mSelectTabListener.selectTab(mCurrentStackIndex);
    }

    private void multiBackStackOperation(Stack<Fragment> stack) {
        while (stack.size() > 1) {
            mFragmentManager.beginTransaction().remove(stack.pop()).commit();
        }
        performFragmentTransaction(stack.peek(), true);
    }

    private void backStackOperation(Stack<Fragment> stack) {
        if (null != mFragmentManager) {
            mFragmentManager.beginTransaction().remove(stack.pop()).commit();
            performFragmentTransaction(stack.peek(), true);
        }
    }

    private void performFragmentTransaction(Fragment fragment, Boolean isHideShow) {
        if (isFragmentHideStrategy()) {
            FragmentNavUtil.performHideShowFragmentTransaction(this.mFragmentManager, fragment
                    , mActiveFragment, mContainerId, mFragmentTransit, fragment.getClass().getSimpleName(), isHideShow);
        } else {
            FragmentNavUtil.performDetachAttachFragmentTransaction(this.mFragmentManager, fragment
                    , mActiveFragment, mContainerId, mFragmentTransit, fragment.getClass().getSimpleName(), isHideShow);
        }
        mActiveFragment = fragment;
    }

    public Boolean isFragmentHideStrategy() {
        return mFragmentDetachStrategy == HIDE;
    }

    // Declare Transit Styles
    @IntDef({FragmentTransaction.TRANSIT_NONE, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, FragmentTransaction.TRANSIT_FRAGMENT_FADE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Transit {
    }

    @IntDef({DETACH, HIDE})
    @Retention(RetentionPolicy.SOURCE)
    @interface FragmentDetachStrategy {
    }

    @IntDef({DEFAULT_BACK_STRATEGY, HOME})
    @Retention(RetentionPolicy.SOURCE)
    @interface FragmentBackStrategy {
    }

    @IntDef({DEFAULT_TAB_STRATEGY, ROOT})
    @Retention(RetentionPolicy.SOURCE)
    @interface FragmentTabStrategy {
    }
}
