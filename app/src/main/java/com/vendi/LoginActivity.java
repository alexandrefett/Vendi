package com.vendi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.vendi.model.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //private UserLoginTask mAuthTask = null;
    private EditText mCode;
    private static final String TAG = "--->>> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        mCode = (EditText) findViewById(R.id.code);
        Button mUserSignInButton = (Button) findViewById(R.id.sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCode.setError(getString(R.string.error_field_required));
                User u = Prefs.getUser();
                //attemptLogin(u.uuid, mCode.getText().toString());
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        setResult(Activity.RESULT_CANCELED);
        finish();
        return true;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
