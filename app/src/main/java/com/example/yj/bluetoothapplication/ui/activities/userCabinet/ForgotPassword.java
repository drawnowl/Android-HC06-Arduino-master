package com.example.yj.bluetoothapplication.ui.activities.userCabinet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yj.bluetoothapplication.R;
import com.example.yj.bluetoothapplication.ui.activities.SendToHCModule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    InputMethodManager inm;
    Snackbar snackBar;

    EditText inputEmail;
    Button btnResetPass;
    TextView btnBack;
    RelativeLayout activityForgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        inputEmail = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_back);
        activityForgot = (RelativeLayout)findViewById(R.id.activity_forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_back) {
            startActivity(new Intent(this,SendToHCModule.class));
            finish();
        } else if(view.getId() == R.id.forgot_btn_reset) {
            resetPassword(inputEmail.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        inm.hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);
                        if(task.isSuccessful()) {
                            snackBar = Snackbar.make(activityForgot, getString(R.string.change_password)
                                    + email, Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        } else {
                            snackBar = Snackbar.make(activityForgot, R.string.failed_to_send_password,
                                    Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }
                    }
                });
    }
}

