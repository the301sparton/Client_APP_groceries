package com.vaicomp.shopclient.ui.home;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.vaicomp.shopclient.Adapters.ItemAdapter;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.AppDataBase;
import com.vaicomp.shopclient.db.ShopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class HomeFragment extends Fragment {

    private RecyclerView listView;
    private List<ShopItem> list, searchList, fireBaseList;
    private ItemAdapter adapter;
    private TextInputEditText searchBar;
    private Button clear;
    @SuppressLint("StaticFieldLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);

        fireBaseList = new ArrayList<>();
        list = new ArrayList<>();
        searchList = new ArrayList<>();
        listView = root.findViewById(R.id.list);
        searchBar = root.findViewById(R.id.searchBar);
        clear = root.findViewById(R.id.calc_clear_txt_Prise);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });


        final AppDataBase db = Room.databaseBuilder(getActivity(),
                AppDataBase.class, "schoolDB").fallbackToDestructiveMigration().build();
        try {
            list = new AsyncTask<Void, Void, List<ShopItem>>() {
                @Override
                protected List<ShopItem> doInBackground(Void... voids) {
                    return db.shopItemDao().getAll();
                }
            }.execute().get();
            fireBaseList.addAll(list);
            adapter = new ItemAdapter(list, getActivity());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(adapter);

            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !s.toString().equals("")) {
                        clear.setVisibility(View.VISIBLE);
                        searchList.clear();
                        searchList = new ArrayList<>();
                        for (ShopItem item : fireBaseList) {
                            if (item.getItemName().toLowerCase().matches(s.toString().toLowerCase() + ".*")) {
                                searchList.add(item);
                            }
                        }
                        list.clear();
                        list.addAll(searchList);

                    } else {
                        clear.setVisibility(View.GONE);
                        list.clear();
                        list.addAll(fireBaseList);
                    }

                    if (list.size() > 0) {
                        listView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    } else {
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            });

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }
}

