package ca.harshgupta.seg2105_project;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClientHomeFragment1 extends Fragment {
    private TextView clientText;
    View myView;
    private FirebaseUser user;
    private DatabaseReference userInfo;

    private AppointmentClientCustomAdapter appointmentAdapter;
    private ListView appointmentList;
    private String [] keys;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_client_home, container, false);
        return myView;
    }

    public void onStart (){
        super.onStart();
        clientText = myView.findViewById(R.id.clientText);
        user = FirebaseAuth.getInstance().getCurrentUser();

        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(user.getUid());

        userInfo.child("FirstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                clientText.setText("Client: " + userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("LastName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                clientText.setText(clientText.getText().toString() + " " + userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

    }

    public void viewAppointmentAdapter(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                appointmentAdapter = new AppointmentClientCustomAdapter(myView.getContext(), keys);
                appointmentList = (ListView) myView.findViewById(R.id.listAvailabilites);
                appointmentList.setAdapter(appointmentAdapter);
                appointmentAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }
}