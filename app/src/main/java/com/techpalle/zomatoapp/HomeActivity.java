package com.techpalle.zomatoapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RestaurantFrag restaurantFrag = new RestaurantFrag();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, restaurantFrag);
        transaction.commit();
    }
    public boolean internetConnection(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected())
        {return false;}
        return true;
    }
    public void map(String name, String lat, String lon){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("lat", lat);
        intent.putExtra("long", lon);
        startActivity(intent);
    }
    public void map(String name){
        WebFrag webFrag = new WebFrag();
        Bundle b = new Bundle();
        b.putString("name", name);
        webFrag.setArguments(b);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, webFrag);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
