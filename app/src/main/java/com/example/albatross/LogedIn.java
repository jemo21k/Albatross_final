package com.example.albatross;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LogedIn extends AppCompatActivity {


//    private TextView fullName_v ;
//    private TextView getFullName2_v;
//    private TextView Email_v;
//    private TextView Phone_v;
//    private TextView NestID_v;
//    private TextView Location_v;


    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged_in);

        Toolbar toolbar = findViewById(R.id.toolbar);

//        ((TextView)findViewById( R.id.editTextTextPersonName)).setText(getIntent().getExtras().getString("FullName"));
//        ((TextView)findViewById( R.id.USer2)).setText(getIntent().getExtras().getString("FullName"));
//        ((TextView)findViewById( R.id.editTextTextEmailAddress)).setText(getIntent().getExtras().getString("Email"));
//        ((TextView)findViewById( R.id.editTextPhone)).setText(getIntent().getExtras().getString("Phone"));



//        fullName_v.setText( "Name:" );
//        getFullName2_v.setText("NAME2");
////        Email_v.setText(getIntent().getExtras().getString("Email"));
////        Phone_v.setText(getIntent().getExtras().getString("Phone"));


        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loged_in, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}