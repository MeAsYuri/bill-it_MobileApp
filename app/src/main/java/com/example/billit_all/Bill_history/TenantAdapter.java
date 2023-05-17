package com.example.billit_all.Bill_history;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billit_all.R;

import java.util.ArrayList;

public class TenantAdapter extends RecyclerView.Adapter<TenantAdapter.myViewHolder> {

    protected ArrayList<Tenant> tenants;
    public TenantAdapter (ArrayList<Tenant> tenants){
        this.tenants=tenants;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_list,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
//        myViewHolder.date.setText(String.valueOf(tenants.get(position).getDate()));
        myViewHolder.elec_due_date.setText(String.valueOf(tenants.get(position).getElec_due_date()));
        myViewHolder.water_due_date.setText(String.valueOf(tenants.get(position).getWater_due_date()));
        myViewHolder.water_amount.setText(String.valueOf(tenants.get(position).getWater_amount()));
        myViewHolder.water_paid.setText(String.valueOf(tenants.get(position).getWater_paid()));
        myViewHolder.elec_amount.setText(String.valueOf(tenants.get(position).getElec_amount()));
        myViewHolder.elec_paid.setText(String.valueOf(tenants.get(position).getElec_paid()));
        myViewHolder.rent_paid.setText(String.valueOf(tenants.get(position).getRent_paid()));

        if (myViewHolder.water_paid.getText().toString() == "PAID"){

        } else if (myViewHolder.water_paid.getText().toString() == "UNPAID") {
            myViewHolder.water_paid.setTextColor(Color.RED);
        }

        if (myViewHolder.elec_paid.getText().toString() == "PAID"){

        } else if (myViewHolder.elec_paid.getText().toString() == "UNPAID") {
            myViewHolder.elec_paid.setTextColor(Color.RED);
        }

        if (myViewHolder.rent_paid.getText().toString() == "PAID"){

        } else if (myViewHolder.rent_paid.getText().toString() == "UNPAID") {
            myViewHolder.rent_paid.setTextColor(Color.RED);
        }




        myViewHolder.water_consump.setText(String.valueOf(tenants.get(position).getWater_consump()));
        myViewHolder.elec_consump.setText(String.valueOf(tenants.get(position).getElec_consump()));
        myViewHolder.water_unitConsump.setText(String.valueOf(tenants.get(position).getWater_unitConsump()));
        myViewHolder.elec_unitConsump.setText(String.valueOf(tenants.get(position).getElec_unitConsump()));
        myViewHolder.water_unitAmount.setText(String.valueOf(tenants.get(position).getWater_unitAmount()));
        myViewHolder.elec_unitAmount.setText(String.valueOf(tenants.get(position).getElec_unitAmount()));



//        myViewHolder.elec_consump.setText(String.valueOf(tenants.get(position).getElec_consump()));
//        myViewHolder.water_date.setText((CharSequence) tenants.get(position).getWater_date());
//        myViewHolder.elec_date.setText((CharSequence) tenants.get(position).getElec_date());
//        myViewHolder.rent_date.setText((CharSequence) tenants.get(position).getRent_date());
//        myViewHolder.maynilad_id.setText(String.valueOf(tenants.get(position).getMaynilad_id()));
//        myViewHolder.meralco_id.setText(String.valueOf(tenants.get(position).getMeralco_id()));
//        myViewHolder.rent_user_id.setText(String.valueOf(tenants.get(position).getRent_user_id()));


//        myViewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(myViewHolder.editbtn.getContext(),"work in progress",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }



    public class myViewHolder extends RecyclerView.ViewHolder {

//        ImageView tenantImg;
        TextView water_amount, water_paid, elec_amount, elec_paid,rent_paid;
        TextView elec_due_date, water_due_date, water_consump, elec_consump, water_unitConsump, elec_unitConsump;
        Button payBtn;
        TextView water_unitAmount, elec_unitAmount;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

//            tenantImg = itemView.findViewById(R.id.tenantImg);
            water_amount = itemView.findViewById(R.id.water_amount);
            water_paid = itemView.findViewById(R.id.water_paid);
            elec_amount = itemView.findViewById(R.id.elec_amount);
            elec_paid = itemView.findViewById(R.id.elec_paid);
            rent_paid = itemView.findViewById(R.id.rent_paid);
//            date = itemView.findViewById(R.id.date);
            elec_due_date = itemView.findViewById(R.id.elec_due_date);
            water_due_date = itemView.findViewById(R.id.water_due_date);


            water_consump = itemView.findViewById(R.id.water_consump);
            elec_consump = itemView.findViewById(R.id.elec_consump);

            water_unitConsump = itemView.findViewById(R.id.water_unitConsump);
            elec_unitConsump = itemView.findViewById(R.id.elec_unitConsump);

            water_unitAmount = itemView.findViewById(R.id.water_unitAmount);
            elec_unitAmount = itemView.findViewById(R.id.elec_unitAmount);


//            elec_consump = itemView.findViewById(R.id.elec_consump);
//            water_date = itemView.findViewById(R.id.water_date);
//            elec_date = itemView.findViewById(R.id.elec_date);
//            rent_date = itemView.findViewById(R.id.rent_date);
//            maynilad_id = itemView.findViewById(R.id.maynilad_id);
//            meralco_id = itemView.findViewById(R.id.meralco_id);
//            rent_user_id = itemView.findViewById(R.id.rent_user_id);

        }


    }
}
