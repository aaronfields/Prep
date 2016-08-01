package ly.generalassemb.prep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mSignIn;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

        mSignIn = (Button) findViewById(R.id.button_signin);
        mRegister = (Button) findViewById(R.id.button_register);
        mSignIn.setOnClickListener(this);
        mRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == mSignIn) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            Intent registerIntent = new Intent(this, RegistrationActivity.class);
            startActivity(registerIntent);
        }
    }
}
