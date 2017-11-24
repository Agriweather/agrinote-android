package tw.com.agrinote.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.User;

/**
 * Created by orc59 on 2017/11/1.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignupActivity";

    private ImageButton mBackBtn;
    private ImageView mPic;
    private EditText mName;
    private EditText mAccount;
    private EditText mPassword;
    private EditText mConfirm;
    private Button mSignup;

    private String mCurrentPicPath;
    private Context mContext;

    private AgrinoteDBHelper mDatabase;

    private void doSignup(){
        User user = new User();
        user.setAccount(mAccount.getText().toString());
        user.setName(mAccount.getText().toString());
        user.setToken(mPassword.getText().toString());
        user.setPicPath(mCurrentPicPath);
        Log.i(TAG, user.toString());

        mDatabase.createUser(user);
        User tmp = mDatabase.getUser(mAccount.getText().toString());
        Log.i(TAG, tmp.toString());

        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        mContext = this;
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mPic = (ImageView) findViewById(R.id.pic);
        mAccount = (EditText) findViewById(R.id.account);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirm = (EditText) findViewById(R.id.confirm);

        mSignup = (Button) findViewById(R.id.signup);
        mSignup.setOnClickListener(this);

        mDatabase = AgrinoteDBHelper.getInstance(mContext);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.signup:
                doSignup();
                break;
        }
    }


}
