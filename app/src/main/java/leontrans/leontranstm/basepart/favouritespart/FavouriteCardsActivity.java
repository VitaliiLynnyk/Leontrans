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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.AdvertisementAdapter;
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
    private LinearLayout contentArea;

    private ArrayList<JSONObject> arrayListJsonObjectAdvertisement = new ArrayList<>();

    private ListView advertisementListView;

    public AdvertisementSelectedAdapter selected_item_adapter;
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
        contentArea = (LinearLayout) findViewById(R.id.content_area);
        contentArea.setVisibility(View.GONE);

        siteDataUtils = new SiteDataParseUtils();
        selected_item_adapter = new AdvertisementSelectedAdapter(this,R.layout.list_item_layout,arrayListSelectedItem);

        advertisementListView = (ListView)findViewById(R.id.listView);
        advertisementListView.setAdapter(selected_item_adapter);
        advertisementListView.setEmptyView((TextView) findViewById(R.id.empty_favourite));

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
                    JSONObject jsonObject = siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_bid&id=" + informationList.get(i).getId_selected_item());
                    if (jsonObject == null) continue;
                    arrayListJsonObjectAdvertisement.add(i, jsonObject);
                }

                Log.d("TEST_SIZE_WORK", "doInBackground: " + arrayListJsonObjectAdvertisement.size());

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


            if (advertisementOwnerInfo.getString("person_type").equals("employee")){
                userCreatorEmploeeOwner = siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_user&id=" + advertisementOwnerInfo.getString("employee_owner"));

                if (userCreatorEmploeeOwner.getString("full_name").equals("")){
                    result = "(" + nominationPrefixTranslation(userCreatorEmploeeOwner.getString("nomination_prefix"))+ " " +userCreatorEmploeeOwner.getString("nomination_name")
                            + ")\n" + getFullOrNomName(advertisementOwnerInfo);
                }
                else result = "(" + userCreatorEmploeeOwner.getString("full_name") + ")\n" + getFullOrNomName(advertisementOwnerInfo);
            }
            else {
                result = getFullOrNomName(advertisementOwnerInfo);
            }

            return result.replace("&quot;", "\"");
        }

        private String getFullOrNomName(JSONObject jsonObject) throws JSONException{
            String result = "";

            if (jsonObject.getString("full_name").equals("")){
                result = nominationPrefixTranslation(jsonObject.getString("nomination_prefix")) + " " +jsonObject.getString("nomination_name");
            }
            else result = jsonObject.getString("full_name");

            return result;
        }

        private String nominationPrefixTranslation(String nominationPrefix){
            switch(nominationPrefix){
                case "ag":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ag));
                }
                case "apop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.apop));
                }
                case "apf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.apf));
                }
                case "at":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.at));
                }
                case "atzt":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.atzt));
                }
                case "atov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.atov));
                }
                case "bkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.bkp));
                }
                case "bsok":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.bsok));
                }

                case "by":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.by));
                }

                case "bf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.bf));
                }


                case "b-kpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.b_kpp));
                }

                case "b-kpf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.b_kpf));
                }

                case "b-tp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.b_tp));
                }

                case "wat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.wat));
                }

                case "vk":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vk));
                }


                case "vkkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vkkp));
                }

                case "vkoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vkoop));
                }

                case "vkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vkp));
                }

                case "vktov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vktov));
                }

                case "vkf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vkf));
                }

                case "vo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vo));
                }

                case "vsp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vsp));
                }

                case "vtzov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vtzov));
                }

                case "vtf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vtf));
                }

                case "vf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.vf));
                }

                case "go":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.go));
                }
                case "gtzov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.gtzov));
                }

                case "dat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dat));
                }

                case "dahk":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dahk));
                }

                case "dvat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dvat));
                }

                case "dip":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dip));
                }

                case "dp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dp));
                }

                case "dp2":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dp2));
                }

                case "dpzd":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dpzd));
                }

                case "dpmoy":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dpmoy));
                }

                case "dsp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dsp));
                }

                case "dpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dpp));
                }

                case "dtgo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dtgo));
                }

                case "dtep":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dtep));
                }

                case "dchp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.dchp));
                }

                case "jbk":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.jbk));
                }

                case "zat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.zat));
                }
                case "zogo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.zogo));
                }

                case "zp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.zp));
                }

                case "iaa":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.iaa));
                }

                case "kmp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.kmp));
                }

                case "ip":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ip));
                }

                case "knvmp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.knvmp));
                }

                case "knvo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.knvo));
                }

                case "komp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.komp));
                }

                case "concern":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.concern));
                }

                case "koop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.koop));
                }

                case "koopp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.koopp));
                }
                case "corp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.corp));
                }case "kp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.kp));
                }case "kt":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.kt));
                }case "kfg":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.kfg));
                }case "mbf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mbf));
                }case "mbkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mbkp));
                }case "mp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mp));
                }case "mpzov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mpzov));
                }case "mpvkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mpvkp));
                }case "mpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mpp));
                }case "mspp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.mspp));
                }case "nva":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.nva));
                }
                case "nvo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.nvo));
                }
                case "nvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.nvp));
                }case "nvpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.nvpp));
                }case "nnvpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.nnvpp));
                }case "ntyy":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ntyy));
                }case "ob":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ob));
                }case "okoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.okoop));
                }case "ooo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ooo));
                }case "pii":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pii));
                }case "pop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pop));
                }case "pap":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pap));
                }case "pat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pat));
                }case "paf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.paf));
                }case "pbk":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pbk));
                }case "pbmp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pbmp));
                }
                case "pbp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pbp));
                }
                case "pvkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvkp));
                }case "pvkf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvkf));
                }case "pvtp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvtp));
                }case "pvtf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvtf));
                }case "pvgp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvgp));
                }case "pvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvp));
                }case "pvf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pvf));
                }case "pgo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pgo));
                }case "pz":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pz));
                }case "pzvkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pzvkp));
                }case "pzii":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pzii));
                }case "prid":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.prid));
                }case "pkvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pkvp));
                }case "pmp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pmp));
                }case "pnvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pnvp));
                }case "pnvf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pnvf));
                }case "po":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.po));
                }case "posp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.posp));
                }case "pog":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pog));
                }case "pp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pp));
                }case "ppbf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ppbf));
                }case "ppf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ppf));
                }case "prat":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.prat));
                }case "prbp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.prbp));
                }case "predst":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.predst));
                }case "prsp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.prsp));
                }case "psp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.psp));
                }case "ptep":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ptep));
                }case "ptvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ptvp));
                }case "ptmp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ptmp));
                }case "ptp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ptp));
                }case "pyfsi":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pyfsi));
                }case "pf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pf));
                }case "pbkoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.pbkoop));
                }case "svkoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.svkoop));
                }case "svf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.svf));
                }case "sg":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sg));
                }case "sgpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sgpp));
                }case "smpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.smpp));
                }case "sokoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sokoop));
                }case "sp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sp));
                }case "spzii":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.spzii));
                }case "sskoop":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sskoop));
                }case "st":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.st));
                }case "stov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.stov));
                }case "sfg":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.sfg));
                }case "tepp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tepp));
                }case "tvkp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tvkp));
                }case "tvo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tvo));
                }case "tdv":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tdv));
                }case "tov":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tov));
                }case "tovzii":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tovzii));
                }case "tpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.tpp));
                }case "ttpp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ttpp));
                }case "ttpf":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ttpf));
                }case "yvp":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.yvp));
                }case "ydppz":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.ydppz));
                }case "fg":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.fg));
                }case "fgvs":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.fgvs));
                }case "fili":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.fili));
                }case "firm":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.firm));
                }case "fo":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.fo));
                }case "hk":{
                    return splitStrings(FavouriteCardsActivity.this.getResources().getString(R.string.hk));
                }
                default: return "";
            }
        }

        private String splitStrings(String string){
            if (string.indexOf("(") > 0) return string.substring(0,string.indexOf("("));
            else return string;
        }
    }

    public void onBackPressed(){
        if(mainNavigationDrawer.isDrawerOpen()){
            mainNavigationDrawer.closeDrawer();
        }
        else{
            Intent intent = new Intent(FavouriteCardsActivity.this, CardsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
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