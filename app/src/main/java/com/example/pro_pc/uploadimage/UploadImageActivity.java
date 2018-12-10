package com.example.pro_pc.uploadimage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    ImageView imageView;
    EditText editText;
    Button buttonUpload, buttonSelectImage;
    int IMG_REQUEST = 1;
    Bitmap bitmap;
    private static final int STORAGE_PERMISSION_CODE = 2930;
    private static final int PICK_IMAGE_REQUEST = 12;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
        setContentView(R.layout.activity_upload_image);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonUpload = findViewById(R.id.buttonUploadImage);
        buttonUpload.setOnClickListener(this);
        buttonSelectImage.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
    }

    private void uploadImage() {
        progressDialog.show();
        //لینکا php یا سێرڤەری
        /*
        <?php
            $response = array();
            $response['message'] = "Error...";
                include('connection_db.php');
                if($link)
                {
                    $image = $_POST['image'];
                    $name = $_POST['name'];
                    $sql = "INSERT INTO image_user (name) VALUES ('$name');";
                    $upload_path = "uploads/$name.jpg";
                    if(mysqli_query($link,$sql))
                    {
                        file_put_contents($upload_path, base64_decode($image));
                        $response['message'] = "upload image successfully";
                    }
                }
                else {
                    $response['message'] = "errorrrr";
                }
            echo json_encode($response);
            ?>
        * */
        String url = "https://salarx.000webhostapp.com/php/image/uploadImage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(UploadImageActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(UploadImageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageToString(bitmap));
                params.put("name", editText.getText().toString().trim());
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmapf) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission graded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not graded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSelectImage)
            selectImage();
        else if (v == buttonUpload)
            uploadImage();
    }
}