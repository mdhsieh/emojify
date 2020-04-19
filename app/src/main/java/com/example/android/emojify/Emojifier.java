package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
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
    public static void detectFaces(Context context, Bitmap bitmap)
    {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if (!detector.isOperational()) {
            // The face detector is not operational.
            Toast.makeText(context, "Could not set up the face detector!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The required native library to do face detection is not available");
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = detector.detect(frame);

        int numFaces = 0;

        for (int i = 0; i < faces.size(); ++i) {
            numFaces++;
        }

        Log.d(TAG, "Number of faces detected: " + numFaces);

        if (numFaces == 0)
        {
            Toast.makeText(context, "No Faces Detected", Toast.LENGTH_SHORT).show();
        }

        // release the detector instance once it is no longer needed
        detector.release();
    }
}
