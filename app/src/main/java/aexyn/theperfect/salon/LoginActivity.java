package aexyn.theperfect.salon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private final int DEFAULT_LOGIN = 2;
    private Button login;
    private EditText userId;
    private EditText password;
    private ImageView imageView;
    private RelativeLayout layout;
    private boolean loginValid = false;
    private JSONObject jsonResponse;
    private int loginType = DEFAULT_LOGIN;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBlur();

        setContentView(R.layout.activity_login);
        initViews();
        login.setOnClickListener(this);
        loadSavedPreferences();
        login(loginType);
    }

    private void imageBlur() {
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), blurImage(R.drawable.back)));

    }

    public Bitmap blurImage(int imageId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        return blurImage(bitmap);
    }

    public Bitmap blurImage(Bitmap bitmap) {
        bitmap = getResizedImage(bitmap);
        RenderScript renderScript = RenderScript.create(getApplicationContext());
        // Create another bitmap that will hold the results of the filter.
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int scaledHeight = height / 2;
        int scaledWidth = width / 2;

        // Scale Down Image so that image will be 4x smaller hence blurring 4x+ faster.
        // TODO: use inSampleScale with this.. till now not able to configure it
        Bitmap scaledDownBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);

        // Allocate memory for Renderscript to work with
        final Allocation.MipmapControl mipmapFull = Allocation.MipmapControl.MIPMAP_FULL;
        final int usageShared = Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED;
        Allocation input = Allocation.createFromBitmap(renderScript, scaledDownBitmap, mipmapFull, usageShared);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(15);

        // Start the ScriptIntrinsicBlur
        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(scaledDownBitmap);

        // ScaleUp image back to original size
        Bitmap scaledOriginalBitmap = Bitmap.createScaledBitmap(scaledDownBitmap, width, height, true);

        // Recycle is to be use if image is scaled
        bitmap.recycle();

        return scaledOriginalBitmap;
    }

    private void initViews() {
        login = (Button) findViewById(R.id.btnLogin);
        userId = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);
        imageView = (ImageView) findViewById(R.id.blur_image);
        layout = (RelativeLayout) findViewById(R.id.parent);
    }

    public void startLogin() {
        if (userId.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Defs.showToast(getApplicationContext(), getString(R.string.validation_error));
        } else {
            if (Network.isAvailable(getApplicationContext())) {
                new CheckLogin().execute();
            } else {
                Defs.showToast(getApplicationContext(), getString(R.string.no_network));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
//            login(1);
            startLogin();
        }
    }

    private void login(int type) {
        switch (type) {
            case 0:
                startActivity(new Intent(getApplicationContext(), UserRegistration.class));
                finish();
                break;
            case 1:
                startActivity(new Intent(getApplicationContext(), AdminPanel.class));
                finish();
                break;
        }
    }

    private void savePreferences(String key, int value) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    private void savePreferences(String key, String value) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private void loadSavedPreferences() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        loginType = preferences.getInt(Defs.KEY_LOGIN_TYPE, DEFAULT_LOGIN);
        Defs.LOCATION = preferences.getString(Defs.KEY_LOCATION, "");
    }

    private Bitmap getResizedImage(Bitmap bitmap) {

        Point screenSize = getWorkableDimensions();
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;

//        int screenWidth = layout.getWidth();
//        int screenHeight = layout.getWidth();
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        float aspectRatio = (float) height / screenHeight;
        int newWidth = (int) (width / aspectRatio);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, screenHeight, true);
        return Bitmap.createBitmap(resizedBitmap, (newWidth - screenWidth) / 2, 0, screenWidth, screenHeight);
    }

    private Point getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point workableSize = new Point();
        display.getSize(workableSize);


        return workableSize;
    }

    private Point getWorkableDimensions() {
        Point screenSize = getDisplaySize();

        Resources resources = getApplicationContext().getResources();
//        int navigationBarHeight=0;
//        int navBarId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        if (navBarId > 0) {
//            navigationBarHeight= resources.getDimensionPixelSize(navBarId);
//        }

        int statusBarHeight=0;
        int statuisBarId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statuisBarId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(statuisBarId);
        }

//        TypedValue typedValue = new TypedValue();
//        int actionBarHeight=0;
//        if(getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)){
//            actionBarHeight= getResources().getDimensionPixelSize(typedValue.resourceId);
//        }

        screenSize.y =screenSize.y-statusBarHeight;
        return screenSize;
    }

    private class CheckLogin extends AsyncTask<String, String, String> {

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
            String id = userId.getText().toString().toLowerCase();
            String password = LoginActivity.this.password.getText().toString().toLowerCase();

            Defs.USERNAME = userId.getText().toString().toUpperCase();
            Defs.PASSWORD = LoginActivity.this.password.getText().toString().toUpperCase();

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
                    savePreferences(Defs.KEY_LOGIN_TYPE, loginType);
                    savePreferences(Defs.KEY_LOCATION, Defs.LOCATION);
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
                login(loginType);
            } else {
                try {
                    password.setText("");
                    Defs.showToast(getApplicationContext(),
                            jsonResponse.getString(Defs.TAG_MESSAGE));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}