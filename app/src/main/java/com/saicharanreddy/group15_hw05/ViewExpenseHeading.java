package com.saicharanreddy.group15_hw05;
/*
               *Assignment : Home Work 5
               * File Name : Group15_HW05
               * Full Name : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
               *
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewExpenseHeading extends AppCompatActivity {
    TextView name,cost,date;
    ImageView image;
    Button finish;
    Expense expense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense_heading);
        setTitle("Expense Manager");
        expense = (Expense) getIntent().getExtras().getSerializable("VIEW");
        name = (TextView) findViewById(R.id.viewExpenseName);
        cost = (TextView) findViewById(R.id.viewExpenseCost);
        date = (TextView) findViewById(R.id.viewExpenseDate);
        ImageView image= (ImageView) findViewById(R.id.displayImage);
        if(!expense.getImage().equals("")){

            Picasso.with(ViewExpenseHeading.this).load(expense.getImage()).into(image);
        }
        finish = (Button) findViewById(R.id.finishButton);
        name.setText(expense.getName());
        cost.setText(expense.getCost());
        date.setText(expense.getDate());
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewExpenseHeading.this,MainActivity.class);
                startActivity(intent);

            }
        });

    }
}
