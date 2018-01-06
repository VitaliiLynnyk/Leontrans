package leontrans.leontranstm.basepart.userprofile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.utils.SiteDataParseUtils;
import leontrans.leontranstm.utils.SystemServicesUtils;

public class UserCardOwenerProfile extends AppCompatActivity{
    private int userID;

    protected Toolbar toolbar;
    protected ProgressBar loaderView;
    protected ScrollView contentArea;
    protected int animationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.user_card_creator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loaderView = (ProgressBar) findViewById(R.id.loading_spinner);
        contentArea = (ScrollView) findViewById(R.id.content_area);
        contentArea.setVisibility(View.GONE);
        animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        userID = getIntent().getIntExtra("userID",-1);
        if (userID > 0) new LoadFragmentData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
        }
        return true;
    }

    private class LoadFragmentData extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            return new SiteDataParseUtils().getSiteRequestResult("https://leon-trans.com/api/ver1/login.php?action=get_user&id=" + userID);
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            try {
                JSONObject dataJson = new JSONObject(jsonStr);
                final SystemServicesUtils systemServicesUtils = new SystemServicesUtils();

                Button employeeOwner = (Button) findViewById(R.id.employee_owner);

                TextView userNameInfo = (TextView) findViewById(R.id.underImageInfo);
                TextView TV_name_value = (TextView) findViewById(R.id.TV_name_value);
                final TextView TV_city_value = (TextView) findViewById(R.id.TV_city_value);
                final TextView TV_email_value = (TextView) findViewById(R.id.TV_email_value);
                final TextView TV_telephone_value = (TextView) findViewById(R.id.TV_telephone_value);
                TextView TV_skype_value = (TextView) findViewById(R.id.TV_skype_value);
                TextView TV_icq_value = (TextView) findViewById(R.id.TV_icq_value);
                final TextView TV_website_value = (TextView) findViewById(R.id.TV_website_value);
                TextView TV_occupation_value = (TextView) findViewById(R.id.TV_occupation_value);
                TextView TV_occupation_type_value = (TextView) findViewById(R.id.TV_occupation_type_value);
                TextView TV_occupation_description_value = (TextView) findViewById(R.id.TV_occupation_description_value);
                TextView TV_register_date_value = (TextView) findViewById(R.id.TV_register_date_value);
                TextView TV_last_online_value = (TextView) findViewById(R.id.TV_last_online_value);
                ImageView userAvatarImageView = (ImageView) findViewById(R.id.userAvatarImageView);


                Picasso.with(UserCardOwenerProfile.this)
                        .load("https://leon-trans.com/uploads/user-avatars/" + dataJson.getString("avatar"))
                        .error(R.drawable.default_avatar)
                        .into(userAvatarImageView);

                userNameInfo.setText(getFullName(dataJson) + "\n" + dataJson.getString("login"));
                TV_name_value.setText(getFullName(dataJson));

                TV_city_value.setText(dataJson.getString("location_city"));
                TV_email_value.setText(dataJson.getString("email"));
                TV_telephone_value.setText(dataJson.getString("phones"));
                TV_skype_value.setText(dataJson.getString("skype"));
                TV_icq_value.setText(dataJson.getString("icq"));
                TV_website_value.setText(dataJson.getString("website"));
                TV_occupation_value.setText(getOcupation(dataJson.getString("metier_type")));
                TV_occupation_type_value.setText(getActivityKind(dataJson.getString("activity_kind")));
                TV_occupation_description_value.setText(dataJson.getString("activity_desc"));
                TV_register_date_value.setText(makeDate(dataJson.getString("date_registry")));
                TV_last_online_value.setText(makeDate(dataJson.getString("date_last_action")));

                TV_telephone_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
                        systemServicesUtils.startDial(UserCardOwenerProfile.this, TV_telephone_value.getText().toString());

                    }
                });

                TV_email_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
                        systemServicesUtils.startMail(UserCardOwenerProfile.this, TV_email_value.getText().toString());
                    }
                });

                TV_website_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TV_website_value.getText().toString().isEmpty()) {
                            startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
                            systemServicesUtils.startInternetBrowser(UserCardOwenerProfile.this, TV_website_value.getText().toString());
                        }
                    }
                });

                TV_city_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TV_city_value.getText().toString().isEmpty()) {
                            startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
                            systemServicesUtils.startMaps(UserCardOwenerProfile.this, TV_city_value.getText().toString());
                        }
                    }
                });

                String employeeOwnerName = getUserOwnerName(dataJson);
                employeeOwner.setOnClickListener(showUserOwnerProfile(Integer.parseInt(dataJson.getString("employee_owner"))));

                if (!employeeOwnerName.equals("")){
                    employeeOwner.setVisibility(View.VISIBLE);
                    String employeeOwnerText = UserCardOwenerProfile.this.getString(R.string.employee_owner_btn) + ": " + employeeOwnerName;
                    employeeOwner.setText(employeeOwnerText);
                }

                crossfade();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private View.OnClickListener showUserOwnerProfile(final int ownerId){
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserCardOwenerProfile.this,UserCardOwenerProfile.class);
                    intent.putExtra("userID", ownerId);
                    UserCardOwenerProfile.this.startActivity(intent);
                }
            };
        }

        private String getFullName(JSONObject advertisementOwnerInfo) throws JSONException {
            if (advertisementOwnerInfo.getString("full_name").equals("")){
                return nominationPrefixTranslation(advertisementOwnerInfo.getString("nomination_prefix")) + " " +advertisementOwnerInfo.getString("nomination_name");
            }
            else return advertisementOwnerInfo.getString("full_name");
        }

        private String getUserOwnerName(JSONObject advertisementOwnerInfo) throws JSONException{
            JSONObject userCreatorEmploeeOwner;

            if (!advertisementOwnerInfo.getString("employee_owner").equals("0")){
                userCreatorEmploeeOwner = new SiteDataParseUtils().getCardUserId("https://leon-trans.com/api/ver1/login.php?action=get_user&id=" + advertisementOwnerInfo.getString("employee_owner"));
                return getFullName(userCreatorEmploeeOwner);
            }

            return "";
        }

        private String getOcupation(String ocupation){
            String res = "";
            switch (ocupation){
                case "carrier":{
                    res = UserCardOwenerProfile.this.getString(R.string.carrier);
                    break;
                }
                case "customer":{
                    res = UserCardOwenerProfile.this.getString(R.string.customer);
                    break;
                }
                case "manager":{
                    res = UserCardOwenerProfile.this.getString(R.string.manager);
                    break;
                }
            }
            return res;
        }

        private String getActivityKind(String kind){
            String res = "";
            switch (kind){
                case "production":{
                    res = UserCardOwenerProfile.this.getString(R.string.production);
                    break;
                }
                case "trade":{
                    res = UserCardOwenerProfile.this.getString(R.string.trade);
                    break;
                }
                case "services":{
                    res = UserCardOwenerProfile.this.getString(R.string.services);
                    break;
                }
            }
            return res;
        }

        private String nominationPrefixTranslation(String nominationPrefix){
            switch(nominationPrefix){
                case "ag":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ag));
                }
                case "apop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.apop));
                }
                case "apf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.apf));
                }
                case "at":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.at));
                }
                case "atzt":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.atzt));
                }
                case "atov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.atov));
                }
                case "bkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.bkp));
                }
                case "bsok":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.bsok));
                }

                case "by":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.by));
                }

                case "bf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.bf));
                }


                case "b-kpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.b_kpp));
                }

                case "b-kpf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.b_kpf));
                }

                case "b-tp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.b_tp));
                }

                case "wat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.wat));
                }

                case "vk":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vk));
                }


                case "vkkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vkkp));
                }

                case "vkoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vkoop));
                }

                case "vkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vkp));
                }

                case "vktov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vktov));
                }

                case "vkf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vkf));
                }

                case "vo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vo));
                }

                case "vsp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vsp));
                }

                case "vtzov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vtzov));
                }

                case "vtf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vtf));
                }

                case "vf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.vf));
                }

                case "go":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.go));
                }
                case "gtzov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.gtzov));
                }

                case "dat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dat));
                }

                case "dahk":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dahk));
                }

                case "dvat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dvat));
                }

                case "dip":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dip));
                }

                case "dp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dp));
                }

                case "dp2":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dp2));
                }

                case "dpzd":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dpzd));
                }

                case "dpmoy":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dpmoy));
                }

                case "dsp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dsp));
                }

                case "dpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dpp));
                }

                case "dtgo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dtgo));
                }

                case "dtep":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dtep));
                }

                case "dchp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.dchp));
                }

                case "jbk":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.jbk));
                }

                case "zat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.zat));
                }
                case "zogo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.zogo));
                }

                case "zp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.zp));
                }

                case "iaa":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.iaa));
                }

                case "kmp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.kmp));
                }

                case "ip":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ip));
                }

                case "knvmp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.knvmp));
                }

                case "knvo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.knvo));
                }

                case "komp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.komp));
                }

                case "concern":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.concern));
                }

                case "koop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.koop));
                }

                case "koopp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.koopp));
                }
                case "corp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.corp));
                }case "kp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.kp));
                }case "kt":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.kt));
                }case "kfg":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.kfg));
                }case "mbf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mbf));
                }case "mbkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mbkp));
                }case "mp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mp));
                }case "mpzov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mpzov));
                }case "mpvkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mpvkp));
                }case "mpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mpp));
                }case "mspp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.mspp));
                }case "nva":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.nva));
                }
                case "nvo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.nvo));
                }
                case "nvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.nvp));
                }case "nvpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.nvpp));
                }case "nnvpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.nnvpp));
                }case "ntyy":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ntyy));
                }case "ob":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ob));
                }case "okoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.okoop));
                }case "ooo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ooo));
                }case "pii":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pii));
                }case "pop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pop));
                }case "pap":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pap));
                }case "pat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pat));
                }case "paf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.paf));
                }case "pbk":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pbk));
                }case "pbmp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pbmp));
                }
                case "pbp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pbp));
                }
                case "pvkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvkp));
                }case "pvkf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvkf));
                }case "pvtp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvtp));
                }case "pvtf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvtf));
                }case "pvgp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvgp));
                }case "pvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvp));
                }case "pvf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pvf));
                }case "pgo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pgo));
                }case "pz":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pz));
                }case "pzvkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pzvkp));
                }case "pzii":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pzii));
                }case "prid":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.prid));
                }case "pkvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pkvp));
                }case "pmp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pmp));
                }case "pnvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pnvp));
                }case "pnvf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pnvf));
                }case "po":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.po));
                }case "posp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.posp));
                }case "pog":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pog));
                }case "pp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pp));
                }case "ppbf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ppbf));
                }case "ppf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ppf));
                }case "prat":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.prat));
                }case "prbp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.prbp));
                }case "predst":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.predst));
                }case "prsp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.prsp));
                }case "psp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.psp));
                }case "ptep":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ptep));
                }case "ptvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ptvp));
                }case "ptmp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ptmp));
                }case "ptp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ptp));
                }case "pyfsi":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pyfsi));
                }case "pf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pf));
                }case "pbkoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.pbkoop));
                }case "svkoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.svkoop));
                }case "svf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.svf));
                }case "sg":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sg));
                }case "sgpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sgpp));
                }case "smpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.smpp));
                }case "sokoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sokoop));
                }case "sp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sp));
                }case "spzii":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.spzii));
                }case "sskoop":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sskoop));
                }case "st":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.st));
                }case "stov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.stov));
                }case "sfg":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.sfg));
                }case "tepp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tepp));
                }case "tvkp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tvkp));
                }case "tvo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tvo));
                }case "tdv":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tdv));
                }case "tov":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tov));
                }case "tovzii":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tovzii));
                }case "tpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.tpp));
                }case "ttpp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ttpp));
                }case "ttpf":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ttpf));
                }case "yvp":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.yvp));
                }case "ydppz":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.ydppz));
                }case "fg":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.fg));
                }case "fgvs":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.fgvs));
                }case "fili":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.fili));
                }case "firm":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.firm));
                }case "fo":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.fo));
                }case "hk":{
                    return splitStrings(UserCardOwenerProfile.this.getResources().getString(R.string.hk));
                }
                default: return "";
            }
        }
        private String splitStrings(String string){
            if (string.indexOf("(") > 0) return string.substring(0,string.indexOf("("));
            else return string;
        }
    }

    private String makeDate(String date){
        if (date.equals("")){
            return "";
        }

        long dv;
        Date df;
        String dateFrom;
        dv = Long.valueOf(date) * 1000;
        df = new java.util.Date(dv);
        dateFrom = new SimpleDateFormat("dd.MM.yyyy").format(df);
        return dateFrom;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserCardOwenerProfile.this, CardsActivity.class));
    }

    private void crossfade() {
        contentArea.setAlpha(0f);
        contentArea.setVisibility(View.VISIBLE);

        contentArea.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        loaderView.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loaderView.setVisibility(View.GONE);
                    }
                });
    }
}
