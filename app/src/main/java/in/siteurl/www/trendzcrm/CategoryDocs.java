package in.siteurl.www.trendzcrm;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryDocs extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    Dialog alertDialog;
    String responseToBeFiltered;
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
                Intent intent=new Intent(CategoryDocs.this,Home.class);
                SharedPreferences preferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
                intent.putExtra("response",preferences.getString("responseatoz",null));
                startActivity(intent);
                return true;
            case R.id.chngpswd:
                Intent intent2=new Intent(CategoryDocs.this,ChangePassword.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Intent intent3=new Intent(CategoryDocs.this,Logout.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_docs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.doc_group_item, android.R.id.gropuname, docGroupStringList);

        responseToBeFiltered=getIntent().getExtras().getString("responseFromHome");
        Log.d("212",responseToBeFiltered);
        alertDialog = new Dialog(this);checkConnection();

        ((FloatingActionButton)findViewById(R.id.alldocsfromcategory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToDocs=new Intent(CategoryDocs.this,MyDocuments.class);
                goToDocs.putExtra("responseFromHome",responseToBeFiltered);
                goToDocs.putExtra("flatposition",351);
                //Intent goToDocs=new Intent(Home.this,TestActivity.class);
                startActivity(goToDocs);
            }
        });
        FloatingActionButton techsup1=findViewById(R.id.techsupporta);
        techsup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view,"Kindly leave your message. . . Our tech support team will get in touch with you soon. . .",Snackbar.LENGTH_LONG).show();
                Intent goToTechSuppo=new Intent(getApplicationContext(),TechSupport.class);
                startActivity(goToTechSuppo);

            }
        });

       // Toast.makeText(CategoryDocs.this, responseToBeFiltered, Toast.LENGTH_SHORT).show();

        ListView listOfDocGrpups=(ListView)findViewById(R.id.listOfDocGroupsl);
        try {
            JSONObject forGroupList=new JSONObject(responseToBeFiltered);
            JSONArray listOfDocGroups=forGroupList.getJSONArray("Document_group_list");
            String[] docGroupStringList=new String[listOfDocGroups.length()];
            for (int i=0;i<listOfDocGroups.length();i++){
                docGroupStringList[i]=listOfDocGroups.getJSONObject(i).getString("document_name")+"  ➤";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoryDocs.this, R.layout.doc_group_item, R.id.gropuname, docGroupStringList);
            listOfDocGrpups.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d("11qq",e.toString());
            Toast.makeText(CategoryDocs.this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        listOfDocGrpups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(CategoryDocs.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                filterForGroupID(i+1);
            }
        });
    }

    private void filterForGroupID(int groupIDtoBeRemoved) {
        JSONObject filteredjsonObject=new JSONObject();
        try {
            JSONObject rawjsonObject=new JSONObject(responseToBeFiltered);
            JSONArray temp=new JSONArray();
            JSONArray unit=new JSONArray();
            JSONArray block=new JSONArray();
            JSONArray proj=new JSONArray();

            filteredjsonObject.accumulate("Error",rawjsonObject.getString("Error"));
            filteredjsonObject.accumulate("Message",rawjsonObject.getString("Message"));
            filteredjsonObject.accumulate("Role",rawjsonObject.getString("Role"));
            filteredjsonObject.accumulate("customer_name",rawjsonObject.getString("customer_name"));
            filteredjsonObject.accumulate("sid",rawjsonObject.getString("sid"));
            filteredjsonObject.put("List of units",(Object) rawjsonObject.getJSONArray("List of units"));

            JSONArray allDocuments=rawjsonObject.getJSONArray("Units Documents");
            for (int q=0;q<allDocuments.length();q++){
                JSONObject oneUnitObject=allDocuments.getJSONObject(q);

                if (!oneUnitObject.get("unit_document").toString().contains("No recor")) {
                    JSONArray unitDocArray = oneUnitObject.getJSONArray("unit_document");
                    for (int w = 0; w < unitDocArray.length(); w++) {
                        JSONObject oneDocOfUnit = unitDocArray.getJSONObject(w);
                        if (oneDocOfUnit.getString("doc_group_id").contentEquals(String.valueOf(groupIDtoBeRemoved))) {
                            block.put(oneDocOfUnit);
                        }
                    }
                }

                if (!oneUnitObject.get("block_document").toString().contains("No recor")) {
                JSONArray blockDocArray=oneUnitObject.getJSONArray("block_document");
                for (int w=0;w<blockDocArray.length();w++){
                    JSONObject oneDocOfUnit=blockDocArray.getJSONObject(w);
                    if (oneDocOfUnit.getString("doc_group_id").contentEquals(String.valueOf(groupIDtoBeRemoved))){
                        proj.put(oneDocOfUnit);
                    }
                }


                }

                if (!oneUnitObject.get("project_document").toString().contains("No recor")) {
                    JSONArray projDocArray = oneUnitObject.getJSONArray("project_document");
                    for (int w = 0; w < projDocArray.length(); w++) {
                        JSONObject oneDocOfUnit = projDocArray.getJSONObject(w);
                        if (oneDocOfUnit.getString("doc_group_id").contentEquals(String.valueOf(groupIDtoBeRemoved))) {
                            unit.put(oneDocOfUnit);
                        }
                    }
                }
                JSONObject nth=new JSONObject();
                nth.put("unit_document",unit);
                nth.put("block_document",block);
                nth.put("project_document",proj);
                temp.put(nth);
              //  Toast.makeText(this, "nth\n\n\n\n"+temp.toString(9), Toast.LENGTH_LONG).show();
            }
            filteredjsonObject.put("Units Documents",(Object) temp);



            //Toast.makeText(CategoryDocs.this, "009090\n\n\n"+filteredjsonObject.toString(9), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "-=-=-=-=-=-232=32\n\n\n"+unit.toString(9), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("q1qq1q",e.toString());
            Toast.makeText(this, e.toString()+"123456789012345678912345678", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        Intent goToDocs=new Intent(CategoryDocs.this,MyDocuments.class);
        try {
            goToDocs.putExtra("responseFromHome",filteredjsonObject.toString(9));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        goToDocs.putExtra("flatposition",351);
        //Intent goToDocs=new Intent(Home.this,TestActivity.class);
        startActivity(goToDocs);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        VendorApplication.getInstance().setConnectivityListener(this);

    }

}
