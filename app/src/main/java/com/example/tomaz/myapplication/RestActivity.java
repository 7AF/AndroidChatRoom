package com.example.tomaz.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RestActivity extends ActionBarActivity {
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        setTitle("IRC chat");

        Intent getIntent = getIntent();
        username = getIntent.getStringExtra("username");

        ((TextView) findViewById(R.id.userName)).setText("Logged in as: " + username);

        TextView outputText = (TextView) findViewById(R.id.data);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        String URL = "http://fri-is-63130239.azurewebsites.net/Service1.svc/Messages";
        RestTask task = new RestTask(URL);
        task.execute();
    }

    public void refreshData(View v) {
        String URL = "http://fri-is-63130239.azurewebsites.net/Service1.svc/Messages";
        RestTask task = new RestTask(URL);
        task.execute();
    }

    public void sendTask(View v) {
        String text = ((EditText) findViewById(R.id.sendText)).getText().toString();
        if (text.equals("")) {
            Context context = getApplicationContext();
            CharSequence error = "Please type a message!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
        else {
            String URL = "http://fri-is-63130239.azurewebsites.net/Service1.svc/AddMessage/" + Uri.encode(username) + "/" + Uri.encode(text);
            SendRestTask send = new SendRestTask(URL);
            send.execute();
            refreshData(v);
        }
    }

    public void backClick2(View v) {
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }

    private class RestTask extends AsyncTask<String, Void, String> {
        private String URL;
        String result = "";

        DefaultHttpClient hc = new DefaultHttpClient();

        public RestTask(String url) { this.URL = url; }

        @Override
        protected String doInBackground(String... params) {

            try {
                HttpGet request = new HttpGet(URL);
                request.setHeader("Accept", "Application/json");
                String webResult = EntityUtils.toString(hc.execute(request).getEntity());

                JSONArray jsonArray = new JSONArray(webResult);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    result += String.format("| %s | %s: %s\n",
                            jsonObject.getString("Time"),
                            jsonObject.getString("Username"),
                            jsonObject.getString("Text"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            ((TextView) findViewById(R.id.data)).setText(result);
        }
    }

    private class SendRestTask extends AsyncTask<String, Void, String> {
        private String URL = "";

        public SendRestTask(String url) {
            this.URL = url;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient hc = new DefaultHttpClient();
            String result = "";

            try {
                HttpPost request = new HttpPost(URL);
                HttpResponse response = hc.execute(request);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            ((EditText) findViewById(R.id.sendText)).setText("");
        }
    }
}