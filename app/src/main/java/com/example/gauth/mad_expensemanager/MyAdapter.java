package com.example.gauth.mad_expensemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

   static ArrayList<ExpenseManager>mData;
    long megabyte=1024*1024;
    public  MyAdapter(ArrayList<ExpenseManager>mData)
    {
        Log.i("Constructor","Called!!!!");
        this.mData=mData;

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("onCreateViewHolder","Called!!!!");
         View view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.i("onBindViewHolder","Called!!!!");
        ExpenseManager expenseManager=mData.get(position);
        holder.electricity.setText(expenseManager.Electricity);
        holder.cost.setText(expenseManager.Cost);
        holder.dates.setText(expenseManager.Date);
      if(expenseManager.randomUUID.length()>1) {
          StorageReference tempRef = FirebaseStorage.getInstance().getReference().child("Photos2").child(expenseManager.randomUUID + ".png");
          tempRef.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
              @Override
              public void onSuccess(byte[] bytes) {
                  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                  holder.imageView2.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
              }
          });
      }else {

      }
        holder.expenseManager=expenseManager;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.i("getItemCount","Called!!!!");
        Log.i("Item no",""+mData.size());

        return mData.size();
    }

        public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView electricity,cost,dates;
        ImageView imageView2;
        ExpenseManager expenseManager;
        private final Context context;
        public ViewHolder(final View itemView) {
            super(itemView);
            context=itemView.getContext();
            Log.i("static class ViewHolder","Called!!!!");
            electricity=(TextView)itemView.findViewById(R.id.set_Electricity);
            cost=(TextView)itemView.findViewById(R.id.set_costs);
            dates=(TextView)itemView.findViewById(R.id.set_dates);
            imageView2=itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Item",expenseManager.Electricity.toString());
                    Intent intent=new Intent(context,AddExpense.class);
                    intent.putExtra("Key1",expenseManager);
                 intent.putExtra("Key2",getLayoutPosition());
                    context.startActivity(intent);
                    //Write code here the control comes back from AddExpense Acivity
                    Log.i("Back to","Calling activity");
                    electricity.setText(expenseManager.Electricity);
                }
            });
        }
    }
}
