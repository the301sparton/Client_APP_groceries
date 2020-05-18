package com.vaicomp.shopclient.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.Adapters.ItemAdapter;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.ShopItem;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView listView;
    private ArrayList <ShopItem> fireBaseList, list, searchList;
    private ItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);

        fireBaseList = new ArrayList<>();
        list = new ArrayList<>();
        searchList = new ArrayList<>();
        listView = root.findViewById(R.id.list);
        final TextInputEditText searchBar = root.findViewById(R.id.searchBar);
        final Button clear = root.findViewById(R.id.calc_clear_txt_Prise);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("shopItems").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                if(list1.size() != 0){
                    list.clear();
                    fireBaseList.clear();
                    searchList.clear();
                    Log.i("Length == >>", String.valueOf(list1.size()));
                    for(DocumentSnapshot ds : list1) {
                        ShopItem item = new ShopItem();
                        item.setItemName(String.valueOf(ds.get("itemName")));
                        Log.i("IETM==>", item.getItemName());
                        item.setImageUrl(String.valueOf(ds.get("photoUrl")));
                        item.setAmount((double) 0);
                        item.setQuantity(0);
                        item.setRate(Double.valueOf(String.valueOf(ds.get("itemRate"))));
                        list.add(item);
                        fireBaseList.add(item);
                    }
                    adapter = new ItemAdapter(list, getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    listView.setLayoutManager(mLayoutManager);
                    listView.setItemAnimator(new DefaultItemAnimator());
                    listView.setAdapter(adapter);
                }
            }
        });


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && !s.toString().equals("")) {
                    clear.setVisibility(View.VISIBLE);
                    searchList.clear();
                    searchList = new ArrayList<>();
                    for (ShopItem item : fireBaseList) {
                        if (item.getItemName().toLowerCase().matches(s.toString().toLowerCase()+".*")) {
                            searchList.add(item);
                        }
                    }
                    list.clear();
                    list.addAll(searchList);

                }
                else{
                    clear.setVisibility(View.GONE);
                    list.clear();
                    list.addAll(fireBaseList);
                }

                if(list.size() > 0){
                    listView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
                else{
                    listView.setVisibility(View.INVISIBLE);
                }
            }
        });

        return root;
    }


}
