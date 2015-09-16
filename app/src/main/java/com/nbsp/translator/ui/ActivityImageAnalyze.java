package com.nbsp.translator.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.nbsp.translator.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import android.net.Uri;

public class ActivityImageAnalyze extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Bind(R.id.image)
    protected ImageView mImageView;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analyze);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            openCameraActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

        // TODO: send and analyze image
    }

    private void openCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // TODO: handle error
                ex.printStackTrace();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                // TODO: handle error
                Log.e("ActivityImageAnalyze", "File not created");
            }
        } else {
            // TODO: handle error
            Log.e("ActivityImageAnalyze", "Camera activity not found");
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
