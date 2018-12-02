package com.example.bpr.app2;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Random;

public class Utils {

    public static final Random RANDOM = new Random();
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.density / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int randInt(int min, int max){
        int randomNum = RANDOM.nextInt((max-min) + 1)+min;
        return randomNum;
    }
}
