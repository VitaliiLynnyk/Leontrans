package leontrans.leontranstm.backgraund;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.launching.LauncherActivity;
import leontrans.leontranstm.utils.SiteDataParseUtils;

import static android.content.Context.MODE_PRIVATE;

public class ServiceTimerTask extends TimerTask {

    private Context context;

    public ServiceTimerTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        SharedPreferences lastCardIdSharedPreferences = context.getSharedPreferences("lastCardInfo", MODE_PRIVATE);
        int lastCardId = lastCardIdSharedPreferences.getInt("idLastCard",-1);
        int checkedCardId = -1;

        SharedPreferences userPasswordSharedPreferences = context.getSharedPreferences("hashPassword", MODE_PRIVATE);
        String userPassword = userPasswordSharedPreferences.getString("userPassword","");
        int userID = new SiteDataParseUtils().getUserIdByHashpassword("https://leon-trans.com/api/ver1/login.php?action=get_hash_id&hash=" + userPassword);

        ArrayList<JSONObject> jsonObjectArrayList = new SiteDataParseUtils().getCardsInformation("https://leon-trans.com/api/ver1/login.php?action=get_bids&limit=" + 1 + "&user=" + userID, 1);

        if (jsonObjectArrayList.size() <= 0) return;

        try {
            checkedCardId = Integer.parseInt(jsonObjectArrayList.get(0).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (lastCardId != checkedCardId){
            SharedPreferences cardSharedPreferences = context.getSharedPreferences("lastCardInfo", MODE_PRIVATE);
            cardSharedPreferences.edit().putInt("idLastCard", checkedCardId).commit();

            Intent notificationIntent = new Intent(context, LauncherActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Resources res = context.getResources();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelNotify");

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.logo_volumetric)
                    .setContentTitle(res.getString(R.string.notification_title))
                    .setContentText(res.getString(R.string.notification_content))
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo_volumetric))
                    .setTicker(res.getString(R.string.notification_warning))
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(101, builder.build());
        }
    }

}
