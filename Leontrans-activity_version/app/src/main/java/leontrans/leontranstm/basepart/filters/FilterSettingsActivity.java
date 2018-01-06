package leontrans.leontranstm.basepart.filters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.basepart.filters.editor.FilterEditActivity;
import leontrans.leontranstm.utils.Constants;
import leontrans.leontranstm.utils.InternetStatusUtils;
import leontrans.leontranstm.utils.NavigationDrawerMain;

public class FilterSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Drawer.Result mainNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.filter_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainNavigationDrawer = new NavigationDrawerMain(this, toolbar, Constants.NAVMENU_FILTER_SETTINGS).getMainNavigationDrawer();

        ((Button) findViewById(R.id.filter_1)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_2)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_3)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_4)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_5)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_6)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_7)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_8)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_9)).setOnClickListener(this);
        ((Button) findViewById(R.id.filter_10)).setOnClickListener(this);
    }

    public void onBackPressed(){
        if(mainNavigationDrawer.isDrawerOpen()){
            mainNavigationDrawer.closeDrawer();
        }
        else{
            startActivity(new Intent(FilterSettingsActivity.this, CardsActivity.class));
        }
    }

    @Override
    public void onClick(View view) {

        if (!InternetStatusUtils.isDeviceOnline(this)){
            showConnectionAlertDialog();
            return;
        }

        Intent intent = new Intent(FilterSettingsActivity.this, FilterEditActivity.class);

        switch (view.getId()){
            case R.id.filter_1:{
                intent.putExtra("notifyId", "notify_1");
                break;
            }
            case R.id.filter_2:{
                intent.putExtra("notifyId", "notify_2");
                break;
            }
            case R.id.filter_3:{
                intent.putExtra("notifyId", "notify_3");
                break;
            }
            case R.id.filter_4:{
                intent.putExtra("notifyId", "notify_4");
                break;
            }
            case R.id.filter_5:{
                intent.putExtra("notifyId", "notify_5");
                break;
            }
            case R.id.filter_6:{
                intent.putExtra("notifyId", "notify_6");
                break;
            }
            case R.id.filter_7:{
                intent.putExtra("notifyId", "notify_7");
                break;
            }
            case R.id.filter_8:{
                intent.putExtra("notifyId", "notify_8");
                break;
            }
            case R.id.filter_9:{
                intent.putExtra("notifyId", "notify_9");
                break;
            }
            case R.id.filter_10:{
                intent.putExtra("notifyId", "notify_10");
                break;
            }
        }

        startActivity(intent);
    }

    private void showConnectionAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FilterSettingsActivity.this);
        builder.setTitle(FilterSettingsActivity.this.getResources().getString(R.string.internet_dialog_title))
                .setMessage(FilterSettingsActivity.this.getResources().getString(R.string.internet_dialog_message))
                .setIcon(R.drawable.icon_internet_disabled)
                .setCancelable(false)
                .setNegativeButton("Return",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
