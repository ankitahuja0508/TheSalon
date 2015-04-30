package aexyn.theperfect.salon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayVisitorNames extends AppCompatActivity {

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
    JSONObject jsonResponse;
	ArrayList<HashMap<String, String>> visitorsList;



	// products JSONArray
	JSONArray visitors = null;

    int noOfVisits = 0;
    boolean fetched = false;
    ListView lv;
    String selectedLocation;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_visitor_names);

		// Hashmap for ListView
		visitorsList = new ArrayList<HashMap<String, String>>();

        Intent i = getIntent();
        selectedLocation = i.getStringExtra(Defs.TAG_LOCATION);

		// Loading products in Background Thread
		new LoadAllVisitors().execute();


		lv = (ListView)findViewById(R.id.list);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String phone = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				Intent in = new Intent(getApplicationContext(),
						DisplayVisitorDetails.class);

				in.putExtra(Defs.TAG_PHONE, phone);

				startActivityForResult(in, 100);
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}


	class LoadAllVisitors extends AsyncTask<String, String, String> {


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Defs.pDialog = new ProgressDialog(DisplayVisitorNames.this);
            Defs.pDialog.setMessage("Loading products. Please wait...");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
		}


		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("location", selectedLocation));
            jsonResponse = jParser.makeHttpRequest(Defs.url_get_visitor_names, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.e("All Visitors: ", jsonResponse.toString());

			try {
				// Checking for SUCCESS TAG
				int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

				if (success == 1) {
                    fetched = true;
					visitors = jsonResponse.getJSONArray(Defs.TAG_VISITORS);
                    noOfVisits = jsonResponse.getInt(Defs.TAG_COUNT);

					for (int i = 0; i < visitors.length(); i++) {
						JSONObject c = visitors.getJSONObject(i);

						// Storing each json item in variable
						String name = c.getString(Defs.TAG_NAME);
						String phone = c.getString(Defs.TAG_PHONE);
                        String visit_count = c.getString(Defs.TAG_VISIT_COUNT);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(Defs.TAG_PHONE, phone);
						map.put(Defs.TAG_NAME, name);
                        map.put(Defs.TAG_VISIT_COUNT, visit_count);
						// adding HashList to ArrayList
						visitorsList.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {

			Defs.pDialog.dismiss();

            try {
                if (fetched) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            ListAdapter adapter = new SimpleAdapter(
                                    DisplayVisitorNames.this, visitorsList,
                                    R.layout.list_item, new String[]{Defs.TAG_PHONE,
                                    Defs.TAG_NAME , Defs.TAG_VISIT_COUNT},
                                    new int[]{R.id.pid, R.id.name , R.id.count});
                            // updating listview
                            lv.setAdapter(adapter);
                        }
                    });
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
}