package in.siteurl.www.trendzcrm;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import dmax.dialog.SpotsDialog;

public class DocumentsViewer extends AppCompatActivity {

    PDFView pdfView;

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
                Intent intent=new Intent(DocumentsViewer.this,Home.class);
                SharedPreferences preferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
                intent.putExtra("response",preferences.getString("responseatoz",null));
                startActivity(intent);
                return true;
            case R.id.chngpswd:
                Intent intent2=new Intent(DocumentsViewer.this,ChangePassword.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Intent intent3=new Intent(DocumentsViewer.this,Logout.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_viewer);

        FloatingActionButton techsup1=findViewById(R.id.techsupporta);
        techsup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view,"Kindly leave your message. . . Our tech support team will get in touch with you soon. . .",Snackbar.LENGTH_LONG).show();
                Intent goToTechSuppo=new Intent(getApplicationContext(),TechSupport.class);
                startActivity(goToTechSuppo);

            }
        });

        FloatingActionButton downloadPDF=findViewById(R.id.downloadPDFfab);
        downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                String url = "https://appharbor.com/assets/images/stackoverflow-logo.png";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
*/
                String url = getIntent().getExtras().getString("PDFLink").toString();//"https://appharbor.com/assets/images/stackoverflow-logo.png";//"url you want to download";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("download");
                request.setTitle("Trendz- "+getIntent().getExtras().getString("PDFName"));
// in order for this if to run, you must use the android 3.2 to compile your app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

// get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                Toast.makeText(DocumentsViewer.this, "Download started. . .", Toast.LENGTH_SHORT).show();
            }
        });
        setTitle(getIntent().getExtras().getString("PDFName"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pdfView=findViewById(R.id.pdfviewerview);
        alertDialog=new SpotsDialog(DocumentsViewer.this,"Fetching PDF. . .");
        alertDialog.show();
        //getActionBar().setTitle(getIntent().getExtras().getString("PDFName"));
        //((TextView)findViewById(R.id.totle)).setText(getIntent().getExtras().getString("PDFName"));
        //  getSupportActionBar().setTitle(getIntent().getExtras().getString("PDFName").toString());
        new PDFviewerClass().execute(getIntent().getExtras().getString("PDFLink").toString());

    }
    class PDFviewerClass extends AsyncTask<String,Void,InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {

            try {
                URL url=new URL(strings[0]);
                InputStream inputStream=url.openConnection().getInputStream();
                return inputStream;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(DocumentsViewer.this, String.valueOf(values), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            alertDialog.dismiss();
                        }
                    }).load();

        }
    }
}
