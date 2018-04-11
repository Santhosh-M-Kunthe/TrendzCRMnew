package in.siteurl.www.trendzcrm;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlatDetails extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{

    Dialog alertDialog;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.homemenu:
                Intent intent=new Intent(FlatDetails.this,Home.class);
                SharedPreferences preferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
                intent.putExtra("response",preferences.getString("responseatoz",null));
                startActivity(intent);
                return true;
            case R.id.chngpswd:
                Intent intent2=new Intent(FlatDetails.this,ChangePassword.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Intent intent3=new Intent(FlatDetails.this,Logout.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new Dialog(this);checkConnection();

        // set details of all textview by initializing arrays
        String[] flatlistdetailsarray=(String[])getIntent().getSerializableExtra("flatDetails");
        ((TextView)findViewById(R.id.unitnName)).setText(flatlistdetailsarray[0]);
        //((TextView)findViewById(R.id.value)).setText(flatlistdetailsarray[1]);
        ((TextView)findViewById(R.id.size)).setText(flatlistdetailsarray[2]);
        ((TextView)findViewById(R.id.salval)).setText(flatlistdetailsarray[3]);
        ((TextView)findViewById(R.id.balamnt)).setText(flatlistdetailsarray[4]);
        ((TextView)findViewById(R.id.recamnt)).setText(flatlistdetailsarray[5]);
        ((TextView)findViewById(R.id.floor)).setText(flatlistdetailsarray[6]);
        ((TextView)findViewById(R.id.block)).setText(flatlistdetailsarray[7]);
        ((TextView)findViewById(R.id.project)).setText(flatlistdetailsarray[8]);
        ((TextView)findViewById(R.id.status)).setText(flatlistdetailsarray[9]);

        Button goTOAddTicket=findViewById(R.id.flattoat);
        goTOAddTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAT=new Intent(FlatDetails.this,AddTicket.class);
                toAT.putExtra("unitid",getIntent().getExtras().getString("unitid"));
                startActivity(toAT);
            }
        });


    }

    public void finish(View view) {
        finish();
    }


    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        return isConnected;
    }
    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        if (isConnected) {
            //Method to signin
            //Signinwithmail(email, password);
            if(alertDialog.isShowing())
            {
                alertDialog.dismiss();
            }
        } else {
            shownointernetdialog();
        }
    }
    //To show no internet dialog
    private void shownointernetdialog() {
        //alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.nointernet);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        Button retry = alertDialog.findViewById(R.id.exit_btn);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //checkConnection();
                System.exit(0);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    ConnectivityReceiver mNetworkReceiver=new ConnectivityReceiver();
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)); }

        VendorApplication.getInstance().setConnectivityListener(this);
    }

    public void passFlatDocsToMyDocs(View view) {

        try {
            JSONObject wholeJSONobject=new JSONObject(getIntent().getExtras().getString("responseFromHome"));
            JSONObject emptyJSON=new JSONObject();
            JSONArray emptyListOfUnitsArray=new JSONArray();
            JSONArray emptyUnitDocsList=new JSONArray();

            emptyJSON.accumulate("Error",wholeJSONobject.getString("Error"));
            emptyJSON.accumulate("Message",wholeJSONobject.getString("Message"));
            emptyJSON.accumulate("Role",wholeJSONobject.getString("Role"));
            emptyJSON.accumulate("customer_name",wholeJSONobject.getString("customer_name"));
            emptyJSON.accumulate("sid",wholeJSONobject.getString("sid"));
            emptyJSON.put("List of units",(Object)emptyListOfUnitsArray);
            emptyJSON.put("Units Documents",(Object)emptyUnitDocsList);
            emptyJSON.put("Document_group_list",wholeJSONobject.getJSONArray("Document_group_list"));

            JSONArray unitListArray=wholeJSONobject.getJSONArray("List of units");
            for (int ulal=0;ulal<unitListArray.length();ulal++)
            {
                JSONObject onrUnitList=unitListArray.getJSONObject(ulal);
                if (onrUnitList.getString("unit_id").toString().contains(getIntent().getExtras().getString("unitid").toString())){

                    emptyListOfUnitsArray.put(onrUnitList);
                    emptyUnitDocsList.put(wholeJSONobject.getJSONArray("Units Documents").get(ulal));
                }
            }
            Intent goToDocs=new Intent(FlatDetails.this,CategoryDocs.class);
            goToDocs.putExtra("responseFromHome",emptyJSON.toString(9));
            goToDocs.putExtra("flatposition",351);
            //Intent goToDocs=new Intent(Home.this,TestActivity.class);
            startActivity(goToDocs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}