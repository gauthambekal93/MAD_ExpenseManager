package com.example.gauth.mad_expensemanager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
   static public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
   static ArrayList<ExpenseManager> expenses=new ArrayList<>();


    public DatabaseReference databaseReference;
    public DatabaseReference conditionReference;
    static public boolean storageCaller=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        conditionReference=databaseReference.child("ExpenseManager2");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(expenses);
        mRecyclerView.setAdapter(mAdapter);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
          mAdapter.notifyDataSetChanged();
    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        conditionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if(storageCaller){
storageCaller=false;
Context context=getBaseContext();
Toast.makeText(context,"Cloud storage called",Toast.LENGTH_SHORT).show();
    expenses.add(new ExpenseManager("JAN_From_Local","100","2018/01/01","e546df59-f987-44e8-afaa-10b01b29ef4f"));
    for (DataSnapshot expenses : dataSnapshot.getChildren()) {
        if (expenses != null) {
            ExpenseManager expenseManager = expenses.getValue(ExpenseManager.class);
            Log.i("Stored data is", expenseManager.Electricity);
            Log.i("Stored data is", expenseManager.Cost);
            Log.i("Stored data is", expenseManager.Date);
           Log.i("Stored data is", expenseManager.randomUUID);
           if(expenseManager.randomUUID.length()<1)
           {
               Toast.makeText(context,"No IMAGE AVAILABLE AT"+expenseManager.Electricity,Toast.LENGTH_SHORT).show();
           }

            addToArrayList(expenseManager);
        } else {
            Log.i("Child", "Not available");
        }
    }

}
  mAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
 public void addToArrayList(ExpenseManager expenseManager)
{
    if(expenseManager.randomUUID.length()<1)
    {expenses.add(new ExpenseManager(expenseManager.Electricity,expenseManager.Cost,expenseManager.Date,""));

    }else{
    expenses.add(new ExpenseManager(expenseManager.Electricity,expenseManager.Cost,expenseManager.Date,expenseManager.randomUUID));
    //expenses.add(expenseManager);
    mAdapter.notifyDataSetChanged();
}
}
}
