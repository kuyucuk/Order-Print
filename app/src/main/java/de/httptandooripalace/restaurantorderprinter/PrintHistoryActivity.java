package de.httptandooripalace.restaurantorderprinter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entities.Bill;
import entities.Settings;
import helpers.Printer;
import helpers.SharedPrefHelper;

import static helpers.Rounder.round;

/**
 * Created by uiz on 05/07/2017.
 */

public class PrintHistoryActivity extends BaseActivity {

    private final int CHARCOUNT_BIG = 48; // Amount of characters fit on one printed line, using $big$ format
    private final int CHARCOUNT_BIGW = 24; // Amount of characters fit on one printed line, using $bigw$ format

    private final String INITIATE = "·27··64·"; // ESC @
    private final String CHAR_TABLE_EURO = "·27··116··19·"; // ESC t 19 -- 19 for euro table
    private final String EURO = "·213·";
    private final String DOT = "·46·";
    private final String BR = "$intro$"; // Line break
    private final String u = "·129·";
    private final String U = "·154·";
    private final String HEADER_FONT = "·27··33··32·";
    static Context context;
    static int bill_nr;
    static Activity activity = null;
    int isThereBill=0;
    public List<Bill> closed_bills = new ArrayList<Bill>();
    int j1=0;
    double total_bill_price;
    double total_high_tax_payment;
    double total_low_tax_payment;
    Calendar now;
    double food;
    double drinks;
    int tablenr;

    //comment
    private entities.Settings settings;
    private static Printer pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.print_history);
        setContentView(R.layout.print_history);
        pr = new Printer();
        final Button daily_records = (Button)findViewById(R.id.daily_records);

        final PrintActivity myPrintAct = new PrintActivity();

        closed_bills = HistoryActivity.bills;

        daily_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                            total_bill_price=0;
                            total_high_tax_payment=0;
                            total_low_tax_payment=0;
                            tablenr = 0;

                            for(int j=0; j<closed_bills.size();j++)
                            {
                                Date date = closed_bills.get(j).getDate();
                                now = Calendar.getInstance();
                                Calendar billDate = Calendar.getInstance();
                                billDate.setTime(date);
                                int billDay = billDate.get(Calendar.DAY_OF_MONTH);
                                int currentDay = now.get(Calendar.DAY_OF_MONTH);
                                int billMonth=billDate.get(Calendar.MONTH);
                                int currentMonth=now.get(Calendar.MONTH);
                                int billYear= billDate.get(Calendar.YEAR);
                                int currentYear = now.get(Calendar.YEAR);
                                j1=j;

                                if (billYear==currentYear && billMonth==currentMonth && billDay == currentDay) {
                                    isThereBill = 1;
                                    try {
                                        tablenr = Integer.parseInt(closed_bills.get(j).getTableNr());
                                    } catch(NumberFormatException nfe) {
                                        System.out.println("Could not parse " + nfe);
                                    }
                                    bill_nr = closed_bills.get(j).getId();



                                    if (tablenr>=100){
                                        total_low_tax_payment+= closed_bills.get(j).getTotal_price_excl();
                                    }
                                    else {
                                        total_high_tax_payment+= closed_bills.get(j).getTotal_price_excl();
                                    }
                                    total_bill_price= total_high_tax_payment+total_low_tax_payment;
                                    /*

                                    try {
                                        JSONObject jsonParams = new JSONObject();
                                        Log.d("RESPONSE", "GETTING BILL PRODUCTS");
                                        jsonParams.put("bill_id", bill_nr);
                                        StringEntity entity = new StringEntity(jsonParams.toString());
                                        RequestClient.post(context,"products/getforbill/", entity, "application/json", new JsonHttpResponseHandler(){
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                // If the response is JSONObject instead of expected JSONArray

                                                try {
                                                    Log.d("RESPONSE", response.getJSONArray("products").toString()); // RESPONSE: {"success":"true","products":[{"id_cat":"18","name_cat":" Dienstag","id_prod":"371","name_prod":"Chicken Curry","reference_prod":"512,","price_prod_excl":"4.03","price_prod_incl":"4.32","description_prod":"","bill_id":"1"},
                                                    JSONArray jsonarray = response.getJSONArray("products");

                                                    for (int i = 0; i<jsonarray.length(); i++)
                                                    {
                                                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                                                        String name = jsonobject.getString("name_prod");
                                                        int id = jsonobject.getInt("id_prod");
                                                        double price_excl = jsonobject.getDouble("price_prod_excl");
                                                        double price_incl = jsonobject.getDouble("price_prod_incl");
                                                        String reference = jsonobject.getString("reference_prod");
                                                        String category = jsonobject.getString("name_cat");
                                                        int count = jsonobject.getInt("count");
                                                        Product p = new Product(id, name, price_excl, price_incl, reference, category, count);
                                                        products.add(p);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                closed_bills.get(j1).setProducts(products);
                                                products.clear();
                                                return;
                                            }
                                            @Override
                                            public void onFailure(int c, Header[] h, String r, Throwable t) {
                                                try {
                                                    Log.d("RESPONSE", r.toString());
                                                }
                                                catch(Exception e) {
                                                    Log.d("Exception HTTP", e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                    catch(Exception e) {
                                        Log.d("Ex", e.getMessage());
                                    }

                                    printBill(j);

                                    */

                                }
                            }
                printBill(total_bill_price, total_low_tax_payment, total_high_tax_payment);

                if(isThereBill==0)
                {
                    Toast myToast= Toast.makeText(context, R.string.no_bill,Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }
        });

        // Get products
        // products = SharedPrefHelper.getPrintItems(getApplicationContext());
        settings = SharedPrefHelper.loadSettings(getApplicationContext());
        context = this;
        activity = this;

        if(settings == null) {
            settings = new Settings();
            SharedPrefHelper.saveSettings(getApplicationContext(), settings);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void printBill(double price, double low, double high) {
       if (price<=0)return;
        sendPrintJob(getBillHeader() + getBillContent(price, low, high) + getBillFooter());
    }

    public String getBillHeader(){
        StringBuilder strb = new StringBuilder("");
        String s;

        strb.append(INITIATE);
        strb.append(CHAR_TABLE_EURO);
        strb.append(BR);

        s = "$bighw$" + BR + pr.getAlignCenterBigw(getString(R.string.daily_journal)) + BR;
        strb.append(s);

        // tableNr = "randomThingToTestIfThePrintWorksWithATableNr";
        strb.append("$bighw$");
        strb.append(BR);

        strb.append("$big$" + BR + BR);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        strb.append(date);
        strb.append(BR + BR);
        //strb.append(alignCenterBigw(getString(R.string.table_nr).toUpperCase() + closed_bills.get(number).getTableNr()));

        return strb.toString();
    }

    public double calculate_tax(double tax,double before_tax){
        double totalPrice_withTax = before_tax*tax;
        int decimal =(int) totalPrice_withTax;
        int fractional = (int) ( 100*(totalPrice_withTax-decimal));
        int rounded_fractional = (int) (Math.round(fractional/10.0)*10);
        String newTotalPrice_incl = round(decimal + 0.01*rounded_fractional);
        double totalPriceIncl=0;
        try {
            totalPriceIncl = Double.parseDouble(newTotalPrice_incl);
        } catch(NumberFormatException nfe) {
            Log.d("msg",nfe.toString());
        }
        return totalPriceIncl;
    }
    // sendPrintJob bill layout
    public String getBillContent(double price,double low, double high) {

        //String tableNr = SharedPrefHelper.getString(getApplicationContext(), "tableNr");
        String s;
        StringBuilder strb = new StringBuilder("");
        double sum;
        strb.append(getLineOf('=', CHARCOUNT_BIG));

        //items = closed_bills.get(number).getProducts();
        double low_incl = calculate_tax(1.07,low);
        double high_incl=calculate_tax(1.19,high);

        sum=low_incl+high_incl;
        food = sum;
        drinks=0;

        strb.append(BR);

        // Booking
        strb.append("$bigw$");
        strb.append(getString(R.string.book));
        strb.append("$big$");
        strb.append(BR);
        s = getString(R.string.normal) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.normal)).length());
        strb.append(s);
        strb.append(BR);

        // Total sales
        s = getString(R.string.total_sales) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.total_sales)).length());
        strb.append(s);
        strb.append(BR);
        strb.append(getLineOf('-', CHARCOUNT_BIG));
        strb.append(BR);

        //Tax
        String tax = round(sum - price);
        String high_tax= "1 = 19.00% = ";
        String low_tax = "2 = 7.00% = ";
        strb.append("$bigw$");
        strb.append(getString(R.string.tax));
        strb.append("$big$");
        strb.append(BR);
        s =  high_tax+round(high_incl-high)+
                pr.getAlignRight((EURO + round(high_incl)), (high_tax+round(high_incl-high)).length());
        strb.append(s);
        strb.append(BR);
        s =  low_tax+round(low_incl-low)+
                pr.getAlignRight((EURO + round(low_incl)), (low_tax+round(low_incl-low)).length());
        strb.append(s);
        strb.append(getLineOf('-', CHARCOUNT_BIG));
        strb.append(BR);


        // Payments
        strb.append("$bigw$");
        strb.append(getString(R.string.payments));
        strb.append("$big$");
        strb.append(BR);
        s = getString(R.string.bar) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.bar)).length());
        strb.append(s);
        strb.append(BR);

        s = getString(R.string.total_sales) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.total_sales)).length());
        strb.append(s);
        strb.append(BR);
        strb.append(getLineOf('-', CHARCOUNT_BIG));
        strb.append(BR);


        // Treasury stock
        strb.append("$bigw$");
        strb.append(getString(R.string.kasse));
        strb.append("$big$");
        strb.append(BR);
        s = getString(R.string.cash) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.cash)).length());
        strb.append(s);
        strb.append(BR);

        s = getString(R.string.total_sales) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.total_sales)).length());
        strb.append(s);
        strb.append(BR);
        strb.append(getLineOf('-', CHARCOUNT_BIG));
        strb.append(BR);

        // Saved
        strb.append("$bigw$");
        strb.append(getString(R.string.saves));
        strb.append("$big$");
        strb.append(BR);
        s = getString(R.string.food) +
                pr.getAlignRight((EURO + round(sum-drinks)), (getString(R.string.food)).length());
        strb.append(s);
        strb.append(BR);
        s = getString(R.string.drinks) +
                pr.getAlignRight((EURO + round(sum-food)), (getString(R.string.drinks)).length());
        strb.append(s);
        strb.append(BR);
        s = getString(R.string.total) +
                pr.getAlignRight((EURO + round(sum)), (getString(R.string.total)).length());
        strb.append(s);
        strb.append(BR);
        strb.append(getLineOf('-', CHARCOUNT_BIG));
        strb.append(BR);



        return strb.toString();
    }
    private static String getLineOf(char c, int lineSize) {
        StringBuilder strb = new StringBuilder("");
        for(int i = 0; i < lineSize; i++) {
            strb.append(c);
        }
        return strb.toString();
    }

    private static String getLineOf(String s, int lineSize) {
        StringBuilder strb = new StringBuilder("");
        for(int i = 0; i < lineSize; i++) {
            strb.append(s);
        }
        return strb.toString();
    }
    private String getBillFooter() {

        StringBuilder strb = new StringBuilder("");
        String s;
        /*
        // Date
        //Date Format Example = Mon Jul 03 15:45:33 GMT+02:00 2017
        int currentTime= now.get(Calendar.SECOND);
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        int currentMonth=now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);

        String time = getString(currentTime);
        String day = getString(currentDay);
        String month = getString(currentMonth);
        String year = getString(currentYear);

        String editedDate= month+" "+day+", "+year+" "+time; //edited format = Jul 03, 2017 15:45:33
*/
        strb.append("$big$" + BR + BR);
        //strb.append(editedDate);    //Prints the date that bill was created
        strb.append(BR + BR);
        strb.append("$big$");

        // Served by waiter
        /*if(!settings.getWaiter().equals(""))
        strb.append(alignCenter(getString(R.string.tyvm)) + "$intro$");
        strb.append(alignCenter(getString(R.string.visit_again)));
 */
        for(int i = 0; i < 8; i++) {
            strb.append(BR);
        }
        strb.append("$cut$");
        return strb.toString();
    }

    public void sendPrintJob(String dataToPrint) {
        Intent intentPrint = new Intent();
        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
//        intentPrint.putExtra("printer_type_id", "1");// For IP
//        intentPrint.putExtra("printer_ip", settings.getPrinterIp());
//        intentPrint.putExtra("printer_port", "9100");
        intentPrint.setType("text/plain");
        /*this.*/startActivity(intentPrint);
    }

}
