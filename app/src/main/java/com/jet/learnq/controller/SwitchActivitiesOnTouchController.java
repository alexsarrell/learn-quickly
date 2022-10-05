package com.jet.learnq.controller;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

public class SwitchActivitiesOnTouchController implements View.OnTouchListener, GestureDetector.OnGestureListener {
    Context context;
    private GestureDetector gestureDetector;

    public SwitchActivitiesOnTouchController(Context context) {
        gestureDetector = new GestureDetector(context, this);
        this.context = context;
    }

    public void createListener(View view) {
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        Log.d(TAG, "onDown: called");
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {
        Log.d(TAG, "onShowPress: called");
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        Log.d(TAG, "onSingleTap: called");
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onScroll: called");
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress: called");
    }

    @Override
    public boolean onFling(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onFling: called");
        return false;
    }
}