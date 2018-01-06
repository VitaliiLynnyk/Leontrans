package leontrans.leontranstm.utils;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SiteDataParseUtils {

    private final String LOG_TAG = "SiteDataParseUtils_log";

    public int getUserIdByHashpassword(String urlRequest){
        int requestResultID = -1;

        try {
            JSONObject dataJsonObj = new JSONObject(getSiteRequestResult(urlRequest));
            requestResultID = dataJsonObj.getInt("id");
            Log.d(LOG_TAG, "" + requestResultID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestResultID;
    }

    public  String getUserHashPassword(String urlRequest){
        try {
            JSONObject rootJsonObj = new JSONObject(getSiteRequestResult(urlRequest));
            JSONObject userInfo = rootJsonObj.getJSONObject("id");

            if (userInfo.getInt("id") != 0) {
                return userInfo.getString("password");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<JSONObject> getCardsInformation(String urlRequest , int numOfRequests){
        JSONObject dataJsonObj = null;
        JSONArray dataJsonArr = null ;
        ArrayList<JSONObject> resultArray = new ArrayList<>();

        try{
            dataJsonArr = new JSONArray(getSiteRequestResult(urlRequest));
            Log.d("LENGTH_TEST", "getCardsInformation: " + dataJsonArr.length());
            for(int i = 0 ; i < dataJsonArr.length() ; i++){
                dataJsonObj = dataJsonArr.getJSONObject(i);
                resultArray.add(dataJsonObj);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return resultArray;
    }

    public JSONObject getCardUserId(String urlRequest){
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(getSiteRequestResult(urlRequest));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return dataJsonObj;
    }

    public String getSiteRequestResult(String urlAddress){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        try {
            URL url = new URL(urlAddress);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        return resultJson;
    }

}
