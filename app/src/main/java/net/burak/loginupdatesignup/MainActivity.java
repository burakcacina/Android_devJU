package net.burak.loginupdatesignup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity {
    Button btnSignIn,btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.showAdvertisement();
        btnSignIn=(Button)findViewById(R.id.buttonSignIN);
        btnSignUp=(Button)findViewById(R.id.buttonSignUP);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentSignUP=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intentSignUP);
            }
        }
        );

        btnSignIn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             // TODO Auto-generated method stub

                                             Intent intentSignIn=new Intent(getApplicationContext(),LoginActivity.class);
                                             startActivity(intentSignIn);
                                         }
                                     }
        );


    }

}
