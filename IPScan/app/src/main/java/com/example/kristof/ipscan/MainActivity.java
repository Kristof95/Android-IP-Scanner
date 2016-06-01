package com.example.kristof.ipscan;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    TextView ipTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ipTextView = (TextView) findViewById(R.id.ipText);
        ipTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                new JSONResponse().execute("http://ip-api.com/json/"+ipTextView.getText().toString());
            }
        });
    }

    public class JSONResponse extends AsyncTask<String, String, String>
    {
        List<String> info;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params)
        {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder buffer = new StringBuilder();

                String line = "";

                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                info = new ArrayList<>();

                JSONObject parentObject = new JSONObject(finalJson);
                info.add("AS: "+parentObject.optString("as"));
                info.add("City: "+parentObject.optString("city"));
                info.add("Country: "+parentObject.optString("country"));
                info.add("Country code: "+parentObject.optString("countryCode"));
                info.add("ISP: "+parentObject.optString("isp"));
                info.add("Lat: "+parentObject.optString("lat"));
                info.add("Lon: "+parentObject.optString("lon"));
                info.add("Org: "+parentObject.optString("org"));
                info.add("IP address: "+parentObject.optString("query"));
                info.add("Region: "+parentObject.optString("region"));
                info.add("Region name: "+parentObject.optString("regionName"));
                info.add("Zip: "+parentObject.optString("zip"));
                info.add("Status: "+parentObject.optString("status"));
                info.add("Timezone: "+parentObject.optString("timezone"));

                return buffer.toString();
            }
            catch (Exception k)
            {
                k.printStackTrace();
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.listview_row, info);
                    ListView listView = (ListView)findViewById(R.id.listView);

                    if (listView != null && info != null)
                    {
                        listView.setAdapter(arrayAdapter);
                    }
                }
            });
        }
    }
}
