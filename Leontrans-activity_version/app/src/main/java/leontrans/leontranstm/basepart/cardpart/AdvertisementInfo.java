package leontrans.leontranstm.basepart.cardpart;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import leontrans.leontranstm.R;
import leontrans.leontranstm.utils.RoutPointsCoordinates;

public class AdvertisementInfo {
    private Context context;
    private JSONObject list;

    private int id;
    private String trans_type;
    private String date_from;
    private String date_to;
    private String country_from;
    private String country_to;
    private String city_from;
    private String city_to;
    private String userid_creator;
    private String pay_type;
    private String goods;
    private String goods_load_type;
    private String trans_weight;
    private String trans_capacity;
    private String telephone;
    private String person_type;
    private String full_name;
    private String trans_height;
    private String trans_width;
    private String trans_length;
    private String trans_trailer;
    private String pay_form_moment;
    private Boolean isInFavourite;
    private Date creation_date;
    private String region_from;
    private String region_to;
    private String distance;

    private RoutPointsCoordinates routPointsCoordinates;

    public AdvertisementInfo(JSONObject list, AdvertisementOwnerInfo advertisementOwnerInfo, Context context, Locale locale) throws JSONException {
        this.context = context;
        this.list = list;

        this.id = Integer.parseInt(list.getString("id"));
        this.trans_trailer = getTrans_trailer(list.getString("trans_trailer"));
        this.trans_height = list.getString("trans_height");
        this.trans_width = list.getString("trans_width");
        this.trans_length = list.getString("trans_length");
        this.trans_type = getTrans_type(list.getString("trans_type"));
        this.date_from = makeDate(list.getString("date_from"));
        this.date_to = checkData(list.getString("date_from"), list.getString("date_to"));
        this.country_from = getDestinationPoint(list.getString("country_from_ru"),list.getString("country_from_ua"),list.getString("country_from_en"),locale);
        this.country_to = getDestinationPoint(list.getString("country_to_ru"),list.getString("country_to_ua"),list.getString("country_to_en"),locale);
        this.city_from = getDestinationPoint(list.getString("city_from_ru"),list.getString("city_from_ua"),list.getString("city_from_en"),locale);
        this.city_to = getDestinationPoint(list.getString("city_to_ru"),list.getString("city_to_ua"),list.getString("city_to_en"),locale);
        this.pay_form_moment = getPay_form_moment(list.getString("pay_form"),list.getString("pay_moment"));
        this.userid_creator = list.getString("userid_creator");
        this.pay_type = getPay(list.getString("pay_type"),list.getString("pay_price"),list.getString("pay_currency"));
        this.goods = list.getString("goods");
        this.goods_load_type = getGoods_load_type(list.getString("goods_load_type"));
        this.trans_weight = list.getString("trans_weight");
        this.trans_capacity = list.getString("trans_capacity");

        this.telephone = advertisementOwnerInfo.getTelephone();
        this.person_type = advertisementOwnerInfo.getPerson_type();
        this.full_name = advertisementOwnerInfo.getFull_name();

        this.routPointsCoordinates = new RoutPointsCoordinates(list.getString("lat_from"), list.getString("lng_from"), list.getString("lat_to"), list.getString("lng_to"));
        this.isInFavourite = false;
        this.creation_date = new Date(Long.valueOf(list.getString("date_creation")) * 1000);

        this.region_from = getDestinationPoint(list.getString("region_from_ru"),list.getString("region_from_ua"),list.getString("region_from_en"),locale);
        this.region_to = getDestinationPoint(list.getString("region_to_ru"),list.getString("region_to_ua"),list.getString("region_to_en"),locale);
        this.distance = list.getString("distance");
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public String getRegion_from() {
        return region_from;
    }

    public void setRegion_from(String region_from) {
        this.region_from = region_from;
    }

    public String getRegion_to() {
        return region_to;
    }

    public void setRegion_to(String region_to) {
        this.region_to = region_to;
    }

    public Boolean getInFavourite() {
        return isInFavourite;
    }

    public void setInFavourite(Boolean inFavourite) {
        isInFavourite = inFavourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String getPay_form_moment(String pay_form, String pay_moment){
        String res = "";
        switch (pay_form){
            case "777":{
                res += " "+ context.getString(R.string.payform_777);
                break;
            }
            case "1":{
                res += " "+ context.getString(R.string.payform_1);
                break;
            }
            case "3":{
                res += " "+ context.getString(R.string.payform_3);
                break;
            }
            case "6":{
                res += " "+ context.getString(R.string.payform_6);
                break;
            }
            case "4":{
                res += " "+ context.getString(R.string.payform_4);
                break;
            }
            case "71":{
                res += " "+ context.getString(R.string.payform_71);
                break;
            }
            case "72":{
                res += " "+ context.getString(R.string.payform_71);
                break;
            }
            /*default:{
                res += "";
                break;
            }*/
        }
        switch (pay_moment){
            case "52":{
                res += " "+ context.getString(R.string.paymoment_52);
                break;
            }
            case "53":{
                res += " "+ context.getString(R.string.paymoment_53);
                break;
            }
            case "6":{
                res += " "+ context.getString(R.string.paymoment_6);
                break;
            }
            case "61":{
                res += " "+ context.getString(R.string.paymoment_61);
                break;
            }

        }

        return res;
    }

    public String getPay_form_moment() {
        return pay_form_moment;
    }

    private String getDestinationPoint(String dest_ru, String dest_ua, String dest_en, Locale locale){
        String res;

        Log.d("LOCALE_TEST_TAG", "getDestinationPoint: " + locale.getLanguage());

        switch (locale.getLanguage()){
            case "en":{
                res = dest_en;
                break;
            }
            case "ru":{
                res = dest_ru;
                break;
            }
            case "uk":{
                res = dest_ua;
                break;
            }
            default: res = dest_en;
        }

        return res;
    }

    private String getPay(String pay_type ,String pay_price,String pay_currency){
        if(pay_type.equals("request")){
            return  context.getString(R.string.request);
        }
        else if(pay_price.equals("0")){
            return"";
        }else return pay_price+" "+pay_currency;
    }

    private String getTrans_trailer(String trans_trailer){
        String res = "";
        switch (trans_trailer){
            case "truck":{
                res = context.getString(R.string.truck);
                break;
            }
            case "trailer":{
                res = context.getString(R.string.trailer);
                break;
            }
            case "half-trailer":{
                res = context.getString(R.string.half_trailer);
                break;
            }
        }
        return res;
    }
    private String getGoods_load_type(String goods_load_type){
        String res = "";
        String[] load_type = goods_load_type.split(",");

        for (int i = 0;i<load_type.length;i++){
            switch (load_type[i]){
                case "top":{
                    res += " "+ context.getString(R.string.top);
                    break;
                }
                case "side":{
                    res += " "+context.getString(R.string.side);
                    break;
                }
                case "back":{
                    res += " "+context.getString(R.string.back);
                    break;
                }
                case "fulluntent":{
                    res += " "+context.getString(R.string.fulluntent);
                    break;
                }
                case "uncrossbar":{
                    res += " "+context.getString(R.string.uncrossbar);
                    break;
                }
                case "unrack":{
                    res += " "+context.getString(R.string.unrack);
                    break;
                }
                case "ungate":{
                    res += " "+context.getString(R.string.ungate);
                    break;
                }case "gydrobort":{
                    res += " "+context.getString(R.string.gydrobort);
                    break;
                }
            }}
        return res;
    }
    private String getTrans_type(String type){
        String res = "";
        switch (type){
            case "any":{
                res = " "+context.getString(R.string.any);
                break;
            }
            case "easy":{
                res = " "+context.getString(R.string.easy);
                break;
            }
            case "bus":{
                res = " "+context.getString(R.string.bus);
                break;
            }
            case "avto":{
                res = " "+context.getString(R.string.avto);
                break;
            }
            case "fuel_oil":{
                res = " "+context.getString(R.string.fuel_oil);
                break;
            }
            case "concrete":{
                res = " "+context.getString(R.string.concrete);
                break;
            }
            case "gas":{
                res = " "+context.getString(R.string.gas);
                break;
            }
            case "hard":{
                res = " "+context.getString(R.string.hard);
                break;
            }
            case "grain":{
                res = " "+context.getString(R.string.grain);
                break;
            }
            case "isotherms":{
                res = " "+context.getString(R.string.isotherms);
                break;
            }
            case "containertrans":{
                res = " "+context.getString(R.string.containertrans);
                break;
            }
            case "tap":{
                res = " "+context.getString(R.string.tap);
                break;
            }
            case "closed":{
                res = " "+context.getString(R.string.closed);
                break;
            }
            case "trees":{
                res = " "+context.getString(R.string.trees);
                break;
            }
            case "microbus":{
                res = " "+context.getString(R.string.microbus);
                break;
            }
            case "oversized":{
                res = " "+context.getString(R.string.oversized);
                break;
            }
            case "unclosed":{
                res = " "+context.getString(R.string.unclosed);
                break;
            }
            case "refrigerator":{
                res = " "+context.getString(R.string.refrigerator);
                break;
            }
            case "tipper":{
                res = " "+context.getString(R.string.tipper);
                break;
            }
            case "animaltruck":{
                res = " "+context.getString(R.string.animaltruck);
                break;
            }
            case "awning":{
                res = " "+context.getString(R.string.awning);
                break;
            }
            case "trall":{
                res = " "+context.getString(R.string.trall);
                break;
            }
            case "avtotipper":{
                res = " "+context.getString(R.string.avtotipper);
                break;
            }
            case "fullmetal":{
                res = " "+context.getString(R.string.fullmetal);
                break;
            }
            case "fuel_oil_small":{
                res = " "+context.getString(R.string.fuel_oil_small);
                break;
            }
            case "evacuator":{
                res = " "+context.getString(R.string.evacuator);
                break;
            }
            default:
                res = " "+context.getString(R.string.any);
                break;
        }
        return res;
    }

    public String getPay_type() {
        return pay_type;
    }

    public String getPerson_type() {
        return person_type;
    }

    public String getTrans_height() {
        return trans_height;
    }

    public String getTrans_width() {
        return trans_width;
    }

    public String getTrans_length() {
        return trans_length;
    }

    public String getTrans_trailer() {
        return trans_trailer;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public String getDate_from() {
        return date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public String getCountry_from() {
        return country_from;
    }

    public String getCountry_to() {
        return country_to;
    }

    public String getCity_from() {
        return city_from;
    }

    public String getCity_to() {
        return city_to;
    }

    public String getUserid_creator() {
        return userid_creator;
    }

    public String getGoods() {
        return goods;
    }

    public String getGoods_load_type() {
        return goods_load_type;
    }

    public String getTrans_weight() {
        return trans_weight;
    }

    public String getTrans_capacity() {
        return trans_capacity;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getFull_name() {
        return full_name;
    }
    public RoutPointsCoordinates getRoutPointsCoordinates() {
        return routPointsCoordinates;
    }

    private String checkData(String date_from, String date_to) throws JSONException{
        if (date_from.equals("") || date_to.equals("")){
            return makeDate(date_to);
        }

        if (date_from.equals(date_to)){
            return makeTime(list.getString("date_creation"));
        }
        else{
            return makeDate(date_to);
        }
    }

    private String makeTime(String date){
        if (date.equals("")){
            return "";
        }

        long dv = 0;
        Date df;
        String dateFrom;
        dv = Long.valueOf(date) * 1000;
        df = new java.util.Date(dv);
        dateFrom = new SimpleDateFormat("HH:mm").format(df);
        return dateFrom;
    }

    private String makeDate(String date){
        if (date.equals("")){
            return "";
        }

        long dv = 0;
        Date df;
        String dateFrom;
        dv = Long.valueOf(date) * 1000;
        df = new java.util.Date(dv);
        dateFrom = new SimpleDateFormat("dd.MM.yyyy").format(df);
        return dateFrom;
    }
}