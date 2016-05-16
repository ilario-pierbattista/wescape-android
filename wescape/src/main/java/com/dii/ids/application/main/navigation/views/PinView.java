/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.dii.ids.application.main.navigation.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dii.ids.application.R;

import java.util.ArrayList;

public class PinView extends SubsamplingScaleImageView {

    private PointF sPin;

    ArrayList<MapPin> mapPins;
    ArrayList<DrawPin> drawnPins;
    Context context;
    String tag = getClass().getSimpleName();

    public PinView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();
    }

    public void setPins(ArrayList<MapPin> mapPins) {
        this.mapPins = mapPins;
        initialise();
        invalidate();
    }

    public void setPin(PointF pin) {
        this.sPin = pin;
    }

    public PointF getPin() {
        return sPin;
    }

    private void initialise() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during       setup.
        if (!isReady()) {
            return;
        }

        drawnPins = new ArrayList<>();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float density = getResources().getDisplayMetrics().densityDpi;


        for (int i = 0; i < mapPins.size(); i++) {
            MapPin mPin = mapPins.get(i);
            //Bitmap bmpPin = Utils.getBitmapFromAsset(context, mPin.getPinImgSrc());
            Bitmap bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pushpin_blue);

            float w = (density / 960f) * bmpPin.getWidth();
            float h = (density / 960f) * bmpPin.getHeight();
            bmpPin = Bitmap.createScaledBitmap(bmpPin, (int) w, (int) h, true);

            PointF vPin = sourceToViewCoord(mPin.getPoint());
            //in my case value of point are at center point of pin image, so we need to adjust it here

            float vX = vPin.x - (bmpPin.getWidth() / 2);
            float vY = vPin.y - bmpPin.getHeight();


            canvas.drawBitmap(bmpPin, vX, vY, paint);

            //add added pin to an Array list to get touched pin
            DrawPin dPin = new DrawPin();
            dPin.setStartX(mPin.getX() - w / 2);
            dPin.setEndX(mPin.getX() + w / 2);
            dPin.setStartY(mPin.getY() - h / 2);
            dPin.setEndY(mPin.getY() + h / 2);
            dPin.setId(mPin.getId());
            drawnPins.add(dPin);
        }
    }

    public int getPinIdByPoint(PointF point) {

        for (int i = drawnPins.size() - 1; i >= 0; i--) {
            DrawPin dPin = drawnPins.get(i);
            if (point.x >= dPin.getStartX() && point.x <= dPin.getEndX()) {
                if (point.y >= dPin.getStartY() && point.y <= dPin.getEndY()) {
                    return dPin.getId();
                }
            }
        }
        return -1; //negative no means no pin selected
    }
}
