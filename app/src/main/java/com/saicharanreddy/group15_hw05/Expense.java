package com.saicharanreddy.group15_hw05;
/*
               *Assignment : Home Work 5
               * File Name : Group15_HW05
               * Full Name : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
               *
 */
import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by valla on 29-03-2019.
 */
public class Expense implements Serializable  {
    String name,cost,date;
    String image;


    public Expense(String name, String cost, String date, String image) {
        this.name = name;
        this.cost = cost;
        this.date = date;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "name='" + name + '\'' +
                ", cost='" + cost + '\'' +
                ", date='" + date + '\'' +
                ", image=" + image +
                '}';
    }
    public static Comparator<Expense> CostComparator = new Comparator<Expense>() {
        @Override
        public int compare(Expense o1, Expense o2) {
            double object1 = Double.parseDouble(o1.getCost());
            double object2 = Double.parseDouble(o2.getCost());
            return Double.compare(object1,object2);
        }
    };
    public static Comparator<Expense> DateComparator = new Comparator<Expense>() {
        @Override
        public int compare(Expense o1, Expense o2) {
            try {
                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(o1.getDate());
                Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(o2.getDate());
                if (date2.before(date1)) {
                    return -1;
                } else if (date2.after(date1)) {
                    return 1;
                } else {
                    return 0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;

        }
    };


}

