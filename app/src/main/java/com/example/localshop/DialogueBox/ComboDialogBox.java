package com.example.localshop.DialogueBox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ComboDialogBox extends AppCompatDialogFragment {

    private EditText name , quantity;
    private RadioGroup quantitygrp, typegrp;
    private RadioButton quantitybtn, typebtn;
    private String currentdate,currenttime,Productrandomkey;
    private DatabaseReference todayProductRef;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        todayProductRef = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name()).child("Todayoffer");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view =  layoutInflater.inflate(R.layout.offer_layout,null);

        name=(EditText)view.findViewById(R.id.Today_Comboproduct_name);
        quantity=(EditText)view.findViewById(R.id.Today_Comboproduct_quantity);
        quantitygrp = (RadioGroup)view.findViewById(R.id.Today_quantity_btn) ;
        typegrp =(RadioGroup)view.findViewById(R.id.Today_type_btn) ;
        builder.setView(view)
                .setTitle("add product to combo")
                .setCancelable(false);
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(name.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "enter product name ", Toast.LENGTH_SHORT).show();
                } else if(quantity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "enter product quantity ", Toast.LENGTH_SHORT).show();
                } else {

                    int qty = quantitygrp.getCheckedRadioButtonId();
                    quantitybtn=view.findViewById(qty);

                    int type = typegrp.getCheckedRadioButtonId();
                    typebtn = view.findViewById(type);

                    uploaddetials(quantitybtn,typebtn);
                }
            }
        });
        return  builder.create();

    }

    private void uploaddetials(RadioButton quantitybtn, RadioButton typebtn) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        currentdate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss a");
        currenttime = time.format(calendar.getTime());
        Productrandomkey = currentdate + currenttime;
        HashMap<String, Object> add = new HashMap<>();
        add.put("pid", Productrandomkey);
        add.put("name", name.getText().toString());
        add.put("quantity", quantity.getText().toString());
        add.put("shopname", shopPrevalent.current_shop.getShop_name());
        if (typebtn.getText().equals("veg")) {
            add.put("producttype", productType.veg);
        }
        if (typebtn.getText().equals("nonveg")) {
            add.put("producttype", productType.nonveg);
        }
        if (typebtn.getText().equals("natural")) {
            add.put("producttype", productType.natural);
        }
        add.put("kgmglml", quantitybtn.getText().toString());
        todayProductRef.child("product").child(Productrandomkey).updateChildren(add);
    }
}
