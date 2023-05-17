package com.example.billit_all;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import java.util.ArrayList;

public class NotifAdapterLandlord extends RecyclerView.Adapter<NotifAdapterLandlord.myViewHolder>{

    private Context mContext;
    protected ArrayList<NotifLandlordModel> tenants;

    public NotifAdapterLandlord(Context context, ArrayList<NotifLandlordModel> tenants){
        mContext = context;
        this.tenants=tenants;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_landlord,parent,false);
        return new myViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
        myViewHolder.idTxt.setText(tenants.get(position).getId());
//        myViewHolder.nameTxt.setText(tenants.get(position).getType());
        myViewHolder.dateTxt.setText(tenants.get(position).getDate());
//        myViewHolder.isVerifiedId.setText(tenants.get(position).getVerifyId());


        String Type = tenants.get(position).getType();
        String typeofid = tenants.get(position).getTypeOfId();
        String isSeen = tenants.get(position).getIsSeen();

        if(Type.equals("verification")){
            myViewHolder.nameTxt.setText("Account Verification");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }

            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.verifyId.setVisibility(View.VISIBLE);
            myViewHolder.paymentId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("approval")){
            myViewHolder.nameTxt.setText("Approval");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }

            myViewHolder.approveId.setVisibility(View.VISIBLE);
            myViewHolder.verifyId.setVisibility(View.INVISIBLE);
            myViewHolder.paymentId.setVisibility(View.INVISIBLE);
        }
        else if(Type.equals("payment")){
            myViewHolder.nameTxt.setText("Bill Payment");

            if (isSeen.equals("0")){
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.notSeen));
            } else {
                myViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }

            myViewHolder.paymentId.setVisibility(View.VISIBLE);
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.verifyId.setVisibility(View.INVISIBLE);
        }
        else {
            myViewHolder.approveId.setVisibility(View.INVISIBLE);
            myViewHolder.verifyId.setVisibility(View.INVISIBLE);
            myViewHolder.elecId.setVisibility(View.INVISIBLE);
            myViewHolder.paymentId.setVisibility(View.INVISIBLE);
        }

//        String profileImageUrl = tenants.get(position).getProfileImage();
//        Log.d("PROFILE_IMAGE_URL", "Profile Image URL for position " + position + " is: " + profileImageUrl);
//
//        Glide.with(myViewHolder.itemView.getContext())
//                .load(profileImageUrl)
//                .into(myViewHolder.profileImage);

    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }



    public class myViewHolder extends RecyclerView.ViewHolder {
        Button editbtn;
        ImageView profileImage;
        TextView nameTxt,idTxt, isVerifiedId, verifyId, approveId, dateTxt, elecId, waterId, rentId, paymentId;
        CardView cardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardViewId);
            paymentId = itemView.findViewById(R.id.paymentId);
            approveId = itemView.findViewById(R.id.approveId);
            verifyId = itemView.findViewById(R.id.verifyId);
            isVerifiedId = itemView.findViewById(R.id.isVerifiedId);
//            profileImage = itemView.findViewById(R.id.profileImg);
            idTxt = itemView.findViewById(R.id.tenantId);
            nameTxt = itemView.findViewById(R.id.tenantName);
            dateTxt = itemView.findViewById(R.id.dateId);
            elecId = itemView.findViewById(R.id.elecId);
            waterId = itemView.findViewById(R.id.waterId);
            rentId = itemView.findViewById(R.id.rentId);


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
                        intent = new Intent(itemView.getContext(), AccountApprovedLandlord.class);
                    } else if (nameTxt.getText().equals("Account Verification")) {
                        intent = new Intent(itemView.getContext(), NotifInfo.class);
                    }
                    else {
                        intent = new Intent(itemView.getContext(), NotifBillPayment.class);
                    }

                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}