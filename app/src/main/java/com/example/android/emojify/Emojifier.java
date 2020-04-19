package com.example.android.emojify;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {

    private static final String TAG = Emojifier.class.getSimpleName();

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap picture)
    {
        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        if (!detector.isOperational()) {
            // The face detector is not operational.
            Toast.makeText(context, R.string.detector_not_operational, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The required native library to do face detection is not available");


            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(context, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.d(TAG, context.getString(R.string.low_storage_error));
            }
        }

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        int numFaces = faces.size();

        // Log the number of faces
        Log.d(TAG, "Number of faces detected: " + numFaces);

        // If there are no faces detected, show a Toast message
        if (numFaces == 0)
        {
            Toast.makeText(context, R.string.no_faces_detected, Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (int i = 0; i < numFaces; i++)
            {
                Face thisFace = faces.valueAt(i);

                whichEmoji(thisFace);
            }
        }

        // Release the detector object once it is no longer needed
        detector.release();
    }

    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static void whichEmoji(Face face)
    {
        float leftEyeOpenProb = face.getIsLeftEyeOpenProbability();
        float rightEyeOpenProb = face.getIsRightEyeOpenProbability();
        float smilingProb = face.getIsSmilingProbability();

        // Log all the probabilities
        Log.d(TAG, "Probability left eye open: " + leftEyeOpenProb);
        Log.d(TAG, "Probability right eye open: " + rightEyeOpenProb);
        Log.d(TAG, "Probability smiling: " + smilingProb);

        double LEFT_EYE_CLOSED_THRESHOLD = 0.5;
        double RIGHT_EYE_CLOSED_THRESHOLD = 0.5;
        double SMILING_THRESHOLD = 0.5;

        boolean leftEyeOpen;
        boolean rightEyeOpen;
        boolean isSmiling;

        if (leftEyeOpenProb > LEFT_EYE_CLOSED_THRESHOLD)
        {
            leftEyeOpen = true;
        }
        else
        {
            leftEyeOpen = false;
        }
        if (rightEyeOpenProb > RIGHT_EYE_CLOSED_THRESHOLD)
        {
            rightEyeOpen = true;
        }
        else {
            rightEyeOpen = false;
        }
        if (smilingProb > SMILING_THRESHOLD)
        {
            isSmiling = true;
        }
        else
        {
            isSmiling = false;
        }

        // initialize emoji to smiling
        Emoji emoji = Emoji.SMILING;
        if (leftEyeOpen && rightEyeOpen && isSmiling)
        {
             emoji = Emoji.SMILING;
        }
        else if (leftEyeOpen && rightEyeOpen && !isSmiling)
        {
            emoji = Emoji.FROWNING;
        }
        else if (!leftEyeOpen && rightEyeOpen && isSmiling)
        {
            emoji = Emoji.LEFT_WINK;
        }
        else if (leftEyeOpen && !rightEyeOpen && isSmiling)
        {
            emoji = Emoji.RIGHT_WINK;
        }
        else if (!leftEyeOpen && rightEyeOpen && !isSmiling)
        {
            emoji = Emoji.LEFT_WINK_FROWNING;
        }
        else if (leftEyeOpen && !rightEyeOpen && !isSmiling)
        {
            emoji = Emoji.RIGHT_WINK_FROWNING;
        }
        else if (!leftEyeOpen && !rightEyeOpen && isSmiling)
        {
            emoji = Emoji.CLOSED_EYE_SMILING;
        }
        else if (!leftEyeOpen && !rightEyeOpen && !isSmiling)
        {
            emoji = Emoji.CLOSED_EYE_FROWNING;
        }

        Log.d(TAG, "Emoji is " + emoji);
    }

    enum Emoji {
        SMILING,
        FROWNING,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWNING,
        RIGHT_WINK_FROWNING,
        CLOSED_EYE_SMILING,
        CLOSED_EYE_FROWNING
    }

    /*static Bitmap detectBoundingRectangles(Context context, Bitmap picture)
    {
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(picture, 0, 0, null);

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        if (!detector.isOperational()) {
            // The face detector is not operational.
            Toast.makeText(context, R.string.detector_not_operational, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The required native library to do face detection is not available");
        }

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        int numFaces = faces.size();

        // Log the number of faces
        Log.d(TAG, "Number of faces detected: " + numFaces);

        for (int i = 0; i < numFaces; i++)
        {
            Face thisFace = faces.valueAt(i);

            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            Log.d(TAG, "x and y of bounding rectangle's top left corner: " + x1 + ", " + y1);
            Log.d(TAG, "width and height of bounding rectangle: " + x2 + ", " + y2);
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }

        // Release the detector object once it is no longer needed
        detector.release();

        //myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
        return tempBitmap;
    }*/
}
