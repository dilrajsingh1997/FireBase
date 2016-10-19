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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText, editText2;
    TextView textView;
    ProgressDialog progressDialog;

    public FirebaseAuth firebaseAuth;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        textView = (TextView) findViewById(R.id.textView);
        progressDialog = new ProgressDialog(this);
        if(firebaseAuth.getCurrentUser()!=null){
            Toast.makeText(MainActivity.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(MainActivity.this, Main3Activity.class);
            startActivity(in);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(in);
            }
        });
    }

    protected boolean isEmpty(EditText editText){

        return editText.getText().toString().trim().length() == 0;

    }

    public void onSignUp(View view){
        String email = editText.getText().toString();
        String pass = editText2.getText().toString();
        if(isEmpty(editText) || isEmpty(editText2)){
            Toast.makeText(MainActivity.this, "Please enter some information", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Registering");
            progressDialog.show();
            if(isNetworkAvailable()){
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent in = new Intent(MainActivity.this, Main3Activity.class);
                            startActivity(in);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Invalid e-mail or password", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
            else {
                Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
