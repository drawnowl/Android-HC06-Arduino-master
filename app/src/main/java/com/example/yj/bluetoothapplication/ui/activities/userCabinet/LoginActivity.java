package com.example.yj.bluetoothapplication.ui.activities.userCabinet;

import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    InputMethodManager inm;
    Snackbar snackBar;

    Button btnLogin;
    EditText inputEmail;
    EditText inputPassword;
    TextView btnSignUp;
    TextView btnForgotPass;

    RelativeLayout activityLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        btnLogin = (Button)findViewById(R.id.login_btn);
        inputEmail = (EditText)findViewById(R.id.login_email);
        inputPassword = (EditText)findViewById(R.id.login_password);
        btnSignUp = (TextView)findViewById(R.id.login_btn_signUp);
        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        activityLogin = (RelativeLayout)findViewById(R.id.activity_login);

        btnSignUp.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        //Check already session , if ok-> DashBoard
        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, DashBoard.class));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password) {
            startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            finish();
        } else if(view.getId() == R.id.login_btn_signUp) {
            startActivity(new Intent(LoginActivity.this, SignUp.class));
            finish();
        } else if(view.getId() == R.id.login_btn) {
            loginUser(inputEmail.getText().toString(), inputPassword.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            inm.hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);
                            if(password.length() < 6) {
                                snackBar = Snackbar.make(activityLogin, R.string.password_legth_check, Snackbar.LENGTH_LONG);
                                snackBar.show();
                            }
                        } else {
                            startActivity(new Intent(LoginActivity.this, DashBoard.class));
                        }
                    }
                });
    }
}
