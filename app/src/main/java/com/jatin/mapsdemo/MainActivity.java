package com.jatin.mapsdemo;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    AutoCompleteTextView edt_start,edt_end;
    Button btn_submit;

    private PlaceAutocompleteAdapter mAdapter;
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_main);


        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);

        edt_start = (AutoCompleteTextView) findViewById(R.id.autocomplete_start_places);
        edt_end = (AutoCompleteTextView) findViewById(R.id.autocomplete_end_places);

        btn_submit = (Button) findViewById(R.id.btn_submit);

        edt_start.setAdapter(mAdapter);
        edt_end.setAdapter(mAdapter);

        edt_start.setOnItemClickListener(mAutocompleteClickListener);
        edt_end.setOnItemClickListener(mAutocompleteClickListener);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasData(edt_start.getText().toString()) && hasData(edt_end.getText().toString())){
                    startActivity(new Intent(getApplicationContext(),TransitDirectionActivity.class)
                            .putExtra("originLatLng",origin).putExtra("destLatLng",destination));
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please enter both source & destination to proceed",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("MapsDemo", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            if (edt_start.isFocused()){
                edt_start.setText(formatPlaceDetails(getResources(), place.getName()));
                origin = place.getLatLng();
                edt_start.clearFocus();
            }
            else {
                edt_end.setText(formatPlaceDetails(getResources(), place.getName()));
                destination = place.getLatLng();
                edt_end.clearFocus();
            }

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name) {
        Log.e("MapsDemo", res.getString(R.string.place_details, name));
        return Html.fromHtml(res.getString(R.string.place_details, name));
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MapsDemo", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public boolean hasData(String text) {
        return !(text == null || text.length() == 0);
    }

}
