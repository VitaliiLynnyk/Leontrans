package leontrans.leontranstm.basepart.cardpart;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kcode.bottomlib.BottomDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import leontrans.leontranstm.basepart.favouritespart.DBHelper;
import leontrans.leontranstm.R;
import leontrans.leontranstm.basepart.userprofile.UserCardOwenerProfile;
import leontrans.leontranstm.utils.SystemServicesUtils;


public class AdvertisementAdapter extends ArrayAdapter<AdvertisementInfo> {
    private CardsActivity activity;
    private LayoutInflater inflater;
    private ArrayList<AdvertisementInfo> advertisementInfoList;
    public  ImageView icon_asterisk;
    public static DBHelper dbHelper;
    Typeface type;

    public AdvertisementAdapter(CardsActivity activity, int resource, ArrayList<AdvertisementInfo> advertisementInfoList) {
        super(activity, resource, advertisementInfoList);
        this.advertisementInfoList = advertisementInfoList;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.list_item_layout, parent, false);
        }
        dbHelper = new DBHelper(getContext());

        icon_asterisk = (ImageView) view.findViewById(R.id.icon_asterisk);
        icon_asterisk.setImageResource(R.drawable.icon_unfavourite);
        icon_asterisk.setTag(R.drawable.icon_unfavourite);

        if(dbHelper.checkIfExist(Integer.toString(advertisementInfoList.get(position).getId()))){
            advertisementInfoList.get(position).setInFavourite(true);
            icon_asterisk.setImageResource(R.drawable.icon_favourite);
            icon_asterisk.setTag(R.drawable.icon_favourite);
        }else{
            icon_asterisk.setImageResource(R.drawable.icon_unfavourite);
            icon_asterisk.setTag(R.drawable.icon_unfavourite);
        }

        TextView trans_type = (TextView) view.findViewById(R.id.trans_type);
        TextView date_from = (TextView) view.findViewById(R.id.date_from);
        TextView telephone = (TextView) view.findViewById(R.id.telephone);
        TextView date_to = (TextView) view.findViewById(R.id.date_to);
        Button country_from_ru = (Button) view.findViewById(R.id.country_from_ru);

        Button country_to_ru = (Button) view.findViewById(R.id.country_to_ru);
        Button city_to_ru = (Button) view.findViewById(R.id.city_to_ru);

        Button city_from_ru = (Button) view.findViewById(R.id.city_from_ru);
        Button goods = (Button) view.findViewById(R.id.goods);
        Button pay_type = (Button) view.findViewById(R.id.pay_type);

        pay_type.setText(advertisementInfoList.get(position).getPay_type() + " "+advertisementInfoList.get(position).getPay_form_moment());

        if(advertisementInfoList.get(position).getTrans_capacity().equals("0")){
            goods.setText(advertisementInfoList.get(position).getGoods_load_type()+" "+advertisementInfoList.get(position).getGoods()+" "+advertisementInfoList.get(position).getTrans_weight()+"т ");
        }else if(advertisementInfoList.get(position).getGoods().isEmpty()){
            goods.setText(advertisementInfoList.get(position).getGoods_load_type()+" "+advertisementInfoList.get(position).getTrans_weight()+"т " +advertisementInfoList.get(position).getTrans_capacity()+"м3 ");
        }else if(advertisementInfoList.get(position).getGoods().isEmpty()&&advertisementInfoList.get(position).getTrans_capacity().equals("0")){
            goods.setText(advertisementInfoList.get(position).getGoods_load_type()+" "+advertisementInfoList.get(position).getTrans_weight()+"т");
        }
        else{
            goods.setText(advertisementInfoList.get(position).getGoods_load_type()+" "+advertisementInfoList.get(position).getGoods()+" "+advertisementInfoList.get(position).getTrans_weight()+"т " +advertisementInfoList.get(position).getTrans_capacity()+"м3 ");
        }

        if(!advertisementInfoList.get(position).getTrans_height().equals("") && !advertisementInfoList.get(position).getTrans_height().equals("0")){
            goods.setText(goods.getText()+" "+ advertisementInfoList.get(position).getTrans_height()+"м");
        }

        if(!advertisementInfoList.get(position).getTrans_length().equals("") && !advertisementInfoList.get(position).getTrans_length().equals("0")){
            goods.setText(goods.getText()+" "+advertisementInfoList.get(position).getTrans_length()+"м");
        }
        if(!advertisementInfoList.get(position).getTrans_width().equals("") && !advertisementInfoList.get(position).getTrans_width().equals("0")){
            goods.setText(goods.getText()+" "+advertisementInfoList.get(position).getTrans_width()+"м");
        }

        if(!advertisementInfoList.get(position).getTrans_trailer().isEmpty()){
            goods.setText(goods.getText()+" "+advertisementInfoList.get(position).getTrans_trailer());
        }

        String [] telephoneNumbers = advertisementInfoList.get(position).getTelephone().split(",");
        telephone.setOnClickListener(getTelephoneFieldListener(telephoneNumbers));

        trans_type.setText(advertisementInfoList.get(position).getTrans_type());
        date_from.setText(advertisementInfoList.get(position).getDate_from());
        date_to.setText(advertisementInfoList.get(position).getDate_to());

        setBackgroundColorByDate(date_from,date_to,advertisementInfoList.get(position).getCreation_date());

        country_from_ru.setText(advertisementInfoList.get(position).getCountry_from());
        country_to_ru.setText(advertisementInfoList.get(position).getCountry_to());

        city_from_ru.setText(getCityInfo(advertisementInfoList.get(position).getCity_from(), advertisementInfoList.get(position).getRegion_from()));
        city_to_ru.setText(getCityInfo(advertisementInfoList.get(position).getCity_to(), advertisementInfoList.get(position).getRegion_to()));


        Button name = (Button) view.findViewById(R.id.name);
        name.setText(advertisementInfoList.get(position).getFull_name());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,UserCardOwenerProfile.class);
                intent.putExtra("userID",Integer.parseInt(advertisementInfoList.get(position).getUserid_creator()));
                activity.startActivity(intent);
            }
        });

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        if(advertisementInfoList.get(position).getGoods().isEmpty()){
            imageView.setBackgroundColor(Color.parseColor("#627ea1"));
            imageView.setImageResource(R.drawable.icon_truck);
        }else{
            imageView.setBackgroundColor(Color.parseColor("#83a84a"));
            imageView.setImageResource(R.drawable.icon_cargo);
        }


        Button routView = (Button) view.findViewById(R.id.routBtn);
        if (advertisementInfoList.get(position).getDistance().isEmpty()){
            routView.setText("A ↔ B");
        }
        else {
            routView.setText(advertisementInfoList.get(position).getDistance());
        }
        routView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SystemServicesUtils().startRoutMaps(activity, advertisementInfoList.get(position).getRoutPointsCoordinates());
            }
        });

        icon_asterisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast_choose = Toast.makeText(getContext(),R.string.saved_favourite_bids, Toast.LENGTH_SHORT);
                Toast toast_close = Toast.makeText(getContext(),R.string.delete_favourite_bids, Toast.LENGTH_SHORT);


                if(!advertisementInfoList.get(position).getInFavourite()){
                    advertisementInfoList.get(position).setInFavourite(true);
                    icon_asterisk.setImageResource(R.drawable.icon_favourite);
                    icon_asterisk.setTag(R.drawable.icon_favourite);
                    toast_choose.show();
                    if(!dbHelper.checkIfExist(Integer.toString(advertisementInfoList.get(position).getId()))){
                        dbHelper.insertContact(advertisementInfoList.get(position).getId());
                    }
                }else{
                    icon_asterisk.setImageResource(R.drawable.icon_unfavourite);
                    icon_asterisk.setTag(R.drawable.icon_unfavourite);
                    advertisementInfoList.get(position).setInFavourite(false);
                    toast_close.show();
                    if(dbHelper.checkIfExist(Integer.toString(advertisementInfoList.get(position).getId()))){
                        dbHelper.deleteContact(advertisementInfoList.get(position).getId());
                    }
                }

                notifyDataSetChanged();
            }
        });

        dbHelper.close();

        return view;
    }

    private View.OnClickListener getTelephoneFieldListener(final String [] telephoneNumbers){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomDialog bottomDialog = BottomDialog.newInstance(activity.getResources().getString(R.string.telephone_numbers), activity.getResources().getString(R.string.close_telephone_dialog), telephoneNumbers);
                bottomDialog.setListener(new BottomDialog.OnClickListener() {
                    @Override
                    public void click(int i) {
                        new SystemServicesUtils().startDial(activity, telephoneNumbers[i]);
                    }
                });

                bottomDialog.show(activity.getSupportFragmentManager(),"dialogTag");
            }
        };
    }

    private void setBackgroundColorByDate(TextView tvFrom, TextView tvTo, Date cardCreationTime){
        Date currentDate = new Date();

        if (currentDate.getTime() - cardCreationTime.getTime() < 3600000){
            tvFrom.setBackgroundColor(activity.getResources().getColor(R.color.time_red));
            tvTo.setBackgroundColor(activity.getResources().getColor(R.color.time_red));
        }
        else if (currentDate.getTime() - cardCreationTime.getTime() > 10800000){
            tvFrom.setBackgroundColor(activity.getResources().getColor(R.color.time_green));
            tvTo.setBackgroundColor(activity.getResources().getColor(R.color.time_green));
        }
        else{
            tvFrom.setBackgroundColor(activity.getResources().getColor(R.color.time_yellow));
            tvTo.setBackgroundColor(activity.getResources().getColor(R.color.time_yellow));
        }

        notifyDataSetChanged();
    }

    private String getCityInfo(String city, String region){
        if (region.isEmpty()){
            return city;
        }
        else{
            return city + "\n(" + region + ")";
        }
    }

}