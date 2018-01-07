package de.httptandooripalace.restaurantorderprinter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.HistoryAdapter;
import entities.Bill;

/**
 * Created by uiz on 25/07/2017.
 */

public class FilterActivity extends BaseActivity {

    TextView id_label,date_label,waiter_label,table_label,price_label,first_date,second_date,date_results;
    EditText id_filter,waiter_filter,tableNr_filter;
    Button apply_filters,select_date,clear;
    RadioButton date1, date2;
    DatePicker datePicker1, datePicker2;
    ScrollView date_selection;

    private List<Bill> bills = new ArrayList<Bill>();
    private TextWatcher textWatcher;
    private boolean filterChoosen;
    private String choosen_waiter,choosen_id, choosen_tableNr;
    private int date1_day,date1_month,date1_year,date2_day,date2_month,date2_year;
    private Date date;
    private int bill_day,bill_month,bill_year;
    private Calendar billCalander,date1Cal,date2Cal;

    private List<Bill> choosen_bills = new ArrayList<>();
    private List<Bill> dateFilteredData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.filter);
        setContentView(R.layout.filter_activity);
        id_filter = (EditText) findViewById(R.id.id_filter);
        waiter_filter = (EditText) findViewById(R.id.waiter_filter);
        tableNr_filter = (EditText) findViewById(R.id.tableNr_filter);
        apply_filters = (Button) findViewById(R.id.apply_filters);
        date1 = (RadioButton) findViewById(R.id.date1);
        date2 = (RadioButton) findViewById(R.id.date2);
        id_label = (TextView) findViewById(R.id.id_label);
        date_label = (TextView) findViewById(R.id.date_label);
        waiter_label = (TextView) findViewById(R.id.waiter_label);
        table_label = (TextView) findViewById(R.id.table_label);
        price_label = (TextView) findViewById(R.id.price_label);
        select_date = (Button) findViewById(R.id.select_date);
        datePicker1 = (DatePicker) findViewById(R.id.date_selection_1);
        datePicker2 = (DatePicker) findViewById(R.id.date_selection_2);
        date_selection = (ScrollView) findViewById(R.id.date_selection);
        first_date = (TextView) findViewById(R.id.first_date);
        second_date = (TextView) findViewById(R.id.second_date);
        date_results = (TextView) findViewById(R.id.date_results);
        clear = (Button) findViewById(R.id.clear);

        tableNr_filter.setImeOptions(EditorInfo.IME_ACTION_DONE);


        filterChoosen=false;
        bills = HistoryActivity.bills;


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterChoosen=true;
                bindNothing();
            }
        };

        id_filter.addTextChangedListener(textWatcher);
        waiter_filter.addTextChangedListener(textWatcher);
        tableNr_filter.addTextChangedListener(textWatcher);


        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindNothing();
                date2.setChecked(false);
                date_selection.setVisibility(date_selection.VISIBLE);
                datePicker2.setVisibility(datePicker2.GONE);

            }
        });

        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindNothing();
                date1.setChecked(false);
                date_selection.setVisibility(date_selection.VISIBLE);
                datePicker2.setVisibility(datePicker2.VISIBLE);


            }
        });


        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_results.setVisibility(date_results.VISIBLE);

                date1_year = datePicker1.getYear();
                date1_month = datePicker1.getMonth();
                date1_day = datePicker1.getDayOfMonth();

                date2_year = datePicker2.getYear();
                date2_month = datePicker2.getMonth();
                date2_day = datePicker2.getDayOfMonth();

                if (date1.isChecked()){
                    first_date.setText(date1_day + "/" + date1_month + "/" + date1_year);
                    second_date.setText("");
                    dailyDateFilter();
                }else if (date2.isChecked()){
                    first_date.setText(date1_day + "/" + date1_month + "/" + date1_year);
                    second_date.setText(date2_day + "/" + date2_month + "/" + date2_year);
                    intervalDateFilter();
                }

                filterChoosen=true;
                clear.setVisibility(clear.VISIBLE);
                datePicker2.setVisibility(datePicker2.GONE);
                date_selection.setVisibility(date_selection.GONE);



            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date1_year = 0;
                date1_month = 0;
                date1_day = 0;

                date2_year = 0;
                date2_month = 0;
                date2_day = 0;

                bindNothing();
                first_date.setText("");
                second_date.setText("");
                date_results.setVisibility(date_results.GONE);
                dateFilteredData.clear();
                clear.setVisibility(clear.GONE);
            }
        });

        //TODO: Write an API to get waiter names from the database and then bind them in a spinner/dropbox instead of editText


        apply_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterChoosen) {

                    List<Bill> data;


                    choosen_id = id_filter.getText().toString();
                    choosen_waiter = waiter_filter.getText().toString().toUpperCase(Locale.getDefault());
                    choosen_tableNr = tableNr_filter.getText().toString();

                    choosen_bills.clear();

                    if (!(choosen_id.isEmpty() && choosen_waiter.isEmpty() && choosen_tableNr.isEmpty())){

                        if (dateFilteredData.size()==0) data=bills;
                        else data = dateFilteredData;

                        for (int i = 0; i < data.size(); i++) {
                            String bill_id = String.valueOf(data.get(i).getId());
                            String bill_waiter = data.get(i).getWaiter().toUpperCase(Locale.getDefault());
                            String bill_tableNr = data.get(i).getTableNr();

                            //MULTIPLE FILTERING
                            if (    choosen_waiter.equals("") && choosen_tableNr.equals("") && bill_id.equals(choosen_id) ||
                                    choosen_id.equals("") && choosen_tableNr.equals("") && bill_waiter.equals(choosen_waiter) ||
                                    choosen_id.equals("") && choosen_waiter.equals("") && bill_tableNr.equals(choosen_tableNr) ||
                                    bill_id.equals(choosen_id) && bill_waiter.equals(choosen_waiter) && bill_tableNr.equals(choosen_tableNr) ||
                                    choosen_tableNr.equals("") && bill_id.equals(choosen_id) && bill_waiter.equals(choosen_waiter) ||
                                    choosen_waiter.equals("") && bill_id.equals(choosen_id) && bill_tableNr.equals(choosen_tableNr) ||
                                    choosen_id.equals("") && bill_waiter.equals(choosen_waiter) && bill_tableNr.equals(choosen_tableNr)) {

                                choosen_bills.add(data.get(i));
                            }

                        }


                        if (choosen_bills.size() == 0) {
                            create_toast(getString(R.string.no_results));
                        }else {

                            id_label.setVisibility(id_label.VISIBLE);
                            date_label.setVisibility(date_label.VISIBLE);
                            waiter_label.setVisibility(waiter_label.VISIBLE);
                            table_label.setVisibility(table_label.VISIBLE);
                            price_label.setVisibility(price_label.VISIBLE);

                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filtered_bills);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            HistoryAdapter adapter = new HistoryAdapter(getApplicationContext(), choosen_bills);
                            recyclerView.setAdapter(adapter);


                        }

                    }else if (dateFilteredData.size()>0){
                        id_label.setVisibility(id_label.VISIBLE);
                        date_label.setVisibility(date_label.VISIBLE);
                        waiter_label.setVisibility(waiter_label.VISIBLE);
                        table_label.setVisibility(table_label.VISIBLE);
                        price_label.setVisibility(price_label.VISIBLE);

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filtered_bills);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        HistoryAdapter adapter = new HistoryAdapter(getApplicationContext(), dateFilteredData);
                        recyclerView.setAdapter(adapter);
                    }else{

                        setLabelsInvisible();
                        create_toast(getString(R.string.no_filter) + '\n'
                            + getString(R.string.please_try_again));}
                }else {

                    setLabelsInvisible();

                    create_toast(getString(R.string.no_filter) + '\n'
                            + getString(R.string.please_try_again));
                }

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
       onBackPressed();
        return true;
    }



    private void dailyDateFilter(){
        dateFilteredData.clear();

        for (int j=0; j<bills.size(); j++){
            date = bills.get(j).getDate();
            billCalander = Calendar.getInstance();
            billCalander.setTime(date);
            bill_day = billCalander.get(Calendar.DAY_OF_MONTH);
            bill_month = billCalander.get(Calendar.MONTH);
            bill_year = billCalander.get(Calendar.YEAR);

            if (bill_year == date1_year && bill_month == date1_month && bill_day==date1_day){
                dateFilteredData.add(bills.get(j));
            }
        }

    }

    private void intervalDateFilter(){
        dateFilteredData.clear();

        date2Cal = Calendar.getInstance();
        date1Cal = Calendar.getInstance();

        date2Cal.set(date2_year, date2_month, date2_day, 0, 0, 0);
        date1Cal.set(date1_year, date1_month, date1_day, 0, 0, 0);

        if (date2Cal.before(date1Cal)) {
            create_toast(getString(R.string.date_cannot_be_smaller)
                    + '\n' + getString(R.string.please_try_again));
        }else{
        for (int k=0; k<bills.size(); k++) {
            date = bills.get(k).getDate();
            billCalander = Calendar.getInstance();
            billCalander.setTime(date);
            billCalander.set(Calendar.HOUR_OF_DAY, 0);
            billCalander.set(Calendar.MINUTE, 0);
            billCalander.set(Calendar.SECOND,0);
            bill_day = billCalander.get(Calendar.DAY_OF_MONTH);
            bill_month = billCalander.get(Calendar.MONTH);
            bill_year = billCalander.get(Calendar.YEAR);


            if ((billCalander.after(date1Cal) && billCalander.before(date2Cal)) ||
                    bill_year == date1_year && bill_month == date1_month && bill_day==date1_day ||
                    bill_year == date2_year && bill_month == date2_month && bill_day==date2_day){
                dateFilteredData.add(bills.get(k));
            }

        }
        }
    }

    //Clear the recyclerview while you choose dates
    private void bindNothing(){

        List<Bill> emptyList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filtered_bills);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        HistoryAdapter adapter = new HistoryAdapter(getApplicationContext(), emptyList);
        recyclerView.setAdapter(adapter);
    }

    private void setLabelsInvisible(){
        id_label.setVisibility(id_label.INVISIBLE);
        date_label.setVisibility(date_label.INVISIBLE);
        waiter_label.setVisibility(waiter_label.INVISIBLE);
        table_label.setVisibility(table_label.INVISIBLE);
        price_label.setVisibility(price_label.INVISIBLE);
    }


}
