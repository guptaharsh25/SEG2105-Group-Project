package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;

    private ArrayList<String> serviceNames;
    private ArrayList<Double> serviceRates;
    private String[] keys;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Services");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if(dataSnapshot.getValue() != null){
                        keys = (String[])((Map<String,Object>) dataSnapshot.getValue()).keySet().toArray();}
                        //collectServiceNames((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        //String[] serviceNameArr = new String[serviceNames.size()];
        //serviceNameArr = (String[])serviceNames.toArray();

        ListView serviceList = (ListView) findViewById(R.id.serviceList);
        if (keys!=null){
            ServiceCustomAdapter adapter = new ServiceCustomAdapter(this, keys);
            serviceList.setAdapter(adapter);
        }

        add = findViewById(R.id.add);

        //serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() { @Override
        /*public void onItemClick(AdapterView<?> parent, final View view, int position, long id){
            //Here we insert some code to do something!
            Intent editorLaunchInterest = new Intent(getApplicationContext(), ChoreEditorActivity.class);
            editorLaunchInterest.putExtra("position",position);
            editorLaunchInterest.putExtra("name",choreList[position]);
            startActivityForResult(editorLaunchInterest, 0);
        }});*/
    }

    public void addService(final View view){
        final String[] serviceName = {""};
        final double[] serviceRate = {0};

        final AlertDialog.Builder serviceAdd = new AlertDialog.Builder(this);
        serviceAdd.setTitle("Add New Service");

        final EditText getServiceName = new EditText(this);
        final EditText getServiceRate = new EditText(this);
        getServiceName.setHint("Service Name");
        getServiceRate.setHint("Rate");

        getServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        getServiceRate.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);

        linLayout.addView(getServiceName);
        linLayout.addView(getServiceRate);

        serviceAdd.setView(linLayout);

        serviceAdd.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newService = getServiceName.getText().toString();
                Context context = getApplicationContext();
                CharSequence text = "Method working";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                boolean duplicateFound = false;
                if (keys!=null){
                    for(int i=0; i< keys.length; i++){
                        String name = FirebaseDatabase.getInstance().getReference().child("Services").child(keys[i]).child("name").toString();
                        if(serviceName[0].equals(name)){
                            duplicateFound = true;
                        }
                    }
                }

                if(duplicateFound){
                    CharSequence textDuplicateService = "Please Enter a Valid Service Rate";

                    Toast toastService = Toast.makeText(context, textDuplicateService, duration);
                    toastService.show();
                } else {
                    serviceName[0] = newService;
                }

                try{
                    double newRate = Double.parseDouble(getServiceRate.getText().toString());
                    serviceRate[0] = newRate;
                } catch (Exception exception){
                    CharSequence textValidRate = "Please Enter a Valid Service Rate";
                    Toast toastValidRate = Toast.makeText(context, textValidRate, duration);
                    toastValidRate.show();

                    addService(view);
                }

                if(!serviceName[0].equals("") || serviceRate[0]!=0) {
                    mServicesRef.child(serviceName[0]);
                    mServicesRef.child(serviceName[0]).child("rate").setValue(serviceRate[0]);
                    mServicesRef.child(serviceName[0]).child("user").setValue(user.getDisplayName());
                }

            }
        });



        serviceAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog ad = serviceAdd.create();
        ad.show();

        if(!serviceName[0].equals("") || serviceRate[0]!=0) {
            mServicesRef.child("name").setValue(serviceName[0]);
            mServicesRef.child("rate").setValue(serviceRate[0]);
        }

    }

    private void collectServiceNames(Map<String,Object> services) {

        serviceNames = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : services.entrySet()){

            //Get user map
            Map singleService = (Map) entry.getValue();
            //Get phone field and append to list
            serviceNames.add((String) singleService.get("name"));
        }
    }

    /*private void collectServiceRates(Map<String,Object> services) {

        serviceRates = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : services.entrySet()){

            //Get user map
            Map singleService = (Map) entry.getValue();
            //Get phone field and append to list
            serviceRates.add((Double) singleService.get("rate"));
        }
    }*/
}