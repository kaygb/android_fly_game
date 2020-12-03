package com.kaygb.fly_game.view;

import android.graphics.Bitmap;
import android.graphics.Paint;

public class Score {
    public Score(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }
    public int s = 0;
    public Bitmap bitmap = null;

    //画笔
    public Paint paint = null;
}
