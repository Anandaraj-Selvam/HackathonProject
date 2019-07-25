package in.hackathon.apiservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.stream.Collectors;

import in.hackathon.crimefinder.MainActivity;

public class UploadFileAsync extends AsyncTask<String, String, String> {


    private final String sourceFileUri;

    private final Activity applicationContext;

    private final TextView imagetextView;
    private final ImageView imageView;
    private final TextView vehicleMatchedText;
    ProgressDialog dialog;

    private String responseMessage="Please upload valid image";


    public UploadFileAsync(String imageFilePath, Activity applicationContext, TextView imagetextView, ImageView imageView, TextView vechicleMatchedText) {
        this.applicationContext=applicationContext;
        sourceFileUri=imageFilePath;
        this.imagetextView=imagetextView;
        this.imageView=imageView;
        this.vehicleMatchedText=vechicleMatchedText;
    }

    @Override
    protected void onPreExecute(){
        dialog=new ProgressDialog(applicationContext);
        dialog.setMessage("Loading...");
        dialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String... strings) {
        try {
            int serverResponseCode=0;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
           // String boundary = "*****";
            LocalDateTime now = LocalDateTime.now();
            String boundary = "----------" + LocalDateTime.now().toString();
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (sourceFile.isFile()) {

                try {
                    String upLoadServerUri ="http://hackimageapp.azurewebsites.net/api/Upload/user/PostImage"; //"http://localhost:52696/api/Upload/user/PostImage";//"";

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                   // multipart/form-data; boundary=--------------------------402940223803753659661412
                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; filename=\""
                            + sourceFileUri + "\"" + lineEnd);
                    dos.writeBytes("Content-type: "+"image/jpg"+lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn
                            .getResponseMessage();

                    if (serverResponseCode == 200) {
                        serverResponseMessage.toCharArray();

                        responseMessage=new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().collect(Collectors.joining());
                        new GetMissedVechicle(applicationContext,imagetextView,imageView,vehicleMatchedText,responseMessage).execute();
                        // messageText.setText(msg);
                    //    Toast.makeText(applicationContext, responseMessage,
                            //  Toast.LENGTH_SHORT).show();

                        // recursiveDelete(mDirectory1);

                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }
        return responseMessage;
    }

    @Override
    protected void onPostExecute(String responseMessage){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        imagetextView.setText(responseMessage);
        imagetextView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        Toast.makeText(applicationContext.getApplicationContext(),responseMessage,Toast.LENGTH_LONG).show();
    }
}
