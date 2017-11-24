package tw.com.agrinote.Task;

import android.os.AsyncTask;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by orc59 on 2017/11/3.
 */

public class PreStartingTask extends AsyncTask<Void, Void, Void>{

    private TaskCompletedListener eventListener;
    private RotateLoading innerRotateLoading;
    private RotateLoading outterRotateLoading;
    private ImageView logo;
    private YoYo.YoYoString rope;

    public PreStartingTask(RotateLoading inner, RotateLoading outter, ImageView logo){
        innerRotateLoading = inner;
        outterRotateLoading = outter;
        this.logo = logo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        innerRotateLoading.start();
        outterRotateLoading.start();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        innerRotateLoading.stop();
        outterRotateLoading.stop();
        startAnimation();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        publishProgress();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dispitchEvent();
    }

    private void dispitchEvent(){
        if(null != eventListener){
            eventListener.onComplete();
        }
    }

    private void startAnimation() {
        rope = YoYo.with(Techniques.values()[2])
                .duration(1200)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(logo);
    }

    public void setEventListener(TaskCompletedListener listener){
        this.eventListener = listener;
    }



    public interface TaskCompletedListener {
        public void onComplete();
    }
}
