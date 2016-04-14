package com.tappitz.app.util;

import com.tappitz.app.Global;
import com.tappitz.app.R;

/**
 * Created by sampaio on 01-12-2015.
 */
public class GetColor {
    public static int getColor(int order){
        int color = 0;
        switch (order){
            case Global.RED:
                color =  R.color.redA;
                break;
            case Global.YELLOW:
                color = R.color.yellowA;
                break;
            case Global.GREEN:
                color = R.color.greenA;
                break;
        }
        return color;
    }

    public static int getColor(String order){
        int color = 0;
        switch (order){
            case Global.RED +"":
                color =  R.color.redA;
                break;
            case Global.YELLOW+"":
                color = R.color.yellowA;
                break;
            case Global.GREEN+"":
                color = R.color.greenA;
                break;
        }
        return color;
    }
}
