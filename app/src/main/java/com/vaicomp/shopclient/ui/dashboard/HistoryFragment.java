package com.vaicomp.shopclient.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaicomp.shopclient.db.OrderModal;
import com.vaicomp.shopclient.preferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vaicomp.shopclient.R;

import java.util.List;

public class HistoryFragment extends Fragment {
    FirebaseFirestore fdb;
    List<OrderModal> list;
  public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history, container, false);


      fdb = FirebaseFirestore.getInstance();
      String uid = preferenceManager.getUID(getContext());
      fdb.collection("orders").whereEqualTo("uid", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
          @Override
          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              for(QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots){
                  OrderModal orderModal = querySnapshot.toObject(OrderModal.class);
                  orderModal.setOrderId(querySnapshot.getId());
                  list.add(orderModal);
              }
          }
      });
        return root;
    }
}
