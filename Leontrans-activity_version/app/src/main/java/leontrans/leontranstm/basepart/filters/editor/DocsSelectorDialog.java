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

public class DocsSelectorDialog extends AppCompatActivity {

    ArrayList<String> docsArrayList;
    Map<String,Switch> switchersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_docs_selector_dialog);

        docsArrayList = getIntent().getStringArrayListExtra("docsArray");
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
                intent.putStringArrayListExtra("docsResult",resultArray);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private View.OnClickListener getCancelBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocsSelectorDialog.this.finish();
            }
        };
    }

    private Map<String,Switch> getSwitcherArrayList(){
        Map<String,Switch> switchersMap = new HashMap<>();

        switchersMap.put("tir",(Switch) findViewById(R.id.doc_switcher_1));
        switchersMap.put("cmr",(Switch) findViewById(R.id.doc_switcher_2));
        switchersMap.put("ekmt",(Switch) findViewById(R.id.doc_switcher_3));
        switchersMap.put("t1",(Switch) findViewById(R.id.doc_switcher_4));
        switchersMap.put("mbook",(Switch) findViewById(R.id.doc_switcher_5));
        switchersMap.put("customscontrol",(Switch) findViewById(R.id.doc_switcher_6));

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
