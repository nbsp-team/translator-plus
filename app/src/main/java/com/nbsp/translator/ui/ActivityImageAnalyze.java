package com.nbsp.translator.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nbsp.translator.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public abstract class ActivityImageAnalyze extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String ARG_CURRENT_PHOTO_PATH = "arg_current_photo_path";

    public static final String ARG_ANALYZE_RESULT = "arg_analyze_result";

    @Bind(R.id.image)
    protected ImageView mImageView;

    @Bind(R.id.analyze_bar)
    protected View mAnalyzeBar;

    private String mCurrentPhotoPath;
    private Subscription mAnalyticResultSubscription;

    protected abstract Observable<String> analyzeImage(String imagePath, ImageView imageView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analyze);
        ButterKnife.bind(this);

        initStatusBarColor();
        initDataBySavedInstance(savedInstanceState);
    }

    private void initStatusBarColor() {
        getWindow().setStatusBarColor(Color.BLACK);
    }

    private void initDataBySavedInstance(Bundle savedInstance) {
        if (savedInstance == null) {
            openCameraActivity();
        } else {
            mCurrentPhotoPath = savedInstance.getString(ARG_CURRENT_PHOTO_PATH);
            setPic();
        }
    }

    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.analyze_bar);
        mAnalyzeBar.startAnimation(animation);

        startAnalyzing();
    }

    private void startAnalyzing() {
        mAnalyticResultSubscription = analyzeImage(mCurrentPhotoPath, mImageView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        finishWithResult(s);
                    }
                });
    }

    private void finishWithResult(String result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ARG_ANALYZE_RESULT, result);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onDestroy() {

        if (mAnalyticResultSubscription != null) {
            mAnalyticResultSubscription.unsubscribe();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_CURRENT_PHOTO_PATH, mCurrentPhotoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
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
