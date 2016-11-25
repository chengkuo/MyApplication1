package com.example.a52374.myapplication.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by 吴广庆 on 2016/11/24.
 */

public class BitmapCup {
    public static Bitmap bitmap(Bitmap source){
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),source.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(source.getWidth()/2,source.getHeight()/2,source.getWidth()/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,0,0,paint);
        return bitmap;
    }
}
