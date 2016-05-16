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
    private static final String TAG = PinView.class.getName();
    private MapPin singlePin = null;
    private ArrayList<MapPin> multiplePins;
    private ArrayList<DrawPin> drawnPins;
    private Context context;
    private float density;
    private Paint paint;

    /**
     * Constructor
     *
     * @param context
     */
    public PinView(Context context) {
        this(context, null);
        this.context = context;

        initDrawing();
    }

    /**
     * Constructor
     *
     * @param context
     * @param attr
     */
    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();

        initDrawing();
    }

    /**
     * Set an array of pins
     *
     * @param mapPins
     */
    public void setMultiplePins(ArrayList<MapPin> mapPins) {
        this.multiplePins = mapPins;
        initialise();
        invalidate();
    }

    /**
     * Set a single pin
     *
     * @param pin
     */
    public void setSinglePin(MapPin pin) {
        this.singlePin = pin;
        initialise();
        invalidate();
    }

    /**
     * Get the single pin
     *
     * @return
     */
    public MapPin getSinglePin() {
        return singlePin;
    }

    /**
     * Get multiple pins
     * @return
     */
    public ArrayList<MapPin> getMultiplePins() {
        return multiplePins;
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

        if (singlePin == null) {
            for (MapPin pin : multiplePins) {
                drawPin(canvas, pin);
            }
        } else {
            drawPin(canvas, singlePin);
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

    private void drawPin(Canvas canvas, MapPin pin) {
        //Bitmap bmpPin = Utils.getBitmapFromAsset(context, mPin.getPinImgSrc());
        Bitmap bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker_icon);

        float w = (density / 1200f) * bmpPin.getWidth();
        float h = (density / 1200f) * bmpPin.getHeight();
        bmpPin = Bitmap.createScaledBitmap(bmpPin, (int) w, (int) h, true);

        PointF vPin = sourceToViewCoord(pin.getPoint());
        //in my case value of point are at center point of pin image, so we need to adjust it here

        float vX = vPin.x - (bmpPin.getWidth() / 2);
        float vY = vPin.y - bmpPin.getHeight();

        canvas.drawBitmap(bmpPin, vX, vY, paint);

        //add added pin to an Array list to get touched pin
        DrawPin dPin = new DrawPin();
        dPin.setStartX(pin.getX() - w / 2);
        dPin.setEndX(pin.getX() + w / 2);
        dPin.setStartY(pin.getY() - h / 2);
        dPin.setEndY(pin.getY() + h / 2);
        dPin.setId(pin.getId());
        drawnPins.add(dPin);
    }

    /**
     * Initialize drawing tools
     */
    private void initDrawing() {
        density = getResources().getDisplayMetrics().densityDpi;
        drawnPins = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
    }
}
