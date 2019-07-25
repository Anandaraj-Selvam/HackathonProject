package in.hackathon.crimefinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.hackathon.apiservices.UploadFileAsync;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;
    private ImageView imageView;
    private TextView imagetextView;
    private EditText editText;
    private EditText vechicleMatchedText;

    private int PICK_Image_Request = 1;
    private int PICK_IMAGE_FROM_CAMERA = 2;

    private static  final int STORAGE_PERMISSION_CODE = 123;

    private Bitmap bitmap;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestStoragePermission();

        buttonChoose = (Button) findViewById(R.id.chooseImage);
        buttonUpload = (Button) findViewById(R.id.uploadImage);
        imageView = (ImageView) findViewById(R.id.imageView);
        imagetextView=(TextView) findViewById(R.id.ImageText);
        editText = (EditText) findViewById(R.id.editText);
        vechicleMatchedText = (EditText) findViewById(R.id.VechicleMatchedText);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return;
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
        // this is to display the request popup
        }
        //finally take the permission

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonChoose){
            imageView.setVisibility(View.VISIBLE);
            vechicleMatchedText.setVisibility(View.INVISIBLE);
            imagetextView.setVisibility(View.INVISIBLE);
            dispatchTakePictureIntent();
            //showFileChooser();
        }
        if(v == buttonUpload){
            uploadMultiPart();
        }
    }
   private boolean uploadMultiPart(){


   AsyncTask<String, String, String> responseMassage =new UploadFileAsync(imageFilePath,this,imagetextView,imageView,vechicleMatchedText).execute();


       //Toast.makeText(getApplicationContext(),responseMassage.toString(),Toast.LENGTH_SHORT).show();
       return true;
   }
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_Image_Request);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();

                Uri photoURI = FileProvider.getUriForFile(this,getString(R.string.file_provider), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);

                startActivityForResult(takePictureIntent, PICK_IMAGE_FROM_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File cameraOutput = new File(getCacheDir(), "camera");
        cameraOutput.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                cameraOutput      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected  void onActivityResult( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_Image_Request && resultCode == RESULT_OK){
            filePath = data.getData();
            try{
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
               imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                imagetextView.setVisibility(View.INVISIBLE);
                vechicleMatchedText.setVisibility(View.INVISIBLE);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }

        else if(requestCode == PICK_IMAGE_FROM_CAMERA && resultCode == RESULT_OK){
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
                imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
            vechicleMatchedText.setVisibility(View.INVISIBLE);
            imagetextView.setVisibility(View.INVISIBLE);
        }
    }
}
