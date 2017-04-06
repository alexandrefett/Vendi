package com.vendi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.vendi.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG="--->>>";
    private static final int REQUEST_PHOTO = 1;

    private Bitmap image;
    private User mUser;

    private CircleImageView imageView;
    private EditText username;
    private EditText email;
    private PhoneEditText phone;
    private TextView loc;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog prog;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = (EditText)findViewById(R.id.user_fullname);
        email = (EditText)findViewById(R.id.user_email);
        phone = (PhoneEditText)findViewById(R.id.user_phone);
        imageView = (CircleImageView)findViewById(R.id.user_pic);
        loc = (TextView)findViewById(R.id.user_location);

        mUser = new User();

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://firebase-vendi.appspot.com");

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "TOKEN: "+token);
        mUser.setToken(token);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        image = BitmapFactory.decodeResource(getResources(), R.drawable.userimage);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude());
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses!=null) {
                    if (addresses.size() > 0) {
                        for (Address a : addresses) {
                            loc.setText(a.getLocality());
                            mUser.setLocation(a.getLocality());
                            Log.d(TAG, a.getLocality());
                        }
                    }
                }
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "onStatusChanged: "+s);

            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled: "+s);

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled: "+s);

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);

        TelephonyManager t = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = t.getLine1Number();
        phone.setText(t.getLine1Number());

        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                email.setText(account.name);
            }
        }

    }

    private void saveUserData(){
        myRef = FirebaseDatabase.getInstance().getReference("users");
        myRef.child(mUser.getUsername()).setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "saveUserData: " + task.isSuccessful());
                if(task.isSuccessful()) {
                    Prefs.setUser(mUser);
                    Prefs.setStatus(User.CONFIRMED);
                    setResult(Activity.RESULT_OK);
                    prog.dismiss();
                    finish();
                } else {
                    Log.d(TAG, "saveUserData: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail: " + task.getException().getMessage());
                    } else {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.getResult().getUser().getUid());
                        savePicture(image);
                    }
                }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                String path = intent.getStringExtra("path");
                File imgFile = new  File(path);
                if(imgFile.exists()){
                    Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image = scaleCenterCrop(image, 1080, 1080);
                    imageView.setImageBitmap(image);
                }
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this,"Falha na captura da image.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void doAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void savePicture(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] b = baos.toByteArray();
        Log.d(TAG, "salvando imagem");
        StorageReference imagesRef = storageRef.child("images/"+mAuth.getCurrentUser().getUid()+".jpg");

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
                mUser.setPicture(downloadUrl.toString());
                //RegTask mTask = new RegTask(mUser.getUsername());
                //mTask.execute();
                saveUserData();
                Log.d(TAG, "downloadUrl: "+downloadUrl.toString());
            }
        });
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
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.user_pic:{
                Intent intent = new Intent(this, PhotoActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        }
    }
    public void save(){
        boolean fields = true;

        if(username.getText().toString().isEmpty()){
            username.setError("Não esqueça de preencher todos os campos.");
            fields = false;
        }else{
            mUser.setFullname(username.getText().toString());
        }

        if(email.getText().toString().isEmpty()){
            email.setError("Não esqueça de preencher todos os campos.");
            fields = false;
        }else{
            mUser.setEmail(email.getText().toString());
        }

        if(phone.getText().toString().isEmpty()){
            phone.setError("Não esqueça de preencher todos os campos.");
            fields = false;
        }else{
            mUser.setPhone(phone.getText().toString());
        }

        if(fields) {
            prog = new ProgressDialog(this);
            prog.setMessage(getResources().getString(R.string.post_sendtoserver));
            prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prog.setCancelable(true);
            prog.show();
            createAccount(mUser.getEmail(), mUser.getPhone());
        }
    }

}
