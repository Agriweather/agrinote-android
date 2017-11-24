package tw.com.agrinote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.User;

/**
 * Created by orc59 on 2017/10/20.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private Button btnLogin;
    private Button btnSignup;
    private TextView btnForgetPassword;
    private YoYo.YoYoString rope;
    private ImageView logo;

    private EditText mAccount;
    private EditText mPassword;

    private ImageButton mGoogleSignin;
    private ImageButton mFacebookSignin;

    private Context mContext;
    private AgrinoteDBHelper mDatabase;

    private void startAnimation() {
        rope = YoYo.with(Techniques.values()[1])
                .duration(3000)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(logo);
    }

    private void validateUser() {
        String account = mAccount.getText().toString();
        String password = mPassword.getText().toString();

        User user = mDatabase.getUser(account);

        if (null == user) {
            Log.i(TAG, "user not exist");
            return;
        }

        Log.i(TAG, user.toString());
        if (user.validate(password)) {
            user.setLogin(true);
            mDatabase.updateUser(user);
            goToMainActivity(user);
        } else {
            Log.i(TAG, "user invalidate ");
        }

    }

    private void goToSignupActivity() {
        Intent intent = new Intent();
        intent.setClass(this, SignupActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity(User user) {
        Gson gson = new Gson();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra("user", gson.toJson(user));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mAccount = (EditText) findViewById(R.id.editText_account);
        mPassword = (EditText) findViewById(R.id.editText_password);

        btnForgetPassword = (TextView) findViewById(R.id.btn_forget_password);
        btnForgetPassword.setOnClickListener(this);

        mGoogleSignin = (ImageButton) findViewById(R.id.btn_google_plus_login);
        mGoogleSignin.setOnClickListener(this);
        mFacebookSignin = (ImageButton) findViewById(R.id.btn_facebool_login);
        mFacebookSignin.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnSignup = (Button) findViewById(R.id.btn_sign_up);
        btnSignup.setOnClickListener(this);

        logo = (ImageView) findViewById(R.id.logo);

        mContext = this;
        mDatabase = AgrinoteDBHelper.getInstance(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                goToSignupActivity();
                break;
            case R.id.btn_login:
                validateUser();
                break;
        }
    }


}
