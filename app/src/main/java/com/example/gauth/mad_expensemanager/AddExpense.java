package com.example.gauth.mad_expensemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static android.view.View.VISIBLE;
//Create another expensemanager object and initialize it with new then try to add to fire base
public class AddExpense extends AppCompatActivity {

    EditText Electricity;
    EditText Cost;
    EditText Date;
    CalendarView calendarView;
    public DatabaseReference databaseReference;
    public DatabaseReference conditionReference;
    ExpenseManager expenseManager;
    String path;
    StorageReference reference;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance("gs://firecast-app-65037.appspot.com");
    ImageView image_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        conditionReference=databaseReference.child("ExpenseManager2");

        Intent intent=getIntent();
        expenseManager=(ExpenseManager)intent.getSerializableExtra("Key1");
        expenseManager.randomUUID="";
        Button button=(Button)findViewById(R.id.add);
        Button date=(Button)findViewById(R.id.add_date);
        Button uploadImage=(Button) findViewById(R.id.add_image);
        calendarView=(CalendarView)findViewById(R.id.calendar);
          Electricity=(EditText) findViewById(R.id.add_electricity);
          Cost=(EditText) findViewById(R.id.add_cost);
          Date=(EditText)findViewById(R.id.add_Date);
          calendarView.setVisibility(View.INVISIBLE);
        image_view=findViewById(R.id.imageView);
        MainActivity.mAdapter.notifyDataSetChanged();

          date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Calendar","IS visble");
                calendarView.setVisibility(VISIBLE);
            }
        });
//SET THE DATE USING CALENDAR VIEW STARTS HERE.......
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
            Log.i("Clicked date is",""+year+"/"+(month+1)+"/"+dayOfMonth);
            Date.setText(""+year+"/"+(month+1)+"/"+dayOfMonth);

            }
        });


        //UPLOAD THE IMAGE TO FIREBASE STARTS HERE......
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Check button","Clicked!!!");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Log.i("Check button","Clicked!!!");
                    int permissionCheck=AddExpense.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
                    permissionCheck+=AddExpense.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                    if(permissionCheck!=0)
                    {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    else {
                        Log.i("No need","To check permission");
                        getPhoto();
                    }
                }else {getPhoto();}
            }
        });


          button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        //    Intent intent=getIntent();
          // expenseManager=(ExpenseManager)intent.getSerializableExtra("Key1");
 expenseManager.Electricity=Electricity.getText().toString();
 expenseManager.Cost=Cost.getText().toString();
 expenseManager.Date=Date.getText().toString();


           saveUserInfo();
        }
    });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED);
            {
                getPhoto();
            }

        }
    }

    public  void getPhoto()
    {
        Toast.makeText(this,"get photo called!!",Toast.LENGTH_SHORT).show();
        Intent intent_photo=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //  intent_photo.setType("image/*");
        startActivityForResult(intent_photo,1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 && resultCode==RESULT_OK &&data!=null)
            try {
                Uri selectedImage = data.getData();
                // imageContainer.setDrawingCacheEnabled(true);
                //         Bitmap bitmap=imageContainer.getDrawingCache();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                //   imageContainer.setDrawingCacheEnabled(false);
                byte[] byteArray = baos.toByteArray();
                final String random = UUID.randomUUID().toString();
                path = "Photos2/" + random + ".png";
                reference = firebaseStorage.getReference(path);

                UploadTask uploadTask = reference.putBytes(byteArray); //this line
                uploadTask.addOnSuccessListener(AddExpense.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddExpense.this, "Photo Uploaded!!", Toast.LENGTH_SHORT).show();
                        StorageReference tempRef = FirebaseStorage.getInstance().getReference().child("Photos2").child(random + ".png");
                        Log.i("Address is", tempRef.toString());
                        expenseManager.randomUUID = random;

                        long megabyte = 1024 * 1024;
                        tempRef.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                image_view.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Log.i("Some error", "in converting the image!!!");
            }
    }


    //Saving of USER'S TEXT DATA STARTS HERE......
    public void saveUserInfo()
    {
       if(expenseManager.Electricity.length()<1 &&(expenseManager.Cost.length()<1)&&(expenseManager.Date.length()<1))
{
Toast.makeText(this,expenseManager.Electricity.toString(),Toast.LENGTH_SHORT).show();
}
 else {
           if(expenseManager.randomUUID.length()<1){
               Toast.makeText(this, "No image uploaded", Toast.LENGTH_SHORT).show();
           }
            //   Toast.makeText(this, expenseManager.Electricity.toString(), Toast.LENGTH_SHORT).show();
               MainActivity.expenses.clear();
               Toast.makeText(this, expenseManager.randomUUID, Toast.LENGTH_SHORT).show();
               conditionReference.push().setValue(expenseManager);
               MainActivity.storageCaller = true;

        }
    }
}
