package com.example.gauth.mad_expensemanager;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class ExpenseManager implements Serializable {
String Electricity;
String Cost;
String Date;
String randomUUID;



public ExpenseManager(String Electricity,String Cost,String Date,String randomUUID)
{
this.Electricity=Electricity;
this.Cost=Cost;
this.Date=Date;
this.randomUUID=randomUUID;
}
public ExpenseManager()
{
}
}
