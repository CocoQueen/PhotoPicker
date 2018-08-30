package com.coco.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import picker.prim.com.primpicker_core.PrimPicker;
import picker.prim.com.primpicker_core.engine.ImageEngine;
import picker.prim.com.primpicker_core.entity.MediaItem;
import picker.prim.com.primpicker_core.entity.MimeType;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_maxCount)
    EditText etMaxCount;
    @BindView(R.id.et_spanCount)
    EditText etSpanCount;
    @BindView(R.id.btn_video)
    Button btnVideo;
    @BindView(R.id.btn_img)
    Button btnImg;
    @BindView(R.id.btn_all)
    Button btnAll;
    @BindView(R.id.btn_current_add)
    Button btnCurrentAdd;
    @BindView(R.id.result)
    TextView result;
    @BindView(R.id.iv_one)
    ImageView ivOne;

    ArrayList<MediaItem> list = new ArrayList<>();
    ArrayList<String> pathList = new ArrayList<>();
    private String spanCount;
    private String maxCount;

    private final int REQUEST_CODE = 1001;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestAllPower();
        initClick();
    }

    private void initClick() {
        btnVideo.setOnClickListener(this);
        btnImg.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnCurrentAdd.setOnClickListener(this);
        ivOne.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video:
                sameEdit();
                PrimPicker
                        .with(this)
                        .choose(MimeType.ofVideo())
                        .setSpanCount(Integer.parseInt(spanCount))
                        .setMaxSelected(Integer.parseInt(maxCount))
                        .setImageLoader(new ImageLoader())
                        .setShowSingleMediaType(true)
                        .setCapture(true)
                        .lastGo(REQUEST_CODE);
                break;
            case R.id.btn_img:
                sameEdit();
                PrimPicker
                        .with(this)
                        .choose(MimeType.ofImage())
                        .setSpanCount(Integer.parseInt(spanCount))
                        .setMaxSelected(Integer.parseInt(maxCount))
                        .setImageLoader(new ImageLoader())
                        .setShowSingleMediaType(true)
                        .setCapture(true)
                        .lastGo(REQUEST_CODE);
                break;
            case R.id.btn_all:
                sameEdit();
                PrimPicker
                        .with(this)
                        .choose(MimeType.ofAll())
                        .setCapture(true)
                        .setSpanCount(Integer.parseInt(spanCount))
                        .setImageLoader(new ImageLoader())
                        .setMaxSelected(Integer.parseInt(maxCount))
                        .setShowSingleMediaType(true)
                        .lastGo(REQUEST_CODE);
                break;
            case R.id.btn_current_add:
                PrimPicker
                        .with(this)
                        .choose(MimeType.ofImage())
                        .setSpanCount(3)
                        .setMaxSelected(9)
                        .setImageLoader(new ImageLoader())
                        .setShowSingleMediaType(true)
                        .setCapture(true)
                        .setDefaultItems(list)
                        .lastGo(REQUEST_CODE);
                break;
            case R.id.iv_one:
                PrimPicker.with(MainActivity.this)
                        .preview(MimeType.ofImage())
                        .setImageLoader(new ImageLoader())
                        .setPreviewItems(list)
                        .lastGo(REQUEST_CODE);
                break;
        }
    }

    private void sameEdit() {
        spanCount = etSpanCount.getText().toString().trim();
        maxCount = etMaxCount.getText().toString().trim();
        if (TextUtils.isEmpty(spanCount)) {
            spanCount = "3";
        }
        if (TextUtils.isEmpty(maxCount)) {
            maxCount = "1";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                StringBuffer str = new StringBuffer();
                list = PrimPicker.obtainItemsResult(data);
                pathList = PrimPicker.obtainPathResult(data);
                if (pathList != null) {
                    ivOne.setVisibility(View.VISIBLE);
                    Glide.with(this).load("file://" + pathList.get(0)).into(ivOne);
                } else {
                    ivOne.setVisibility(View.GONE);
                }
                str.append("返回结果:").append("Uri:").append("\n");
                ArrayList<Uri> uriArrayList = PrimPicker.obtainUriResult(data);
                for (Uri uri : uriArrayList) {
                    str.append(uri).append("\n");
                }
                str.append("Path:").append("\n");
                ArrayList<String> stringArrayList = PrimPicker.obtainPathResult(data);
                for (String s : stringArrayList) {
                    str.append(s).append("\n");
                }
                if (PrimPicker.obtainCompressResult(data)) {
                    str.append("压缩视频");
                } else {
                    str.append("不压缩视频");
                }
                result.setText(str.toString());
            }
        }

    }

    class ImageLoader implements ImageEngine {

        @Override
        public void loadImageThumbnail(Context context, int resize, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(resize, resize).centerCrop().into(view);
        }

        @Override
        public void loadImage(Context context, int resizeX, int resizeY, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(resizeX, resizeY).fitCenter().into(view);
        }

        @Override
        public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asGif().placeholder(placeholder).override(resize, resize).centerCrop().into(view);
        }

        @Override
        public void loadGifImage(Context context, int resizeX, int resizeY, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asGif().placeholder(placeholder).override(resizeX, resizeY).into(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
