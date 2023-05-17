//package com.example.billit_all;
//
//import androidx.annotation.NonNull;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//
//public class TenantAdapterFirebase extends FirebaseRecyclerAdapter<Tenant, TenantAdapterFirebase.myViewHolder> {
//
//    public TenantAdapterFirebase(@NonNull FirebaseRecyclerOptions<Tenant> options){
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i, @NonNull Tenant tenant) {
////        myViewHolder.housetxt.setText(tenant.getHouseTenant());
//        myViewHolder.nametxt.setText(tenant.getNameTenant());
//        myViewHolder.emailtxt.setText(tenant.getEmailTenant());
//
////        Glide.with(myViewHolder.tenantImg.getContext().load)
//
//    }
//
//    @NonNull
//    @Override
//    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_list,parent,false);
//
//        return new myViewHolder(view);
//    }
//
//    public class myViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView tenantImg;
//        TextView housetxt,nametxt, emailtxt;
//
//        public myViewHolder(@NonNull View itemView) {
//            super(itemView);
//
////            tenantImg = itemView.findViewById(R.id.tenantImg);
//            housetxt = itemView.findViewById(R.id.tenantNum_id);
//            nametxt = itemView.findViewById(R.id.name_id);
//            emailtxt = itemView.findViewById(R.id.email_id);
//
//
//        }
//    }
//}
