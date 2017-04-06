package com.vendi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.vendi.model.Post;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "--->>>";

    private ImageView imgPreview1;
    private ImageView imgPreview2;
    private ImageView imgPreview3;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Bitmap image;
    private Button btn;
    private static String IMAGE_DIRECTORY_NAME = "/Vendi";
    private static int REQUEST_PHOTO = 1;
    private Post post;
    private HashMap<String, Object>  images = new HashMap<String, Object>();

    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog prog;

    private EditText title;
    private EditText desc;
    private MultiAutoCompleteTextView brands;
    private EditText price;
    private double progress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Prefs.getUser().getUsername());
        getSupportActionBar().setSubtitle("Categoria: "+Prefs.getCategory().getCategory());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://firebase-vendi.appspot.com");
        myRef = FirebaseDatabase.getInstance().getReference();

        imgPreview1 = (ImageView) findViewById(R.id.imgPreview1);
        imgPreview2 = (ImageView) findViewById(R.id.imgPreview2);
        imgPreview3 = (ImageView) findViewById(R.id.imgPreview3);

        imgPreview1.setOnClickListener(this);
        imgPreview2.setOnClickListener(this);
        imgPreview3.setOnClickListener(this);

        post = new Post();
        post.setCategory_id(Prefs.getCategory().getCategory_uid());
        post.setUser(Prefs.getUser().getUsername());
        post.setPicture(Prefs.getUser().getPicture());

        captureImage();
        initView();
    }

    public void initView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.brands));
        title = (EditText) findViewById(R.id.post_title);
        desc = (EditText) findViewById(R.id.post_desc);
        brands = (MultiAutoCompleteTextView) findViewById(R.id.post_brand);
        brands.setAdapter(adapter);
        brands.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        price = (EditText) findViewById(R.id.post_price);


    }
    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.post:
                saveImages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.imgPreview1: {
                captureImage();
                break;
            }
            case R.id.imgPreview2: {
                captureImage();
                break;
            }
            case R.id.imgPreview3: {
                captureImage();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        Log.d(TAG, " media storage ok");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
            Log.d(TAG, " media storage create dir");
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else return null;
        return mediaFile;
    }


    private void captureImage(){
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivityForResult(intent, REQUEST_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                File imgFile = new  File(path);
                if(imgFile.exists()){
                    Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image = scaleCenterCrop(image, 1080, 1080);
                    int i = images.size();
                    Log.d(TAG, "images size"+i);
                    switch(i){
                        case 0:{
                            images.put("img1", image);
                            imgPreview1.setImageBitmap(image);
                            Log.d(TAG, "image "+i);
                            break;
                        }
                        case 1:{
                            images.put("img2", image);
                            imgPreview2.setImageBitmap(image);
                            Log.d(TAG, "image "+i);
                            break;
                        }
                        case 2:{
                            images.put("img3", image);
                            imgPreview3.setImageBitmap(image);
                            Log.d(TAG, "image "+i);
                            break;
                        }
                    }
                }
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this,"Falha na captura da image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePostData(){

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("categories/"+post.getCategory_id()+"/posts/"  + post.getPost_id(), post.toMap());
        childUpdates.put("/user-posts/" + post.getUser() + "/" + post.getPost_id(), post.toMap());

        myRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "savePostData: " + task.isSuccessful());
                if(task.isSuccessful()) {
                    prog.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Log.d(TAG, "savePostData: " + task.getException().getMessage());
                }
            }
        });
    }

    public void saveImages(){
        boolean fields = true;
        post.setBrands(brands.getText().toString());

        post.setTitle(title.getText().toString());
        if(title.getText().toString().isEmpty()){
            title.setError("Não esqueça de preencher todos os campos.");
            fields = false;
        }

        post.setDescription(desc.getText().toString());
        if(desc.getText().toString().isEmpty()){
            desc.setError("Não esqueça de preencher todos os campos.");
                fields = false;
            }

        if(price.getText().toString().isEmpty()){
            price.setError("Não esqueça de preencher todos os campos.");
            fields = false;
        } else {
            post.setPrice(Double.parseDouble(price.getText().toString()));
        }

        if(fields) {
            post.setTimestamp(System.currentTimeMillis() * -1);
            prog = new ProgressDialog(this);
            prog.setMessage(getResources().getString(R.string.post_sendtoserver));
            prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prog.setCancelable(true);
            prog.setProgress(0);
            prog.setMax(100);
            Log.d(TAG, "progressbar");
            prog.show();

            int i = 1;
            String img = "img";

            double totalBytes = 0;

            while (images.containsKey(img + i)) {
                Log.d(TAG, "i=" + i);
                totalBytes = totalBytes + ((Bitmap) images.get(img + i)).getByteCount();
                i++;
                Log.d(TAG, "bytes=" + totalBytes);
            }
            Log.d(TAG, "total=" + totalBytes);
            saveImage((Bitmap) images.get("img" + 1), 1, totalBytes);
        }
    }

    public void saveImage(Bitmap bmp, final int position, final  double bytes) {
        Log.d(TAG, "salvando imagem "+position);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] b = baos.toByteArray();

        String key = myRef.push().getKey();
        Log.d(TAG,"key: "+key);
        post.setPost_id(key);
        StorageReference imagesRef = storageRef.child("images/posts/"+post.getPost_id()+"_"+position+".jpg");

        UploadTask uploadTask = imagesRef.putBytes(b);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Falha ao salvar image");
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                post.addImage("img"+position, downloadUrl.toString());
                Log.d(TAG, "img"+(position+1));
                Log.d(TAG, "img "+images.containsKey("img"+(position+1)));
                if(images.containsKey("img"+(position+1))){
                    saveImage((Bitmap)images.get("img"+(position+1)), (position+1), bytes);
                }else {
                    savePostData();
                }
                Log.d(TAG, "downloadUrl: "+downloadUrl.toString());
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
            @Override
            public void onProgress(TaskSnapshot taskSnapshot) {
                progress = progress + taskSnapshot.getBytesTransferred();
                double p = 100.0 * (progress / bytes);
                Log.d(TAG, "progress: "+p);
                prog.setProgress((int)p);
            }
        });
    }
}