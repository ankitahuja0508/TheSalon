package aexyn.theperfect.salon;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;


public class Admin_FTP extends ActionBarActivity implements OnClickListener {

    FTPClient ftp = new FTPClient();
    String currentDir;
    FTPFile[] listFTPFiles;
    ArrayList<String> listFiles = new ArrayList<String>();

    Button btnDaily, btnWeekly, btnMonthly, btnYearly;
    TextView tvSelectReport;
    String dir = "";
    File folder;
    String filename = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_screen_ftp);

        btnDaily = (Button) findViewById(R.id.btnDaily);
        btnMonthly = (Button) findViewById(R.id.btnMonthly);
        btnWeekly = (Button) findViewById(R.id.btnWeekly);
        btnYearly = (Button) findViewById(R.id.btnYearly);
        tvSelectReport = (TextView)findViewById(R.id.tvSelectReport);

        btnDaily.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);
        btnMonthly.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);
        btnWeekly.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);
        btnYearly.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault);
        tvSelectReport.setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault_Large);

        btnDaily.setOnClickListener(this);
        btnWeekly.setOnClickListener(this);
        btnMonthly.setOnClickListener(this);
        btnYearly.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        listFiles.clear();
            if(view.getId() == R.id.btnDaily) {
                dir = "/public_html/wbt_demo/daily";
            }
            if(view.getId() == R.id.btnWeekly) {
                dir = "/public_html/wbt_demo/weekly";
            }
            if(view.getId() == R.id.btnMonthly) {
                dir = "/public_html/wbt_demo/monthly";
            }
            if(view.getId() == R.id.btnYearly) {
                dir = "/public_html/wbt_demo/yearly";
            }
            new FTPGetFiles().execute();


    }

    class FTPDownload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(Admin_FTP.this);
            Defs.pDialog.setMessage("Downloading file...");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }


        protected String doInBackground(String... args) {

            try {
                ftp.download(filename, folder);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
            } catch (FTPAbortedException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();
            Defs.showToast(getBaseContext() , "File Downloaded");
        }

    }

    class FTPGetFiles extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Defs.pDialog = new ProgressDialog(Admin_FTP.this);
            Defs.pDialog.setMessage("Fetching files....");
            Defs.pDialog.setIndeterminate(false);
            Defs.pDialog.setCancelable(false);
            Defs.pDialog.show();
        }


        protected String doInBackground(String... args) {


            try {
                if(!ftp.isConnected()) {
                    ftp.connect(Defs.FTPhost, Defs.FTPport);
                    ftp.login(Defs.FTPusername, Defs.FTPpassword);
                }
                ftp.changeDirectory(dir);
                listFTPFiles = ftp.list();
                for (int i = 0; i < listFTPFiles.length; i++)
                {
                    FTPFile file = listFTPFiles[i];

                    if (file != null)
                    {
                        if (file.TYPE_FILE == FTPFile.TYPE_FILE)
                        {
                            listFiles.add(file.getName());
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
            } catch (FTPAbortedException e) {
                e.printStackTrace();
            } catch (FTPListParseException e) {
                e.printStackTrace();
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            Defs.pDialog.dismiss();
            if(listFiles.size() > 0)
                showDialog();
            else
                Defs.showToast(getBaseContext() , "No files to download");

        }

    }
    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a file to Download");
        builder.setItems(listFiles.toArray(new String[listFiles.size()]), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Defs.showToast(getBaseContext(), listFiles.get(which));
                filename = listFiles.get(which);
                     folder = new File(String.valueOf(Environment.getExternalStorageDirectory() + "/" + TimeStamp.getTimeStamp() + ".csv"));
                    if(!folder.exists()){
                        try {
                            folder.createNewFile();
                            new FTPDownload().execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Defs.showToast(getBaseContext() , folder.toString());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
