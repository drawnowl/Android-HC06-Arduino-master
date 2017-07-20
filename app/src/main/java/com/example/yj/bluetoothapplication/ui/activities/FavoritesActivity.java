package com.example.yj.bluetoothapplication.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yj.bluetoothapplication.R;
import com.example.yj.bluetoothapplication.adapters.FavViewDataAdapter;
import com.example.yj.bluetoothapplication.data.LocationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView_tv;
    private Button emptyView_btn;
    private Button sendValues;
    private Intent intent;
    private RecyclerView.Adapter adapter;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    private ItemTouchHelper itemTouchHelper;

    private DatabaseReference databaseReference;
    private ArrayList<LocationItem> locationItems;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("locations");

        locationItems = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sendValues = (Button) findViewById(R.id.sendCoordinates);
        recyclerView = (RecyclerView) findViewById(R.id.favList);
        emptyView_tv = (TextView) findViewById(R.id.empty_view_tv);
        emptyView_btn = (Button) findViewById(R.id.empty_view_btn);

        getFavorites();
    }

    private void getFavorites() {
        if (!isSignedIn()) {
            Log.d("firebase", "not auth");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.login_first)
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        } else {
            checkEmptyList();

            databaseReference.child(getUid()).addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    showProgress();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        LocationItem locationItem = postSnapshot.getValue(LocationItem.class);
                        locationItems.add(locationItem);
                    }

                    adapter = new FavViewDataAdapter(locationItems);

                    simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                            // Remove swiped item from list and notify the RecyclerView

//                    LocationItem favItem = (LocationItem) adapter.getItem(0);
//                    Log.e("TAG", favItem.getTitle());
//                    databaseReference.child(getUid()).child(favItem.getKey).removeValue();
//                    databaseReference.child(getUid()).child(((FavViewDataAdapter) viewHolder)).removeValue();
                        }
                    };
//            itemTouchHelper.attachToRecyclerView(recyclerView);
//            itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    recyclerView.setAdapter(adapter);
                    hideProgress();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkEmptyList() {
        databaseReference.child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView_tv.setVisibility(View.VISIBLE);
                    emptyView_btn.setVisibility(View.VISIBLE);

                    emptyView_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onSendValues(View view) {
        List<LocationItem> locationItems = ((FavViewDataAdapter) adapter).getLocationItems();

        if(locationItems.size() > 0) {
            ArrayList<String> values = new ArrayList<>();
            ArrayList checkDublicates = new ArrayList();

            for (int i = 0; i < locationItems.size(); i++) {
                LocationItem locationItem = locationItems.get(i);

                if (locationItem.getChecked()) {
                    int properNavNumber = Integer.parseInt(String.valueOf(locationItem.getNavigatorNumber())) - 1;
                    checkDublicates.add(properNavNumber);
                    String res = properNavNumber + "";

                    values.add("progeeprom" +
                            String.valueOf(locationItem.getLat()).replace(".", "").substring(0, 8) +
                            String.valueOf(locationItem.getLng()).replace(".", "").substring(0, 8) +
                            "W" + res);
                }
            }

            for (int i = 0; i < checkDublicates.size(); i++) {
                for (int j = i + 1; j < checkDublicates.size(); j++) {
                    if(checkDublicates.get(i) == checkDublicates.get(j)) {
                        Toast.makeText(FavoritesActivity.this, R.string.check_dublicates,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            Intent intent = new Intent(this, DevicesListActivity.class);
            intent.putStringArrayListExtra("list", values);
            startActivity(intent);
        }
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}