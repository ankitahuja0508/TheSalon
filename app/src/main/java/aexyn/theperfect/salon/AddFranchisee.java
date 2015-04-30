package aexyn.theperfect.salon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddFranchisee extends ActionBarActivity implements OnClickListener {
    Button add;
    EditText franchaiseeId, edtPass, edtLocation;
    boolean franchiseeAdded = false;
    JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_franchisee);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        add = (Button) findViewById(R.id.btnAddFranchisee);
        franchaiseeId = (EditText) findViewById(R.id.edtFranchiseeID);
        edtPass = (EditText) findViewById(R.id.edtFranchiseePass);
        edtLocation = (EditText) findViewById(R.id.edtLocation);


        add.setOnClickListener(this);

        franchaiseeId.requestFocus();
    }

    public void startAddingTask() {
        if (!franchaiseeId.getText().toString().isEmpty()
                && !edtPass.getText().toString().isEmpty()
                && !edtLocation.getText().toString().isEmpty()) {
            if (Network.isAvailable(getApplicationContext())) {
                new AddFranchiseeTask().execute();
            } else {
                Defs.showToast(getApplicationContext(),
                        "You don't have internet connection.");
            }
        } else {
            Defs.showToast(getApplicationContext(), "Missing field(s)");
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == add) {
            startAddingTask();
        }
    }

    class AddFranchiseeTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(AddFranchisee.this);
            Defs.pDialog.setMessage("Adding Franchisee...");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }


        protected String doInBackground(String... args) {
            String id = franchaiseeId.getText().toString().toLowerCase();
            String password = edtPass.getText().toString().toLowerCase();
            String location = edtLocation.getText().toString().toLowerCase();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("pass", password));
            params.add(new BasicNameValuePair("location", location));

            jsonResponse = Defs.jsonParser.makeHttpRequest(
                    Defs.url_add_franchisee, "GET", params);

            // check log cat for response
            Log.e("Login Response", jsonResponse.toString());

            // check for success tag
            try {
                int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

                if (success == 1) {
                    franchiseeAdded = true;
                } else {
                    // failed to login
                    franchiseeAdded = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();
            Intent i;
            try {
            if (franchiseeAdded) {
                franchaiseeId.setText("");
                edtPass.setText("");
                edtLocation.setText("");
                Defs.showToast(getApplicationContext(),
                        jsonResponse.getString(Defs.TAG_MESSAGE));
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
