package leontrans.leontranstm.basepart.favouritespart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.AdvertisementInfo;
import leontrans.leontranstm.basepart.cardpart.AdvertisementOwnerInfo;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.utils.Constants;
import leontrans.leontranstm.utils.InternetStatusUtils;
import leontrans.leontranstm.utils.NavigationDrawerMain;
import leontrans.leontranstm.utils.SiteDataParseUtils;

import static leontrans.leontranstm.basepart.cardpart.AdvertisementAdapter.dbHelper;

public class FavouriteCardsActivity extends AppCompatActivity {

    private SiteDataParseUtils siteDataUtils;

    private Toolbar toolbar;
    private Drawer.Result mainNavigationDrawer;

    private ProgressBar loaderView;
    private ConstraintLayout contentArea;

    private ArrayList<JSONObject> arrayListJsonObjectAdvertisement = new ArrayList<>();

    private ListView advertisementListView;

    public AdvertisementAdapterSelectedItem selected_item_adapter;
    public ArrayList<AdvertisementInfo> arrayListSelectedItem = new ArrayList<>();
    private ArrayList<DBinformation> informationList;

    private Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!InternetStatusUtils.isDeviceOnline(this)){
            showConnectionAlertDialog();
            return;
        }

        setContentView(R.layout.activity_favourite_cards);
        //en ru uk
        String language = getSharedPreferences("app_language", MODE_PRIVATE).getString("language","en");
        locale = new Locale("" + language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.favourite_cards_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainNavigationDrawer = new NavigationDrawerMain(this, toolbar, Constants.NAVMENU_FAQ).getMainNavigationDrawer();

        loaderView = (ProgressBar) findViewById(R.id.loading_spinner);
        contentArea = (ConstraintLayout) findViewById(R.id.content_area);
        contentArea.setVisibility(View.GONE);

        siteDataUtils = new SiteDataParseUtils();
        selected_item_adapter = new AdvertisementAdapterSelectedItem(this,R.layout.list_item_layout,arrayListSelectedItem);

        advertisementListView = (ListView)findViewById(R.id.listView);
        advertisementListView.setAdapter(selected_item_adapter);
        if(arrayListSelectedItem.isEmpty()){
            informationList = dbHelper.getAllTODOLIST();
            new LoadCards().execute(0);
        }else{
            arrayListSelectedItem.clear();
            arrayListSelectedItem.removeAll(arrayListSelectedItem);
            informationList = dbHelper.getAllTODOLIST();
            new LoadCards().execute(0);
        }

    }


    private class LoadCards extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                for(int i = 0 ; i < informationList.size();i++){
                    arrayListJsonObjectAdvertisement.add(i,siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_bid&id=" + informationList.get(i).getId_selected_item()));
                }

                for(int i = integers[0]; i < arrayListJsonObjectAdvertisement.size() ; i ++){
                    JSONObject advertisementOwnerInfoJSON = siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_user&id="
                            +arrayListJsonObjectAdvertisement.get(i).getString("userid_creator"));

                    AdvertisementOwnerInfo advertisementOwnerInfo = new AdvertisementOwnerInfo(advertisementOwnerInfoJSON.getString("phones"), advertisementOwnerInfoJSON.getString("person_type"), getFullName(advertisementOwnerInfoJSON));
                    arrayListSelectedItem.add(i,new AdvertisementInfo(arrayListJsonObjectAdvertisement.get(i), advertisementOwnerInfo ,getApplicationContext(),locale));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            selected_item_adapter.notifyDataSetChanged();

            loaderView.setVisibility(View.GONE);
            contentArea.setVisibility(View.VISIBLE);
        }

        private String getFullName(JSONObject advertisementOwnerInfo) throws JSONException {
            JSONObject userCreatorEmploeeOwner;
            String result = "";

            switch (advertisementOwnerInfo.getString("person_type")){
                case "individual":{
                    result = advertisementOwnerInfo.getString("full_name");
                    break;
                }
                case "entity":{
                    result = advertisementOwnerInfo.getString("nomination_prefix") + "\n" +advertisementOwnerInfo.getString("nomination_name");
                    break;
                }
                case "fop":{
                    result = advertisementOwnerInfo.getString("nomination_prefix") + " " +advertisementOwnerInfo.getString("nomination_name");
                    break;
                }
                case "employee":{
                    userCreatorEmploeeOwner = siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_user&id=" + advertisementOwnerInfo.getString("employee_owner"));

                    result = "(" + userCreatorEmploeeOwner.getString("full_name")+ userCreatorEmploeeOwner.getString("nomination_prefix")+ " " +userCreatorEmploeeOwner.getString("nomination_name")
                            + ")\n " + advertisementOwnerInfo.getString("full_name")+ advertisementOwnerInfo.getString("nomination_prefix") + " " +advertisementOwnerInfo.getString("nomination_name");

                    break;
                }
            }
            return result;
        }
    }

    private View.OnClickListener getUpButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advertisementListView.setSelectionAfterHeaderView();
            }
        };
    }

    public void onBackPressed(){
        if(mainNavigationDrawer.isDrawerOpen()){
            mainNavigationDrawer.closeDrawer();
        }
        else{
            startActivity(new Intent(FavouriteCardsActivity.this, CardsActivity.class));
        }
    }

    private void showConnectionAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteCardsActivity.this);
        builder.setTitle("You are offline!")
                .setMessage("Check your internet connection and try again.")
                .setIcon(R.drawable.icon_internet_disabled)
                .setCancelable(false)
                .setNegativeButton("Refresh",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent;
                                intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                overridePendingTransition(0, 0);

                                dialog.cancel();
                                finish();
                                startActivity(intent);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}