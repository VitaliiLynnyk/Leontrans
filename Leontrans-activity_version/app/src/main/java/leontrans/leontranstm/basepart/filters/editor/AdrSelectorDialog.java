package leontrans.leontranstm.basepart.filters.editor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import leontrans.leontranstm.R;

public class AdrSelectorDialog extends AppCompatActivity {
    ArrayList<String> docsArrayList;
    Map<String,Switch> switchersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_adr_selector_dialog);

        docsArrayList = getIntent().getStringArrayListExtra("adrArray");
        switchersMap = getSwitcherArrayList();
        setInitialSwitchersValue();

        ((Button) findViewById(R.id.save_button)).setOnClickListener(getSaveBtnClickListener());
        ((Button) findViewById(R.id.cancel_button)).setOnClickListener(getCancelBtnClickListener());
    }

    private View.OnClickListener getSaveBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> resultArray = new ArrayList<>();
                for (Map.Entry<String,Switch> entry : switchersMap.entrySet()){
                    if (entry.getValue().isChecked()) resultArray.add(entry.getKey());
                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("adrResult",resultArray);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private View.OnClickListener getCancelBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdrSelectorDialog.this.finish();
            }
        };
    }

    private Map<String,Switch> getSwitcherArrayList(){
        Map<String,Switch> switchersMap = new HashMap<>();

        switchersMap.put("dangerous",(Switch) findViewById(R.id.adr_switcher_1));
        switchersMap.put("undangerous",(Switch) findViewById(R.id.adr_switcher_2));
        switchersMap.put("adr1",(Switch) findViewById(R.id.adr_switcher_3));
        switchersMap.put("adr2",(Switch) findViewById(R.id.adr_switcher_4));
        switchersMap.put("adr3",(Switch) findViewById(R.id.adr_switcher_5));
        switchersMap.put("adr4",(Switch) findViewById(R.id.adr_switcher_6));
        switchersMap.put("adr5",(Switch) findViewById(R.id.adr_switcher_7));
        switchersMap.put("adr6",(Switch) findViewById(R.id.adr_switcher_8));
        switchersMap.put("adr7",(Switch) findViewById(R.id.adr_switcher_9));
        switchersMap.put("adr8",(Switch) findViewById(R.id.adr_switcher_10));
        switchersMap.put("adr9",(Switch) findViewById(R.id.adr_switcher_11));
        switchersMap.put("adr10",(Switch) findViewById(R.id.adr_switcher_12));

        return  switchersMap;
    }

    private void setInitialSwitchersValue(){

        for (Map.Entry<String,Switch> entry : switchersMap.entrySet()){
            for (String docName : docsArrayList){
                if (entry.getValue().isChecked()) break;
                entry.getValue().setChecked(entry.getKey().equals(docName));
            }
        }
    }
}
