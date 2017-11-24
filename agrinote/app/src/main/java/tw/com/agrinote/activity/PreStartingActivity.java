package tw.com.agrinote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

import tw.com.agrinote.R;
import tw.com.agrinote.Task.PreStartingTask;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.User;

/**
 * Created by orc59 on 2017/11/3.
 */

public class PreStartingActivity extends AppCompatActivity {
    private RotateLoading innerRotateLoading;
    private RotateLoading outterRotateLoading;
    private ImageView logo;

    private PreStartingTask mTask;

    private Context mContext;
    private AgrinoteDBHelper mDatabase;

    private void toLogin(){
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toFarmInfo(User user){
        Gson gson = new Gson();

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra("user", gson.toJson(user));

        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestarting);
        innerRotateLoading = (RotateLoading) findViewById(R.id.rotateloading1);
        outterRotateLoading = (RotateLoading) findViewById(R.id.rotateloading2);
        logo = (ImageView) findViewById(R.id.logo);

        mTask = new PreStartingTask(innerRotateLoading, outterRotateLoading, logo);
        mTask.setEventListener(new PreStartingTask.TaskCompletedListener() {
            @Override
            public void onComplete() {
                List<User> users = mDatabase.getAllUser();
                if(users.size() == 0){
                    toLogin();
                    return;
                }

                boolean isLogin = false;
                for(User user : users){
                    if(user.isLogin()){
                        toFarmInfo(user);
                        return;
                    }
                }

                toLogin();
            }
        });

        mContext = this;
        mDatabase = AgrinoteDBHelper.getInstance(mContext);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



}
