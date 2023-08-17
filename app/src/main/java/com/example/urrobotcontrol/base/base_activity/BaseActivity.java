package com.example.urrobotcontrol.base.base_activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;


public abstract class BaseActivity extends AppCompatActivity {
    //==============================
    // region // variable
    //==============================

    protected Context mContext;
    protected Activity mActivity;
    protected String TAG;



    private AppCompatDialog progressDialog;

    //==============================
    // endregion // variable
    //==============================

    //==============================
    //
    //==============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);


        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }


    private void init() {
        TAG = this.getClass().getSimpleName();
//        LanguageUtils.setResources(this);

        if (mContext == null)
            mContext = this;
        if (mActivity == null)
            mActivity = this;


    }



    protected abstract void initLayout();

    protected abstract void initialize();

    /**
     * 액티비티 실행
     *
     * @param cls 실행할 액티비티 클래스
     */
    protected void goActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mContext.startActivity(intent);
    }

    /**
     * 액티비티 실행
     *
     * @param cls    실행할 액티비티 클래스
     * @param intent 넘길 데이터
     */
    protected void goActivity(Class<?> cls, Intent intent) {
        intent.setClass(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mContext.startActivity(intent);
    }

    /**
     * 액티비티 실행
     *
     * @param cls         실행할 액티비티 클래스
     * @param requestCode request Code
     */
    protected void goActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * 액티비티 실행
     *
     * @param cls         실행할 액티비티 클래스
     * @param intent      넘길 데이터
     * @param requestCode request Code
     */
    protected void goActivity(Class<?> cls, Intent intent, int requestCode) {
        intent.setClass(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivityForResult(intent, requestCode);
    }



    /**
     * 토스트 메시지를 띄운다
     *
     * @param message
     * @param duration
     */
    protected void toast(String message, int duration) {
        Toast.makeText(mContext, message, duration).show();
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.layout_toast, findViewById(R.id.layoutToast));
//        TextView text = layout.findViewById(R.id.text);
//        Toast toast = new Toast(mContext);
//        text.setText(message);
//        toast.setDuration(duration);
//        toast.setView(layout);
//        toast.show();
    }

    protected void toast(String message) {
        toast(message, Toast.LENGTH_LONG);
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.layout_toast, findViewById(R.id.layoutToast));
//        TextView text = layout.findViewById(R.id.text);
//        Toast toast = new Toast(mContext);
//        text.setText(message);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(layout);
//        toast.show();
    }

    /**
     * 상단 StatusBar 제거
     */
    protected int setStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        int resourceID = getResources().getIdentifier("status_bar_height", "dimen", "android");

        int statusBarHeight = 0;
        if (resourceID > 0)
            statusBarHeight = getResources().getDimensionPixelSize(resourceID);

        return statusBarHeight;
    }


    @Override
    protected void onPause() {
        super.onPause();

//        if (mLoadingBar != null) {
//            mLoadingBar.dismiss();
//        }
    }


    /**
     * 키보드 숨기기
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }






}
