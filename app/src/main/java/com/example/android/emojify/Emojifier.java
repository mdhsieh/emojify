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
     * @param bitmap The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap bitmap)
    {
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
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

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

        // Release the detector object once it is no longer needed
        detector.release();
    }
}
