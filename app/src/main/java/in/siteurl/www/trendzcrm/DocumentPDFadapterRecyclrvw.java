package in.siteurl.www.trendzcrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by SiteURL on 2/27/2018.
 */

public class DocumentPDFadapterRecyclrvw extends RecyclerView.Adapter<DocumentPDFadapterRecyclrvw.ViewHolder> {

    //document adapter global varialbles
    ArrayList<DocumentsContent> PDFarraylist=new ArrayList<>();
    Context PDFcontext;
    public DocumentPDFadapterRecyclrvw(Context context, ArrayList<DocumentsContent> arrayList) {
        PDFcontext=context;
        PDFarraylist=arrayList;
    }

    // view is created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //intialize the view and inflate
        View view= LayoutInflater.from(PDFcontext).inflate(R.layout.pdf_content,parent,false);
        return new ViewHolder(view);
    }

    //view binding
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        String pdf = PDFarraylist.get(position).getImageURL();

        //load pdf to webview
        holder.webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);

        holder.ttt.setText(PDFarraylist.get(position).getDocName());
        //Open PDF in PDF viewer
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(PDFcontext, "Google Drive PDF viewer works best for this option. . . ", Toast.LENGTH_LONG).show();
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PDFarraylist.get(position).getImageURL()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PDFarraylist.get(position).getImageURL()));
                PDFcontext.startActivity(browserIntent);*/

                Intent goToPDFviewer=new Intent(PDFcontext,DocumentsViewer.class);
                goToPDFviewer.putExtra("PDFName",PDFarraylist.get(position).getDocName());
                goToPDFviewer.putExtra("PDFLink",PDFarraylist.get(position).getImageURL());
                PDFcontext.startActivity(goToPDFviewer);
            }
        });
    }

    // position based list size
    @Override
    public int getItemCount() {
        return PDFarraylist.size();
    }

    //all widgets are initialized here using view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        WebView webview;
        ImageView imageView;
        TextView ttt;
        public ViewHolder(View itemView) {
            super(itemView);

            //initialize and use webview
            webview = (WebView) itemView.findViewById(R.id.pdfviewer);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                }
            });

            //initialize and use image view
            imageView=itemView.findViewById(R.id.overlapimageview);
            ttt=itemView.findViewById(R.id.pdftv);
        }
    }
}
