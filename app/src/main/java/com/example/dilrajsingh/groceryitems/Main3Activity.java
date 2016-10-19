package com.example.dilrajsingh.groceryitems;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> items;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String id;
    TextView text;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if(isNetworkAvailable()){
            firebaseAuth = FirebaseAuth.getInstance();
            //text = (TextView) findViewById(R.id.textView2)
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseUser = firebaseAuth.getCurrentUser();
            id = firebaseUser.getUid();
            ProgressDialog pd = new ProgressDialog(Main3Activity.this);
            pd.setCancelable(false);
            pd.setMessage("Loading");
        }
        else{
            Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(Main3Activity.this, "Loading content, please wait...", Toast.LENGTH_SHORT).show();
        if(isNetworkAvailable()){
            refresh();
        }
        else{
            Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
        listView = (ListView) findViewById(R.id.listView);
        items = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main3Activity.this);
                dialog.setTitle("Do you want to delete this entry?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String q = String.valueOf(parent.getItemAtPosition(position));
                        String[] r  =q.split(" --> ");
                        String query = r[0];
                        if(isNetworkAvailable()){
                            delete(query);
                        }
                        else{
                            Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }

    public void delete(String item){
        databaseReference.child(id).child(item).removeValue();
        Toast.makeText(Main3Activity.this, "Deleted", Toast.LENGTH_SHORT).show();
    }

    public void refresh(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProgressDialog pd = new ProgressDialog(Main3Activity.this);
                pd.setMessage("Loading Content");
                pd.setCancelable(false);
                items.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                   if(data.getKey().equals(id)) {
                       for (DataSnapshot post : data.getChildren()) {
                           items.add(post.child("name").getValue().toString() + " --> " + post.child("quant").getValue().toString());
                       }
                   }
                }
                ArrayAdapter<String> ad = new ArrayAdapter<String>(Main3Activity.this, android.R.layout.simple_list_item_1, items);
                listView.setAdapter(ad);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
        Toast.makeText(Main3Activity.this, "Loaded", Toast.LENGTH_SHORT).show();
    }

    public void addItem(View view){
        AlertDialog.Builder ad = new AlertDialog.Builder(Main3Activity.this);
        ad.setTitle("Enter the information");
        LinearLayout ll = new LinearLayout(Main3Activity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(params);
        final EditText editText = new EditText(Main3Activity.this);
        editText.setHint("Enter the product");
        editText.setLayoutParams(params);
        final EditText editText2 = new EditText(Main3Activity.this);
        editText2.setHint("Enter quantity");
        editText2.setLayoutParams(params);
        ll.addView(editText);
        ll.addView(editText2);
        ad.setView(ll);
        ad.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                String quan = editText2.getText().toString();
                ItemAdded itemAdded = new ItemAdded(name, quan);
                if(isNetworkAvailable()){
                    firebaseAuth = FirebaseAuth.getInstance();
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference.child(id).child(name).setValue(itemAdded);
                    refresh();
                    Toast.makeText(Main3Activity.this, "Added", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    EditText edit;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.buttonLogOut :
                if(isNetworkAvailable()){
                    firebaseAuth.signOut();
                    Toast.makeText(Main3Activity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(Main3Activity.this, MainActivity.class);
                    startActivity(in);
                }
                else{
                    Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.refresh:
                if(isNetworkAvailable()){
                    Toast.makeText(Main3Activity.this, "Loading content, please wait...", Toast.LENGTH_SHORT).show();
                    refresh();
                }
                else {
                    Toast.makeText(Main3Activity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonChangePassword:
                AlertDialog.Builder al = new AlertDialog.Builder(Main3Activity.this);
                al.setTitle("Confirm change password");
                al.setMessage("You will be logged out and you will be able to sign in with new password");
                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail());
                        firebaseAuth.signOut();
                        Intent in = new Intent(Main3Activity.this, MainActivity.class);
                        startActivity(in);
                    }
                });
                al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                al.show();
                break;
        }
        return true;
    }
}
