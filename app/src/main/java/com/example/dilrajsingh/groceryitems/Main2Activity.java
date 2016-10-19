package com.example.dilrajsingh.groceryitems;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {

    EditText editText, editText2;
    Button button;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    TextView textView;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(in);
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage("Logging In");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
    }

    protected boolean isEmpty(EditText editText){

        return editText.getText().toString().trim().length() == 0;

    }

    public void onSignIn(View view){
        String email = editText.getText().toString();
        String pass = editText2.getText().toString();
        if(isEmpty(editText) || isEmpty(editText2)){
            Toast.makeText(Main2Activity.this, "Please add some information", Toast.LENGTH_SHORT).show();
        }
        else {
            if(isNetworkAvailable()){
                firebaseAuth = FirebaseAuth.getInstance();
                if(firebaseAuth.getCurrentUser()!=null){
                    Toast.makeText(Main2Activity.this, "Already Logged in", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(Main2Activity.this, Main3Activity.class);
                    startActivity(in);
                }
                pd.show();
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(Main2Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Main2Activity.this, "Signed In", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(Main2Activity.this, Main3Activity.class);
                            startActivity(in);
                            pd.dismiss();
                        } else {
                            Toast.makeText(Main2Activity.this, "Invalid", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                });
            }
            else{
                Toast.makeText(Main2Activity.this, "Please check your nwtwork connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
