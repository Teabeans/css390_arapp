package com.example.css390_arapp.arlocations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.css390_arapp.R;
import com.example.css390_arapp.lobby;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Credits: https://github.com/dat-ng/ar-location-based-android
 *
 * Modified CSS390
 * Todo: Add locations from Firebase
 */

public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;
    private String userCoord = ARActivity.userCoordinates;
    private String usernameCoord = ARActivity.usernameCoord;
    private String username;
    private ImageView profileIm = ARActivity.profileImg;
    private double userlat;
    private double userlong;
    private Bitmap myBitmap;
    public AROverlayView(Context context) {
        super(context);
        this.context = context;

//        String imagePath = "test.jpg";
//        try {
//            // get input stream
//            InputStream ims = context.getAssets().open(imagePath);
//            Drawable d = Drawable.createFromStream(ims, null);
//            // set image to ImageView
//            profileIm.setImageDrawable(d);
//
//        }
//        catch(IOException ex) {
//            ex.printStackTrace();
//        }
        //Firebase user info split
        username = usernameCoord;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child("images").child(username + ".jpg");

        imageRef.getBytes(1024*1024).addOnSuccessListener(bytes -> {
            myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 120, 120, false);
        });

        String namepass[] = userCoord.split(":");
        double[] doubleArray = Arrays.stream(namepass).mapToDouble(Double::parseDouble).toArray();
        userlat = doubleArray[0];
        userlong = doubleArray[1];
        //Points
        arPoints = new ArrayList<ARPoint>() {{
            add(new ARPoint(username, userlat, userlong, 0));
            add(new ARPoint("New York NY", 40.707710, -74.012944, 0));
            add(new ARPoint("Vancouver BC", 49.233741, -123.119525, 0));
            add(new ARPoint("Los Angeles CA", 33.925130, -118.175464, 0));
            add(new ARPoint("Tokyo JP", 35.689487, 139.691711, 0));
        }};
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawCircle(x, y, radius, paint);
                if(myBitmap != null && arPoints.get(i).getName() == username){
                    canvas.drawBitmap(myBitmap, x - 80, y - 30, null);
                }
                canvas.drawText(arPoints.get(i).getName(), x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
            }
        }
    }
}
