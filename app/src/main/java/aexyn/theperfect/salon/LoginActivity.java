package aexyn.theperfect.salon;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    Button btnLogin;
    EditText edtID, edtPass;
    boolean loginValid = false;
    JSONObject jsonResponse;
    int loginType = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setTitle("Login");

        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtID = (EditText) findViewById(R.id.edtUserID);
        edtPass = (EditText) findViewById(R.id.edtPass);

        edtID.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);

        edtPass.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);

        btnLogin.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);

        btnLogin.setOnClickListener(this);

        Defs.cd = new ConnectionDetector(getApplicationContext());
        loadSavedPreferences();
        login();
    }

    public void startLogin() {
        if (!edtID.getText().toString().isEmpty()
                && !edtPass.getText().toString().isEmpty()) {
            if (Defs.cd.isConnectingToInternet()) {
                new CheckLogin().execute();
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
        if (v.getId() == R.id.btnLogin) {
            startLogin();
        }
    }

    class CheckLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(LoginActivity.this);
            Defs.pDialog.setMessage("Logging in..");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }


        protected String doInBackground(String... args) {
            String id = edtID.getText().toString().toLowerCase();
            String password = edtPass.getText().toString().toLowerCase();

            Defs.USERNAME = edtID.getText().toString().toUpperCase();
            Defs.PASSWORD = edtPass.getText().toString().toUpperCase();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("pass", password));

            jsonResponse = Defs.jsonParser.makeHttpRequest(
                    Defs.url_validate_login, "GET", params);

            // check log cat for response
            Log.e("Login Response", jsonResponse.toString());

            // check for success tag
            try {
                int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

                if (success == 1) {
                    loginValid = true;
                    Defs.LOCATION = jsonResponse.getString(Defs.TAG_LOCATION);
                    loginType = jsonResponse.getInt(Defs.TAG_LOGIN_TYPE);
                    savePreferences(Defs.KEY_LOGIN_TYPE , loginType);
                    savePreferences(Defs.KEY_LOCATION , Defs.LOCATION);
                } else {
                    // failed to login
                    loginValid = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();

            if (loginValid) {
                login();
            } else {
                try {
                    edtPass.setText("");
                    Defs.showToast(getApplicationContext(),
                            jsonResponse.getString(Defs.TAG_MESSAGE));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
   }

    public void login(){
        Intent i;
        if (loginType == 0) {
            i = new Intent(getApplicationContext(),
                    UserRegistration.class);
            startActivity(i);
            finish();
        } else if(loginType == 1) {
            i = new Intent(getApplicationContext(),
                    AdminPanel.class);
            startActivity(i);
            finish();
        }
    }
    private void savePreferences(String key, int value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void loadSavedPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        loginType = sp.getInt(Defs.KEY_LOGIN_TYPE, 2);
        Defs.LOCATION = sp.getString(Defs.KEY_LOCATION, "");
    }
}