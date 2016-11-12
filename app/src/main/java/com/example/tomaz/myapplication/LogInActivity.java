package com.example.tomaz.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogInActivity extends ActionBarActivity {
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("IRC login");
    }

    public void backClick1(View v) {
        Intent a = new Intent(this, MainActivity.class);
        startActivity(a);
        finish();
    }

    public void LogInClick(View v) {
        username = ((EditText) findViewById(R.id.logUsername)).getText().toString();
        password = md5(((EditText) findViewById(R.id.logPassword)).getText().toString());

        if(username.equals("") || password.equals("")) {
            Context context = getApplicationContext();
            CharSequence error = "Please type all informations!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
        else {
            String URL = "http://fri-is-63130239.azurewebsites.net/Service1.svc/Login" + "/" + Uri.encode(username) + "/" + password;
            RestCallTask sendTask = new RestCallTask(URL);
            sendTask.execute();
        }
    }

    private class RestCallTask extends AsyncTask<String, Void, String> {
        private String URL = "";

        public RestCallTask(String URL) {
            this.URL = URL;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            DefaultHttpClient hc = new DefaultHttpClient();

            try {
                HttpGet request = new HttpGet(URL);
                request.setHeader("Accept", "application/json");
                HttpResponse response = hc.execute(request);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("true")) {
                Intent newIntent = new Intent(LogInActivity.this, RestActivity.class);
                newIntent.putExtra("username", username);
                startActivity(newIntent);
                finish();
            }
            else {
                ((EditText) findViewById(R.id.logUsername)).setText("");
                ((EditText) findViewById(R.id.logPassword)).setText("");

                Context context = getApplicationContext();
                CharSequence error = "Wrong username or password!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, error, duration);
                toast.show();
            }
        }
    }

    private String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(s.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while(md5.length() < 32) {
                md5 = "0" + md5;
            }

            return md5;
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}