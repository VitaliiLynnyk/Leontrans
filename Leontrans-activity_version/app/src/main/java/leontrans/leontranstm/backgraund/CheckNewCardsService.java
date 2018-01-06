package leontrans.leontranstm.backgraund;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;

public class CheckNewCardsService extends Service {

    Timer timer;
    ServiceTimerTask serviceTimerTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer != null){
            timer.cancel();
        }
        timer = new Timer();
        serviceTimerTask = new ServiceTimerTask(this);

        timer.schedule(serviceTimerTask, 0,5000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
