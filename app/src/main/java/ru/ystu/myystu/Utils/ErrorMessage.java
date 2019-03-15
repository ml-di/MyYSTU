package ru.ystu.myystu.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Fragments.ErrorMessageFragment;

public class ErrorMessage {

    public static void show(View view, int code, String msg, Context mContext){

        final FragmentManager mFragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        final ErrorMessageFragment mErrorMessageFragment = new ErrorMessageFragment();
        final Bundle mBundle = new Bundle();
        mBundle.putString("error_msg", msg);
        mBundle.putInt("error_code", code);
        mErrorMessageFragment.setArguments(mBundle);

        if(mFragmentManager.getFragments().size() > 0){
            for(Fragment fragment : mFragmentManager.getFragments()){
                mFragmentManager.beginTransaction().remove(fragment).commit();
            }
            mFragmentManager.getFragments().clear();
        }

        mFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(view.getId(), mErrorMessageFragment, "ERROR_FRAGMENT")
                .commit();

    }
}
