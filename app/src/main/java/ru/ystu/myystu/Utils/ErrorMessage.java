package ru.ystu.myystu.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Fragments.ErrorMessageFragment;

public class ErrorMessage {

    public static void show(View view, int code, String msg, Context mContext){
        showError(view, code, msg, mContext, null);
    }

    public static void showToFragment(View view, int code, String msg, Context mContext, String tag){
        showError(view, code, msg, mContext, tag);
    }

    private static void showError(View view, int code, String msg, Context mContext, String tag){

        final FragmentManager mFragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        final ErrorMessageFragment mErrorMessageFragment = new ErrorMessageFragment();
        final Bundle mBundle = new Bundle();
        mBundle.putString("error_msg", msg);
        mBundle.putInt("error_code", code);
        if(tag != null){
            mBundle.putString("fragment_tag", tag);
            mBundle.putInt("view_id", view.getId());
        }
        mErrorMessageFragment.setArguments(mBundle);

        if(mFragmentManager.getFragments().size() > 0){
            for(Fragment fragment : mFragmentManager.getFragments()){
                if (fragment != null)
                    mFragmentManager.beginTransaction().remove(fragment).commit();
            }
            mFragmentManager.getFragments().clear();
        }

        if(view != null && ((ViewGroup)view).getChildCount() > 0) {
            ((ViewGroup)view).removeAllViews();
        }

        if (view != null) {
            mFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(view.getId(), mErrorMessageFragment, "ERROR_FRAGMENT")
                    .commit();
        }
    }
}
