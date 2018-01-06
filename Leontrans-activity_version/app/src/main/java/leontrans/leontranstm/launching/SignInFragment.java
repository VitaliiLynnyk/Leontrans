package leontrans.leontranstm.launching;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import leontrans.leontranstm.R;
import leontrans.leontranstm.utils.SiteDataParseUtils;

import static android.content.Context.MODE_PRIVATE;

public class SignInFragment extends Fragment {

    EditText loginInputField;
    EditText passwordInputField;
    Button signinBtn;
    ProgressBar loaderSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        loginInputField = (EditText) view.findViewById(R.id.login_input_field);
        passwordInputField = (EditText) view.findViewById(R.id.password_input_field);

        signinBtn = (Button) view.findViewById(R.id.signin_btn);
        signinBtn.setOnClickListener(getSigninBtnClickListener());

        loaderSpinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
        loaderSpinner.setVisibility(View.GONE);

        return view;
    }

    private View.OnClickListener getSigninBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginInputField.getText().toString();
                String password = passwordInputField.getText().toString();

                new Async().execute(login, password);
            }
        };
    }

    private class Async extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return new SiteDataParseUtils().getUserHashPassword("https://leon-trans.com/api/ver1/login.php?action=login&login=" + strings[0] + "&password=" + strings[1]);
        }

        @Override
        protected void onPostExecute(String requestResult) {
            super.onPostExecute(requestResult);
            if (requestResult != null){
                SharedPreferences signinSharedPrefernences = getContext().getSharedPreferences("hashPassword", MODE_PRIVATE);
                signinSharedPrefernences.edit().putString("userPassword", requestResult).commit();
                startActivity(new Intent(getContext(), LauncherActivity.class));
            }
            else{
                Toast.makeText(getContext(), "Login or Password is uncorrect", Toast.LENGTH_SHORT).show();
                loginInputField.setText("");
                passwordInputField.setText("");
                loaderSpinner.setVisibility(View.GONE);
            }
        }
    }

}
