package com.conference.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.conference.custom.Constants;
import com.conference.model.Conference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by maulik on 11/27/16.
 */

public class WSConfList {

    ProgressDialog prgDialog;
    ArrayList<Conference> alConfList = new ArrayList<Conference>();

    public ArrayList<Conference> invokeConfListWS(final Context context, RequestParams params) {
        // Show Progress Dialog
        // prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object

        prgDialog = new ProgressDialog(context);
        prgDialog.setMessage("Loading...");
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.WSURL + Constants.TABLE_NAME + "list", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                Log.d("Conf List>>>", ">>>" + response);
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Conference conference = new Conference();
                        conference.setConf_ID(jObj.getString("conf_ID"));
                        conference.setConf_title(jObj.getString("conf_title"));
                        conference.setConf_date(jObj.getString("conf_date"));
                        conference.setConf_time(jObj.getString("conf_time"));
                        conference.setConf_location(jObj.getString("conf_location"));
                        alConfList.add(conference);
                    }

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

        return alConfList;
    }
}
