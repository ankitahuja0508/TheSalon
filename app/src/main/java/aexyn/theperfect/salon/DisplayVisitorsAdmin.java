package aexyn.theperfect.salon;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayVisitorsAdmin extends ActionBarActivity implements OnClickListener {

    JSONObject jsonResponse;
    boolean visitorsFetched= false;
    int screenWidth, screenHeight;

    ArrayList<String> listName = new ArrayList<String>();
    ArrayList<String> listPhone = new ArrayList<String>();
    ArrayList<Integer> listNoVisits = new ArrayList<>();
    ArrayList<String> listDate = new ArrayList<String>();
    ArrayList<String> listTime = new ArrayList<String>();
    ArrayList<String> listAmount = new ArrayList<String>();
    ArrayList<String> listService = new ArrayList<String>();

    ArrayList<String> tmplistName = new ArrayList<String>();
    ArrayList<String> tmplistPhone = new ArrayList<String>();
    ArrayList<Integer> tmplistNoVisits = new ArrayList<>();
    ArrayList<String> tmplistDate = new ArrayList<String>();
    ArrayList<String> tmplistTime = new ArrayList<String>();
    ArrayList<String> tmplistAmount = new ArrayList<String>();
    ArrayList<String> tmplistService = new ArrayList<String>();

    Button btnStartDate, btnEndDate, btnSaveDetails;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_visitors_admin);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Defs.header = (TableLayout) findViewById(R.id.table_header);
        Defs.scrollablePart = (TableLayout) findViewById(R.id.scrollable_part);
        btnStartDate = (Button) findViewById(R.id.startDatePicker);
        btnEndDate = (Button) findViewById(R.id.endDatePicker);
        btnSaveDetails = (Button) findViewById(R.id.btnSaveDetails);
        tvTotalVisits = (TextView)findViewById(R.id.tvTotalVisits);
        tvTotalVisitors = (TextView)findViewById(R.id.tvTotalVisitors);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSaveDetails.setOnClickListener(this);

        addHeader();
        Defs.cd = new ConnectionDetector(getApplicationContext());
        new DisplayAllVisitors().execute();
    }

    public void addHeader() {
        Defs.row = new TableRow(this);
        Defs.row.setLayoutParams(Defs.wrapWrapTableRowParams);
        Defs.row.setGravity(Gravity.CENTER);

        Defs.row.addView(makeTableRowWithText("Name",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Phone",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Visited Count",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));
		Defs.row.addView(makeTableRowWithText("Date",
				20, Defs.fixedHeaderHeight,
				R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Time",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Amount",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));
        Defs.row.addView(makeTableRowWithText("Service",
                20, Defs.fixedHeaderHeight,
                R.drawable.header));

        Defs.header.addView(Defs.row);
    }

    public void addScan(String name, String ph, int vCount, String date, String time, String amt, String ser) {
        tmpTotalVisitors = tmpTotalVisitors + 1;
        tmpTotalVisits = tmpTotalVisits + vCount;
        tvTotalVisitors.setText(Defs.TOTAL_VISITORS_TEXT + tmpTotalVisitors);
        tvTotalVisits.setText(Defs.TOTAL_VISITS_TEXT + tmpTotalVisits);

        tmplistName.add(name);
        tmplistPhone.add(ph);
        tmplistNoVisits.add(vCount);
        tmplistDate.add(date);
        tmplistTime.add(time);

        Defs.row = new TableRow(this);
        Defs.row.setLayoutParams(Defs.wrapWrapTableRowParams);
        Defs.row.setGravity(Gravity.CENTER);

        Defs.row.addView(makeTableRowWithText(name,
                20, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(ph,
                20, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(Integer.toString(vCount),
                20, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(date,
                20, Defs.scrollColumnHeight,
                R.drawable.scanned_items));
        Defs.row.addView(makeTableRowWithText(time,
                20, Defs.scrollColumnHeight,
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
                tmplistName.clear();
                tmplistPhone.clear();
                tmplistNoVisits.clear();
                tmplistDate.clear();
                tmplistTime.clear();
                Defs.scrollablePart.removeAllViews();
                for (int j = 0; j < listName.size(); j++) {
                    addScan(listName.get(j) , listPhone.get(j) , listNoVisits.get(j) , listDate.get(j) , listTime.get(j) , listAmount.get(j), listService.get(j));
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
        if(v.getId() == R.id.btnSaveDetails){
            if(tmplistName.size() != 0){
            try {
                exportInCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
                Defs.showToast(getBaseContext(), "Details saved...");
        }else {
                Defs.showToast(getBaseContext(), "No Data to store...");
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        //set date picker as current date
        final Calendar c = Calendar.getInstance();


        switch (id) {

            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, startdatePickerListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, enddatePickerListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startdatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            btnStartDate.setText(new StringBuilder().append(year).append("-").append(month + 1)
                    .append("-").append(day));

            try {
                startDate = formatter.parse(new StringBuilder().append(year).append("-").append(month + 1)
                        .append("-").append(day).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(btnEndDate.getVisibility() == View.VISIBLE && endDate.after(startDate))
                filterVisitors();
            btnEndDate.setVisibility(View.VISIBLE);

        }
    };

    private DatePickerDialog.OnDateSetListener enddatePickerListener
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
        tmplistName.clear();
        tmplistPhone.clear();
        tmplistNoVisits.clear();
        tmplistDate.clear();
        tmplistTime.clear();
        Defs.scrollablePart.removeAllViews();
        for(int x = 0 ; x < listName.size() ; x++){
            try {
                tmpDate = formatter.parse(listDate.get(x));
                if((startDate.equals(tmpDate) || startDate.before(tmpDate)) && (tmpDate.before(endDate) || tmpDate.equals(endDate) )){
                    addScan(listName.get(x) , listPhone.get(x) , listNoVisits.get(x) , listDate.get(x) , listTime.get(x) , listAmount.get(x), listService.get(x));
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
            Defs.pDialog = new ProgressDialog(DisplayVisitorsAdmin.this);
            Defs.pDialog.setMessage("Fetching Visitor List...");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("location", Defs.LOCATION_SELECTED_ADMIN));

            jsonResponse = Defs.jsonParser.makeHttpRequest(
                    Defs.url_get_visitor_datails, "GET", params);

            // check log cat for response
            Log.e("Visitors response", jsonResponse.toString());

            // check for success tag
            try {
                int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

                if (success == 1) {
                    visitorsFetched = true;
                    listName.clear();
                    listPhone.clear();
                    listNoVisits.clear();
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
                listName.add(tmpObject.getString("name"));
                listPhone.add(tmpObject.getString("contact"));
                listNoVisits.add(tmpObject.getInt("no_of_visits"));
                listDate.add(tmpObject.getString("date"));
                listTime.add(tmpObject.getString("time"));
                listAmount.add(tmpObject.getString("amount"));
                listService.add(tmpObject.getString("service"));

                tmplistName.add(tmpObject.getString("name"));
                tmplistPhone.add(tmpObject.getString("contact"));
                tmplistNoVisits.add(tmpObject.getInt("no_of_visits"));
                tmplistDate.add(tmpObject.getString("date"));
                tmplistTime.add(tmpObject.getString("time"));
                tmplistAmount.add(tmpObject.getString("amount"));
                tmplistService.add(tmpObject.getString("service"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        tmpTotalVisitors = 0;
        tmpTotalVisits = 0;
        for (int j = 0; j < listName.size(); j++) {
            addScan(listName.get(j) , listPhone.get(j) , listNoVisits.get(j) , listDate.get(j) , listTime.get(j), listAmount.get(j), listService.get(j));
        }
    }


    public void exportInCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/The Saloon");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);

            final String filename = folder.toString() + "/"
                    + Defs.LOCATION_SELECTED_ADMIN + " " + TimeStamp.getTimeStamp() + ".csv";

            // show waiting screen
            CharSequence contentTitle = getString(R.string.app_name);
            final ProgressDialog progDailog = ProgressDialog.show(
                    DisplayVisitorsAdmin.this, contentTitle, "Saving...",
                    true);// please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                }
            };

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);

                        fw.append("Name");
                        fw.append(',');

                        fw.append("Phone");
                        fw.append(',');

                        fw.append("Visited Count");
                        fw.append(',');

                        fw.append("Date");
                        fw.append(',');

                        fw.append("Time");
                        fw.append(',');

                        fw.append("Amount");
                        fw.append(',');

                        fw.append("Service");
                        fw.append(',');

                        for (int j = 0; j < tmplistName.size() - 1; j++) {
                            fw.append('\n');
                            fw.append(tmplistName.get(j));
                            fw.append(',');
                            fw.append(tmplistPhone.get(j));
                            fw.append(',');
                            fw.append(Integer.toString(tmplistNoVisits.get(j)));
                            fw.append(',');
                            fw.append(tmplistDate.get(j));
                            fw.append(',');
                            fw.append(tmplistTime.get(j));
                            fw.append(',');
                            fw.append(tmplistAmount.get(j));
                            fw.append(',');
                            fw.append(tmplistService.get(j));
                            fw.append(',');
                        }

                        fw.close();

                    } catch (Exception e) {
                    }
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();
                }
            }.start();

        }

    }
}
