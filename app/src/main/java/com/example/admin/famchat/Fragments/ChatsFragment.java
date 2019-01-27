package com.example.admin.famchat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.famchat.Adapter.UserAdapter;
import com.example.admin.famchat.Model.Chat;
import com.example.admin.famchat.Model.User;
import com.example.admin.famchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<User> mUsers;

    RecyclerView recyclerView;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<String> usersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats,container,false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        usersList=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getSender().equals(fUser.getUid())){

                        usersList.add(chat.getReceiver());
                    }

                    if(chat.getReceiver().equals(fUser.getUid())){
                        usersList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

     private void  readChats(){
        mUsers =new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                     User user = snapshot.getValue(User.class);

                     for(String id : usersList){
                         if(user.getId().equals(id)){
                             if(mUsers.size() !=0) {
                                 for(User user1 : mUsers){
                                     if(!user.getId().equals(user1.getId())){
                                         mUsers.add(user);
                                     }
                                 }
                             }else {
                                 mUsers.add(user);
                             }
                         }
                     }
                }

                userAdapter =new UserAdapter(getContext(),mUsers);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     }

}
