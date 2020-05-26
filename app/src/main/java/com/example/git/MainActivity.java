package com.example.git;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<Repo> arrayList;
    ListView repoListView;
    Button search;
    EditText searchtxt;
    TextView count,pageNo,apiLimitError;
    JSONArray items;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();
        repoListView = (ListView) findViewById(R.id.listView);
        apiLimitError = (TextView) findViewById(R.id.apiLimitError);

        search = (Button) findViewById(R.id.search);
        searchtxt = (EditText) findViewById(R.id.searchText);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }



                if (!isInternetConnected(getBaseContext())) {
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }


                searchText = searchtxt.getText().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Search Box can't be Empty!!", Toast.LENGTH_LONG).show();
                    return;
                }else{

                    new Atask().execute(searchText);
                }

            }
        });


    }

    class Atask extends AsyncTask<String,Void,Void> {
        private ProgressDialog pDialog;
        boolean apiLimitExceeded = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection;
            URL url;
            InputStream inputStream;
            String response="";
            try{
                url = new URL("https://api.github.com/search/repositories?q="+params[0]);
                Log.e("url valeu", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");

                urlConnection.setDoInput(true);
                urlConnection.connect();


                int httpStatus = urlConnection.getResponseCode();
                Log.e("httpstatus", "The response is: " + httpStatus);


                if (httpStatus != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getErrorStream();
                    Map<String, List<String>> map = urlConnection.getHeaderFields();
                    System.out.println("Printing Response Header...\n");
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        System.out.println(entry.getKey()
                                + " : " + entry.getValue());
                    }
                }
                else {
                    inputStream = urlConnection.getInputStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                while((temp = bufferedReader.readLine())!=null){
                    response+=temp;
                }
                Log.e("webapi json object",response);


                if(response.contains("API rate limit exceeded")){
//                    items= new JSONArray();
//                    total_count = "0";
                    apiLimitExceeded =true;
                }else {

                    JSONObject obj = (JSONObject) new JSONTokener(response).nextValue();
                    items = obj.getJSONArray("items");

                }

                urlConnection.disconnect();
            } catch (MalformedURLException | ProtocolException | JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!apiLimitExceeded){
                apiLimitError.setVisibility(View.INVISIBLE);
                setResultListView();
            }else{
                CustomListAdapter adapter =
                        new CustomListAdapter(getApplicationContext(), R.layout.custom_list_layout, new ArrayList<Repo>());


                repoListView.setAdapter(adapter);
                apiLimitError.setVisibility(View.VISIBLE);

            }
            pDialog.dismiss();
        }
    }

          private void setResultListView() {


              if (items.length() == 0) {
                  return;
              }
              Log.e("some more data", "item.length" + String.valueOf(items.length()));
              try{
              for (int i = 0; i < items.length(); i++) {
                  JSONObject jo;
                      jo = items.getJSONObject(i);
                    JSONObject joo;
                    joo = jo.getJSONObject("owner");

                  arrayList.add(new Repo(
                           joo.getString("avatar_url"),
                           jo.getString("name"),
                           jo.getString("full_name"),
                           jo.getString("watchers_count")
                      ));


                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              CustomListAdapter adapter =
                      new CustomListAdapter(getApplicationContext(), R.layout.custom_list_layout, arrayList);

              //set adapter to list view
              repoListView.setAdapter(adapter);



          }





       public static boolean isInternetConnected(Context context) {
           ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
           return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
       }
}
