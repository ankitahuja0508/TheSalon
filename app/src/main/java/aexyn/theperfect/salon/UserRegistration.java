package aexyn.theperfect.salon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistration extends ActionBarActivity implements OnClickListener {
  Button   btnRegister;
  EditText edtName, edtContact, edtAmount, edtService;
  boolean registered = false;
  JSONObject jsonResponse, jsonVisitor;
  String name, contact, location, amount, service;
  String oldPass = "", newPass = "";

  //TextView credits;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_screen);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    btnRegister = (Button) findViewById(R.id.btnRegister);
    edtName = (EditText) findViewById(R.id.edtUserName);
    edtContact = (EditText) findViewById(R.id.edtContact);
    edtAmount = (EditText) findViewById(R.id.edtAmount);
    edtService = (EditText) findViewById(R.id.edtService);
    //      credits = (TextView) findViewById(R.id.textViewCredits);

    edtName.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    edtContact.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    edtAmount.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    edtService.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    btnRegister.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    //    credits.setTextAppearance(this,
    //            android.R.style.TextAppearance_DeviceDefault_Small);

    btnRegister.setOnClickListener(this);

    //credits.setOnClickListener(this);

  }

  @Override public void onClick(View v) {
    // TODO Auto-generated method stub
    if (v.getId() == R.id.btnRegister) {
      startRegister();
    }

/*        if (v.getId() == R.id.textViewCredits) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.site)));
            startActivity(browserIntent);
        }*/

  }

  public void startRegister() {
    if (!edtName.getText().toString().isEmpty() && !edtContact.getText().toString().isEmpty()) {
      if (edtContact.getText().toString().length() == 10) {
        if (Network.isAvailable(getApplicationContext())) {
          new Register().execute();
        } else {
          Defs.showToast(getApplicationContext(), "You don't have internet connection.");
        }
      } else {
        Defs.showToast(getApplicationContext(), "Please enter a valid phone number");
      }
    } else {
      Defs.showToast(getApplicationContext(), "Missing field(s)");
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.user_registration_menu, menu);
    return true;
  }

  /**
   * Event Handling for Individual menu item selected Identify single menu
   * item by it's id
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    Intent i;
    switch (item.getItemId()) {
      case R.id.display:
        i = new Intent(getApplicationContext(), DisplayVisitorNames.class);
        i.putExtra(Defs.TAG_LOCATION, Defs.LOCATION);
        startActivity(i);
        return true;

      case R.id.changePass:
        showChangePassDialog();
        return true;

      case R.id.log_out:
        savePreferences(Defs.KEY_LOGIN_TYPE, 2);
        i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void showChangePassDialog() {

    int screenWidth  = getResources().getDisplayMetrics().widthPixels;
    int screenHeight = getResources().getDisplayMetrics().heightPixels;

    // custom dialog
    final Dialog dialog = new Dialog(this);//, R.style.FullHeightDialog);

    dialog.setContentView(R.layout.change_password);
    dialog.setTitle("Change Password");
    dialog.getWindow().setLayout(100 * screenWidth / 100, 70 * screenHeight / 100);
    Button         dialogButton = (Button) dialog.findViewById(R.id.btnChangePass);
    final EditText edtOld       = (EditText) dialog.findViewById(R.id.edtOldPass);
    final EditText edtNew       = (EditText) dialog.findViewById(R.id.edtNewPass);

    dialogButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        oldPass = edtOld.getText().toString();
        newPass = edtNew.getText().toString();
        if (oldPass == "" || newPass == "") {
          Defs.showToast(getBaseContext(), "Missing Fields");
        } else {
          new ChangePassword().execute();
          dialog.dismiss();
        }
      }
    });

    dialog.show();
  }

  private void savePreferences(String key, int value) {
    SharedPreferences        sp   = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor edit = sp.edit();
    edit.putInt(key, value);
    edit.commit();
  }

  private void savePreferences(String key, String value) {
    Log.e("Save Json", "Saving : " + value);
    SharedPreferences        sp   = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor edit = sp.edit();
    edit.putString(key, value);
    edit.commit();
  }

  class Register extends AsyncTask<String, String, String> {

    protected String doInBackground(String... args) {
      name = edtName.getText().toString().toLowerCase();
      contact = edtContact.getText().toString();
      location = Defs.LOCATION;
      amount = edtAmount.getText().toString();
      service = edtService.getText().toString();
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("name", name));
      params.add(new BasicNameValuePair("contact", contact));
      params.add(new BasicNameValuePair("location", location));
      params.add(new BasicNameValuePair("date", TimeStamp.getDate()));
      params.add(new BasicNameValuePair("time", TimeStamp.getTime()));
      params.add(new BasicNameValuePair("username", Defs.USERNAME));
      params.add(new BasicNameValuePair("password", Defs.PASSWORD));

      if (!edtAmount.getText().toString().isEmpty()) {
        params.add(new BasicNameValuePair("amount", amount));
      }
      if (!edtService.getText().toString().isEmpty()) {
        params.add(new BasicNameValuePair("service", service));
      }

      jsonResponse = Defs.jsonParser.makeHttpRequest(Defs.url_register_user, "GET", params);

      // check log cat for response
      Log.e("Registration", jsonResponse.toString());

      // check for success tag
      try {
        int success = jsonResponse.getInt(Defs.TAG_SUCCESS);

        registered = success == 1;
      } catch (JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      Defs.pDialog = new ProgressDialog(UserRegistration.this);
      Defs.pDialog.setMessage("Registering...");
      Defs.pDialog.setIndeterminate(false);
      Defs.pDialog.setCancelable(false);
      Defs.pDialog.show();
    }

    protected void onPostExecute(String file_url) {
      // dismiss the dialog once done
      Defs.pDialog.dismiss();
      edtName.setText("");
      edtContact.setText("");
      edtAmount.setText("");
      edtService.setText("");
      try {
        if (registered) {
          Defs.showToast(getApplicationContext(), jsonResponse.getString(Defs.TAG_MESSAGE));
        } else {
          Defs.showToast(getApplicationContext(), jsonResponse.getString(Defs.TAG_MESSAGE));
        }
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  class ChangePassword extends AsyncTask<String, String, String> {

    @Override protected void onPreExecute() {
      super.onPreExecute();
      Defs.pDialog = new ProgressDialog(UserRegistration.this);
      Defs.pDialog.setMessage("Changing Password....");
      Defs.pDialog.setIndeterminate(false);
      Defs.pDialog.setCancelable(false);
      Defs.pDialog.show();
    }

    protected String doInBackground(String... args) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("old_pass", oldPass));
      params.add(new BasicNameValuePair("new_pass", newPass));
      params.add(new BasicNameValuePair("location", Defs.LOCATION));

      jsonResponse = Defs.jsonParser.makeHttpRequest(Defs.url_change_pass, "GET", params);

      // check log cat for response
      Log.e("json response", jsonResponse.toString());

      return null;
    }

    protected void onPostExecute(String file_url) {
      // dismiss the dialog once done
      Defs.pDialog.dismiss();
      try {
        Defs.showToast(getApplicationContext(), jsonResponse.getString(Defs.TAG_MESSAGE));
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
