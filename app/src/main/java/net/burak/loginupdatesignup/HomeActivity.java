package net.burak.loginupdatesignup;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.showAdvertisement();
        Bundle bundle = getIntent().getExtras();

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String dataa = prefs2.getString("USERID", "no id"); //no id: default value
        System.out.println(dataa);

        if (bundle != null) {
            String json = bundle.getString("access_token");
            System.out.println(json);
        }
    }//TEST

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_update) {
            Intent intentUpdate = new Intent(getApplicationContext(), UpdateAccActivity.class);
            startActivity(intentUpdate);
        }
        else if(item.getItemId() == R.id.action_exit) {
            this.finishAffinity();
        }

        return true;
    }






}

