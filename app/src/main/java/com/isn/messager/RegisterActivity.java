package com.isn.messager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isn.services.Client;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * user register
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegisterTask = null;
    private VerifyCodeTask mVerifyCodeTask = null;

    // UI references.
    private EditText mMobileView;
    private EditText mVerifyCodeView;
    private EditText mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private Button mGetVerifyCodeButton;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private Handler mHandler = null;

    private int count = 60;

    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s

    private static final int UPDATE_TEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mMobileView = (EditText) findViewById(R.id.reg_mobile);
        mVerifyCodeView = (EditText) findViewById(R.id.reg_verify_code);
        mNameView = (EditText) findViewById(R.id.reg_name);
        mPasswordView = (EditText) findViewById(R.id.reg_password);
        mVerifyCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.mobile_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mGetVerifyCodeButton = (Button) findViewById(R.id.get_verify_code);
        mGetVerifyCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getVerifyCode();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXT:
                        updateText();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void startTimer(){

        stopTimer();

        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    count--;
                    sendMessage(UPDATE_TEXT);
                    if(count == 0 || count == 60 ){
                        stopTimer();
                    }
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, delay, period);

    }

    private void stopTimer(){

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        count = 60;

    }

    public void sendMessage(int id){
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }

    private void updateText(){
        if(count != 0 && count != 60) {
            mGetVerifyCodeButton.setEnabled(false);
            mGetVerifyCodeButton.setText(String.valueOf(count) + getString(R.string.button_verify_code_timer));
        }else{
            mGetVerifyCodeButton.setEnabled(true);
            mGetVerifyCodeButton.setText(R.string.button_verify_code);
        }
    }

    private void getVerifyCode(){
        if (mVerifyCodeTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mobile.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mVerifyCodeTask = new VerifyCodeTask(mMobileView.getText().toString().trim());
            mVerifyCodeTask.execute((Void)null);
            startTimer();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);
        mVerifyCodeView.setError(null);
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String verifyCode = mVerifyCodeView.getText().toString();
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(verifyCode)) {
            mVerifyCodeView.setError(getString(R.string.error_field_required));
            focusView = mVerifyCodeView;
            cancel = true;
        } else if (!isVerifyCodeValid(verifyCode)) {
            mVerifyCodeView.setError(getString(R.string.error_invalid_verify_code));
            focusView = mVerifyCodeView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid mobile.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegisterTask = new UserRegisterTask(mobile, name, password, verifyCode);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isMobileValid(String mobile) {
        return mobile.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isVerifyCodeValid(String verifyCode) {
        return verifyCode.length() == 6;
    }

    private boolean isNameValid(String name) {
        return name.length() < 50;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMobile;
        private final String mName;
        private final String mPassword;
        private final String mVerificationCode;
        private long result;

        UserRegisterTask(String mobile, String name, String password, String verificationCode) {
            mMobile = mobile;
            mName = name;
            mPassword = password;
            mVerificationCode = verificationCode;
            result = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                result = Client.getInstance().register(mMobile, mName, mPassword, mVerificationCode);
                if(result < 0){
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                if(result == -1) {
                    mMobileView.setError(getString(R.string.error_user_exist));
                    mMobileView.requestFocus();
                }else if (result == -2) {
                    mVerifyCodeView.setError(getString(R.string.error_verify_code_incorrect));
                    mVerifyCodeView.requestFocus();
                }else {
                    //Toast.makeText(getApplicationContext(), R.string.error_register_failed, Toast.LENGTH_LONG).show();
                    Snackbar.make(mRegisterFormView, R.string.error_register_failed, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }

    public class VerifyCodeTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMobile;

        VerifyCodeTask(String mobile) {
            mMobile = mobile;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Client.getInstance().requestVerificationCode(mMobile);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mVerifyCodeTask = null;

            if (!success) {
                Toast.makeText(getApplicationContext(), R.string.error_request_verify_code_failed, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mVerifyCodeTask = null;
        }
    }


}

