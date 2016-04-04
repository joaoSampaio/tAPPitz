package com.tappitz.tappitz.util;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

/**
 * Created by Sampaio on 03/04/2016.
 */
public class NiceColor {
    public static int betterNiceColor(String name)
    {
        double goldenRatioConj = 0.618033988749895;
//        float hue = new Random().nextInt(360);
        float hue = nameToValue(name);
        hue += (goldenRatioConj*360);
        hue = hue % 360;

        int c = Color.HSVToColor(new float[]{hue, 0.5f, 0.95f});
        return c;
    }

    private static int nameToValue(String name){
        name = name.replace(" ", "");
        char[] c = name.toCharArray();
        int value = 0;
        for (Character ss : c)
            value += (ss - 'a' + 1);

        value = value % 360;
        return value;
    }


}
