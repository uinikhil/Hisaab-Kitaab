package in.aadara.hisaabkitaab;

import android.Manifest;
import android.app.assist.AssistContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import in.aadara.hisaabkitaab.localDB.RewardModel;
import in.aadara.hisaabkitaab.localDB.Update;
import in.aadara.hisaabkitaab.localDB.User;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.android.gms.ads.AdRequest;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {//implements FirebaseAuth.AuthStateListener
    private static final int RC_SIGN_IN = 123;
    private int user_after_charge = 10;
    private int first_time_user_capacity = 5;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String user_exist = "user_exist";
    private String UID = "";
    private RecyclerView recyclerView;
    private TextView mEmptyListMessage;
    protected static Query sUserQuery = null;
    protected static Query sAllUserQuery = null;
    protected static Query sKeysQuery = null;
    protected static Query sRewardsQuery = null;
    protected static Query sCustomersQuery = null;
    protected static Query sUserLocationQuery = null;
    private static final int RC_CHOOSE_PHOTO = 101;
    private static final int RC_CAMERA = 103;
    private static final int RC_IMAGE_PERMS = 102;
    private static final int RC_CAM_IMAGE_PERMS = 104;
    private static final String PERMS = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String CAMPREF = Manifest.permission.CAMERA;
    private FusedLocationProviderClient mFusedLocationClient;
    private String salt;
    private String mid;
    private String key;
    private Location currentLocation;
    private AdView mAdView;

    static List<User> mUserList = new ArrayList<>();
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.customerList);
        mEmptyListMessage = findViewById(R.id.emptyTextView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        setTitle("");
        MobileAds.initialize(this,
                "ca-app-pub-9109508432817685~7441236906");
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private boolean isSignedIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            sAllUserQuery = FirebaseDatabase.getInstance().getReference()
                    .child("users");

            sAllUserQuery.keepSynced(true);

            sUserQuery = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderByChild("date");
            sUserQuery.keepSynced(true);

            sRewardsQuery = FirebaseDatabase.getInstance().getReference()
                    .child("rewards")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            sRewardsQuery.keepSynced(true);

            sKeysQuery = FirebaseDatabase.getInstance().getReference()
                    .child("keys");

            sCustomersQuery = FirebaseDatabase.getInstance().getReference()
                    .child("customers");

            sCustomersQuery.keepSynced(true);

            if (currentLocation != null) {
                FirebaseDatabase.getInstance().getReference()
                        .child("userLocations")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(currentLocation);
            }

            getKeys();
            getRewards();
            getCustomers();
            // getAllUsers();
        }
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    private void getCustomers() {
        sCustomersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    final User user = fruitSnapshot.getValue(User.class);
                    if (!TextUtils.isEmpty(user.getAddress()) && user.getAddress().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        user.setShared_to_uid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        onAddMessage(user);
                        fruitSnapshot.getRef().removeValue();
                        //FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());

                        sAllUserQuery.getRef().child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    User user2 = dataSnapshot1.getValue(User.class);
                                    if (user2.getAddress().equalsIgnoreCase(user.getAddress())) {
                                        dataSnapshot1.getRef().child("shared_to_uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    FirebaseRecyclerAdapter<User, ViewHolder> adapter = null;

    private void attachRecyclerViewAdapter() {
        adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        if(mFirebaseAnalytics != null)
            mFirebaseAnalytics.setCurrentScreen(this,"Main Activty",new Date().toString());
    }

    public Action getAction(String key, String value) {
        return Actions.newView(key, value);
    }

    private void indexRecipe(User user) {
        Indexable recipeToIndex = new Indexable.Builder()
                .setName(user.getName())
                .setUrl("https://aadara.in")
                .setDescription(user.getAddress())
                .build();

        FirebaseAppIndex.getInstance().update(recipeToIndex);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        //FirebaseUserActions.getInstance().end();
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
            if (mFirebaseAnalytics != null)
                mFirebaseAnalytics.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            Toast.makeText(this, R.string.signing_in, Toast.LENGTH_SHORT).show();
            showPopup();
        }
    }


    protected FirebaseRecyclerAdapter<User, ViewHolder> newAdapter() {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(sUserQuery, User.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.customer_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull User model) {
                int resImageID = getResources().getIdentifier(model.getName().toLowerCase().charAt(0) + "_letter",
                        "drawable", getPackageName());
                if (resImageID != 0) {
                    holder.letter_img.setBackgroundResource(resImageID);
                } else {
                    holder.letter_img.setBackgroundResource(R.drawable.a_letter);
                }
                if (model.getShared() && !TextUtils.isEmpty(model.getShared_to_uid())
                        && !TextUtils.isEmpty(model.getShared_to_uid())) {
                    if (model.getShared_by_email().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        holder.shared_by_text.setVisibility(View.VISIBLE);
                        holder.shared_by_text.setText("shared");
                    } else {
                        holder.shared_by_text.setVisibility(View.VISIBLE);
                        holder.shared_by_name.setText(model.getShared_by_name());
                    }

                } else {
                    holder.shared_by_text.setVisibility(View.GONE);
                }

                holder.customer_name.setText(model.getName());
                holder.customer_address.setText(model.getAddress());
                holder.customer_amount.setText("Balance: ₹" + model.getAmount());
                holder.last_date.setText("last updated: " + model.getDate());
                holder.add_money.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final User user = getItem(holder.getAdapterPosition());
                        showAmountBox(user);
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final User user = getItem(holder.getAdapterPosition());

                        sUserQuery.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                                    User user1 = fruitSnapshot.getValue(User.class);
                                    if (user1 != null) {
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
                                    if (user1 != null) {
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
                    }
                });

                holder.pay_money.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final User user = getItem(holder.getAdapterPosition());
                        showPayAmountBox(user);
                    }
                });

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final User user = getItem(holder.getAdapterPosition());
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invitation for Hisaab Kitaab by " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Invited by " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " to view and collaborate your Hisaab Kitaab. Link: https://goo.gl/jEZDDM");
                        sharingIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, user.getMobile());
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final User user = getItem(holder.getAdapterPosition());
                        Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", user.getName());
                        bundle.putString("address", user.getAddress());
                        bundle.putString("mobile", user.getMobile());
                        bundle.putString("amount", user.getAmount());
                        bundle.putString("date", user.getDate());
                        bundle.putString("uid", user.getUid());
                        bundle.putString("remark", user.getRemark());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void updateAmount(final User user) {
        sUserQuery.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    final User user1 = fruitSnapshot.getValue(User.class);
                    if (user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {
                            final List<Update> list = user1.getUpdate();
                            Update update = new Update();
                            update.setDate(user.getDate());
                            update.setAmount(Double.parseDouble(user.getAmount()));
                            update.setType("Added:");
                            update.setRemark(user.getRemark());
                            list.add(update);

                            double amount = Double.parseDouble(user1.getAmount());
                            final String finalAmount = String.valueOf(amount + Double.parseDouble(user.getAmount()));

                            fruitSnapshot.getRef().child("amount").setValue(finalAmount);
                            fruitSnapshot.getRef().child("update").setValue(list);

                            if (user1.getShared()) {
                                if (user1.getShared_to_uid() != null) {
                                    if (user1.getShared_by_email().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                        sAllUserQuery.getRef().child(user1.getShared_to_uid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                    User user2 = dataSnapshot1.getValue(User.class);
                                                    if (user2.getAddress().equalsIgnoreCase(user1.getAddress())) {
                                                        dataSnapshot1.getRef().child("amount").setValue(finalAmount);
                                                        dataSnapshot1.getRef().child("update").setValue(list);
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (user1.getAddress().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                        sAllUserQuery.getRef().child(user1.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                    User user2 = dataSnapshot1.getValue(User.class);
                                                    if (user2.getAddress().equalsIgnoreCase(user1.getAddress())) {
                                                        dataSnapshot1.getRef().child("amount").setValue(finalAmount);
                                                        dataSnapshot1.getRef().child("update").setValue(list);
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                            }
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void payAmount(final User user) {
        sUserQuery.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    final User user1 = fruitSnapshot.getValue(User.class);
                    if (user1 != null) {
                        if (user1.getAddress().equals(user.getAddress()) && user1.getName().equals(user.getName())) {

                            final List<Update> list = user1.getUpdate();
                            Update update = new Update();
                            update.setDate(user.getDate());
                            update.setAmount(Double.parseDouble(user.getAmount()));
                            update.setType("Paid:");
                            update.setRemark(user.getRemark());
                            list.add(update);

                            double amount = Double.parseDouble(user1.getAmount());
                            double dif = amount - Double.parseDouble(user.getAmount());
                            if (dif <= 0) {
                                dif = 0;
                            }
                            final String finalAmount = String.valueOf(dif);
                            fruitSnapshot.getRef().child("amount").setValue(finalAmount);

                            fruitSnapshot.getRef().child("update").setValue(list);


                            if (user1.getShared()) {
                                if (user1.getShared_to_uid() != null) {
                                    if (user1.getShared_by_email().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                        sAllUserQuery.getRef().child(user1.getShared_to_uid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                    User user2 = dataSnapshot1.getValue(User.class);
                                                    if (user2.getAddress().equalsIgnoreCase(user1.getAddress())) {
                                                        dataSnapshot1.getRef().child("amount").setValue(finalAmount);
                                                        dataSnapshot1.getRef().child("update").setValue(list);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (user1.getAddress().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                        sAllUserQuery.getRef().child(user1.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                    User user2 = dataSnapshot1.getValue(User.class);
                                                    if (user2.getAddress().equalsIgnoreCase(user1.getAddress())) {
                                                        dataSnapshot1.getRef().child("amount").setValue(finalAmount);
                                                        dataSnapshot1.getRef().child("update").setValue(list);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showPopup() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void showpaymentBox() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        lp1.setMargins(30, 20, 20, 10);

        final TextView amount = new TextView(this);
        amount.setText(R.string.prompt_payment);
        amount.setLayoutParams(lp1);

        linearLayout.addView(amount);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(linearLayout)
                .setIcon(R.drawable.ic_icon_user)
                .setTitle("Add More User")
                .setPositiveButton("Pay Now", null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogs) {

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doPayment();
                    }
                });
            }
        });
        dialog.show();
    }

    private double amount = 50.0;
    private String name = "";
    private String pname = "";
    private String email = "";

    private void doPayment() {
        txnid = random().substring(0, 5);
        name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        pname = "Hisaab Kitaaab";
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        launchPayUMoneyFlow();
    }

    public String random() {
        return UUID.randomUUID().toString();
    }

    String txnid = "";
    String udf1 = "";
    String udf2 = "";
    String udf3 = "";
    String udf4 = "";
    String udf5 = "";
    String udf6 = "";
    String udf7 = "";
    String udf8 = "";
    String udf9 = "";
    String udf10 = "";

    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;

    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Go Back");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Hisaab Kitaab Payment");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();


        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                                             // Transaction ID
                .setPhone("8510902134")
                .setProductName(pname)                   // Product Name or description
                .setFirstName(name)                              // User First name
                .setEmail(email)                                            // User Email ID
                .setsUrl("https://www.aadara.in/success")                    // Success URL (surl)
                .setfUrl("https://www.aadara.in/failure")                     //Failure URL (furl)
                .setKey(key)                        // Merchant key
                .setMerchantId(mid)             // Merchant ID
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false);

        try {
            mPaymentParams = builder.build();

            /*
            * Hash should always be generated from your server side.
            * */
            //  generateHashFromServer(mPaymentParams);


            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
            //if (AppPreference.selectedTheme != -1) {
            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MainActivity.this, R.style.AppTheme_Green, false);
//            } else {
//                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,MainActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
//            }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //payNowButton.setEnabled(true);
        }
    }


    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        //AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(salt);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
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
                                //indexNote(user);
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
                            if (!TextUtils.isEmpty(user_remark.getText().toString().trim())) {
                                user.setAmount(amount.getText().toString());
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                                user.setDate(date);
                                user.setRemark(user_remark.getText().toString());
                                payAmount(user);
                                onPayCustomersData(user);
                                dialog.dismiss();
                                FirebaseUserActions.getInstance().start(getAction(user.getAddress(), user.getName()));
                                // indexNote(user);
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

    private void initAddUserAlert() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.add_user_box)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogs) {

                final EditText name = dialog.findViewById(R.id.username);

                final EditText mobile = dialog.findViewById(R.id.mobile);

                final EditText address = dialog.findViewById(R.id.user_address);

                final EditText amount = dialog.findViewById(R.id.enter_amount);

                final EditText user_remark = dialog.findViewById(R.id.user_remark);

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!TextUtils.isEmpty(name.getText().toString().trim())) {
                            if (!TextUtils.isEmpty(mobile.getText().toString().trim()) && mobile.getText().toString().length() == 10) {
                                if (!TextUtils.isEmpty(address.getText().toString().trim()) && isValidEmail(address.getText().toString().trim())) {
                                    if (!TextUtils.isEmpty(amount.getText().toString().trim())) {
                                        User user = new User();
                                        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        user.setName(name.getText().toString().trim());

                                        user.setAmount(amount.getText().toString().trim());
                                        user.setMobile(mobile.getText().toString().trim());
                                        user.setRemark(user_remark.getText().toString().trim());
                                        user.setAddress(address.getText().toString().trim());
                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                                        user.setDate(date);

                                        Update update = new Update();
                                        update.setAmount(Double.parseDouble(amount.getText().toString().trim()));
                                        update.setDate(date);
                                        update.setType("Added:");
                                        update.setRemark(user_remark.getText().toString().trim());
                                        List<Update> list = new ArrayList<>();
                                        list.add(update);
                                        user.setUpdate(list);

                                        user.setShared(true);
                                        user.setShared_by_name(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                        user.setShared_by_email(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                                        onAddMessage(user);
                                        if (!user.getAddress().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            onAddCustomersData(user);
                                        }
                                        dialog.dismiss();
                                        // indexNote(user);
                                    } else {
                                        amount.setError(getString(R.string.valid_amount));
                                        amount.requestFocus();
                                    }
                                } else {
                                    address.setError(getString(R.string.valid_address));
                                    address.requestFocus();
                                }
                            } else {
                                mobile.setError(getString(R.string.valid_mobile));
                                mobile.requestFocus();
                            }
                        } else {
                            name.setError(getString(R.string.valid_name));
                            name.requestFocus();
                        }

                    }
                });
            }
        });
        dialog.show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView customer_name;
        TextView shared_by_name;
        TextView shared_by_text;
        TextView customer_address;
        TextView customer_amount;
        TextView last_date;
        ImageView letter_img;
        Button add_money;
        Button delete;
        Button pay_money;
        Button share;

        public ViewHolder(View view) {
            super(view);
            customer_name = view.findViewById(R.id.customer_name);
            customer_address = view.findViewById(R.id.customer_address);
            customer_amount = view.findViewById(R.id.customer_amount);
            add_money = view.findViewById(R.id.add_money);
            delete = view.findViewById(R.id.delete);
            pay_money = view.findViewById(R.id.paid);
            share = view.findViewById(R.id.share);
            last_date = view.findViewById(R.id.date);
            letter_img = view.findViewById(R.id.letter_img);
            shared_by_name = view.findViewById(R.id.shared_by);
            shared_by_text = view.findViewById(R.id.shared_by_text);
        }
    }

    protected void onAddMessage(User user) {
        sUserQuery.getRef().push().setValue(user);
    }

    protected void onUpdateCustomersData(final User user) {
        sCustomersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    User user1 = fruitSnapshot.getValue(User.class);
                    if (user.getAddress().equalsIgnoreCase(user1.getAddress()) && user.getName().equalsIgnoreCase(user1.getName())) {
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
                    if (user.getAddress().equalsIgnoreCase(user1.getAddress()) && user.getName().equalsIgnoreCase(user1.getName())) {
                        List<Update> list = user1.getUpdate();
                        Update update = new Update();
                        update.setDate(user.getDate());
                        update.setAmount(Double.parseDouble(user.getAmount()));
                        update.setType("Paid:");
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

    protected void onAddCustomersData(User user) {
        sCustomersQuery.getRef().push().setValue(user);
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                if (adapter.getItemCount() < current_capacity) {
                    initAddUserAlert();
                } else {
                    showpaymentBox();
                }
                return true;
            case R.id.pay_now_menu_home:
                showPaymentAlert();
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.reward:
            case R.id.add_reward:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    int rewards;
    int current_capacity;

    private void updateMenuTitles(int rewards) {
        if (menu != null) {
            MenuItem bedMenuItem = menu.findItem(R.id.reward);
            bedMenuItem.setTitle(rewards + "/100 (100 reward = 1 user)");
        }
    }

    private void getKeys() {
        sKeysQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    if (fruitSnapshot.getKey().equalsIgnoreCase("payu_key")) {
                        key = fruitSnapshot.getValue().toString();
                    } else if (fruitSnapshot.getKey().equalsIgnoreCase("payu_salt")) {
                        salt = fruitSnapshot.getValue().toString();
                    } else if (fruitSnapshot.getKey().equalsIgnoreCase("payu_mid")) {
                        mid = fruitSnapshot.getValue().toString();
                    } else if (fruitSnapshot.getKey().equalsIgnoreCase("amount_to_charge")) {
                        amount = Double.parseDouble(fruitSnapshot.getValue().toString());
                    } else if (fruitSnapshot.getKey().equalsIgnoreCase("user_after_charge")) {
                        user_after_charge = Integer.parseInt(fruitSnapshot.getValue().toString());
                    } else if (fruitSnapshot.getKey().equalsIgnoreCase("first_time_user_capacity")) {
                        first_time_user_capacity = Integer.parseInt(fruitSnapshot.getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getRewards() {
        sRewardsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                    if (fruitSnapshot.getValue() != null) {
                        RewardModel rewardModels = fruitSnapshot.getValue(RewardModel.class);
                        if (rewardModels != null) {
                            updateMenuTitles(rewardModels.getReward());
                            rewards = rewardModels.getReward();
                            current_capacity = rewardModels.getCurrent_capacity();
                            break;
                        }
                    }
                }
                if (dataSnapshot.getValue() == null) {
                    RewardModel rewardModel = new RewardModel();
                    rewardModel.setCurrent_capacity(first_time_user_capacity);
                    rewardModel.setReward(0);
                    sRewardsQuery.getRef().child("reward").setValue(rewardModel);
                    rewards = 0;
                    current_capacity = first_time_user_capacity;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    sRewardsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot fruitSnapshot : dataSnapshot.getChildren()) {
                                if (fruitSnapshot.getValue() != null) {
                                    RewardModel rewardModels = fruitSnapshot.getValue(RewardModel.class);

                                    RewardModel rewardModel = new RewardModel();
                                    rewardModel.setCurrent_capacity(rewardModels.getCurrent_capacity() + user_after_charge);
                                    rewardModel.setReward(rewardModels.getReward());

                                    fruitSnapshot.getRef().setValue(rewardModel);

                                    rewards = rewardModel.getReward();
                                    current_capacity = rewardModel.getCurrent_capacity();
                                    showPaymentDoneAlert();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                }
            }
        }
    }

    private void showPaymentAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle_green);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Pay Now")
                .setMessage("Pay ₹" + amount + " to get " + user_after_charge + " more users.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doPayment();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(amount));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private void showPaymentDoneAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle_green);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Congratulations !!!")
                .setMessage(user_after_charge + " users are added to your account. Now you have total " + current_capacity + " users.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(amount));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
                    }
                })
                .show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
