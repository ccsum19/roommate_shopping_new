package edu.uga.cs.roommate_shopping_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;

public class PendingFragment extends Fragment {

    private ArrayList<ShoppingList> currentLists;
    ListView pendingListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_pending, container, false);

        pendingListView = rootView.findViewById(R.id.pendingListView);

        DAO dao = DAO.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail() + "/houseID");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String houseID = dataSnapshot.getValue(String.class);
                DatabaseReference listsRef = database.getReference("/houses/" + houseID + "/lists/");
                listsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentLists = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                ShoppingList list = snapshot.getValue(ShoppingList.class);
                                list.setId(snapshot.getKey());
                            if(list.getStatus().equals("basket")) {
                                currentLists.add(list);
                                ListAdapter currentAdapter = new CustomAdapter(getContext(), currentLists, houseID);
                                pendingListView.setAdapter(currentAdapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return rootView;
    }
}
