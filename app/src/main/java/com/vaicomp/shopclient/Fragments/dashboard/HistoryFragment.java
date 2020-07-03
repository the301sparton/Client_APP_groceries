package com.vaicomp.shopclient.Fragments.dashboard;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.Adapters.HistoryAdapter;
import com.vaicomp.shopclient.R;
import com.vaicomp.shopclient.db.OrderModal;
import com.vaicomp.shopclient.preferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView listView;
    private List<OrderModal> list;
    private LinearLayout loader, noResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        list = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        listView = root.findViewById(R.id.list);
        noResult = root.findViewById(R.id.noResult);

        loader = root.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);


        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 5;
                outRect.right = 5;
                outRect.bottom = 40;

                // Add top margin only for the first item to avoid double space between items
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.top = 5;
                } else {
                    outRect.top = 0;
                }
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        String uid = preferenceManager.getUID(getContext());
        fdb.collection("orders").whereEqualTo("uid", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear();
                for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                    OrderModal orderModal = querySnapshot.toObject(OrderModal.class);
                    orderModal.setOrderId(querySnapshot.getId());
                    list.add(orderModal);
                    Log.i("OBJ: ", String.valueOf(orderModal));
                }

                listView.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                if(queryDocumentSnapshots.size() > 0) {

                    Collections.sort(list, new Comparator<OrderModal>() {
                        @Override
                        public int compare(OrderModal o1, OrderModal o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });

                    HistoryAdapter adapter = new HistoryAdapter(list, getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    listView.setLayoutManager(mLayoutManager);

                    listView.setItemAnimator(new DefaultItemAnimator());
                    listView.setAdapter(adapter);
                }
                else {
                    listView.setVisibility(View.GONE);
                    noResult.setVisibility(View.VISIBLE);
                }


            }
        });
    }
}
