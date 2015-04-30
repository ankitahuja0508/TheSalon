package aexyn.theperfect.salon;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayVisitorDetails extends ActionBarActivity implements OnClickListener {

    JSONObject jsonResponse;
    boolean visitorsFetched= false;
    int screenWidth, screenHeight;

    ArrayList<String> listDate = new ArrayList<String>();
    ArrayList<String> listTime = new ArrayList<String>();
    ArrayList<String> listAmount = new ArrayList<String>();
    ArrayList<String> listService = new ArrayList<String>();

    Button btnStartDate, btnEndDate;
    TextView tvTotalVisitors,tvTotalVisits;
    static final int START_DATE_DIALOG_ID = 998;
    static final int END_DATE_DIALOG_ID = 999;
    private int year;
    private int month;
    private int day;
    int tmpTotalVisitors = 0;
    int tmpTotalVisits = 0;
    Date startDate = new Date();
    Date endDate = new Date();
    Date tmpDate = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    boolean validPeriod = false;

    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_visitors);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Defs.header = (TableLayout) findViewById(R.id.table_header);
        Defs.scrollablePart = (TableLayout) findViewById(R.id.scrollable_part);
        btnStartDate = (Button) findViewById(R.id.startDatePicker);
        btnEndDate = (Button) findViewById(R.id.endDatePicker);
        tvTotalVisits = (TextView)findViewById(R.id.tvTotalVisits);
        tvTotalVisitors = (TextView)findViewById(R.id.tvTotalVisitors);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);

        addHeader();


        Intent i = getIntent();
        phone = i.getStringExtra(Defs.TAG_PHONE);

        new DisplayAllVisitors().execute();
    }

    public void addHeader() {
        Defs.row = new TableRow(this);
        Defs.row.setLayoutParams(Defs.wrapWrapTableRowParams);
        Defs.row.setGravity(Gravity.CENTER);

        Defs.row.addView(makeTableRowWithText("Amount",
                25, Defs.fixedHeaderHeight,
                R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Service",
                25, Defs.fixedHeaderHeight,
                R.drawable.header));
		Defs.row.addView(makeTableRowWithText("Date",
				25, Defs.fixedHeaderHeight,
				R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Time",
                25, Defs.fixedHeaderHeight,
                R.drawable.header));

        Defs.header.addView(Defs.row);
    }

    public void addScan(String amt, String ser , String date, String time) {
        //tmpTotalVisitors = tmpTotalVisitors + 1;
        tmpTotalVisits = tmpTotalVisits + 1;
        tvTotalVisitors.setText(Defs.TOTAL_VISITORS_TEXT + tmpTotalVisitors);
        tvTotalVisits.setText(Defs.TOTAL_VISITS_TEXT + tmpTotalVisits);

        Defs.row = new TableRow(this);
        Defs.row.setLayoutParams(Defs.wrapWrapTableRowParams);
        Defs.row.setGravity(Gravity.CENTER);


        Defs.row.addView(makeTableRowWithText(amt,
                25, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(ser,
                25, Defs.scrollColumnHeight,
                R.drawable.scanned_items));

        Defs.row.addView(makeTableRowWithText(date,
                25, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(time,
                25, Defs.scrollColumnHeight,
                R.drawable.scanned_items));

        Defs.scrollablePart.addView(Defs.row);
    }
    /*public void addFooter() {
        Defs.row = new TableRow(this);
        Defs.row.setLayoutParams(Defs.wrapWrapTableRowParams);
        Defs.row.setGravity(Gravity.CENTER);

        Defs.row.addView(makeTableRowWithText(Defs.COUNT_TEXT,
                Defs.WIPtoPKG_FooterWidth, Defs.fixedHeaderHeight,
                Defs.COUNT_TEXT_ID, R.drawable.footer));


        Defs.footer.addView(Defs.row);
    }
*/
    public TextView makeTableRowWithText(String text,
                                         int widthInPercentOfScreenWidth, int fixedHeightInPixels,
                                         int background) {
        Defs.recyclableTextView = new TextView(this);
        Defs.recyclableTextView.setText(text);
        Defs.recyclableTextView.setTextColor(Color.BLACK);
        Defs.recyclableTextView.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault_Small);
        Defs.recyclableTextView.setWidth(widthInPercentOfScreenWidth
                * screenWidth / 100);
        Defs.recyclableTextView.setHeight(fixedHeightInPixels * screenHeight
                / 100);
        Defs.recyclableTextView.setGravity(Gravity.CENTER);

        Defs.recyclableTextView.setBackgroundResource(background);
        Defs.recyclableTextView.setPadding(5, 5, 5, 5);

        return Defs.recyclableTextView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.display_all, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.display_all:
                   tmpTotalVisitors = 0;
                    tmpTotalVisits = 0;
                Defs.scrollablePart.removeAllViews();
                for (int j = 0; j < listDate.size(); j++) {
                    addScan(listAmount.get(j), listService.get(j) , listDate.get(j) , listTime.get(j));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v.getId() == R.id.startDatePicker){
            showDialog(START_DATE_DIALOG_ID);
        }
        if(v.getId() == R.id.endDatePicker){
            showDialog(END_DATE_DIALOG_ID);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        //set date picker as current date
        final Calendar c = Calendar.getInstance();


        switch (id) {

            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, startDateListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, endDateListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            btnStartDate.setText(String.valueOf(year) + "-" + (month + 1) + "-" + day);

            try {
                startDate = formatter.parse(String.valueOf(year) + "-" + (month + 1) + "-" + day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(btnEndDate.getVisibility() == View.VISIBLE && endDate.after(startDate))
                filterVisitors();
            btnEndDate.setVisibility(View.VISIBLE);

        }
    };

    private DatePickerDialog.OnDateSetListener endDateListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            btnEndDate.setText(new StringBuilder().append(year).append("-").append(month + 1)
                    .append("-").append(day));

            try {
                endDate = formatter.parse(new StringBuilder().append(year).append("-").append(month + 1)
                        .append("-").append(day).toString());

                if(endDate.compareTo(startDate)>=0){
                    filterVisitors();
                }else
                    Defs.showToast(getBaseContext() , "Invalid Period");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };

    public void filterVisitors(){
           tmpTotalVisitors = 0;
        tmpTotalVisits = 0;
        Defs.scrollablePart.removeAllViews();
        for(int x = 0 ; x < listDate.size() ; x++){
            try {
                tmpDate = formatter.parse(listDate.get(x));
                if((startDate.equals(tmpDate) || startDate.before(tmpDate)) && (tmpDate.before(endDate) || tmpDate.equals(endDate) )){
                    addScan(listAmount.get(x), listService.get(x) , listDate.get(x) , listTime.get(x));
                }else{
                    tvTotalVisitors.setText(Defs.TOTAL_VISITORS_TEXT + tmpTotalVisitors);
                    tvTotalVisits.setText(Defs.TOTAL_VISITS_TEXT + tmpTotalVisits);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class DisplayAllVisitors extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(DisplayVisitorDetails.this);
            Defs.pDialog.setMessage("Fetching Visitor List...");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("contact", phone));

            jsonResponse = Defs.jsonParser.makeHttpRequest(
                    Defs.url_get_visitor_details, "GET", params);

            // check log cat for response
            Log.e("Visitors details", jsonResponse.toString());

            // check for success tag
            try {
                int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

                if (success == 1) {
                    visitorsFetched = true;
                    listDate.clear();
                    listTime.clear();
                    listAmount.clear();
                    listService.clear();

                    Defs.visitorsArray = jsonResponse
                            .getJSONArray(Defs.TAG_VISITOR);

                } else {
                    visitorsFetched = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();
            try {
                if (visitorsFetched) {
                   displayAll();
                } else {
                    Defs.showToast(getApplicationContext(),
                            jsonResponse.getString(Defs.TAG_MESSAGE));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void displayAll(){
        for (int k = 0; k < Defs.visitorsArray.length(); k++) {
            JSONObject tmpObject = new JSONObject();
            try {
                tmpObject = Defs.visitorsArray.getJSONObject(k);
                listDate.add(tmpObject.getString("date"));
                listTime.add(tmpObject.getString("time"));
                listAmount.add(tmpObject.getString("amount"));
                listService.add(tmpObject.getString("service"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        tmpTotalVisitors = 0;
        tmpTotalVisits = 0;
        for (int j = 0; j < listDate.size(); j++) {
            addScan(listAmount.get(j), listService.get(j) , listDate.get(j) , listTime.get(j));
        }
    }

}
