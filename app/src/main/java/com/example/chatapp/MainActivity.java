package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText number;
    CountryCodePicker cpp;
    Button btnGenerate;
    FirebaseAuth mAuth;
    String FullNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText) findViewById(R.id.PhoneNumber);
        cpp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        btnGenerate = (Button) findViewById(R.id.btnVerify);
        mAuth = FirebaseAuth.getInstance();

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = number.getText().toString();
                if (phoneNumber.length() == 0) {
                    Snackbar.make(view, "Please Input Your Phone Number", Snackbar.LENGTH_LONG).setAnchorView(cpp).show();
                } else {
                    String fullNamber = cpp.getSelectedCountryCodeWithPlus() + phoneNumber;
                    AttempAuth(fullNamber);

                }
            }
        });
    }

    private void AttempAuth(String fullNamber) {
        FullNumber = fullNamber;
        btnGenerate.setTextColor(767676);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                fullNamber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

     private PhoneAuthProvider.OnVerificationStateChangedCallbacks  mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted :" + phoneAuthCredential);
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w("TAG", "onVerificationFailed: ", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException){

            }else if(e instanceof FirebaseTooManyRequestsException){
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.d("TAG", "onCodeSent: " + s);
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            intent.putExtra("verificationId",s);
            intent.putExtra("number", FullNumber);
            startActivity(intent);
            finish();
        }
    };

    private void SendToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "onComplete: Success"+ task);
                    FirebaseUser user = task.getResult().getUser();
                    SendToHomeActivity();
                }else{
                    Log.w("TAG", "onComplete: ",task.getException() );
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                    }
                }
            }
        });
    }
}