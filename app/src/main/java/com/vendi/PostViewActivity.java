package com.vendi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vendi.model.Post;
import com.vendi.model.User;
import com.vendi.task.SendMsg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewActivity extends AppCompatActivity implements OnClickListener{
    private Post post;
    private static String TAG="--->>>";
    private DatabaseReference myRef;
    private ValueEventListener listner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        post = MyApplication.getInstance().getPost();
        myRef = FirebaseDatabase.getInstance().getReference("categories/"+post.getCategory_id()+"/posts/"+post.getPost_id());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                initViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addValueEventListener(listner);
        saveView();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_share:
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = post.getDescription();
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Compre aqui!");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Compartilhe com"));
                return true;
            case R.id.menu_setting:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){

        TextView name = (TextView)findViewById(R.id.user_email);
        TextView description = (TextView) findViewById(R.id.post_desc);
        CircleImageView picture = (CircleImageView) findViewById(R.id.user_pic);
        TextView price = (TextView) findViewById(R.id.post_price);
        TextView likes = (TextView) findViewById(R.id.post_likes);
        TextView time = (TextView) findViewById(R.id.post_time);
        TextView views = (TextView) findViewById(R.id.post_views);
        TextView location = (TextView) findViewById(R.id.user_location);
        TextView brands = (TextView) findViewById(R.id.post_brands);
        final ToggleButton like = (ToggleButton) findViewById(R.id.post_like);
        Button want = (Button) findViewById(R.id.post_want);

        name.setText(post.getUser());
        description.setText(post.getDescription());
        location.setText("Rio de Janeiro");
        price.setText("R$ "+String.format( "%.2f", post.getPrice()));
        likes.setText(((post.getLikes().size()==0) ? "": (post.getLikes().size()==1) ? post.getLikes().size()+" curtiu": post.getLikes().size()+" curtiram"));
        name.setText(post.getUser());
        views.setText(String.valueOf(post.getViews().size()));
        brands.setText(post.getBrands());
        time.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp()*-1, System.currentTimeMillis(),1000));
        name.setOnClickListener(this);
        want.setOnClickListener(this);

        like.setText(null);
        like.setTextOn(null);
        like.setTextOff(null);
        like.setChecked(post.getLikes().containsKey(Prefs.getUser().getUsername()));
        like.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (like.isChecked()) {
                    saveLike();
                } else {
                    deleteLike();
                }
            }
        });

        setupPager();

        Glide.with(this)
                .load(post.getPicture())
                .into(picture);
    }

    private void deleteLike() {
        Log.d(TAG, "deleteLikeData: " + Prefs.getUser().getUsername());
        if (post.getLikes().containsKey(Prefs.getUser().getUsername())) {
            myRef.child("likes").child(Prefs.getUser().getUsername()).removeValue();
        }
    }

    private void saveLike(){
        Log.d(TAG, "saveLikeData: " + Prefs.getUser().getUsername());
        myRef.child("likes").child(Prefs.getUser().getUsername()).setValue(System.currentTimeMillis());
    }

    private void saveView(){
        myRef.child("views").child(Prefs.getUser().getUsername()).setValue(System.currentTimeMillis());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_view, menu);
        return true;
    }

    @Override
    public void onStop(){
        super.onStop();
        myRef.removeEventListener(listner);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        Intent intent;
        switch(i){
            case R.id.user_email:
                intent = new Intent(this, StoreActivity.class);
                intent.putExtra(User.EMAIL,post.getUser());
                startActivityForResult(intent, 1);
                break;
            case R.id.post_want:
                showMsgDialog();
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void showMsgDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_name, null);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit_name);

        final AlertDialog dialog = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                .setView(dialogView)
                .setTitle("Mensagem para o vendedor")
                .setMessage("Agora você pode enviar uma mensagem para o vendedor. Tire duas dúvidas e combine os detalhes de entrega e pagamento.")
                .setPositiveButton(getResources().getString(R.string.next), null)
                .create();

        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendMsg();
                    }
                });
            }
        });
        dialog.show();
    }
    private void sendMsg(){
        Log.d(TAG, "query user:"+post.getUser());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(post.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    Log.d(TAG,"dataSpanpshot Count:"+dataSnapshot.getChildrenCount());
                    User user = dataSnapshot.getValue(User.class);
                    SendMsg task = new SendMsg(user.getToken(),"Mensagem pra você","Alguém quer o seu produto.");
                    task.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static FragmentImage newInstance(String url)
    {
        FragmentImage fragment = new FragmentImage();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setupPager(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Iterator<Object> u = post.getImage().values().iterator();
        while(u.hasNext()){
            String s = (String)u.next();
            adapter.addFragment(newInstance(s));
            Log.d(TAG,"string "+s);
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final FragmentManager manager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.manager = manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
            //mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

}
