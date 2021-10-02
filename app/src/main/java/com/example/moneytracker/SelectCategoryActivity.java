package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.moneytracker.model.Category;
import com.example.moneytracker.model.NewCategory;
import com.example.moneytracker.model.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import util.TransactionApi;

public class SelectCategoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 7;
    private ImageButton addActionButton,delete_icon;
    private String idCollection;
    private TextView deleteText;
    private Button confirm, cancel;
    public static final String CATEGORY_SELECTED = "category_selected";
   ArrayList<String>  listviewCategory = new ArrayList<>();
    ArrayList<Integer> listviewImage = new ArrayList<>();
     List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
     private ListView androidListView;
     int flag=0, flag2 =0;
     Dialog dialog;
      private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseAuth.AuthStateListener authStateListener;

        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = db.collection("Category");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialogbox);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();
        addActionButton = findViewById(R.id.action_add);
        androidListView = findViewById(R.id.CategoryListView);
        delete_icon = findViewById(R.id.delete_image);

         dialog = new Dialog(SelectCategoryActivity.this);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        deleteText = dialog.findViewById(R.id.deleteTextView);
        deleteText.setText("   Delete Category?");
        cancel = dialog.findViewById(R.id.cancel);
        confirm = dialog.findViewById(R.id.confirm);

         // NewCategoryList();

         if(getIntent().getStringExtra("No CLick2")!=null){
            if(addActionButton.getVisibility()==View.INVISIBLE){
                addActionButton.setVisibility(View.VISIBLE);
                delete_icon.setVisibility(View.VISIBLE);
            }
                flag=1;
        }


        if(getIntent().getStringExtra("No Click")!=null){
            if(addActionButton.getVisibility()==View.INVISIBLE){
                addActionButton.setVisibility(View.VISIBLE);
                delete_icon.setVisibility(View.VISIBLE);
            }
                flag=1;
        }
           // NewCategoryList();
//        if(flag2!=1){
//        }
        delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectCategoryActivity.this,"Long press the list item to delete.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        addActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectCategoryActivity.this,
                        AddCategoryActivity.class),REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        flag=1;
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            assert data != null;
            String category = data.getStringExtra("NEW_CATEGORY");
            flag2=1;

            Log.d("TAG", "onActivityResult: "+"intent");
        }

    }

    private void updateCategoryList() {
        Log.d("TAG", "updateCategoryList: "+listviewCategory.toString());
     //   Log.d("TAG", "updateCategoryList: "+listviewImage);
            List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < listviewCategory.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("listview_category", listviewCategory.get(i));
                hm.put("listview_image", Integer.toString(listviewImage.get(i)));
                aList.add(hm);
            }
        Log.d("LIst", "updateCategoryList: "+aList.size());
        String[] from = {"listview_image", "listview_category"};
        int[] to = {R.id.imageViewCategory, R.id.textViewCategory};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.category_list_view, from, to);
        androidListView.setAdapter(simpleAdapter);


         if(flag!=1) {

              androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                       Log.d("SELECTED1", "onItemClick: "+position);

                      String category = aList.get(position).get("listview_category");
                      Intent intent = getIntent();
                      intent.putExtra(CATEGORY_SELECTED, category);
                      setResult(RESULT_OK, intent);
                      finish();
                  }
              });
          }else{

              androidListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                  @Override
                  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.show();

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                    flag2 = 1;

                                Log.d("selected", "onClick: "+aList.get(position).get("listview_category"));
                                collectionReference.whereEqualTo("userId",TransactionApi.getInstance().getUserId())
                              //.whereEqualTo("category",aList.get(position).get("listview_Category"))
                              .get()
                              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                  @Override
                                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                      if(!queryDocumentSnapshots.isEmpty()){
                                          Log.d("selected", "onSuccess: "+ "query");
                                          for(QueryDocumentSnapshot value: queryDocumentSnapshots){

                                              if(Objects.equals(value.getString("category"), aList.get(position).get("listview_category"))) {
                                                  Log.d("selected", "onSuccess: " + "query");
                                                  idCollection = value.getId();
                                              }
                                              if(idCollection!=null) {

                                                  collectionReference.document(idCollection).delete();
                                                  simpleAdapter.notifyDataSetChanged();
                                                  dialog.dismiss();

                                              }
                                          }
                                          listviewCategory.clear();
                                          listviewImage.clear();
                                          NewCategoryList();



                                      }

                                  }
                              })
                              .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {

                                  }
                              });


                            }
                        });


                      return true;
                  }
              });
          }

    }


    private void NewCategoryList() {
        listviewCategory.add("Entertainment");
        listviewCategory.add("Food");
        listviewCategory.add("Shopping");
        listviewCategory.add("Work");
        listviewCategory.add("Travel");
        listviewCategory.add("Salary");

        listviewImage.add(R.drawable.ic_baseline_speaker_24);
        listviewImage.add(R.drawable.ic_baseline_restaurant_24);
        listviewImage.add(R.drawable.ic_baseline_shopping_cart_24);
        listviewImage.add(R.drawable.ic_baseline_work_24);
        listviewImage.add(R.drawable.ic_baseline_train_24);
        listviewImage.add(R.drawable.ic_baseline_monetization_on_24);
        collectionReference.whereEqualTo("userId",TransactionApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                     loop1:   for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                NewCategory category = snapshot.toObject(NewCategory.class);
                                for(int i=0;i<listviewCategory.size();i++) {
                                    if (listviewCategory.get(i).toLowerCase().equals((category.getCategory()).toLowerCase())) {
                                            continue loop1;
                                    }
                                }
                                listviewCategory.add(category.getCategory());
                         Log.d("TAG", "onSuccess: "+listviewCategory.toString());
                                listviewImage.add(category.getImageId());

                            }

                        }
                        Log.d("TAG", "onSuccess: "+"not working");
                        updateCategoryList();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("TAG2", "onStart: "+listviewCategory.toString());
       // if(flag2!=1) {
NewCategoryList();
        Log.d("TAG2", "onStart: "+listviewCategory.toString());
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        listviewCategory.clear();
        listviewImage.clear();

    }
}
