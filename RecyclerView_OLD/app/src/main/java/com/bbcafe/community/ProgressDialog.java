package com.bbcafe.community;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class ProgressDialog extends DialogFragment {
        private static final String TAG = "ProgressDialog";
        private static final int DELAY_MILLISECOND = 450;
        private static final int SHOW_MIN_MILLISECOND = 300;
        private static final int PROGRESS_CONTENT_SIZE_DP = 80;

        private ProgressBar mProgressBar;
        private boolean startedShowing;
        private long mStartMillisecond;
        private long mStopMillisecond;

        // default constructor. Needed so rotation doesn't crash
        public ProgressDialog() {
            super();
        }

        @NonNull
        @SuppressLint("InflateParams")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_progress, null));
            return builder.create();
        }

        @Override
        public void onStart() {
            super.onStart();
            mProgressBar = getDialog().findViewById(R.id.progress);

            if (getDialog().getWindow() != null) {
                int px = (int) (PROGRESS_CONTENT_SIZE_DP * getResources().getDisplayMetrics().density);
                getDialog().getWindow().setLayout(px, px);
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        @Override
        public void show(final FragmentManager fm, final String tag) {
            mStartMillisecond = System.currentTimeMillis();
            startedShowing = false;
            mStopMillisecond = Long.MAX_VALUE;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mStopMillisecond > System.currentTimeMillis())
                        showDialogAfterDelay(fm, tag);
                }
            }, DELAY_MILLISECOND);
        }

        private void showDialogAfterDelay(FragmentManager fm, String tag) {
            startedShowing = true;
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }

    public void show(final FragmentManager fm) {
            show(fm, TAG);
    }

        public void cancel() {
            mStopMillisecond = System.currentTimeMillis();

            if (startedShowing) {
                if (mProgressBar != null) {
                    cancelWhenShowing();
                } else {
                    cancelWhenNotShowing();
                }
            }
        }

        private void cancelWhenShowing() {
            if (mStopMillisecond < mStartMillisecond + DELAY_MILLISECOND + SHOW_MIN_MILLISECOND) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissAllowingStateLoss();
                    }
                }, SHOW_MIN_MILLISECOND);
            } else {
                dismissAllowingStateLoss();
            }
        }

        private void cancelWhenNotShowing() {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissAllowingStateLoss();
                }
            }, DELAY_MILLISECOND);
        }


}
