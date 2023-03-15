package com.example.localshop.shopActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShopForgotActivity extends AppCompatActivity {

    private Button sendmail_forgetpassbtn, sendmess_forgetpassbtn;
    private DatabaseReference utils;
    private String ophno, oemail, uemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_forgot);

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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "requesting for password");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "shop name : "+shopPrevalent.current_shop.getShop_name());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {

        }

    }

    public void openWhatsApp(View view) {
        try {
            String text = "I forgot my shop password - shop name : "+ shopPrevalent.current_shop.getShop_name();
            String toNumber = "91"+ophno;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
