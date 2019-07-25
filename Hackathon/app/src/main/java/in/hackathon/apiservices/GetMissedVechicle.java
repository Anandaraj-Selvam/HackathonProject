package in.hackathon.apiservices;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetMissedVechicle extends AsyncTask<String, String, String> {

    private final Context applicationContext;

    private final TextView imagetextView;
    private final ImageView imageView;
    private final TextView vehicleMissedText;
    private String responseMessage;
    private String matched;


    private String missedVechicleInfo="";
    public GetMissedVechicle(Context context, TextView textView, ImageView imageView, TextView vehicleMatchedText, String responseMessage){
        applicationContext=context;
        this.imageView=imageView;
        imagetextView=textView;
        this.vehicleMissedText=vehicleMatchedText;
        this.responseMessage=responseMessage;
    }


    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://hackimageapp.azurewebsites.net/api/Upload/user/GetMissedVechicleInfo");

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                missedVechicleInfo+=current;
                data = isw.read();
                System.out.print(current);
            }
            String res=missedVechicleInfo.replace("\"","");
            List<String> missedVehcileList= new ArrayList<String>(Arrays.asList(res.substring(1,res.length()-1).split(",")));
            String trimmedString=responseMessage.replace("\"","").replace(" ","").replace(",","").trim();
            //String splitResponsMessages[]=trimmedString.split(",");

            for(int i=0;i<missedVehcileList.size();i++){
                if(trimmedString.contains(missedVehcileList.get(i))){
                    matched=missedVehcileList.get(i)+" matched with missed vechile list";
                    break;
                }
            }

                if(matched.equals(""))
                matched=responseMessage.substring(0,trimmedString.indexOf(','))+" not matched with missed vechile list";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return missedVechicleInfo;
    }

    protected void onPostExecute(String missedVechicleInfo){
        vehicleMissedText.setText(matched);
        imagetextView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        vehicleMissedText.setVisibility(View.VISIBLE);
        Toast.makeText(applicationContext,missedVechicleInfo,Toast.LENGTH_LONG).show();
    }
}
