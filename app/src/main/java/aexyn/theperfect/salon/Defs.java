package aexyn.theperfect.salon;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

public class Defs {

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_LOCATION = "location";
    public static final String TAG_LOGIN_TYPE = "login_type";
    public static final String TAG_VISITOR = "visitors";
    public static final String TAG_ACTIVATED = "activated";

    public static final String TAG_VISITORS = "visitors";
    public static final String TAG_PHONE = "contact";
    public static final String TAG_NAME = "name";
    public static final String TAG_COUNT = "count";
    public static final String TAG_VISIT_COUNT = "visits_count";

    public static final String KEY_LOGIN_TYPE = "loginType";
    public static final String KEY_LOCATION = "location";

    public static ProgressDialog pDialog;
    // Connection detector class
    public static ConnectionDetector cd;
    public static String LOCATION = "";
    public static String LOCATION_SELECTED_ADMIN = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static JSONParser jsonParser = new JSONParser();

    public static JSONArray visitorsArray = new JSONArray();

    public static TableLayout header, scrollablePart, footer;
    public static TableRow row;

    public static TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

    public static TextView recyclableTextView;

    public static int fixedHeaderHeight = 10;
    public static int scrollColumnHeight = 15;

    public static int GET_ALL = 1;
    public static int GET_ACTIVATED = 0;
    public static int GET_DELETED = -1;
    public static int GET_ALL_FOR_CHANGE_PASS = 2;

    public static String FTPhost = "wbtdemo.net76.net";
    public static String FTPusername = "a3164488";
    public static String FTPpassword = "@exynwbt";
    public static int FTPport = 21;

    public static String TOTAL_VISITORS_TEXT = "Total Visitors : ";
    public static String TOTAL_VISITS_TEXT = "Total Visits : ";
    public static String server_ip = "http://192.168.1.126/wbt_demo/";

    public static String url_validate_login = server_ip
            + "franchisee_login.php";

    public static String url_register_user = server_ip
            + "register_user.php";

    public static String url_get_visitor_names = server_ip
            + "get_visitor_names.php";

    public static String url_get_visitor_datails = server_ip
            + "get_visitor_details.php";

    public static String url_add_franchisee = server_ip
            + "add_franchisee.php";

    public static String url_get_locations = server_ip
            + "get_locations.php";

    public static String url_delete_franchisee = server_ip
            + "delete_franchisee.php";

    public static String url_activate_franchisee = server_ip
            + "activate_franchisee.php";

    public static String url_change_pass = server_ip
            + "change_password.php";

    public static void showToast(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

}
