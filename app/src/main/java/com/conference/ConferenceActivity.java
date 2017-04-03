package com.conference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.conference.adapter.ConferenceListAdapter;
import com.conference.custom.Constants;
import com.conference.model.Conference;
import com.conference.webservice.WSConfList;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.x;

public class ConferenceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView lvConfList;
    ConferenceListAdapter confAdapter;
    ArrayList<Conference> alConfList = new ArrayList<Conference>();
    ProgressDialog prgDialog;
    SwipeRefreshLayout swiperefresh;
    String LOG_TAG = "ConferenceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        confAdapter = new ConferenceListAdapter(ConferenceActivity.this,alConfList);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        lvConfList = (ListView) findViewById(R.id.lvConfList);
        lvConfList.setAdapter(confAdapter);
        lvConfList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ConferenceActivity.this,ConferenceDetails.class);
                intent.putExtra(Constants.OBJ_CONFERENCE,alConfList.get(position));
                startActivity(intent);
            }
        });

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        fetchConfList();
                    }
                }
        );

        fetchConfList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToCreateConf();

               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */




    @Override
    protected void onResume() {
        super.onResume();
        confAdapter.notifyDataSetChanged();
       // fetchConfList();
    }

    private void redirectToCreateConf(){
        Intent intent = new Intent(ConferenceActivity.this,CreateConference.class);
        startActivity(intent);
    }

    public void fetchConfList(){
        /*WSConfList wsConfList = new WSConfList();
        RequestParams params = new RequestParams();
        alConfList = wsConfList.invokeConfListWS(ConferenceActivity.this,params);/
        Log.d("Size>>.",">>>"+alConfList.size());*/
       // Conference conference = new Conference();
        //conference.setConf_title();
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams();
        invokeConfListWS(ConferenceActivity.this,params);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void invokeConfListWS(final Context context, RequestParams params) {
        // Show Progress Dialog
        // prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object

        prgDialog = new ProgressDialog(ConferenceActivity.this);
        prgDialog.setMessage("Loading...");
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.WSURL + Constants.TABLE_NAME + "list", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                Toast.makeText(ConferenceActivity.this,"Data Successfully loaded",Toast.LENGTH_SHORT).show();
                Log.d("Conf List>>>", ">>>" + response);
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Conference conference = new Conference();
                        JSONObject jObj1 = jObj.getJSONObject("_id");
                        conference.setConf_ID(jObj1.getString("$oid"));
                        conference.setConf_title(jObj.getString("conf_title"));
                        conference.setConf_date(jObj.getString("conf_date"));
                        conference.setConf_time(jObj.getString("conf_time"));
                        conference.setConf_location(jObj.getString("conf_location"));
                        if(jObj.toString().contains("image")){
                            conference.setImage(jObj.getString("image"));
                        }
                        if(jObj.toString().contains("guest_speaker")){
                            conference.setGuest_speaker(jObj.getString("guest_speaker"));
                        }
                        conference.setConf_details(jObj.getString("conf_details"));
                        alConfList.add(conference);
                        confAdapter.notifyDataSetChanged();
                    }
                    swiperefresh.setRefreshing(false);
                    // JSON Object
                   /* JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        // Set Default Values for Edit View controls
                        // setDefaultValues();
                        // Display successfully registered message using Toast
                        Toast.makeText(context, "You are successfully!", Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else {
                        // errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(context, obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
