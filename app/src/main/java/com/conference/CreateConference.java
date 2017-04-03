package com.conference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conference.custom.Constants;
import com.conference.custom.DatePickerFragment;
import com.conference.custom.TimePickerFragment;
import com.conference.model.Conference;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class CreateConference extends AppCompatActivity implements View.OnClickListener{

    EditText edTitle, edHost, edDetails;
    TextView tvDate, tvTime, tvLocation;
    Button btnSubmit;
    ImageView ivDate, ivTime, ivLocation;
    TimePickerFragment timePickerFragment;
    DatePickerFragment datePickerFragment;
    private static final int PLACE_PICKER_REQUEST = 1;
    ProgressDialog prgDialog;
    Conference confernce = new Conference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_conference);

        createConferenceData();
    }

    private void createConferenceData() {
        edTitle = (EditText) findViewById(R.id.etTitleCreate);
        edHost = (EditText) findViewById(R.id.etGuestSpeakerCreate);
        edDetails = (EditText) findViewById(R.id.etConfDetailsCreate);

        tvDate = (TextView) findViewById(R.id.tvDateCreate);
        tvTime = (TextView) findViewById(R.id.tvTimeCreate);
        tvLocation = (TextView) findViewById(R.id.tvLocationCreate);

        ivDate = (ImageView) findViewById(R.id.ivDateCreate);
        ivTime = (ImageView) findViewById(R.id.ivTimeCreate);
        ivLocation = (ImageView) findViewById(R.id.ivLocationCreate);
        btnSubmit = (Button) findViewById(R.id.btnSubmitCreate);

        ivDate.setOnClickListener(CreateConference.this);
        ivTime.setOnClickListener(CreateConference.this);
        ivLocation.setOnClickListener(CreateConference.this);
        btnSubmit.setOnClickListener(CreateConference.this);

        setTitle(R.string.txt_create_conference);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivDateCreate:
                showDatePickerDialog();
                break;
            case R.id.ivTimeCreate:
                showTimePickerDialog();
                break;
            case R.id.ivLocationCreate:
                showPlacePicker();
                break;
            case R.id.btnSubmitCreate:
                if(edTitle.length() <= 0){
                    edTitle.setError("Title is required");
                } else if(tvDate.length() <= 0){
                    tvDate.setError("Date is required");
                } /*else if(tvLocation.length() <= 0){
                    tvLocation.setError("Location is required");
                }*/ else {
                    confernce.setConf_title(edTitle.getText().toString());
                    confernce.setGuest_speaker(edHost.getText().toString());
                    confernce.setConf_details(edDetails.getText().toString());
                    confernce.setConf_date(tvDate.getText().toString());
                    confernce.setConf_time(tvTime.getText().toString());
                    confernce.setConf_location(tvLocation.getText().toString());
                    String jsonData = createJSONData(confernce);
                    invokeCreateConfWS(CreateConference.this,jsonData);
                }
                break;
            default:
                break;
        }
    }

    private String createJSONData(Conference conf) {
        String jsonObj = "";
        try{
            JSONObject jObj = new JSONObject();
            jObj.put(Constants.WS_TITLE,conf.getConf_title());
            jObj.put(Constants.WS_DATE,conf.getConf_date());
            jObj.put(Constants.WS_TIME,conf.getConf_time());
            jObj.put(Constants.WS_DETAILS,conf.getConf_details());
            jObj.put(Constants.WS_GUEST,conf.getGuest_speaker());
            jObj.put(Constants.WS_LOCATION,conf.getConf_location());
            jsonObj = jObj.toString();

        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObj;
    }

    public void showTimePickerDialog() {
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog() {
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showPlacePicker(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(CreateConference.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException gpsne){
            gpsne.printStackTrace();
        } catch (GooglePlayServicesRepairableException gpsre){
            gpsre.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                tvLocation.setText(toastMsg);
                Toast.makeText(this, "Place Selected:"+toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void invokeCreateConfWS(final Context context, String jsonParams) {
        prgDialog = new ProgressDialog(CreateConference.this);
        prgDialog.setMessage("Loading...");
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        try{
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

        client.post(CreateConference.this, Constants.WSURL + Constants.TABLE_NAME + "create", entity, "application/json", new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                Toast.makeText(CreateConference.this,"Submitted Successfully",Toast.LENGTH_SHORT).show();
                Log.d("Create Conf>>>", ">>>" + response);
                try {

                    /*JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        alConfList.add(conference);
                        confAdapter.notifyDataSetChanged();
                    }*/

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
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                onBackPressed();
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
