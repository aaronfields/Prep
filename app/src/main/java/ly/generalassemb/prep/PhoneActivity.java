package ly.generalassemb.prep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PhoneActivity extends AppCompatActivity {

    private Button button;
    private String phoneNumber;
    private EditText myPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        button = (Button) findViewById(R.id.button);
        myPhone = (EditText) findViewById(R.id.phone_number);
        myPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = myPhone.getText().toString();

                Intent myIntent = new Intent(PhoneActivity.this, SubjectsActivity.class);
                myIntent.putExtra("phonenumber", phoneNumber);
                startActivity(myIntent);

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if( user == null) {




            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    7);

        }
        else{
            Intent intent = new Intent(this, SubjectsActivity.class);
            startActivity(intent);
        }
    }
}
