package com.techpalle.zomatoapp;


import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFrag extends Fragment {
    EditText editText;
    Button button;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    MyTask myTask;
    HomeActivity homeActivity;
    LinearLayoutManager linearLayoutManager;

    double curLat, curLong;
    LocationManager locationManager;


    //For ArrayList and Bean
//    ArrayList<Restaurant> arrayList;

    //FOr Database
    MyDataBase myDataBase;
    Cursor cursor;

    //FOr Database
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDataBase = new MyDataBase(getContext());
        myDataBase.openDatabase();
    }

    //FOr Database
    @Override
    public void onDestroy() {
        myDataBase.closeDataBase();
        super.onDestroy();
    }

    public class MyTask extends AsyncTask<String, Void, String>{
        URL myUrl;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader streamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder builder = new StringBuilder();
        @Override
        protected String doInBackground(String... strings) {
                try {
                    myUrl = new URL(strings[0]);
                    httpURLConnection = (HttpURLConnection) myUrl.openConnection();
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestProperty("user-key", "8d8f5a54bd4a584013dd5c813fcf4fe0");
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    streamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(streamReader);
                    line = bufferedReader.readLine();
                    while (line != null){
                        builder = builder.append(line);
                        line = bufferedReader.readLine();
                    }
                    return builder.toString();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray a = jsonObject.getJSONArray("nearby_restaurants");
                for(int i = 0; i<a.length(); i++){
                    JSONObject b = a.getJSONObject(i);
                    JSONObject c = b.getJSONObject("restaurant");
                    String name = c.getString("name");
                    JSONObject d = c.getJSONObject("location");
                    String locality = d.getString("locality");
                    String address = d.getString("address");
                    String longitude = d.getString("longitude");
                    String latitude = d.getString("latitude");
                    String image = c.getString("thumb");
                    //FOr Database
                    if(cursor.getString(cursor.getColumnIndex("name")).isEmpty()) {
                        myDataBase.insert(name, locality, address, image, latitude, longitude);
                    }
                    //For ArrayList and Bean
//                    Restaurant restaurant  = new Restaurant(name, locality, address, image, latitude, longitude);
//                    arrayList.add(restaurant);
                }
                myAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            cursor.moveToPosition(position);
            //For ArrayList and Bean
//            final Restaurant restaurant = arrayList.get(position);
//            String name = restaurant.getName();
//            String locality = restaurant.getLocality();
//            String address = restaurant.getAddress();
//            String imageMain = restaurant.getImage();

            //FOr Database
            final String name = cursor.getString(cursor.getColumnIndex("name"));
            String locality = cursor.getString(cursor.getColumnIndex("locality"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String imageMain = cursor.getString(cursor.getColumnIndex("image"));

            holder.textViewName.setText(name);
            holder.textViewLocality.setText(locality);
            holder.textViewAddress.setText(address);
            Glide.with(getActivity()).load(imageMain).placeholder(R.mipmap.ic_launcher).crossFade().into(holder.imageViewMain);
            holder.imageViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu  popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.my_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.itemMap:
                                    //FOr Database
                                    homeActivity.map(name, cursor.getString(cursor.getColumnIndex("latitude")),
                                            cursor.getString(cursor.getColumnIndex("longitude")));

                                    // /For ArrayList and Bean
//                                    homeActivity.map(restaurant.getName(), restaurant.getLatitude(), restaurant.getLongitude());
                                    break;
                                case R.id.itemGoogle:
                                    //FOr Database
                                    homeActivity.map(name);

                                    //For ArrayList and Bean
//                                    homeActivity.map(restaurant.getName());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            //For ArrayList and Bean
//            return arrayList.size();

            //FOr Database
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageViewMain, imageViewMenu;
            public TextView textViewName,textViewLocality, textViewAddress;
            public ViewHolder(View itemView) {
                super(itemView);
                imageViewMain = (ImageView) itemView.findViewById(R.id.image_view_main);
                imageViewMenu = (ImageView) itemView.findViewById(R.id.image_view_menu);
                textViewName = (TextView) itemView.findViewById(R.id.text_view_name);
                textViewLocality = (TextView) itemView.findViewById(R.id.text_view_locality);
                textViewAddress = (TextView) itemView.findViewById(R.id.text_view_address);
            }
        }
    }
    public RestaurantFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_restaurant, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        editText = (EditText) v.findViewById(R.id.edit_text11);
        button= (Button) v.findViewById(R.id.button1);
        homeActivity = (HomeActivity) getActivity();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = editText.getText().toString();
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(address, 10);
                    Address best = addresses.get(0);
                    curLat = best.getLatitude();
                    curLong = best.getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(homeActivity.internetConnection()){
                    myTask.execute("https://developers.zomato.com/api/v2.1/geocode?lat="+curLat+"&lon="+curLong);
                }
                else {
                    Toast.makeText(getActivity(), "Check Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        myTask = new MyTask();

        //FOr Database
        cursor = myDataBase.query();

        //For ArrayList and Bean
//        arrayList = new ArrayList<Restaurant>();
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return v;
    }
}
