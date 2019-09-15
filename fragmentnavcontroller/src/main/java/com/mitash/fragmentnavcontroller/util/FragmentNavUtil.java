package com.mitash.fragmentnavcontroller.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
public class FragmentNavUtil {

    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     */
    public static void performHideShowFragmentTransaction(FragmentManager fragmentManager, Fragment fragment
            , Fragment activeFragment, Integer frameId, Integer fragmentTransit
            , String tag, Boolean hideShow) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (hideShow) {
            if (fragmentManager.getFragments().contains(activeFragment)) {
                transaction.hide(activeFragment);
            }
            transaction.show(fragment);
        } else {
            if (null != activeFragment && fragmentManager.getFragments().contains(activeFragment)) {
                transaction.hide(activeFragment);
            }
            transaction.add(frameId, fragment, tag);
        }
        transaction.setTransition(fragmentTransit);
        transaction.commit();
    }

    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     */
    public static void performDetachAttachFragmentTransaction(FragmentManager fragmentManager, Fragment fragment
            , Fragment activeFragment, Integer frameId, Integer fragmentTransit
            , String tag, Boolean attachDetach) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (attachDetach) {
            if (fragmentManager.getFragments().contains(activeFragment)) {
                transaction.detach(activeFragment);
            }
            transaction.attach(fragment);
        } else {
            if (null != activeFragment && fragmentManager.getFragments().contains(activeFragment)) {
                transaction.detach(activeFragment);
            }
            transaction.add(frameId, fragment, tag);
        }
        transaction.setTransition(fragmentTransit);
        transaction.commit();
    }
}
