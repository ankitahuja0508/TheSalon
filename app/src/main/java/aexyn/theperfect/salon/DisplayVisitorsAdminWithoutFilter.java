package aexyn.theperfect.salon;

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
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayVisitorsAdminWithoutFilter extends ActionBarActivity {

    JSONObject jsonResponse;
    boolean visitorsFetched= false;
    int screenWidth, screenHeight;

    ArrayList<String> listName = new ArrayList<String>();
    ArrayList<String> listPhone = new ArrayList<String>();
    ArrayList<Integer> listNoVisits = new ArrayList<>();
    ArrayList<String> listDate = new ArrayList<String>();
    ArrayList<String> listTime = new ArrayList<String>();
    TextView tvTotalVisitors,tvTotalVisits;
    static final int START_DATE_DIALOG_ID = 998;
    static final int END_DATE_DIALOG_ID = 999;
    private int year;
    private int month;
    private int day;
    int tmpTotalVisitors = 0;
    int tmpTotalVisits = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_visitors_admin_without_filter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setTitle("Visitors in : " + Defs.LOCATION_SELECTED_ADMIN.toUpperCase());

        Defs.header = (TableLayout) findViewById(R.id.table_header);
        Defs.scrollablePart = (TableLayout) findViewById(R.id.scrollable_part);
        tvTotalVisits = (TextView)findViewById(R.id.tvTotalVisits);
        tvTotalVisitors = (TextView)findViewById(R.id.tvTotalVisitors);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

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
        Defs.header.addView(Defs.row);
    }

    public void addScan(String name, String ph, int vCount, String date, String time) {
        tmpTotalVisitors = tmpTotalVisitors + 1;
        tmpTotalVisits = tmpTotalVisits + vCount;
        tvTotalVisitors.setText(Defs.TOTAL_VISITORS_TEXT + tmpTotalVisitors);
        tvTotalVisits.setText(Defs.TOTAL_VISITS_TEXT + tmpTotalVisits);

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

    class DisplayAllVisitors extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(DisplayVisitorsAdminWithoutFilter.this);
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

                    JSONArray tmpJsonArray = jsonResponse
                            .getJSONArray(Defs.TAG_VISITOR);

                    for (int k = 0; k < tmpJsonArray.length(); k++) {
                        JSONObject tmpObject = new JSONObject();
                         tmpObject = tmpJsonArray.getJSONObject(k);
                        listName.add(tmpObject.getString("name"));
                        listPhone.add(tmpObject.getString("contact"));
                        listNoVisits.add(tmpObject.getInt("no_of_visits"));
                        listDate.add(tmpObject.getString("date"));
                        listTime.add(tmpObject.getString("time"));
                    }

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
                    tmpTotalVisitors = 0;
                    tmpTotalVisits = 0;
                    for (int j = 0; j < listName.size(); j++) {
                        addScan(listName.get(j) , listPhone.get(j) , listNoVisits.get(j) , listDate.get(j) , listTime.get(j));
                    }
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
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                try {
                    exportInCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void exportInCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/WBT");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);

            final String filename = folder.toString() + "/"
                    + Defs.LOCATION_SELECTED_ADMIN + " " + TimeStamp.getTimeStamp() + ".csv";

            // show waiting screen
            CharSequence contentTitle = getString(R.string.app_name);
            final ProgressDialog progDailog = ProgressDialog.show(
                    DisplayVisitorsAdminWithoutFilter.this, contentTitle, "Saving...",
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

                        for (int j = 0; j < listName.size() - 1; j++) {
                            fw.append('\n');
                            fw.append(listName.get(j));
                            fw.append(',');
                            fw.append(listPhone.get(j));
                            fw.append(',');
                            fw.append(Integer.toString(listNoVisits.get(j)));
                            fw.append(',');
                            fw.append(listDate.get(j));
                            fw.append(',');
                            fw.append(listTime.get(j));
                            fw.append(',');
                        }

                        fw.close();

                    } catch (Exception e) {
                    }
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();
                    finish();
                }
            }.start();

        }

    }
}
