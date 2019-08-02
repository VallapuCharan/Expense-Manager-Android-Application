package com.saicharanreddy.group15_hw05;
/*
               *Assignment : Home Work 5
               * File Name : Group15_HW05
               * Full Name : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
               *
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    FloatingActionButton addExpenseButton;
    DatabaseReference mRootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mCategoryref =  mRootref.child("expense");
    ArrayList<Expense> expenseList = new ArrayList<>();
    TextView totalOutput;

    double total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Expense Manager");
        totalOutput = (TextView) findViewById(R.id.totalOutput);
        addExpenseButton = (FloatingActionButton) findViewById(R.id.floatingButton);

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddExpenseActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        Collections.sort(expenseList, Expense.DateComparator);
        mAdapter = new MyAdapter(expenseList);

        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {

            @Override
            public void onEditClick(int position) {
                Expense expenseV = expenseList.get(position);
                Intent intent = new Intent(MainActivity.this,EditExpensesActivity.class);
                intent.putExtra("USER",expenseV);
                startActivity(intent);

            }

            @Override
            public void onItemClick(int position) {
                Expense expenseV = expenseList.get(position);
                Intent intent = new Intent(MainActivity.this,ViewExpenseHeading.class);
                intent.putExtra("VIEW",expenseV);
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(mAdapter);

    }
    public void removeItem(int position) {
        expenseList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sortCost)
        {
            Toast.makeText(this,"Sorted by Cost",Toast.LENGTH_LONG).show();
            Collections.sort(expenseList, Expense.CostComparator);
            mAdapter.notifyDataSetChanged();
        }
        else if(item.getItemId() == R.id.sortDate)
        {
            Toast.makeText(this,"Sorted by Date",Toast.LENGTH_LONG).show();
            Collections.sort(expenseList, Expense.DateComparator);
            mAdapter.notifyDataSetChanged();
        }
        else if(item.getItemId() == R.id.reset)
        {
            Toast.makeText(this,"Resetting",Toast.LENGTH_LONG).show();
            AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
            altdial.setMessage("Do you really want to reset all your expenses").setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCategoryref.setValue(null);
                    expenseList.clear();
                    mAdapter = new MyAdapter(expenseList);
                    recyclerView.setAdapter(mAdapter);
                    Log.d("demo","reset");
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = altdial.create();
            alert.setTitle("Reset All");
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void displayExpenses(Map<String,Object> expenses) {

        ArrayList<Expense> expenseMap = new ArrayList<>();
        expenseList.clear();
        total =0;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : expenses.entrySet()) {
            //Get user map
            Log.d("demoKey",entry.getKey());
            Map singleExpense = (Map) entry.getValue();
            String expenseNameMap = (String) singleExpense.get("name");
            String expenseCost = (String) singleExpense.get("cost");
            String expenseDate = (String) singleExpense.get("date");
            String expenseImage = (String) singleExpense.get("image");
            total = total + Double.parseDouble(expenseCost);
            //expenseMap.add(new Expense((String)singleExpense.get("expenseName"),(String)singleExpense.get("category"),(Double)singleExpense.get("amount"),(String)singleExpense.get("date")));
            expenseList.add(new Expense(expenseNameMap, expenseCost, expenseDate, expenseImage));

        }
        for (Expense list : expenseList)
        {
            Log.d("demo",list.toString());
        }
        Collections.sort(expenseList, Expense.DateComparator);
        totalOutput.setText("$"+String.valueOf(total));
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
