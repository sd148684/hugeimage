package me.liu.hugeimage;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 101;
    private static final int PERMISSION_REQUESTING = 100;

    private View mButton;
    private GestureImageView mImageView;
    private ContentLoadingProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (GestureImageView) findViewById(R.id.imageview);
        mButton = findViewById(R.id.button);
        mProgress = (ContentLoadingProgressBar) findViewById(R.id.progress);

        detectPermission();
    }

    public void showFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a image to display"),
                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "No file chooser supplied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                try {
                    String filePath = FileUtils.getPath(this, data.getData());
                    mImageView.setVisibility(View.VISIBLE);
                    mButton.setVisibility(View.GONE);
                    ImageUtils.loadSampling(mImageView, filePath, mProgress);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid file", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUESTING:
                detectPermission();
                break;
        }
    }

    private void detectPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUESTING);
        }
    }

}
