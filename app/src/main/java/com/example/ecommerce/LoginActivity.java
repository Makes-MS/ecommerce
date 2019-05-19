package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
//        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
//        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

//        AdminLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginButton.setText("Авторизація");
//                AdminLink.setVisibility(View.INVISIBLE);
//                NotAdminLink.setVisibility(View.VISIBLE);
//                parentDbName = "Admins";
//            }
//        });

//        NotAdminLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginButton.setText("Авторизація");
//                AdminLink.setVisibility(View.VISIBLE);
//                NotAdminLink.setVisibility(View.INVISIBLE);
//                parentDbName = "Users";
//            }
//        });
    }

    private void LoginUser(){ // Authorisation button
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Напишіть свій телефон!", Toast.LENGTH_SHORT).show(); // message "Write your phone!"
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Напишіть свій пароль!", Toast.LENGTH_SHORT).show(); // message "Write your password!"
        }
        else{
            loadingBar.setTitle("Авторизація");
            loadingBar.setMessage("Зачекайте");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAssetsToAccount(phone, password);
        }
    }

    private void AllowAssetsToAccount(final String phone, final String password){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)) {

//                            if (parentDbName.equals("Admins")) {
//                                Toast.makeText(LoginActivity.this, "Адміністратор: Авторизація успішно виконана!", Toast.LENGTH_SHORT).show();
//                                loadingBar.dismiss();
//
//                                Intent intent = new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
//                                startActivity(intent);

                            if (parentDbName.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Авторизація успішно виконана!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivityTwo.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Пароль не вірний!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
