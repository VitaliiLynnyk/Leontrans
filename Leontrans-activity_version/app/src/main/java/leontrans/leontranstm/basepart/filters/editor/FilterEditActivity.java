package leontrans.leontranstm.basepart.filters.editor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.basepart.filters.FilterSettingsActivity;
import leontrans.leontranstm.basepart.filters.FilterSwitcherDialogActivity;
import leontrans.leontranstm.utils.InternetStatusUtils;
import leontrans.leontranstm.utils.SiteDataParseUtils;

public class FilterEditActivity extends AppCompatActivity {

    private final int REQUEST_CODE_LOAD_TYPE = 1;
    private final int REQUEST_CODE_DOCS = 2;
    private final int REQUEST_CODE_ADR = 3;

    private Toolbar toolbar;
    String notifyId;

    Spinner notifyTypeSpinner;
    Spinner carTypeSpinner;
    Spinner carKindSpinner;

    ArrayList<String> docsArrayList;
    ArrayList<String> loadTypeArrayList;
    ArrayList<String> adrArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_edit);
        notifyId = getIntent().getStringExtra("notifyId");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.filter_editor) + getNotifyId(notifyId));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifyTypeSpinner = (Spinner) findViewById(R.id.notify_type_spinner);
        carTypeSpinner = (Spinner) findViewById(R.id.car_type);
        carKindSpinner = (Spinner) findViewById(R.id.car_kind);

        ArrayAdapter<?> notifyTypeAdapter = ArrayAdapter.createFromResource(this, R.array.notify_types, android.R.layout.simple_spinner_item);
            notifyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<?> carTypeAdapter = ArrayAdapter.createFromResource(this, R.array.car_types, android.R.layout.simple_spinner_item);
            carTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<?> carKindAdapter = ArrayAdapter.createFromResource(this, R.array.car_kind, android.R.layout.simple_spinner_item);
            carKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        notifyTypeSpinner.setAdapter(notifyTypeAdapter);
        carTypeSpinner.setAdapter(carTypeAdapter);
        carKindSpinner.setAdapter(carKindAdapter);

        docsArrayList = new ArrayList<>();
        loadTypeArrayList = new ArrayList<>();
        adrArrayList = new ArrayList<>();

        ((Button) findViewById(R.id.docs_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterEditActivity.this, DocsSelectorDialog.class);
                intent.putStringArrayListExtra("docsArray",docsArrayList);
                startActivityForResult(intent, REQUEST_CODE_DOCS);
            }
        });

        ((Button) findViewById(R.id.load_type_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterEditActivity.this, LoadTypeSelectorDialog.class);
                intent.putStringArrayListExtra("loadTypeArray",loadTypeArrayList);
                startActivityForResult(intent, REQUEST_CODE_LOAD_TYPE);
            }
        });

        ((Button) findViewById(R.id.adr_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterEditActivity.this, AdrSelectorDialog.class);
                intent.putStringArrayListExtra("adrArray",adrArrayList);
                startActivityForResult(intent, REQUEST_CODE_ADR);
            }
        });

        ((Button) findViewById(R.id.save_button)).setOnClickListener(getSaveBtnClickListener());
        ((Button) findViewById(R.id.cancel_button)).setOnClickListener(getCancelBtnClickListener());

        new LoadFilterInfo().execute();
    }

    private View.OnClickListener getSaveBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetStatusUtils.isDeviceOnline(FilterEditActivity.this)){
                    Toast.makeText(FilterEditActivity.this, FilterEditActivity.this.getResources().getString(R.string.internet_dialog_message), Toast.LENGTH_SHORT).show();
                    return;
                }

                new SentFilterInfo().execute();
                returnToFilterSettingActivity();
            }
        };
    }

    private View.OnClickListener getCancelBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToFilterSettingActivity();
            }
        };
    }

    private class LoadFilterInfo extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences userPasswordSharedPreferences = FilterEditActivity.this.getSharedPreferences("hashPassword", MODE_PRIVATE);
            String userPassword = userPasswordSharedPreferences.getString("userPassword","");
            int userID = new SiteDataParseUtils().getUserIdByHashpassword("https://leon-trans.com/api/ver1/login.php?action=get_hash_id&hash=" + userPassword);
            return new SiteDataParseUtils().getSiteRequestResult("https://leon-trans.com/api/ver1/login.php?action=get_user&id=" + userID);
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            try {
                JSONObject dataJson = new JSONObject(jsonStr);

                if (dataJson.getString(notifyId).isEmpty()) return;

                JSONObject notifyData = new JSONObject(dataJson.getString(notifyId));

                ((EditText) findViewById(R.id.country_from)).setText(notifyData.getString("country_from_name"));
                ((EditText) findViewById(R.id.country_to)).setText(notifyData.getString("country_to_name"));
                ((EditText) findViewById(R.id.city_from)).setText(notifyData.getString("city_from_name"));
                ((EditText) findViewById(R.id.city_to)).setText(notifyData.getString("city_to_name"));

                ((EditText) findViewById(R.id.capacity_from)).setText(notifyData.getString("capacity_from"));
                ((EditText) findViewById(R.id.capacity_to)).setText(notifyData.getString("capacity_to"));

                ((EditText) findViewById(R.id.weight_from)).setText(notifyData.getString("weight_from"));
                ((EditText) findViewById(R.id.weight_to)).setText(notifyData.getString("weight_to"));

                setNotifySpinnerSelection(notifyData.getString("type"));
                setCarTypeSpinnerSelection(notifyData.getString("trans_type"));
                setCarKindSpinnerSelection(notifyData.getString("trans_kind"));

                docsArrayList = getSplittedArrayList(notifyData.getString("docs"));
                loadTypeArrayList = getSplittedArrayList(notifyData.getString("load_type"));
                adrArrayList = getSplittedArrayList(notifyData.getString("adr"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private ArrayList<String> getSplittedArrayList(String data){
            ArrayList<String> resultList = new ArrayList<>();

            String[] docs = data.split(",");
            for (int i = 0; i < docs.length; i++){
                resultList.add(docs[i]);
            }

            return resultList;
        }

        private void setNotifySpinnerSelection(String notifyType){
            switch (notifyType){
                case "": {
                    notifyTypeSpinner.setSelection(0);
                    break;
                }
                case "avto": {
                    notifyTypeSpinner.setSelection(1);
                    break;
                }case "goods": {
                    notifyTypeSpinner.setSelection(2);
                    break;
                }
                default: notifyTypeSpinner.setSelection(0);
            }
        }

        private void setCarTypeSpinnerSelection(String carType){
            switch (carType){
                case "":{
                    carTypeSpinner.setSelection(0);
                    break;
                }
                case "any":{
                    carTypeSpinner.setSelection(1);
                    break;
                }
                case "bus":{
                    carTypeSpinner.setSelection(2);
                    break;
                }
                case "avto":{
                    carTypeSpinner.setSelection(3);
                    break;
                }
                case "fuel_oil":{
                    carTypeSpinner.setSelection(4);
                    break;
                }
                case "concrete":{
                    carTypeSpinner.setSelection(5);
                    break;
                }
                case "gas":{
                    carTypeSpinner.setSelection(6);
                    break;
                }
                case "hard":{
                    carTypeSpinner.setSelection(7);
                    break;
                }
                case "grain":{
                    carTypeSpinner.setSelection(8);
                    break;
                }
                case "isotherms":{
                    carTypeSpinner.setSelection(9);
                    break;
                }
                case "containertrans":{
                    carTypeSpinner.setSelection(10);
                    break;
                }
                case "tap":{
                    carTypeSpinner.setSelection(11);
                    break;
                }
                case "closed":{
                    carTypeSpinner.setSelection(12);
                    break;
                }
                case "trees":{
                    carTypeSpinner.setSelection(13);
                    break;
                }
                case "microbus":{
                    carTypeSpinner.setSelection(1);
                    break;
                }
                case "oversized":{
                    carTypeSpinner.setSelection(14);
                    break;
                }
                case "unclosed":{
                    carTypeSpinner.setSelection(15);
                    break;
                }
                case "refrigerator":{
                    carTypeSpinner.setSelection(16);
                    break;
                }
                case "tipper":{
                    carTypeSpinner.setSelection(17);
                    break;
                }
                case "animaltruck":{
                    carTypeSpinner.setSelection(18);
                    break;
                }
                case "awning":{
                    carTypeSpinner.setSelection(19);
                    break;
                }
                case "trall":{
                    carTypeSpinner.setSelection(20);
                    break;
                }
                case "avtotipper":{
                    carTypeSpinner.setSelection(21);
                    break;
                }
                case "fullmetal":{
                    carTypeSpinner.setSelection(22);
                    break;
                }
                case "fuel_oil_small":{
                    carTypeSpinner.setSelection(23);
                    break;
                }
                case "evacuator":{
                    carTypeSpinner.setSelection(24);
                    break;
                }
                default: carTypeSpinner.setSelection(0);
            }
        }

        private void setCarKindSpinnerSelection(String carKind){
            switch (carKind){
                case "":{
                    carKindSpinner.setSelection(0);
                    break;
                }
                case "truck":{
                    carKindSpinner.setSelection(1);
                    break;
                }
                case "trailer":{
                    carKindSpinner.setSelection(2);
                    break;
                }
                case "half-trailer":{
                    carKindSpinner.setSelection(3);
                    break;
                }
                default: carKindSpinner.setSelection(0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            returnToFilterSettingActivity();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CODE_LOAD_TYPE:{
                if(data != null) loadTypeArrayList = data.getStringArrayListExtra("loadTypeResult");
                break;
            }

            case REQUEST_CODE_DOCS:{
                if(data != null) docsArrayList = data.getStringArrayListExtra("docsResult");
                break;
            }

            case REQUEST_CODE_ADR:{
                if(data != null) adrArrayList = data.getStringArrayListExtra("adrResult");
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        returnToFilterSettingActivity();
    }

    private void returnToFilterSettingActivity(){
        Intent intent = new Intent(FilterEditActivity.this, FilterSettingsActivity.class);
        startActivity(intent);
    }

    private class SentFilterInfo extends AsyncTask<Void,Void,Void> {



        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences userPasswordSharedPreferences = FilterEditActivity.this.getSharedPreferences("hashPassword", MODE_PRIVATE);
            String userPassword = userPasswordSharedPreferences.getString("userPassword","");
            int userID = new SiteDataParseUtils().getUserIdByHashpassword("https://leon-trans.com/api/ver1/login.php?action=get_hash_id&hash=" + userPassword);

            JSONObject filterInfoJSON = new JSONObject();

            try {
                filterInfoJSON.put("id",getNotifyId(notifyId));

                filterInfoJSON.put("type","" + getNotifyTypeResult((Spinner) findViewById(R.id.notify_type_spinner)));
                filterInfoJSON.put("trans_type","" + getCarTypeResult((Spinner) findViewById(R.id.car_type)));
                filterInfoJSON.put("trans_kind","" + getCarKindResult((Spinner) findViewById(R.id.car_kind)));

                filterInfoJSON.put("country_from_name","" + getDestenationString((EditText) findViewById(R.id.country_from)));
                filterInfoJSON.put("country_to_name","" +getDestenationString(((EditText) findViewById(R.id.country_to))));
                filterInfoJSON.put("city_from_name","" +getDestenationString(((EditText) findViewById(R.id.city_from))));
                filterInfoJSON.put("city_to_name","" +getDestenationString((EditText) findViewById(R.id.city_to)));

                filterInfoJSON.put("capacity_from","" + ((EditText) findViewById(R.id.capacity_from)).getText());
                filterInfoJSON.put("capacity_to","" + ((EditText) findViewById(R.id.capacity_to)).getText());
                filterInfoJSON.put("weight_from","" + ((EditText) findViewById(R.id.weight_from)).getText());
                filterInfoJSON.put("weight_to","" + ((EditText) findViewById(R.id.weight_to)).getText());

                filterInfoJSON.put("load_type","" + joinArrayStrings(loadTypeArrayList));
                filterInfoJSON.put("docs","" + joinArrayStrings(docsArrayList));
                filterInfoJSON.put("adr","" + joinArrayStrings(adrArrayList));

                Log.d("JSON_TEST_TAG", "doInBackground: " + filterInfoJSON);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SiteDataParseUtils().getSiteRequestResult("https://leon-trans.com/api/ver1/update.php?action=notify&id=" + userID + "&id_notify=" + getNotifyId(notifyId) + "&notify=" + filterInfoJSON);

            return  null;
        }

        private String getDestenationString(EditText editText){
            if (editText.getText().equals("null")){
                return "";
            }
            else return editText.getText().toString();
        }

        private String getNotifyTypeResult(Spinner spinner){
            switch((int)spinner.getSelectedItemId()){
                case 0: return "";

                case 1: return "avto";

                case 2: return "goods";

                default: return "";
            }
        }

        private String getCarTypeResult(Spinner spinner){
            switch((int)spinner.getSelectedItemId()){

                case 0: return "";
                case 1: return "any";
                case 2: return "bus";
                case 3: return "avto";
                case 4: return "fuel_oil";
                case 5: return "concrete";
                case 6: return "gas";
                case 7: return "hard";
                case 8: return "grain";
                case 9: return "isotherms";
                case 10: return "containertrans";
                case 11: return "tap";
                case 12: return "closed";
                case 13: return "trees";
                case 14: return "microbus";
                case 15: return "oversized";
                case 16: return "unclosed";
                case 17: return "refrigerator";
                case 18: return "tipper";
                case 19: return "animaltruck";
                case 20: return "awning";
                case 21: return "trall";
                case 22: return "avtotipper";
                case 23: return "fullmetal";
                case 24: return "fuel_oil_small";
                case 25: return "evacuator";

                default: return "";
            }
        }

        private String getCarKindResult(Spinner spinner){
            switch((int)spinner.getSelectedItemId()){
                case 0: return "";

                case 1: return "truck";

                case 2: return "trailer";

                case 3: return "half-trailer";

                default: return "";
            }
        }

        private String getEmptyStringIfNotSelected(Spinner spinner){
            if (spinner.getSelectedItemId() == 0){
                return "";
            }
            else return (String) spinner.getSelectedItem();
        }

        private String joinArrayStrings(ArrayList<String> arrayList){
            String arrayJoinText = "";
            for (int i = 0; i < arrayList.size(); i++){
                arrayJoinText += arrayList.get(i);
                arrayJoinText += ",";
            }

            return arrayJoinText.substring(0, arrayJoinText.length() - 1);
        }
    }

    private String getNotifyId(String notifyId){
        return notifyId.substring(notifyId.indexOf("_") + 1, notifyId.length());
    }
}
