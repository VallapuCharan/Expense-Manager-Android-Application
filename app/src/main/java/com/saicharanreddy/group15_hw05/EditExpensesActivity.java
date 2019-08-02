package com.saicharanreddy.group15_hw05;
/*
               *Assignment : Home Work 5
               * File Name : Group15_HW05
               * Full Name : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
               *
 */
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditExpensesActivity extends AppCompatActivity {
    EditText name,cost;
    String key;
    TextView dateText;
    Button datePicker, imagePicker, saveChanges;
    ImageView image;
    final Calendar myCalendar = Calendar.getInstance();
    String dateInput = null;
    private static final int IMAGE_DATE_PICKER =1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Uri imageUri;
    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mCategoryref =  mRootref.child("expense");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String fileURL = new String();
    Expense expense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenses);
        setTitle("Expense Manager");
        expense = (Expense) getIntent().getExtras().getSerializable("USER");
        name = (EditText) findViewById(R.id.editExpenseName);
        cost = (EditText) findViewById(R.id.editExpenseCost);
        dateText = (TextView) findViewById(R.id.editExpenseDate);
        datePicker = (Button) findViewById(R.id.editExpenseDatePicker);
        imagePicker = (Button) findViewById(R.id.editExpenseImagePicker);
        saveChanges = (Button) findViewById(R.id.saveChanges);
        image = (ImageView) findViewById(R.id.editImageView);
        name.setText(expense.getName());
        cost.setText(expense.getCost());
        if(!expense.getImage().equals("")){

            Picasso.with(EditExpensesActivity.this).load(expense.getImage()).into(image);
        }
        dateText.setText(expense.getDate());
        dateInput = expense.getDate().toString();
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditExpensesActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(EditExpensesActivity.this)) {
                    openFileChooser();
                }
            }
        }) ;
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                Log.d("demoKey2",key);
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String year1 = String.valueOf(myCalendar.get(Calendar.YEAR));
            String month = String.valueOf(myCalendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(myCalendar.get(Calendar.DAY_OF_MONTH));
            dateInput = day +"/" + month + "/" +year1;
            Log.d("fff",dateInput);
            TextView date1 = (TextView) findViewById(R.id.editExpenseDate);
            date1.setText(dateInput);
        }

    };
    public void openFileChooser(){
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
                captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

// Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                "Select Image from");

// Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, IMAGE_DATE_PICKER);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_DATE_PICKER && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri = data.getData();
            Log.d("d",imageUri.toString());
            ImageView iv = (ImageView) findViewById(R.id.editImageView);
            iv.setImageURI(imageUri);
        }
    }
    private void uploadImage() {

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageRef.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditExpensesActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // getting image uri and converting into string
                                    Uri downloadUrl = uri;
                                    fileURL = downloadUrl.toString();
                                    Log.d("demoURL",fileURL);
                                    String expenseNameInput,expenseCostInput;
                                    expenseNameInput = name.getText().toString();
                                    expenseCostInput = cost.getText().toString();
                                    //pause();
                                    mCategoryref.child(key).setValue(new Expense(expenseNameInput,expenseCostInput,dateInput,fileURL));
                                    Intent intent = new Intent(EditExpensesActivity.this,MainActivity.class);
                                    startActivity(intent);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditExpensesActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else
        {
            String expenseNameInput,expenseCostInput;
            expenseNameInput = name.getText().toString();
            expenseCostInput = cost.getText().toString();
            //pause();
            mCategoryref.child(key).setValue(new Expense(expenseNameInput,expenseCostInput,dateInput,expense.getImage()));
            Intent intent = new Intent(EditExpensesActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    private void displayExpenses(Map<String,Object> expenses) {

        ArrayList<Expense> expenseMap = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : expenses.entrySet()) {
            //Get user map
            Map singleExpense = (Map) entry.getValue();
            String expenseNameMap = (String) singleExpense.get("name");
            String expenseCost = (String) singleExpense.get("cost");
            String expenseDate = (String) singleExpense.get("date");
            String expenseImage = (String) singleExpense.get("image");
            if(expenseNameMap.equals(name.getText().toString()))
            {
                key = entry.getKey();
                break;
            }

        }
        Log.d("demo",expenseMap.toString());
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("demo","start");
        final List<Expense> tDlist = new ArrayList<>();

        mCategoryref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    displayExpenses((Map<String, Object>) dataSnapshot.getValue());
                    Log.d("demo", tDlist.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("demo","The read failed: " + databaseError.getCode());
            }
        });

    }
}
