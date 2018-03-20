package com.example.geolocation.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adevole.customresources.CustomTextView;
import com.example.geolocation.Helper.Database;
import com.example.geolocation.Helper.InternetConnection;
import com.example.geolocation.Helper.Preference;
import com.example.geolocation.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import static com.example.geolocation.Helper.Preference.INVITE;
import static com.example.geolocation.Helper.Utility.error;
import static com.example.geolocation.Helper.Utility.internetError;
import static com.example.geolocation.Helper.Utility.longtoast;
import static com.example.geolocation.Helper.Utility.toast;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "Login";

    private static final int RC_SIGN_IN = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private EditText usernameEdit,usernumberEdit;
    private Button submitButton;
    private View mAuthLayout, mVerifyLayout;
    private FirebaseAuth mAuth;
    private Button mVerifyButton;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView verificationSuccessful;
    private CustomTextView mobileNumber;
    private String mVerificationId;
    private EditText mVerificationField;
    private SignInButton googleSignin;
    GoogleSignInOptions gso;

    private void initCreate() {
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        mVerifyLayout = (View) findViewById(R.id.verify_layout);
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        usernumberEdit = (EditText) findViewById(R.id.usernumberEdit);
        mobileNumber = findViewById(R.id.mobile_number);
        mAuthLayout = (View) findViewById(R.id.auth_layout);
        mAuth = FirebaseAuth.getInstance();
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        verificationSuccessful = (TextView) findViewById(R.id.verification_successful);
        submitButton = (Button) findViewById(R.id.button_start_verification);
        googleSignin = (SignInButton) findViewById(R.id.googleLogin);
        googleSignin.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);

        getLocationPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initCreate();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(),"Invalid phone number.",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
//                if (mVerifyLayout.getVisibility()==View.VISIBLE)
//                    mResendButton.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_verification:
                validation();
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(),"Cannot be empty.",Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(usernumberEdit.getText().toString(), mResendToken);
                break;
            case R.id.googleLogin:
                signIn();
                break;
        }
    }

    private void signIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                toast(getApplicationContext(), "Google sign in failed Error code - "+e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else
                            toast(getApplicationContext(), "Google sign in failed");
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        mAuthLayout.setVisibility(View.GONE);
        mVerifyLayout.setVisibility(View.GONE);
        verificationSuccessful.setVisibility(View.VISIBLE);
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty())
            verifyDatabase(user.getUid(), user.getDisplayName());
        else
            verifyDatabase(user.getUid(), usernameEdit.getText().toString());
    }

    private void validation() {
        if (InternetConnection.checkConnection(getApplicationContext())) {
            if (usernumberEdit.getText().toString().length() == 10) {
                if (!usernameEdit.getText().toString().isEmpty()) {
                    if (usernameEdit.getText().toString().length() < 2)
                        toast(getApplicationContext(), "Fella! Your Name, Not character");
                    else if (usernameEdit.getText().toString().length() > 8)
                        toast(getApplicationContext(), "Fella! Your Name, Not Family Name");
                    else
                        verifyMobileNumber(usernumberEdit.getText().toString());

                } else
                    toast(getApplicationContext(), "Fella! Enter Your name");
            }
            else
                toast(getApplicationContext(), "Enter valid number");
        }
        else
            internetError(getApplicationContext());
    }

    private void verifyMobileNumber(String mobile) {
        startPhoneNumberVerification(mobile);
        mAuthLayout.setVisibility(View.GONE);
        mVerifyLayout.setVisibility(View.VISIBLE);
        mobileNumber.setText(usernumberEdit.getText().toString());
//        startTimer();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        TimeUnit.SECONDS.toMinutes(1);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Login.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void verifyDatabase(final String userNumber, final String userName) {
        DatabaseReference userRef = Database.getInstance(getApplicationContext()).getAllUserReferences();
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(userNumber)) {
                        Preference.getInstance(getApplicationContext()).setNumber(userNumber);
                        Preference.getInstance(getApplicationContext()).setName(userName);
                        Preference.getInstance(getApplicationContext()).setInviteCode(snapshot.child(userNumber).child(INVITE).getValue().toString());
                        longtoast(getApplicationContext(), "Welcome Back "+userName);
                        callHome();
                    }
                    else {
                        Preference.getInstance(getApplicationContext()).setName(userName);
                        Preference.getInstance(getApplicationContext()).setNumber(userNumber);
                        Database.getInstance(getApplicationContext()).saveUserData();
                        longtoast(getApplicationContext(), "Welcome to GeoLocation "+userName+"! :)");
                        callHome();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    error(getApplicationContext());
                }
            });
        }
        else {
            Preference.getInstance(getApplicationContext()).setName(userName);
            Preference.getInstance(getApplicationContext()).setNumber(userNumber);
            Database.getInstance(getApplicationContext()).saveUserData();
            toast(getApplicationContext(), "Welcome to AntiBunking "+userName+"! Be Ready your sir is watching you :)");
            callHome();
        }
    }
    private boolean validatePhoneNumber() {
        String phoneNumber = usernumberEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getApplicationContext(), "Invalid phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Invalid code.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void callHome() {
        Intent i = new Intent(Login.this, Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}