package org.project.eye_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.project.eye_game.EyeTrackers.FaceTrackerDaemon;
import org.project.eye_game.EyeTrackers.FaceTrackerDaemonCharacter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class CharacterActivity extends AppCompatActivity {
    private View decorView;
    private int uiOption;

    int CHARACTER_ID;

    String email;
    String nickname;
    int TotalRank;
    int TotalEXP;
    int Rank1;
    int Rank2;
    int Rank3;
    int Rank4;
    int EXP1;
    int EXP2;
    int EXP3;
    int EXP4;

    String id;

    TextView nicknameView;
    TextView EXPView;
    TextView rankView;
    TextView emailView;
    ImageView characterView;

    GridView gridView;
    GameDataAdapter adapter;
    Button changeNicknameButton;
    Button friendListButton;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    CameraSource cameraSource;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("id", getIntent().getExtras().getString("id"));
        intent.putExtra("characterID", getIntent().getExtras().getInt("characterID"));
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);

        characterView = findViewById(R.id.characterView);
        CHARACTER_ID = getIntent().getExtras().getInt("characterID");

        switch (CHARACTER_ID){
            case 0:
                characterView.setImageResource(R.drawable.character_open);
                break;
            case 1:
                characterView.setImageResource(R.drawable.character2_open);
                break;
            default:
                characterView.setImageResource(R.drawable.character);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n Grant permission and restart app", Toast.LENGTH_SHORT).show();
        }else{
            initCameraSource();
        }


        nicknameView = findViewById(R.id.nicknameTextView);
        EXPView = findViewById(R.id.EXPTextView);
        rankView = findViewById(R.id.totalRankingTextView);
        emailView = findViewById(R.id.emailTextView);

        id = getIntent().getExtras().getString("id");

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keys : snapshot.getChildren()) {
                    String _id = keys.child("id").getValue(String.class);
                    if (id.equals(_id)) {
                        TotalEXP = keys.child("totalEXP").getValue(int.class);
                        TotalRank = keys.child("totalRank").getValue(int.class);
                        Rank1 = keys.child("rank1").getValue(int.class);
                        Rank2 = keys.child("rank2").getValue(int.class);
                        Rank3 = keys.child("rank3").getValue(int.class);
                        Rank4 = keys.child("rank4").getValue(int.class);
                        EXP1 = keys.child("exp1").getValue(int.class);
                        EXP2 = keys.child("exp2").getValue(int.class);
                        EXP3 = keys.child("exp3").getValue(int.class);
                        EXP4 = keys.child("exp4").getValue(int.class);
                        nickname = keys.child("nickname").getValue(String.class);
                        email = keys.child("email").getValue(String.class);

                        nicknameView.append(nickname);
                        emailView.append(email);

                        EXPView.append(Integer.toString(TotalEXP));
                        if (TotalRank == 0) {
                            rankView.append("NONE");
                        } else {
                            rankView.append(Integer.toString(TotalRank));
                        }

                        gridView = findViewById(R.id.characterGridView);
                        adapter = new GameDataAdapter();

                        adapter.addItem(new GameData("Game1", EXP1, Rank1));
                        adapter.addItem(new GameData("Game2", EXP2, Rank2));
                        adapter.addItem(new GameData("Game3", EXP3, Rank3));
                        adapter.addItem(new GameData("Game4", EXP4, Rank4));
                        gridView.setAdapter(adapter);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        changeNicknameButton = findViewById(R.id.nicknameChangeButton);
        changeNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NicknameChangeActivity.class);
                intent.putExtra("id", getIntent().getExtras().getString("id"));
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        friendListButton = findViewById(R.id.friendListButton);
        friendListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                intent.putExtra("id", getIntent().getExtras().getString("id"));
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void initCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemonCharacter(CharacterActivity.this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(5.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            Toast.makeText(CharacterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource!=null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource!=null) {
            cameraSource.release();
        }
    }

    public void updateState(String state){
        switch (state){
            case "USER_EYES_OPEN":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (CHARACTER_ID){
                            case 0:
                                characterView.setImageResource(R.drawable.character_open);
                                break;
                            case 1:
                                characterView.setImageResource(R.drawable.character2_open);
                                break;
                            default:
                                characterView.setImageResource(R.drawable.character);
                                break;
                        }
                    }
                });
                break;
            case "USER_EYES_CLOSED":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (CHARACTER_ID){
                            case 0:
                                characterView.setImageResource(R.drawable.character_close);
                                break;
                            case 1:
                                characterView.setImageResource(R.drawable.character2_close);
                                break;
                            default:
                                characterView.setImageResource(R.drawable.character);
                                break;
                        }
                    }
                });
                break;
            case "FACE_NOT_FOUND":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Where is your face??", Toast.LENGTH_SHORT);
                    }
                });
                break;
        }
    }
}