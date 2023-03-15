package com.example.localshop.shopActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Shops;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;

public class ShopLoginActivity extends AppCompatActivity {


    private EditText Shopname, Shoppassword;
    private Button Loginbutton, forgotpassword, signup, shopregister;
    private ProgressDialog Loadingbar;
    private CheckBox Checkboxrememberme;

    private DatabaseReference curradminref;
    private FirebaseAuth mAuth;
    private Animation bottom_ani;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        bottom_ani = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r3);
        relativeLayout.setAnimation(bottom_ani);

        Shopname = (EditText) findViewById(R.id.Sname);//now i am using it for shop name
        Shoppassword = (EditText) findViewById(R.id.SPassword);
        Loginbutton = (Button) findViewById(R.id.SLogin_button);
        forgotpassword = (Button) findViewById(R.id.S_forgetpassword);
        signup = (Button) findViewById(R.id.Ssignup_btn);
        Loadingbar = new ProgressDialog(this);
        Checkboxrememberme = (CheckBox) findViewById(R.id.SRememberme_checkbox);
        Paper.init(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopLoginActivity.this, ShopRegisterActivity.class));
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopLoginActivity.this, ShopForgotActivity.class));
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        String shopname = Paper.book().read(shopPrevalent.ShopNameKey);
        String shoppass = Paper.book().read(shopPrevalent.ShopPasswordKey);


        if (!TextUtils.isEmpty(shopname) && !TextUtils.isEmpty(shoppass))
        {
            Loadingbar.setMessage("already logged in...  please wait");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();
            AllowAccessToAccount1(shopname,shoppass);
        }

    }

    private void validateData() {

        String sname = Shopname.getText().toString().trim();
        String spass = Shoppassword.getText().toString().trim();


        if (sname.isEmpty()) {
            Toast.makeText(ShopLoginActivity.this, "enter shop name", Toast.LENGTH_SHORT).show();
        } else if (spass.isEmpty()) {
            Toast.makeText(ShopLoginActivity.this, "enter password", Toast.LENGTH_SHORT).show();
        } else if (spass.length()<productType.passwordLength) {
            Toast.makeText(ShopLoginActivity.this, "minimum character should be 8", Toast.LENGTH_SHORT).show();
        } else {

            Loadingbar.setMessage("verifying your account...");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();
            AllowAccessToAccount1(sname, spass);
        }
    }

    private void AllowAccessToAccount1(final String sname, final String spass) {

        final DatabaseReference current_shop_ref = FirebaseDatabase.getInstance()
                .getReference().child("Admins");

        current_shop_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(sname).exists()) {
                    final Shops shops = dataSnapshot.child(sname).getValue(Shops.class);

                    if (spass.equals(shops.getShop_password())) {
//                        String currentadminID = mAuth.getCurrentUser().getUid();
                        String deviceTokenA = FirebaseInstanceId.getInstance().getToken();
                        current_shop_ref.child(sname).child("device_token").setValue(deviceTokenA);
                        current_shop_ref.child(sname).child("uid").setValue("currentadminID")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            if (Checkboxrememberme.isChecked()) {
                                                Paper.book().write(shopPrevalent.ShopNameKey, sname);
                                                Paper.book().write(shopPrevalent.ShopPasswordKey, spass);
                                            }
                                            Loadingbar.dismiss();
                                            shopPrevalent.current_shop = shops;
                                            Intent intent = new Intent(ShopLoginActivity.this,HomeActivityS.class);
                                            intent.putExtra("code","");
                                            startActivity(intent);
                                        } else {
                                            Loadingbar.dismiss();
                                            Toast.makeText(ShopLoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Loadingbar.dismiss();
                        Toast.makeText(ShopLoginActivity.this, "incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Loadingbar.dismiss();
                    Toast.makeText(ShopLoginActivity.this, "account not present", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
