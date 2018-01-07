package de.httptandooripalace.restaurantorderprinter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapters.HistoryAdapter;
import cz.msebera.android.httpclient.Header;
import entities.Bill;
import entities.Product;
import helpers.DatabaseHelper;
import helpers.RequestClient;

/**
 * Created by uiz on 30/05/2017.
 */

public class HistoryActivity extends BaseActivity {

    private entities.Settings settings;
    public static List<Bill> bills = new ArrayList<>();
    public  List<Product> products = new ArrayList<>();
    Context context;
    int id =0;
    String boolstr = null;
    boolean is_open = true;
    String datestr = null;
    SimpleDateFormat sdf = null;
    Date date = null;
    String table_nr = null;
    JSONArray jsonarray = null;
    JSONObject jsonobject = null;
    String waiter_name = "";
    Double total_price_excl = 0.0;
    FloatingActionButton prin;
    HistoryAdapter adapter = null;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.history);
        setContentView(R.layout.history_activity);
        recyclerView = (RecyclerView) findViewById(R.id.list_closed_bills);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                onBackPressed();
                return true;
            case R.id.bills_overview:
                Intent i2 = new Intent(this, OverviewActivity.class);
                startActivity(i2);
                return true;
            case R.id.bills_history:
                Intent i3 = new Intent(this, HistoryActivity.class);
                startActivity(i3);
                return true;
            case R.id.filter:
                Intent i4 = new Intent(this, FilterActivity.class);
                startActivity(i4);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadData() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new HistoryAdapter(context, bills);
        recyclerView.setAdapter(adapter);
        hideLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_history, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void refreshContent() {
        finish();
        startActivity(getIntent());
    }

    public void new_bill(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void print_history(View view){
        //TODO : access the good table nr
        Intent i = new Intent(this, PrintHistoryActivity.class);
        startActivity(i);
    }


}
