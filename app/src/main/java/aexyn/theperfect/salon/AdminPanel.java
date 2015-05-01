package aexyn.theperfect.salon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminPanel extends ActionBarActivity implements OnClickListener {

  Button btnAddFranchisee, btnGetReports, btnDeleteFranchisee, btnActivateFranchisee, btnChangePass;
  JSONObject jsonResponse;

  ArrayList<String> listWithStatus = new ArrayList<String>();
  ArrayList<String> listLocations  = new ArrayList<String>();

  int    getAll  = 0;
  String oldPass = "", newPass = "";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.admin_screen);

    btnAddFranchisee = (Button) findViewById(R.id.btnAddFranchiseeOption);
    btnGetReports = (Button) findViewById(R.id.btnGetReportsOption);
    btnDeleteFranchisee = (Button) findViewById(R.id.btnDeleteFranchisee);
    btnActivateFranchisee = (Button) findViewById(R.id.btnActivateFranchisee);
    btnChangePass = (Button) findViewById(R.id.btnChangePass);

    btnGetReports.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);
    btnAddFranchisee.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);
    btnDeleteFranchisee.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);
    btnActivateFranchisee.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);
    btnChangePass.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault);

    btnAddFranchisee.setOnClickListener(this);
    btnGetReports.setOnClickListener(this);
    btnDeleteFranchisee.setOnClickListener(this);
    btnActivateFranchisee.setOnClickListener(this);
    btnChangePass.setOnClickListener(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.log_out, menu);
    return true;
  }

  /**
   * Event Handling for Individual menu item selected Identify single menu
   * item by it's id
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.log_out:
        savePreferences(Defs.KEY_LOGIN_TYPE, 2);
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void savePreferences(String key, int value) {
    SharedPreferences        sp   = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor edit = sp.edit();
    edit.putInt(key, value);
    edit.commit();
  }

  @Override public void onClick(View view) {
    // TODO Auto-generated method stub
    Intent i;
    if (view.getId() == R.id.btnAddFranchiseeOption) {
      i = new Intent(getApplicationContext(), AddFranchisee.class);
      startActivity(i);
    }
    if (view.getId() == R.id.btnGetReportsOption) {
      getAll = Defs.GET_ALL;
      new GetLocations().execute();
    }
    if (view.getId() == R.id.btnActivateFranchisee) {
      getAll = Defs.GET_DELETED;
      new GetLocations().execute();
    }
    if (view.getId() == R.id.btnDeleteFranchisee) {
      getAll = Defs.GET_ACTIVATED;
      new GetLocations().execute();
    }
    if (view.getId() == R.id.btnChangePass) {
      getAll = Defs.GET_ALL_FOR_CHANGE_PASS;
      new GetLocations().execute();
    }
  }

  private void showDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Select Franchisee");
    if (getAll == Defs.GET_ALL) {
      builder.setItems(listWithStatus.toArray(new String[listWithStatus.size()]),
                       new DialogInterface.OnClickListener() {

                         public void onClick(DialogInterface dialog, int which) {

                           Defs.LOCATION_SELECTED_ADMIN = listLocations.get(which);
                           Intent i = new Intent(getApplicationContext(), DisplayVisitorNames.class);
                           i.putExtra(Defs.TAG_LOCATION, Defs.LOCATION_SELECTED_ADMIN);
                           startActivity(i);

                           dialog.dismiss();
                         }
                       });
    } else if (getAll == Defs.GET_ACTIVATED) {
      builder.setItems(listLocations.toArray(new String[listLocations.size()]), new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int which) {

          Defs.LOCATION_SELECTED_ADMIN = listLocations.get(which);
          dialog.dismiss();
          new DeleteFranchisee().execute();
        }
      });
    } else if (getAll == Defs.GET_DELETED) {
      builder.setItems(listLocations.toArray(new String[listLocations.size()]), new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int which) {

          Defs.LOCATION_SELECTED_ADMIN = listLocations.get(which);
          dialog.dismiss();
          new ActivateFranchisee().execute();
        }
      });
    } else if (getAll == Defs.GET_ALL_FOR_CHANGE_PASS) {
      builder.setItems(listLocations.toArray(new String[listLocations.size()]), new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int which) {
          Defs.LOCATION_SELECTED_ADMIN = listLocations.get(which);
          //showChangePassDialog();
          dialog.dismiss();
        }
      });
    }
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  class GetLocations extends AsyncTask<String, String, String> {

    protected String doInBackground(String... args) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("get_all", Integer.toString(getAll)));

      jsonResponse = Defs.jsonParser.makeHttpRequest(Defs.url_get_locations, "GET", params);

      // check log cat for response
      Log.e("json response", jsonResponse.toString());

      // check for success tag
      try {
        int success = jsonResponse.getInt(Defs.TAG_SUCCESS);
        listWithStatus.clear();
        listLocations.clear();
        if (success == 1) {
          JSONArray tmpJsonArray = jsonResponse.getJSONArray(Defs.TAG_LOCATION);

          for (int k = 0; k < tmpJsonArray.length(); k++) {
            JSONObject tmpObject = new JSONObject();
            tmpObject = tmpJsonArray.getJSONObject(k);
            listLocations.add(tmpObject.getString(Defs.TAG_LOCATION));
            if (getAll == 1) {
              listWithStatus.add(tmpObject.getString(Defs.TAG_LOCATION) + tmpObject.getString(Defs.TAG_ACTIVATED));
            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      Defs.pDialog = new ProgressDialog(AdminPanel.this);
      Defs.pDialog.setMessage("Fetching Franchisee Names...");
      Defs.pDialog.setIndeterminate(false);
      Defs.pDialog.setCancelable(false);
      Defs.pDialog.show();
    }

    protected void onPostExecute(String file_url) {
      // dismiss the dialog once done
      Defs.pDialog.dismiss();
      try {
        if (listLocations.size() != 0) {
          showDialog();
        } else {
          Defs.showToast(getApplicationContext(), jsonResponse.getString(Defs.TAG_MESSAGE));
        }
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  class DeleteFranchisee extends AsyncTask<String, String, String> {

    @Override protected void onPreExecute() {
      super.onPreExecute();
      Defs.pDialog = new ProgressDialog(AdminPanel.this);
      Defs.pDialog.setMessage("Deleting..." + Defs.LOCATION_SELECTED_ADMIN);
      Defs.pDialog.setIndeterminate(false);
      Defs.pDialog.setCancelable(false);
      Defs.pDialog.show();
    }

    protected String doInBackground(String... args) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("location", Defs.LOCATION_SELECTED_ADMIN));

      jsonResponse = Defs.jsonParser.makeHttpRequest(Defs.url_delete_franchisee, "GET", params);

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

    /*class ChangePassword extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(AdminPanel.this);
            Defs.pDialog.setMessage("Changing Password of .. :" + Defs.LOCATION_SELECTED_ADMIN);
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("old_pass", oldPass));
            params.add(new BasicNameValuePair("new_pass", newPass));
            params.add(new BasicNameValuePair("location", Defs.LOCATION_SELECTED_ADMIN));

            jsonResponse = Defs.jsonParser.makeHttpRequest(
                    Defs.url_change_pass, "GET", params);

            // check log cat for response
            Log.e("json response", jsonResponse.toString());


            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();
            try {
                Defs.showToast(getApplicationContext(),
                        jsonResponse.getString(Defs.TAG_MESSAGE));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/

  class ActivateFranchisee extends AsyncTask<String, String, String> {

    @Override protected void onPreExecute() {
      super.onPreExecute();
      Defs.pDialog = new ProgressDialog(AdminPanel.this);
      Defs.pDialog.setMessage("Activating..." + Defs.LOCATION_SELECTED_ADMIN);
      Defs.pDialog.setIndeterminate(false);
      Defs.pDialog.setCancelable(false);
      Defs.pDialog.show();
    }

    protected String doInBackground(String... args) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("location", Defs.LOCATION_SELECTED_ADMIN));

      jsonResponse = Defs.jsonParser.makeHttpRequest(Defs.url_activate_franchisee, "GET", params);

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

    /*public void showChangePassDialog(){

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // custom dialog
        final Dialog dialog = new Dialog(this);//, R.style.FullHeightDialog);

        dialog.setContentView(R.layout.change_password);
        dialog.setTitle("Change Password");
        dialog.getWindow().setLayout(100 * screenWidth / 100 , 70 * screenHeight / 100);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnChangePass);
        final EditText edtOld = (EditText) dialog.findViewById(R.id.edtOldPass);
        final EditText edtNew = (EditText) dialog.findViewById(R.id.edtNewPass);

        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = edtOld.getText().toString();
                newPass = edtNew.getText().toString();
                if(oldPass == "" || newPass == ""){
                    Defs.showToast(getBaseContext() , "Missing Fields" );
                }else{
                    new ChangePassword().execute();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }*/
}
