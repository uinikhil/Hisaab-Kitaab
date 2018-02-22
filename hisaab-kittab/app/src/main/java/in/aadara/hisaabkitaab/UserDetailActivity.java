package in.aadara.hisaabkitaab;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.aadara.hisaabkitaab.localDB.Update;
import in.aadara.hisaabkitaab.localDB.User;
import pub.devrel.easypermissions.EasyPermissions;

public class UserDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RewardedVideoAd mRewardedVideoAd;
    protected static Query sUserQuery = null;
    protected static Query sCustomersQuery = null;
    RecyclerView.Adapter adapter = null;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.customerList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(getIntent() != null){
            user.setAmount(getIntent().getStringExtra("amount"));
            user.setMobile(getIntent().getStringExtra("mobile"));
            user.setUid(getIntent().getStringExtra("uid"));
            user.setAddress(getIntent().getStringExtra("address"));
            user.setDate(getIntent().getStringExtra("date"));
            user.setName(getIntent().getStringExtra("name"));
            user.setRemark(getIntent().getStringExtra("remark"));
        }
        adaptera = new UserDetailAdapter();
        recyclerView.setAdapter(adaptera);
        setTitle(user.getName());
    }
    UserDetailAdapter adaptera;
    private void initUserDB(){
        sUserQuery = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("address").equalTo(user.getAddress());
        sUserQuery.keepSynced(true);
        sUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        User user1 = issue.getValue(User.class);
                        if (user1 != null) {
                            user.setUpdate(user1.getUpdate());
                            adaptera.setList(user1.getUpdate());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUserDB();
        initCustomers();
    }

    private void initCustomers() {
        sCustomersQuery = FirebaseDatabase.getInstance().getReference()
                .child("customers");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView customer_amount;
        TextView remark_txt;
        TextView last_date;

        public ViewHolder(View view) {
            super(view);
            customer_amount = view.findViewById(R.id.customer_amount);
            last_date = view.findViewById(R.id.date);
            remark_txt = view.findViewById(R.id.remark_txt);
        }
    }

    class UserDetailAdapter extends RecyclerView.Adapter<ViewHolder>{

        private List<Update> mUpdateList;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_detail_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(mUpdateList != null) {
                if(mUpdateList.get(position).getType().equalsIgnoreCase("added:")){
                    holder.customer_amount.setTextColor(getResources().getColor(R.color.material_red_a200));
                } else {
                    holder.customer_amount.setTextColor(getResources().getColor(R.color.persian_green_dark));
                }
                holder.customer_amount.setText(mUpdateList.get(position).getType() + " ₹" + mUpdateList.get(position).getAmount());
                holder.last_date.setText("Date: " + mUpdateList.get(position).getDate());
                holder.remark_txt.setText(mUpdateList.get(position).getRemark());
            }
        }

        public void setList(List<Update> updateList){
            mUpdateList = updateList;
            notifyDataSetChanged();
        }

        public void addListData(Update update){
            if(mUpdateList == null)
                mUpdateList = new ArrayList<>();
            mUpdateList.add(update);
            notifyItemChanged(0);
        }

        @Override
        public int getItemCount() {
            return mUpdateList != null ? mUpdateList.size() : 0;
        }
    }

    private void updateAmount(final User user){
        sUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {
                            List<Update> list = user1.getUpdate();
                            Update update = new Update();
                            update.setDate(user.getDate());
                            update.setAmount(Double.parseDouble(user.getAmount()));
                            update.setRemark(user.getRemark());
                            update.setType("Added:");
                            list.add(update);

                            double amount = Double.parseDouble(user1.getAmount());
                            String finalAmount = String.valueOf(amount + Double.parseDouble(user.getAmount()));

                            fruitSnapshot.getRef().child("amount").setValue(finalAmount);
                            updateMenuTitles(Double.parseDouble(finalAmount));
                            fruitSnapshot.getRef().child("update").setValue(list);
                            if(adaptera != null){
                                adaptera.addListData(update);
                            }
                        }
                    }

                }
                //sUserQuery.getRef().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateMenuTitles(double amount) {
        if(menu != null) {
            MenuItem bedMenuItem = menu.findItem(R.id.amount);
            bedMenuItem.setTitle("₹ " + amount);
        }
    }
    private void payAmount(final User user){
        sUserQuery.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {

                            List<Update> list = user1.getUpdate();
                            Update update = new Update();
                            update.setDate(user.getDate());
                            update.setRemark(user.getRemark());
                            update.setAmount(Double.parseDouble(user.getAmount()));
                            update.setType("Paid:");
                            list.add(update);

                            double amount = Double.parseDouble(user1.getAmount());
                            double dif = amount - Double.parseDouble(user.getAmount());
                            if(dif <= 0){
                                dif = 0;
                            }
                            String finalAmount = String.valueOf(dif);

                            fruitSnapshot.getRef().child("amount").setValue(finalAmount);
                            updateMenuTitles(Double.parseDouble(finalAmount));
                            fruitSnapshot.getRef().child("update").setValue(list);
                            if(adaptera != null){
                                adaptera.addListData(update);
                            }
                        }
                    }
                }
                //sUserQuery.getRef().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_detail_menu, menu);
        this.menu = menu;
        if(user != null)
            updateMenuTitles(Double.parseDouble(user.getAmount()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_amount_menu:
                showAmountBox(user);
                return true;
            case R.id.pay_now_menu:
                showPayAmountBox(user);
                return true;
            case R.id.delete_user:
                deleteUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteUser(){
        sUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {
                            fruitSnapshot.getRef().removeValue();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        sCustomersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {
                            fruitSnapshot.getRef().removeValue();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }

    protected void onUpdateCustomersData(final User user) {
        sCustomersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user.getAddress().equalsIgnoreCase(user1.getAddress()) && user.getName().equalsIgnoreCase(user1.getName())){
                        List<Update> list = user1.getUpdate();
                        Update update = new Update();
                        update.setDate(user.getDate());
                        update.setAmount(Double.parseDouble(user.getAmount()));
                        update.setType("Added:");
                        update.setRemark(user.getRemark());
                        list.add(update);

                        double amount = Double.parseDouble(user1.getAmount());
                        String finalAmount = String.valueOf(amount + Double.parseDouble(user.getAmount()));

                        fruitSnapshot.getRef().child("amount").setValue(finalAmount);
                        fruitSnapshot.getRef().child("update").setValue(list);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onPayCustomersData(final User user) {
        sCustomersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if(user.getAddress().equalsIgnoreCase(user1.getAddress()) && user.getName().equalsIgnoreCase(user1.getName())){
                        List<Update> list = user1.getUpdate();
                        Update update = new Update();
                        update.setDate(user.getDate());
                        update.setAmount(Double.parseDouble(user.getAmount()));
                        update.setType("Added:");
                        update.setRemark(user.getRemark());
                        list.add(update);

                        double amount = Double.parseDouble(user1.getAmount());
                        String finalAmount = String.valueOf(amount - Double.parseDouble(user.getAmount()));

                        fruitSnapshot.getRef().child("amount").setValue(finalAmount);
                        fruitSnapshot.getRef().child("update").setValue(list);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showPayAmountBox(final User user) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.pay_amount)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogs) {

                final EditText amount = dialog.findViewById(R.id.enter_amount);

                final EditText user_remark = dialog.findViewById(R.id.user_remark);

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(amount.getText().toString().trim())) {
                            if(!TextUtils.isEmpty(user_remark.getText().toString().trim())){
                                user.setAmount(amount.getText().toString());
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                                user.setDate(date);
                                user.setRemark(user_remark.getText().toString());
                                payAmount(user);
                                onPayCustomersData(user);
                                dialog.dismiss();
                            } else {
                                user_remark.setError(getString(R.string.validate_remark));
                                user_remark.requestFocus();
                            }
                        } else {
                            amount.setError(getString(R.string.valid_amount));
                            amount.requestFocus();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void showAmountBox(final User user) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.add_amount)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogs) {

                final EditText amount = dialog.findViewById(R.id.enter_amount);

                final EditText user_remark = dialog.findViewById(R.id.user_remark);

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(amount.getText().toString().trim())) {
                            if (!TextUtils.isEmpty(user_remark.getText().toString().trim())) {
                                user.setAmount(amount.getText().toString());
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                                user.setDate(date);
                                user.setRemark(user_remark.getText().toString());
                                updateAmount(user);
                                onUpdateCustomersData(user);
                                dialog.dismiss();
                            } else {
                                user_remark.setError(getString(R.string.validate_remark));
                                user_remark.requestFocus();
                            }
                        } else {
                            amount.setError(getString(R.string.valid_amount));
                            amount.requestFocus();
                        }
                    }
                });
            }
        });
        dialog.show();
    }



}
