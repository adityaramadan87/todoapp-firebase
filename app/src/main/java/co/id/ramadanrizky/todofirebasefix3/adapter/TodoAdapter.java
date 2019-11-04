package co.id.ramadanrizky.todofirebasefix3.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.id.ramadanrizky.todofirebasefix3.DetailTaskActivity;
import co.id.ramadanrizky.todofirebasefix3.R;
import co.id.ramadanrizky.todofirebasefix3.pojo.TodoItems;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    ArrayList<TodoItems> todoItemsList;
    Context context;

    public TodoAdapter(Context context, ArrayList<TodoItems> todoItemsList) {
        this.context = context;
        this.todoItemsList = todoItemsList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        TodoViewHolder todoViewHolder = new TodoViewHolder(v);
        return todoViewHolder;
    }

//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        RecyclerView.ViewHolder viewHolder = null;
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        switch (viewType){
//            case 1:{
//                View v = inflater.inflate(R.layout.task_item, parent, false);
//                viewHolder = new TodoViewHolder(v);
//                break;
//            }
//            case 2:{
//                View v = inflater.inflate(R.layout.item_ads, parent, false);
//                viewHolder = new AdsViewHolder(v);
//                break;
//            }
//        }
//
//        return viewHolder;
//    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
//        switch (getItem(position).getViewType()){
//            case 1:{
//                TodoViewHolder viewHolder = (TodoViewHolder) holder;
////                TodoItems items = todoItemsList.get(position);
//                viewHolder.txt_task.setText(getItem(position).getTask());
//                viewHolder.txt_date.setText(getItem(position).getDate());
//
//                if (getItem(position).getCategory().toString().equals("Important")){
//                    viewHolder.img_category.setImageResource(R.drawable.important);
//                }else if (getItem(position).getCategory().toString().equals("Normal")){
//                    viewHolder.img_category.setImageResource(R.drawable.notimportant);
//                }
//
//                viewHolder.btn_clear.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        onDeleteTask(position);
//                    }
//                });
//                break;
//            }
//        }
//    }



    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, final int position) {

        TodoItems items = todoItemsList.get(position);
        holder.txt_task.setText(items.getTask());
        holder.txt_date.setText(items.getDate());

        if (items.getCategory().toString().equals("Important")){
            holder.img_category.setImageResource(R.drawable.important);
        }else if (items.getCategory().toString().equals("Normal")){
            holder.img_category.setImageResource(R.drawable.notimportant);
        }

        holder.btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteTask(position);
            }
        });

    }

    private void onDeleteTask(final int position) {
        final TodoItems items = todoItemsList.get(position);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("users").child(user.getUid()).child("todoList")
                .child(items.getKey())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Data deleted :)", Toast.LENGTH_SHORT).show();
                        todoItemsList.remove(position);
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return (todoItemsList != null) ? todoItemsList.size():0;
    }
//    private TodoItems getItem(int position) {
//        return todoItemsList.get(position);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return todoItemsList.get(position).getViewType();
//    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView txt_task, txt_date, txt_category;
        public int position;
        ImageView img_category;
        Button btn_clear;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_task = itemView.findViewById(R.id.txt_task);
            txt_date = itemView.findViewById(R.id.txt_date);
            img_category = itemView.findViewById(R.id.img_important);
            btn_clear = itemView.findViewById(R.id.btn_clear);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, DetailTaskActivity.class);
                    i.putExtra("todo", todoItemsList.get(getPosition()));
                    context.startActivity(i);

                }
            });

        }
    }

//    private class AdsViewHolder extends RecyclerView.ViewHolder {
//        public AdView adViewBanner;
//        public AdsViewHolder(View v) {
//            super(v);
//            adViewBanner = (AdView) v.findViewById(R.id.adView);
//            adViewBanner.setVisibility(View.GONE);
//            adViewBanner.setAdUnitId("ca-app-pub-3940256099942544~6300978111");
//            adViewBanner.setAdSize(AdSize.SMART_BANNER);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            if (adRequest != null && v !=null){
//                adViewBanner.loadAd(adRequest);
//            }
//
//            adViewBanner.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    super.onAdLoaded();
//                    adViewBanner.setVisibility(View.VISIBLE);
//                }
//            });
//        }
//    }
}
