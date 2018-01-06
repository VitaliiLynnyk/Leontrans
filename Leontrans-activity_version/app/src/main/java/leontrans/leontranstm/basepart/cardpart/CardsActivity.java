package leontrans.leontranstm.basepart.cardpart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.filters.FilterSwitcherDialogActivity;
import leontrans.leontranstm.utils.Constants;
import leontrans.leontranstm.utils.InternetStatusUtils;
import leontrans.leontranstm.utils.NavigationDrawerMain;
import leontrans.leontranstm.utils.SiteDataParseUtils;

public class CardsActivity extends AppCompatActivity {
    private SiteDataParseUtils siteDataUtils;

    private Toolbar toolbar;
    private Drawer.Result mainNavigationDrawer;

    private ProgressBar loaderView;
    private LinearLayout contentArea;
    LinearLayout.LayoutParams listViewParams;
    LinearLayout footerLinear;

    private ArrayList<JSONObject> arrayListJsonObjectAdvertisement = new ArrayList<>();
    private ArrayList<AdvertisementInfo> arrayListAdvertisement = new ArrayList<>();
    private int numbOfAdvertisement = 10;

    private ListView advertisementListView;
    private Button loadNewCardsBtn;
    private Button btnToUp;
    private AdvertisementAdapter adapter;
    private static long backPressed;

    private Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!InternetStatusUtils.isDeviceOnline(this)){
            showConnectionAlertDialog();
            return;
        }

        setContentView(R.layout.activity_cards);

        //en ru uk
        String language = getSharedPreferences("app_language", MODE_PRIVATE).getString("language","en");
        locale = new Locale("" + language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.advertisement_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainNavigationDrawer = new NavigationDrawerMain(this, toolbar, Constants.NAVMENU_CARDS).getMainNavigationDrawer();

        loaderView = (ProgressBar) findViewById(R.id.loading_spinner);
        contentArea = (LinearLayout) findViewById(R.id.content_area);
        contentArea.setVisibility(View.GONE);

        footerLinear = (LinearLayout) findViewById(R.id.footer_layout);

        siteDataUtils = new SiteDataParseUtils();
        adapter = new AdvertisementAdapter(this,R.layout.list_item_layout,arrayListAdvertisement);

        loadNewCardsBtn = (Button) findViewById(R.id.show_more_bids_btn);
            loadNewCardsBtn.setOnClickListener(getLoadNewCardsBtnListener());

        btnToUp = (Button) findViewById(R.id.go_up_to_list);
            btnToUp.setOnClickListener(getUpButtonClickListener());

        listViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        advertisementListView = (ListView)findViewById(R.id.listView);
            advertisementListView.setAdapter(adapter);
            advertisementListView.setOnScrollListener(getListScrollListener());

        loadNewCardsBtn.setText(R.string.app_name);
        loadNewCardsBtn.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_grey));
        loadNewCardsBtn.setClickable(false);

        btnToUp.setText(R.string.go_up_to_list);
        btnToUp.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_green));

        new LoadCards().execute(0);
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

                SharedPreferences userPasswordSharedPreferences = CardsActivity.this.getSharedPreferences("hashPassword", MODE_PRIVATE);
                String userPassword = userPasswordSharedPreferences.getString("userPassword","");
                int userID = new SiteDataParseUtils().getUserIdByHashpassword("https://leon-trans.com/api/ver1/login.php?action=get_hash_id&hash=" + userPassword);

                arrayListJsonObjectAdvertisement = siteDataUtils.getCardsInformation("https://leon-trans.com/api/ver1/login.php?action=get_bids&limit=" + numbOfAdvertisement + "&user=" + userID, numbOfAdvertisement);

                SharedPreferences lastCardId = getSharedPreferences("lastCardInfo", MODE_PRIVATE);
                lastCardId.edit().putInt("idLastCard", Integer.parseInt(arrayListJsonObjectAdvertisement.get(0).getString("id"))).commit();

                for(int i = integers[0]; i < arrayListJsonObjectAdvertisement.size() ; i ++){
                    Log.d("SOME_TEST_TAG", "id: " + arrayListJsonObjectAdvertisement.get(i).getString("id"));

                    JSONObject advertisementOwnerInfoJSON = siteDataUtils.getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_user&id="
                            +arrayListJsonObjectAdvertisement.get(i).getString("userid_creator"));

                    AdvertisementOwnerInfo advertisementOwnerInfo = new AdvertisementOwnerInfo(advertisementOwnerInfoJSON.getString("phones"), advertisementOwnerInfoJSON.getString("person_type"), getFullName(advertisementOwnerInfoJSON));
                    arrayListAdvertisement.add(i,new AdvertisementInfo(arrayListJsonObjectAdvertisement.get(i), advertisementOwnerInfo ,getApplicationContext(),locale));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();

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

            return result;
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
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ag));
                }
                case "apop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.apop));
                }
                case "apf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.apf));
                }
                case "at":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.at));
                }
                case "atzt":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.atzt));
                }
                case "atov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.atov));
                }
                case "bkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.bkp));
                }
                case "bsok":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.bsok));
                }

                case "by":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.by));
                }

                case "bf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.bf));
                }


                case "b-kpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.b_kpp));
                }

                case "b-kpf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.b_kpf));
                }

                case "b-tp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.b_tp));
                }

                case "wat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.wat));
                }

                case "vk":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vk));
                }


                case "vkkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vkkp));
                }

                case "vkoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vkoop));
                }

                case "vkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vkp));
                }

                case "vktov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vktov));
                }

                case "vkf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vkf));
                }

                case "vo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vo));
                }

                case "vsp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vsp));
                }

                case "vtzov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vtzov));
                }

                case "vtf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vtf));
                }

                case "vf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.vf));
                }

                case "go":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.go));
                }
                case "gtzov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.gtzov));
                }

                case "dat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dat));
                }

                case "dahk":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dahk));
                }

                case "dvat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dvat));
                }

                case "dip":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dip));
                }

                case "dp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dp));
                }

                case "dp2":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dp2));
                }

                case "dpzd":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dpzd));
                }

                case "dpmoy":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dpmoy));
                }

                case "dsp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dsp));
                }

                case "dpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dpp));
                }

                case "dtgo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dtgo));
                }

                case "dtep":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dtep));
                }

                case "dchp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.dchp));
                }

                case "jbk":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.jbk));
                }

                case "zat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.zat));
                }
                case "zogo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.zogo));
                }

                case "zp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.zp));
                }

                case "iaa":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.iaa));
                }

                case "kmp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.kmp));
                }

                case "ip":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ip));
                }

                case "knvmp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.knvmp));
                }

                case "knvo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.knvo));
                }

                case "komp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.komp));
                }

                case "concern":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.concern));
                }

                case "koop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.koop));
                }

                case "koopp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.koopp));
                }
                case "corp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.corp));
                }case "kp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.kp));
                }case "kt":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.kt));
                }case "kfg":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.kfg));
                }case "mbf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mbf));
                }case "mbkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mbkp));
                }case "mp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mp));
                }case "mpzov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mpzov));
                }case "mpvkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mpvkp));
                }case "mpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mpp));
                }case "mspp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.mspp));
                }case "nva":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.nva));
                }
                case "nvo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.nvo));
                }
                case "nvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.nvp));
                }case "nvpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.nvpp));
                }case "nnvpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.nnvpp));
                }case "ntyy":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ntyy));
                }case "ob":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ob));
                }case "okoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.okoop));
                }case "ooo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ooo));
                }case "pii":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pii));
                }case "pop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pop));
                }case "pap":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pap));
                }case "pat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pat));
                }case "paf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.paf));
                }case "pbk":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pbk));
                }case "pbmp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pbmp));
                }
                case "pbp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pbp));
                }
                case "pvkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvkp));
                }case "pvkf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvkf));
                }case "pvtp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvtp));
                }case "pvtf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvtf));
                }case "pvgp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvgp));
                }case "pvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvp));
                }case "pvf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pvf));
                }case "pgo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pgo));
                }case "pz":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pz));
                }case "pzvkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pzvkp));
                }case "pzii":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pzii));
                }case "prid":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.prid));
                }case "pkvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pkvp));
                }case "pmp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pmp));
                }case "pnvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pnvp));
                }case "pnvf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pnvf));
                }case "po":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.po));
                }case "posp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.posp));
                }case "pog":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pog));
                }case "pp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pp));
                }case "ppbf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ppbf));
                }case "ppf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ppf));
                }case "prat":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.prat));
                }case "prbp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.prbp));
                }case "predst":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.predst));
                }case "prsp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.prsp));
                }case "psp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.psp));
                }case "ptep":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ptep));
                }case "ptvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ptvp));
                }case "ptmp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ptmp));
                }case "ptp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ptp));
                }case "pyfsi":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pyfsi));
                }case "pf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pf));
                }case "pbkoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.pbkoop));
                }case "svkoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.svkoop));
                }case "svf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.svf));
                }case "sg":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sg));
                }case "sgpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sgpp));
                }case "smpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.smpp));
                }case "sokoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sokoop));
                }case "sp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sp));
                }case "spzii":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.spzii));
                }case "sskoop":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sskoop));
                }case "st":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.st));
                }case "stov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.stov));
                }case "sfg":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.sfg));
                }case "tepp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tepp));
                }case "tvkp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tvkp));
                }case "tvo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tvo));
                }case "tdv":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tdv));
                }case "tov":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tov));
                }case "tovzii":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tovzii));
                }case "tpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.tpp));
                }case "ttpp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ttpp));
                }case "ttpf":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ttpf));
                }case "yvp":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.yvp));
                }case "ydppz":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.ydppz));
                }case "fg":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.fg));
                }case "fgvs":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.fgvs));
                }case "fili":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.fili));
                }case "firm":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.firm));
                }case "fo":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.fo));
                }case "hk":{
                    return splitStrings(CardsActivity.this.getResources().getString(R.string.hk));
                }
                default: return "";
            }
        }

        private String splitStrings(String string){
            if (string.indexOf("(") > 0) return string.substring(0,string.indexOf("("));
            else return string;
        }
    }

    private AbsListView.OnScrollListener getListScrollListener(){
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem >= totalItemCount-1){
                    loadNewCardsBtn.setText(R.string.show_new_cards_btn);
                    loadNewCardsBtn.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_green));
                    btnToUp.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_grey));
                    loadNewCardsBtn.setClickable(true);
                }else{
                    loadNewCardsBtn.setText(R.string.app_name);
                    loadNewCardsBtn.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_grey));
                    btnToUp.setBackgroundColor(CardsActivity.this.getResources().getColor(R.color.leon_green));
                    loadNewCardsBtn.setClickable(false);
                }
            }
        };
    }

    private View.OnClickListener getLoadNewCardsBtnListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!InternetStatusUtils.isDeviceOnline(CardsActivity.this)){
                    Toast.makeText(CardsActivity.this, CardsActivity.this.getResources().getString(R.string.internet_dialog_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                numbOfAdvertisement += 10;
                new LoadCards().execute(numbOfAdvertisement-10);
            }
        };
    }

    private View.OnClickListener getUpButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advertisementListView.setSelection(0);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards_activity_meny,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.reloadCardActivityMenuBtn:{
                intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case R.id.showFilterNavigationDrawer:{
                if (!InternetStatusUtils.isDeviceOnline(CardsActivity.this)){
                    Toast.makeText(CardsActivity.this, CardsActivity.this.getResources().getString(R.string.internet_dialog_message), Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                intent = new Intent(CardsActivity.this, FilterSwitcherDialogActivity.class);
                break;
            }

            default:{
                intent = getIntent();
            }
        }

        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void showConnectionAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CardsActivity.this);
        builder.setTitle(CardsActivity.this.getResources().getString(R.string.internet_dialog_title))
                .setMessage(CardsActivity.this.getResources().getString(R.string.internet_dialog_message))
                .setIcon(R.drawable.icon_internet_disabled)
                .setCancelable(false)
                .setNegativeButton(CardsActivity.this.getResources().getString(R.string.refresh),
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

    public void onBackPressed(){
        if(mainNavigationDrawer.isDrawerOpen()){
            mainNavigationDrawer.closeDrawer();
        }
        else{
            if (backPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.back_pressed), Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        }
    }
}
