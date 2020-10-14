package org.project.eye_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterRoomActivity extends AppCompatActivity {
    EditText roomCodeEditText;
    Button enterButton;
    Intent receivedIntent;

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        receivedIntent = getIntent();
        intent.putExtra("nickname", receivedIntent.getExtras().getString("nickname"));
        intent.putExtra("email", receivedIntent.getExtras().getString("email"));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);

        roomCodeEditText = (EditText)findViewById(R.id.enterCodeEditText);
        enterButton = (Button)findViewById(R.id.enterButton);

        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
                String roomID = roomCodeEditText.getText().toString().trim();
                if(roomID.equals("")){
                    Toast.makeText(getApplicationContext(), "Room Code is Empty. Retry!", Toast.LENGTH_LONG);
                    return;
                }
                intent.putExtra("roomID", roomID);
                receivedIntent = getIntent();
                intent.putExtra("nickname", receivedIntent.getExtras().getString("nickname"));
                intent.putExtra("email", receivedIntent.getExtras().getString("email"));
                startActivity(intent);
                finish();
            }
        });

    }
}