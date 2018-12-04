package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceProviderAppointmentCustomAdapter extends ArrayAdapter{
    private final Context context;
    private final String[] myKeys;
    private DatabaseReference mAppointments, mAccounts, mServices;
    private TextView dateText, serviceText, clientText, timeText, spIDText, orderIDText, clientIDText;

    public ServiceProviderAppointmentCustomAdapter(Context context, String[] AppointmentList){
        super(context, R.layout.service_provider_appointment_list_layout, AppointmentList);
        this.context = context;
        this.myKeys = AppointmentList;
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.service_provider_appointment_list_layout, parent, false);
        mAppointments = FirebaseDatabase.getInstance().getReference().child("Appointment");
        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");
        mServices = FirebaseDatabase.getInstance().getReference().child("Services");
        setValues(position, rowView, "Date");
        setValues(position, rowView, "ClientID");
        setValues(position, rowView, "Service");
        setValues(position, rowView, "SPID"); //Does IdSP and Company Name
        setValues(position, rowView, "time");

        orderIDText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeIdOrder);  //ID order doesnt need snapshot
        orderIDText.setText(myKeys[position]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { } },1000); //1000ms = 1sec
        return rowView;
    }

    private void setValues(int position, final View rowView, final String info){
        mAppointments.child(myKeys[position]).child(info).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    switch (info) {
                        case "Date":
                            dateText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeDate);
                            final String outputDate = dataSnapshot.getValue(String.class);
                            dateText.setText(outputDate);
                            break;
                        case "time":
                            timeText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeTime);
                            String outputTime = dataSnapshot.getValue(String.class);
                            timeText.setText(outputTime);
                            break;
                        case "SPID":
                            spIDText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeIdSP);
                            String outputSPID = dataSnapshot.getValue(String.class);
                            spIDText.setText(outputSPID);
                            break;
                        case "Service":
                            serviceText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeService);
                            mServices.child(dataSnapshot.getValue(String.class)).child("name")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                            if (dataSnapshot3.getValue() != null && dataSnapshot.getValue() != null) {
                                                String outputService = dataSnapshot3.getValue(String.class);
                                                serviceText.setText(outputService);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                            break;
                        case "ClientID":
                            clientIDText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeIdClient);
                            String outputclientID = dataSnapshot.getValue(String.class);
                            clientIDText.setText(outputclientID);
                            mAccounts.child(dataSnapshot.getValue(String.class)).child("FirstName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {
                                    clientText = (TextView) rowView.findViewById(R.id.textSPAvailabilityHomeClient);
                                    String outputName = dataSnapshot4.getValue(String.class);
                                    clientText.setText(outputName);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}


