package com.example.billit_all;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import java.util.ArrayList;

public class NotifAdapterTenant extends RecyclerView.Adapter<NotifAdapterTenant.myViewHolder>{

    private Context mContext;
    protected ArrayList<NotifTenantModel> tenants;

    public NotifAdapterTenant(Context context, ArrayList<NotifTenantModel> tenants){
        mContext = context;
        this.tenants=tenants;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notif_adapter_tenant,parent,false);
        return new myViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
        myViewHolder.idTxt.setText(tenants.get(position).getId());
//            myViewHolder.nameTxt.setText(tenants.get(position).getType());
        myViewHolder.dateTxt.setText(tenants.get(position).getDate());

        String Type = tenants.get(position).getType();
        String typeofid = tenants.get(position).getTypeOfId();
        String isSeen = tenants.get(position).getIsSeen();

        if(Type.equals("approval")){
            myViewHolder.nameTxt.setText("Approval");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.approveId.setVisibility(View.VISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
            myViewHolder.payUpdateId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("bill statement") && typeofid.equals("water_id")){
            myViewHolder.nameTxt.setText("Bill Statement");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.waterId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
            myViewHolder.payUpdateId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("bill statement") && typeofid.equals("electricity_id")){
            myViewHolder.nameTxt.setText("Bill Statement");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.elecId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
            myViewHolder.payUpdateId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("bill statement") && typeofid.equals("rent_unit_id")){
            myViewHolder.nameTxt.setText("Bill Statement");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.rentId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.payUpdateId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("payment update") && typeofid.equals("gcash_id")){
            myViewHolder.nameTxt.setText("Payment Review");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("reminder") && typeofid.equals("electricity_id")){
            myViewHolder.nameTxt.setText("Reminder");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your bill is due on 3 days from now.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("reminder") && typeofid.equals("water_id")){
            myViewHolder.nameTxt.setText("Reminder");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your bill is due on 3 days from now.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("reminder") && typeofid.equals("rent_unit_id")){
            myViewHolder.nameTxt.setText("Reminder");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your bill is due on 3 days from now.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("penalty") && typeofid.equals("electricity_id")){
            myViewHolder.nameTxt.setText("Penalty");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("You have failed to pay your bill on time.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("penalty") && typeofid.equals("water_id")){
            myViewHolder.nameTxt.setText("Penalty");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("You have failed to pay your bill on time.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("penalty") && typeofid.equals("rent_unit_id")){
            myViewHolder.nameTxt.setText("Penalty");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("You have failed to pay your bill on time.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("cash payment") && typeofid.equals("cash_payment_elec")){
            myViewHolder.nameTxt.setText("Cash Payment");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your payment in cash has been recorded.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("cash payment") && typeofid.equals("cash_payment_water")){
            myViewHolder.nameTxt.setText("Cash Payment");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your payment in cash has been recorded.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }else if(Type.equals("cash payment") && typeofid.equals("cash_payment_rent")){
            myViewHolder.nameTxt.setText("Cash Payment");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            myViewHolder.payUpdateId.setText("Your payment in cash has been recorded.");
            myViewHolder.payUpdateId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
        }
        else {
            myViewHolder.nameTxt.setVisibility(View.INVISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.waterId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.rentId.setVisibility(View.INVISIBLE);
            myViewHolder.payUpdateId.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }



    public class myViewHolder extends RecyclerView.ViewHolder {
        Button editbtn;
        ImageView profileImage;
        TextView nameTxt,idTxt, isVerifiedId, waterId, elecId, approveId, dateTxt, rentId, payUpdateId;
        CardView cardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardViewId);
            payUpdateId = itemView.findViewById(R.id.payUpdateId);
            rentId = itemView.findViewById(R.id.rentId);
            approveId = itemView.findViewById(R.id.approveId);
            waterId = itemView.findViewById(R.id.waterId);
            elecId = itemView.findViewById(R.id.elecId);
            isVerifiedId = itemView.findViewById(R.id.isVerifiedId);
            //            profileImage = itemView.findViewById(R.id.profileImg);
            idTxt = itemView.findViewById(R.id.tenantId);
            nameTxt = itemView.findViewById(R.id.tenantName);
            dateTxt = itemView.findViewById(R.id.dateId);


            // Add OnClickListener to the CardView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle click event here
                    // For example, you can open a new activity or show a dialog
                    // based on the clicked item
                    String tenantId = idTxt.getText().toString();

                    UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(itemView.getContext());
                    userSharedPreferenceDataSource.storeBackendId(Integer.parseInt(tenantId));

                    // Show a Toast message to confirm that the ID was saved
                    //                    Toast.makeText(itemView.getContext(), "Tenant ID: " + tenantId + " was saved.", Toast.LENGTH_SHORT).show();

                    Intent intent;

                    if (nameTxt.getText().equals("Approval")) {
                        intent = new Intent(itemView.getContext(), AccountApprovedTenant.class);
                    } else if (nameTxt.getText().equals("Bill Statement")) {
                        intent = new Intent(itemView.getContext(), NotifBillStatement.class);
                    }else if (nameTxt.getText().equals("Reminder")) {
                        intent = new Intent(itemView.getContext(), NotifReminder.class);
                    }else if (nameTxt.getText().equals("Penalty")) {
                        intent = new Intent(itemView.getContext(), NotifPenalty.class);
                    }else if (nameTxt.getText().equals("Cash Payment")) {
                        intent = new Intent(itemView.getContext(), NotifCashPay.class);
                    }
                    else {
                        intent = new Intent(itemView.getContext(), NotifPaymentReview.class);

                    }

                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}