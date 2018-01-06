package leontrans.leontranstm.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.cardpart.CardsActivity;
import leontrans.leontranstm.basepart.userprofile.UserProfileActivity;

public class LanguageDialogActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private boolean isLanguageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_language_dialog);

        String language = getSharedPreferences("app_language", MODE_PRIVATE).getString("language","en");

        RadioButton rb;

        switch (language){
            case "en":{
                rb = (RadioButton) findViewById(R.id.radio_bt_usa);
                break;
            }
            case "uk":{
                rb = (RadioButton) findViewById(R.id.radio_bt_ukraine);
                break;
            }
            case "ru":{
                rb = (RadioButton) findViewById(R.id.radio_bt_russia);
                break;
            }
            default: rb = (RadioButton) findViewById(R.id.radio_bt_usa);
        }

        rb.setChecked(true);


        radioGroup = (RadioGroup) findViewById(R.id.radio_group_language);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_bt_usa:{
                        getSharedPreferences("app_language", MODE_PRIVATE).edit().putString("language", "en").commit();
                        break;
                    }
                    case R.id.radio_bt_ukraine:{
                        getSharedPreferences("app_language", MODE_PRIVATE).edit().putString("language", "uk").commit();
                        break;
                    }
                    case R.id.radio_bt_russia:{
                        getSharedPreferences("app_language", MODE_PRIVATE).edit().putString("language", "ru").commit();
                        break;
                    }
                    default:getSharedPreferences("app_language", MODE_PRIVATE).edit().putString("language", "en").commit();
                }

                isLanguageChanged = true;
                finish();
                startActivity(new Intent(LanguageDialogActivity.this, CardsActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(!isLanguageChanged) startActivity(new Intent(LanguageDialogActivity.this, CardsActivity.class));
        super.onDestroy();
    }
}
