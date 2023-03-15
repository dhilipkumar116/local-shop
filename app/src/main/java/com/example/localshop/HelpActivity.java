package com.example.localshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HelpActivity extends AppCompatActivity {

    private Button sendmail_forgetpassbtn, sendmess_forgetpassbtn;
    private DatabaseReference utils;
    private String ophno, oemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        sendmess_forgetpassbtn = findViewById(R.id.sendmess_forgetpassbtn);
        sendmail_forgetpassbtn = findViewById(R.id.sendmail_forgetpassbtn);
        utils = FirebaseDatabase.getInstance().getReference().child("UTILITIES");
        utils.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ophno = dataSnapshot.child("phno").getValue().toString();
                oemail = dataSnapshot.child("email").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendmess_forgetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(v);
            }
        });

        sendmail_forgetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
    }


    protected void openEmail() {
        String[] to ={oemail};
        Intent emailIntent =  new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "queries/issues");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "list out your queries and issues: ");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {

        }

    }

    public void openWhatsApp(View view) {
        try {
            String text = "list out your queries or issues";
            String toNumber = "91"+ophno;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
