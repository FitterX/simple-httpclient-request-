package com.requesthandler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.requesthandler.requesthandler.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button httpclientTest = findViewById(R.id.httpclientTest);

        httpclientTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new RequestAsyncGet().execute();
                if(isOnline(MainActivity.this))
                    new RequestAsyncPost().execute();
                else
                    showAlertMessage("Uyarı" ,"İnternet bağlantınızı aktif hale getiriniz!" , "Tamam");
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isOnline(MainActivity.this)) {
                showAlertMessage("Uyarı" ,"İnternet bağlantınızı aktif hale getiriniz!" , "Tamam");
            }
        }
    };

    public class RequestAsyncPost extends AsyncTask<String,String,String> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Verileriniz yükleniyor ...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    RequestAsyncPost.this.cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String jsonValue = "{'var' : 'Bu bir httpclient isteğidir'}";

                JSONObject postDataParams = new JSONObject(jsonValue);

                return RequestHandler.sendPost("https://mobil.candamlalari.com/test.php",postDataParams);

            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null){
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObj = new JSONObject(s);

                    TextView appText = findViewById(R.id.applicationText);

                    appText.setText(jsonObj.getString("message") + jsonObj.getString("returnValue"));

                    showAlertMessage("Sonuç" , jsonObj.getString("message") + jsonObj.getString("returnValue") , "Tamam");

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }


    }

    public class RequestAsyncGet extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return RequestHandler.sendGet("https://raw.github.com/square/okhttp/master/README.md");
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showAlertMessage(String title, String messageText, String buttonText/*, Context context*/) {
        AlertDialog alertMessage = new AlertDialog.Builder(MainActivity.this).create();
        alertMessage.setTitle(title);
        alertMessage.setMessage(messageText);
        alertMessage.setCancelable(false);
        alertMessage.setIcon(R.mipmap.ic_launcher);
        alertMessage.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertMessage.show();
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public void logLargeString(String str, String staticVal) {
        if (str.length() > 3000) {
            Log.i(staticVal, str.substring(0, 3000));
            logLargeString(str.substring(3000), staticVal);
        } else {
            Log.i(staticVal, str); // continuation
        }
    }
}
