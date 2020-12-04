package org.project.eye_game.interfaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.project.eye_game.R;

public class NicknameChangeActivity extends AppCompatActivity {
    Button changeButton;
    EditText newNicknameTextView;

    private View decorView;
    private int	uiOption;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String email;
    String nickname;

    int TotalRank;
    int TotalEXP;
    int EXP2;
    int EXP3;
    String id;

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);
        intent.putExtra("id", getIntent().getExtras().getString("id"));
        intent.putExtra("nickname", getIntent().getExtras().getString("nickname"));
        intent.putExtra("characterID", getIntent().getExtras().getInt("characterID"));
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_change);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );

        id = getIntent().getExtras().getString("id");
        nickname = getIntent().getExtras().getString("nickname");

        newNicknameTextView = findViewById(R.id.newNicknameEditText);
        changeButton = findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot keys : snapshot.getChildren()) {
                            String _id = keys.child("id").getValue(String.class);
                            if (id.equals(_id)) {

                                EXP2 = keys.child("exp2").getValue(int.class);
                                EXP3 = keys.child("exp3").getValue(int.class);

                                email = keys.child("email").getValue(String.class);
                                nickname = newNicknameTextView.getText().toString();

                                UserData newUser = new UserData(email, nickname, id);

                                newUser.setEXP2(EXP2);
                                newUser.setEXP3(EXP3);


                                keys.getRef().setValue(newUser);

                                Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("nickname", nickname);
                                intent.putExtra("characterID", getIntent().getExtras().getInt("characterID"));
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
           }

        });
    }
}