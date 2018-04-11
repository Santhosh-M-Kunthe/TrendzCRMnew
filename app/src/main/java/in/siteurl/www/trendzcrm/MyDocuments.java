package in.siteurl.www.trendzcrm;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MyDocuments extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    ArrayList<DocumentsContent> PicsArrayList=new ArrayList<>();
    ArrayList<DocumentsContent> unit_unitproj=new ArrayList<>();
    ArrayList<DocumentsContent> proj_unitproj=new ArrayList<>();

    ArrayList<DocumentsContent> DocsArrayList=new ArrayList<>();
    int total,timePassed,tp2=100;ProgressBar bar;
    String responceFromHome="";
    RecyclerView docPicsRecyclerView;RecyclerView docDocsRecyclerView;
    TextView pictv,doctv;
    Dialog alertDialog;
    boolean addPicToDocPlace=false,saveToUnit=false,saveToProj=false;

     CheckBox flatcb;
    CheckBox blockcb;
    CheckBox projcb;
    CheckBox mydoccb;
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
                Intent intent=new Intent(MyDocuments.this,Home.class);
                SharedPreferences preferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
                intent.putExtra("response",preferences.getString("responseatoz",null));
                startActivity(intent);
                return true;
            case R.id.chngpswd:
                Intent intent2=new Intent(MyDocuments.this,ChangePassword.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Intent intent3=new Intent(MyDocuments.this,Logout.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_documents);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get response from home
        responceFromHome=getIntent().getExtras().getString("responseFromHome");
        responceFromHome.replace("\"No records found\"","[]");
        Log.d("aw1",responceFromHome.toString());
        pictv   = new TextView(getApplicationContext());
        doctv = new TextView(getApplicationContext());

        //=====================================================
        bar = (ProgressBar) findViewById(R.id.progress);
       /* if(MyDocuments.this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            bar.getLayoutParams().height=20;
            bar.getLayoutParams().width=20;

        }*/

        alertDialog = new Dialog(this); checkConnection();
         flatcb=findViewById(R.id.flatchkbox);
        blockcb=findViewById(R.id.blockchkbox);
        projcb=findViewById(R.id.projectchkbox);
        mydoccb=findViewById(R.id.mydocschkbox);

        flatcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    blockcb.setChecked(false);
                    projcb.setChecked(false);
                    mydoccb.setChecked(false);
                    filterFor("units");
                }
                else
                    putAll();
            }
        });

        blockcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {

                    flatcb.setChecked(false);
                    projcb.setChecked(false);
                    mydoccb.setChecked(false);
                    filterFor("Blocks");
                }
                else
                    putAll();
            }
        });

        projcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    blockcb.setChecked(false);
                    flatcb.setChecked(false);
                    mydoccb.setChecked(false);
                    filterFor("projects");

                }
                else
                    putAll();
            }
        });

        mydoccb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    blockcb.setChecked(false);
                    flatcb.setChecked(false);
                    projcb.setChecked(false);
                    filterFor("mydocs");

                }
                else
                    putAll();
            }
        });


        //==============================================
        bar.setProgress(total);
        int oneMin= 5* 1000;
        //tp2=timePassed;
        CountDownTimer cdt = new CountDownTimer(oneMin, 1000) {

            public void onTick(long millisUntilFinished) {

                total = (int) ((timePassed/ 60) * 100);
                bar.setProgress(total);
            }

            public void onFinish() {
                bar.setVisibility(View.GONE);
            }
        }.start();
        //===============================================
        docPicsRecyclerView=(RecyclerView)findViewById(R.id.picsDocumentsREcyclerView);
        docDocsRecyclerView=(RecyclerView)findViewById(R.id.docsDocumentsRecyclerView);
//
        if (MyDocuments.this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            docDocsRecyclerView.setLayoutManager(new GridLayoutManager(MyDocuments.this,2));
            docPicsRecyclerView.setLayoutManager(new GridLayoutManager(MyDocuments.this,2));
        }
        if (MyDocuments.this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            docDocsRecyclerView.setLayoutManager(new GridLayoutManager(MyDocuments.this,3));
            docPicsRecyclerView.setLayoutManager(new GridLayoutManager(MyDocuments.this,3));
        }
        //====================================

        //=========================================
        //getArrayList();
        DocumentsAdapterRcyclrvw documentsAdapterRcyclrvw=new DocumentsAdapterRcyclrvw(MyDocuments.this,PicsArrayList);
        DocumentPDFadapterRecyclrvw documentPDFadapterRecyclrvw=new DocumentPDFadapterRecyclrvw(MyDocuments.this,DocsArrayList);


        docDocsRecyclerView.setAdapter(documentPDFadapterRecyclrvw);
        docPicsRecyclerView.setAdapter(documentsAdapterRcyclrvw);

        int flatPosition=getIntent().getExtras().getInt("flatposition");
       /* if (flatPosition==3519)
            putUnitAndProjPhotos();
            //Toast.makeText(this, "wwwwwwwwwwwwwwwww", Toast.LENGTH_SHORT).show();
        else */
            if (flatPosition!=351 )
            getArrayListForUnits(flatPosition);
        else getArrayList();

        Log.d("qq11",String.valueOf(getIntent().getExtras().getBoolean("unitProjPics",false)));
        if (getIntent().getExtras().getBoolean("unitProjPics",false))
            putUnitAndProjPhotos();

        if (PicsArrayList.isEmpty())
        {
            bar.setVisibility(View.GONE);


           /* RelativeLayout rl = (RelativeLayout) findViewById(R.id.fornopics);

            // Create a TextView programmatically.

            // Create a LayoutParams for TextView
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

            rl.removeView(pictv);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            pictv.setLayoutParams(lp);
            pictv.setText("No related images available");
            rl.addView(pictv);*/

            ((TextView)findViewById(R.id.nopics)).setVisibility(View.VISIBLE);
        }
        else             ((TextView)findViewById(R.id.nopics)).setVisibility(View.GONE);


        if (DocsArrayList.isEmpty()){
            bar.setVisibility(View.GONE);

           /* RelativeLayout rl = (RelativeLayout) findViewById(R.id.fornodocs);

            // Create a TextView programmatically.

            // Create a LayoutParams for TextView
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

            rl.removeView(doctv);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            doctv.setLayoutParams(lp);
            doctv.setText("No related documents available");
            rl.addView(doctv);*/
            ((TextView)findViewById(R.id.nodocs)).setVisibility(View.VISIBLE);

        }
        else             ((TextView)findViewById(R.id.nodocs)).setVisibility(View.GONE);



    }

    private void putUnitAndProjPhotos() {
       // docDocsRecyclerView.setAdapter(documentPDFadapterRecyclrvw);
        addPicToDocPlace=false;
        saveToProj=true;
        filterFor("projects");
        saveToProj=false;
        //docDocsRecyclerView.setAdapter(documentPDFadapterRecyclrvw);
        addPicToDocPlace=true;
        saveToUnit=true;
        filterFor("units");
        saveToProj=false;saveToUnit=false;

        Intent intent=new Intent(MyDocuments.this,UnitAndProjPics.class);
        /*Bundle args = new Bundle();
        args.putSerializable("unit_unitproj",(Serializable)unit_unitproj);
        args.putSerializable("proj_unitproj",(Serializable)proj_unitproj);
        intent.putExtra("BUNDLE",args);
        */
        Log.d("wer23a", String.valueOf(unit_unitproj.size())+String.valueOf(unit_unitproj.size()));

        intent.putExtra("unit_unitproja",unit_unitproj);
        intent.putExtra("proj_unitproja",proj_unitproj);
        startActivity(intent);

        /*
        flatcb.setVisibility(View.GONE);
        blockcb.setVisibility(View.GONE);
        projcb.setVisibility(View.GONE);
        mydoccb.setVisibility(View.GONE);
        String[] unitImageURLlist=new String[unit_unitproj.size()];
        String[] unitNamelist=new String[unit_unitproj.size()];
        String[] projImageURLlist=new String[proj_unitproj.size()];
        String[] projNamelist=new String[proj_unitproj.size()];

        Log.d("wer32",String.valueOf(unit_unitproj.size())+String.valueOf(proj_unitproj.size()));
        for (int i=0;i<unit_unitproj.size();i++) {
            unitImageURLlist[i] = unit_unitproj.get(i).getImageURL();
            unitNamelist[i]=unit_unitproj.get(i).getDocName();
            Log.d("deepti",unitImageURLlist[i]+unitNamelist[i]);
            Toast.makeText(this, unitImageURLlist[i]+unitNamelist[i], Toast.LENGTH_SHORT).show();}


        for (int i=0;i<proj_unitproj.size();i++) {
            projImageURLlist[i] = proj_unitproj.get(i).getImageURL();
            projNamelist[i]=proj_unitproj.get(i).getDocName();
            Toast.makeText(this, projNamelist[i]+projImageURLlist[i], Toast.LENGTH_SHORT).show();
            Log.d("boost",proj_unitproj.get(1).getDocName()+String.valueOf(unit_unitproj.size())+String.valueOf(proj_unitproj.size()));
    }
        intent.putExtra("unitImageURLlist",unitImageURLlist);
        intent.putExtra("unitNamelist",unitNamelist);
        intent.putExtra("projImageURLlist",projImageURLlist);
        intent.putExtra("projNamelist",projNamelist);
    startActivity(intent);*/
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
    //method to get arraylist of units

    private void getArrayListForUnits(int flatPosition) {
        JSONObject docList= null;
        try {
            docList = new JSONObject(responceFromHome);
            JSONArray UnitBlockProjDocuments=docList.getJSONArray("Units Documents");
                JSONObject oneDoc=UnitBlockProjDocuments.getJSONObject(flatPosition);
                JSONArray unitQ=oneDoc.getJSONArray("unit_document");
                getDocsPathTypeNameID(unitQ);
                JSONArray blockQ=oneDoc.getJSONArray("block_document");
                getDocsPathTypeNameID(blockQ);
                JSONArray projQ=oneDoc.getJSONArray("project_document");
                getDocsPathTypeNameID(projQ);


        } catch (JSONException e) {
            Log.d("aq1",e.toString());
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }/*
        PicsArrayList=PicarrayList;
        DocsArrayList=DOcarrayList;
        return PicarrayList;*/
    }

    private void putAll() {
        bar.setProgress(total);
        int oneMin= 5* 1000;

        CountDownTimer cdt = new CountDownTimer(oneMin, 1000) {

            public void onTick(long millisUntilFinished) {

                total = (int) ((tp2/ 60) * 100);
                bar.setProgress(total);
            }

            public void onFinish() {
                bar.setVisibility(View.GONE);
            }
        }.start();
        DocumentsAdapterRcyclrvw documentsAdapterRcyclrvw=new DocumentsAdapterRcyclrvw(MyDocuments.this,PicsArrayList);
        DocumentPDFadapterRecyclrvw documentPDFadapterRecyclrvw=new DocumentPDFadapterRecyclrvw(MyDocuments.this,DocsArrayList);



        docDocsRecyclerView.setAdapter(documentPDFadapterRecyclrvw);
        docPicsRecyclerView.setAdapter(documentsAdapterRcyclrvw);
    }

    /// filter the array list for particular string
    private void filterFor(String units) {

        ArrayList<DocumentsContent> newPicArray=new ArrayList<>();
        ArrayList<DocumentsContent> newDocArray=new ArrayList<>();

        for (int i=0;i<PicsArrayList.size();i++)
            if (PicsArrayList.get(i).getTableName().contains(units))
                newPicArray.add(PicsArrayList.get(i));


        for (int i=0;i<DocsArrayList.size();i++)
            if (DocsArrayList.get(i).getTableName().contains(units))
                newDocArray.add(DocsArrayList.get(i));

        // adapters for recycle view
        DocumentsAdapterRcyclrvw documentsAdapterRcyclrvw=new DocumentsAdapterRcyclrvw(MyDocuments.this,newPicArray);
        DocumentPDFadapterRecyclrvw documentPDFadapterRecyclrvw=new DocumentPDFadapterRecyclrvw(MyDocuments.this,newDocArray);
        if (addPicToDocPlace)
        {
            docDocsRecyclerView.setAdapter(documentsAdapterRcyclrvw);
            //return;

        }
        if (saveToUnit&&(unit_unitproj.size()==0))
            unit_unitproj=newPicArray;
        if (saveToProj&(proj_unitproj.size()==0))
            proj_unitproj=newPicArray;

        Log.d("yappa",String.valueOf(newPicArray.size())+String.valueOf(unit_unitproj.size())+String.valueOf(saveToProj));

        if (newPicArray.isEmpty())
        {
            bar.setVisibility(View.GONE);
            /*RelativeLayout rl = (RelativeLayout) findViewById(R.id.fornopics);

            // Create a TextView programmatically.

            // Create a LayoutParams for TextView
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

            rl.removeView(pictv);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            pictv.setLayoutParams(lp);
            pictv.setText("No related images available");
            rl.addView(pictv);*/

            ((TextView)findViewById(R.id.nopics)).setVisibility(View.VISIBLE);

        }
        else             ((TextView)findViewById(R.id.nopics)).setVisibility(View.GONE);


        // display text view saying no documents are available
        if (newDocArray.isEmpty()){
            bar.setVisibility(View.GONE);
            /*RelativeLayout rl = (RelativeLayout) findViewById(R.id.fornodocs);

            // Create a TextView programmatically.

            // Create a LayoutParams for TextView
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

            rl.removeView(doctv);

            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            doctv.setLayoutParams(lp);
            doctv.setText("No related documents available");
            rl.addView(doctv);*/
            ((TextView)findViewById(R.id.nodocs)).setVisibility(View.VISIBLE);

        }
        else             ((TextView)findViewById(R.id.nodocs)).setVisibility(View.GONE);



        docDocsRecyclerView.setAdapter(documentPDFadapterRecyclrvw);
        docPicsRecyclerView.setAdapter(documentsAdapterRcyclrvw);

    }

    // get arraylist of documents
    private void getArrayList() {
       // ArrayList<DocumentsContent> PicarrayList=new ArrayList<>();
        //ArrayList<DocumentsContent> DOcarrayList=new ArrayList<>();

        /*for (int i=0;i<9;i++){
            arrayList.add(new DocumentsContent(String.valueOf(i)));
        }*/
        JSONObject docList= null;
        try {
            docList = new JSONObject(responceFromHome);
            Log.d("nvfud",responceFromHome);
            JSONArray UnitBlockProjDocuments=docList.getJSONArray("Units Documents");
            for (int i=0;i<UnitBlockProjDocuments.length();i++){
                JSONObject oneDoc=UnitBlockProjDocuments.getJSONObject(i);

                if (!oneDoc.get("unit_document").toString().contains("No recor")){
                JSONArray unitQ=oneDoc.getJSONArray("unit_document");
                getDocsPathTypeNameID(unitQ);}
                Log.d("asa",oneDoc.get("block_document").toString());

                if (!oneDoc.get("block_document").toString().contains("No recor")){
                    JSONArray blockQ=oneDoc.getJSONArray("block_document");
                getDocsPathTypeNameID(blockQ);}

                if (!oneDoc.get("project_document").toString().contains("No recor")){
                    JSONArray projQ=oneDoc.getJSONArray("project_document");
                getDocsPathTypeNameID(projQ);}

            }
        } catch (JSONException e) {
            Log.d("aq2",e.toString());
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            Toast.makeText(MyDocuments.this, "-0=--0=-=-=0=-0=0==-=0=0=-0=-=0=0=0", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }/*
        PicsArrayList=PicarrayList;
        DocsArrayList=DOcarrayList;
        return PicarrayList;*/
    }

    // gate name and ID of document path name
    private void getDocsPathTypeNameID(JSONArray unitQ) {
        for (int i=0;i<unitQ.length();i++)
            try {
                JSONObject singleDocQ=unitQ.getJSONObject(i);
                if (singleDocQ.getString("doc_path").contains(".pdf"))
                    DocsArrayList.add(new DocumentsContent(singleDocQ.getString("name"),singleDocQ.getString("doc_path"),singleDocQ.getString("association_id"),singleDocQ.getString("table_name"),singleDocQ.getString("doc_type")));
                else
                    PicsArrayList.add(new DocumentsContent(singleDocQ.getString("name"),singleDocQ.getString("doc_path"),singleDocQ.getString("association_id"),singleDocQ.getString("table_name"),singleDocQ.getString("doc_type")));
            } catch (JSONException e) {

                e.printStackTrace();
                Toast.makeText(this, "-=09i0-9876534w5467890-9897867534", Toast.LENGTH_SHORT).show();
            }
    }

}
