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
import android.widget.Toast;

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
        new JSONResponse().execute("http://ip-api.com/json/"+ipTextView.getText().toString());
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
        JSONObject parentObject;
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

                parentObject = new JSONObject(finalJson);
                info.add(" AS: "+ checkJson(parentObject.optString("as")));
                info.add(" City: "+ checkJson(parentObject.optString("city")));
                info.add(" Country: "+ checkJson(parentObject.optString("country")));
                info.add(" Country code: "+ checkJson(parentObject.optString("countryCode")));
                info.add(" ISP: "+ checkJson(parentObject.optString("isp")));
                info.add(" Lat: "+ checkJson(parentObject.optString("lat")));
                info.add(" Lon: "+ checkJson(parentObject.optString("lon")));
                info.add(" Org: "+ checkJson(parentObject.optString("org")));
                info.add(" IP address: "+ checkJson(parentObject.optString("query")));
                info.add(" Region: "+ checkJson(parentObject.optString("region")));
                info.add(" Region name: "+ checkJson(parentObject.optString("regionName")));
                info.add(" Zip: "+ checkJson(parentObject.optString("zip")));
                info.add(" Status: "+ checkJson(parentObject.optString("status")));
                info.add(" Timezone: "+ checkJson(parentObject.optString("timezone")));

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

        public String checkJson(String text)
        {
            if (text.length() != 0)
            {
                return text;
            }
            return "-";
        }

        @Override
        protected void onPostExecute(String s)
        {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.listview_row, info);
            ListView listView = (ListView)findViewById(R.id.listView);

            if (listView != null)
            {
                try
                {
                    listView.setAdapter(arrayAdapter);
                }
                catch (NullPointerException h)
                {
                    Toast.makeText(getApplicationContext(),"Network problem!.",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
