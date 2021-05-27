package com.example.finalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DiscarabutionAdapter extends BaseAdapter {

    Context contex;
    ArrayList<Discarabution> usersList;

    public DiscarabutionAdapter(Context c, ArrayList<Discarabution> usersList)
    {
        this.contex = c;
        this.usersList = usersList;
    }

    @Override
    public int getCount() {
        return this.usersList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View show_Discarabution = layoutInflater.inflate(R.layout.discrabution_row, parent, false);
        TextView fullname= (TextView)show_Discarabution.findViewById(R.id.datedislist);
        TextView username= (TextView)show_Discarabution.findViewById(R.id.placedislist);
        TextView starttime= (TextView)show_Discarabution.findViewById(R.id.startTimedislist);
        TextView endtime= (TextView)show_Discarabution.findViewById(R.id.endTimedislist);


        fullname.setText(usersList.get(position).getDate());
        username.setText(usersList.get(position).getPlace());
        starttime.setText(usersList.get(position).getStart());
        endtime.setText(usersList.get(position).getEnd());



        return show_Discarabution;


    }


}
