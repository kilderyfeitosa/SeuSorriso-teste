/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unicatolica.seusorriso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.PictureDrawable;

import com.unicatolica.seusorriso.ui.camera.GraphicOverlay;

/*Graphics class for rendering Googly Eyes on a graphic overlay given the current eye positions.
*/
 //Classe de gráficos para redenrizar a aplicação em uma
// sobreposição gráfica dada a posição atual da boca.

class MouthGraphic extends GraphicOverlay.Graphic {

    private static final float MOUTH_RADIUS_PROPORTION = 0.45f;
    private static final float SMILE_RADIUS_PROPORTION = MOUTH_RADIUS_PROPORTION / 2.0f;

    Bitmap mSorriso;
    Bitmap efeitos;

    private Paint mMouthWhitesPaint;
    private Paint mMouthSmilePaint;
    private Paint mMouthOutlinePaint;
    private Paint mMouthLidPaint;

    // Keep independent physics state for each eye.
    //Mantenha o estado de física independente de cada ponto da boca.
    private MouthPhysics mButtomPhysics = new MouthPhysics();
    private MouthPhysics mLeftPhysics = new MouthPhysics();
    private MouthPhysics mRightPhysics = new MouthPhysics();


    private volatile PointF mButtomPosition;
    private volatile boolean mButtomOpen;

    private volatile PointF mLeftPosition;
    private volatile boolean mLeftOpen;

    private volatile PointF mRightPosition;
    private volatile boolean mRightOpen;


    //==============================================================================================
    // Methods
    //==============================================================================================
    MouthGraphic(GraphicOverlay overlay, String opcao) {
        super(overlay);

        switch (opcao){
            case "BTNNAOFUMANTE":
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filtro00omega);
                break;
            case  "BTNDEZANOS":
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filtro01omega);
                //efeitos = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.espinha);
                break;
            case "BTNVINTEANOS":
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filtro02omega);
                break;
            case "BTNTRINTAANOS":
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filtro03omega);
                break;
            case "BTNQUARENTAANOS":
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filtro02omega);
                break;
            default:
                mSorriso = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.filter0);
        }

        mMouthWhitesPaint = new Paint();
        mMouthWhitesPaint.setColor(Color.BLUE);
        mMouthWhitesPaint.setStyle(Paint.Style.FILL);
        mMouthWhitesPaint.setTextSize(20);

        mMouthLidPaint = new Paint();
        mMouthLidPaint.setColor(Color.RED);
        mMouthLidPaint.setStyle(Paint.Style.FILL);

        mMouthSmilePaint = new Paint();
        mMouthSmilePaint.setColor(Color.BLUE);
        mMouthSmilePaint.setStyle(Paint.Style.FILL);
        mMouthSmilePaint.setTextSize(20);

        mMouthOutlinePaint = new Paint();
        mMouthOutlinePaint.setColor(Color.BLACK);
        mMouthOutlinePaint.setStyle(Paint.Style.STROKE);
        mMouthOutlinePaint.setStrokeWidth(5);

    }



    /**
     * Updates the eye positions and state from the detection of the most recent frame.  Invalidates
     * the relevant portions of the overlay to trigger a redraw.
     */
    void updateMouth(PointF buttomPosition, boolean buttomOpen,
                    PointF leftPosition, boolean leftOpen,
                    PointF rightPosition, boolean rightOpen) {

        mButtomPosition = buttomPosition;
        mButtomOpen = buttomOpen;

        mLeftPosition = leftPosition;
        mLeftOpen = leftOpen;

        mRightPosition = rightPosition;
        mRightOpen = rightOpen;

        postInvalidate();
    }

    /**
     * Draws the current eye state to the supplied canvas.  This will draw the eyes at the last
     * reported position from the tracker, and the iris positions according to the physics
     * simulations for each iris given motion and other forces.
     */
    @Override
    public void draw(Canvas canvas) {
        PointF detectButtomPosition = mButtomPosition;
        PointF detectLeftPosition = mLeftPosition;
        PointF detectRightPosition = mRightPosition;
        if ((detectButtomPosition == null) || (detectLeftPosition == null) || (detectRightPosition == null)) {
            return;
        }
        PointF buttomPosition =
                new PointF(translateX(detectButtomPosition.x), translateY(detectButtomPosition.y));
        PointF leftPosition =
                new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y));
        PointF rightPosition =
                new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y));

        // Use the inter-eye distance to set the size of the eyes.
        float distance = (float) Math.sqrt(
                Math.pow(rightPosition.x - leftPosition.x, 2) +
                Math.pow(rightPosition.y - leftPosition.y, 2));
        float mouthRadius = MOUTH_RADIUS_PROPORTION * distance;
        float smileRadius = SMILE_RADIUS_PROPORTION * distance;

        PointF buttomSmilePosition =
                mButtomPhysics.nextSmilePosition(buttomPosition, mouthRadius, smileRadius);
        drawMouth(canvas, buttomPosition, mouthRadius, buttomSmilePosition, smileRadius, mButtomOpen);
    }

    private void drawMouth(Canvas canvas, PointF buttomPosition, float mouthRadius,
                           PointF smilePosition, float smileRadius, boolean isOpen) {

        if (isOpen) {
            canvas.drawBitmap(mSorriso,buttomPosition.x-100, buttomPosition.y-80, null);
//            canvas.drawBitmap(mSorriso,buttomPosition.x-100, buttomPosition.y-90, null);
            //canvas.drawBitmap(efeitos,buttomPosition.x, buttomPosition.y, null);

            //canvas.drawText("Boca ABERTA",buttomPosition.x, buttomPosition.y, mMouthWhitesPaint);
            //canvas.drawText(":D", buttomPosition.x, buttomPosition.y, mMouthSmilePaint);
            //canvas.drawCircle(buttomPosition.x, buttomPosition.y, mouthRadius, mMouthWhitesPaint);
            //canvas.drawCircle(smilePosition.x, smilePosition.y, smileRadius, mMouthSmilePaint);
        } else {
            canvas.drawText("",buttomPosition.x, buttomPosition.y, mMouthWhitesPaint);
            //canvas.drawCircle(buttomPosition.x, buttomPosition.y, mouthRadius, mMouthLidPaint);
            //float y = buttomPosition.y;
            //float start = buttomPosition.x - mouthRadius;
            //float end = buttomPosition.x + mouthRadius;
            //canvas.drawLine(start, y, end, y, mMouthOutlinePaint);
        }
        //canvas.drawText("Boca Fechada",buttomPosition.x, buttomPosition.y, mMouthWhitesPaint);
        //canvas.drawCircle(buttomPosition.x, buttomPosition.y, mouthRadius, mMouthOutlinePaint);
    }



}


