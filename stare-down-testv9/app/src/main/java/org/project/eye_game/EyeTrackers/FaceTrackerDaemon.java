package org.project.eye_game.EyeTrackers;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import org.project.eye_game.EyeTrackers.EyesTracker;

public class FaceTrackerDaemon implements MultiProcessor.Factory<Face> {
    private Context context;
    public FaceTrackerDaemon(Context context) {
        this.context = context;
    }

    @Override
    public Tracker<Face> create(Face face) {
        return new EyesTracker(context);
    }
}
