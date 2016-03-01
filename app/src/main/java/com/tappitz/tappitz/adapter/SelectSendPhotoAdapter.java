package com.tappitz.tappitz.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.util.ContactFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joaosampaio on 06-02-2016.
 */
public class SelectSendPhotoAdapter extends RecyclerView.Adapter<SelectSendPhotoAdapter.SelectSendPhotoViewHolder> implements Filterable{

    private List<Contact> contacts;
    private Map<Integer, Integer> selectedContacts;
    private int UNSELECTED, SELECTED;
    private SelectClick selectClick;
    private ContactFilter contactFilter;
    private ContactFilter.OnUpdate update;
    private ContactFilter.OnListChange listChange;

    public SelectSendPhotoAdapter(List<Contact> contacts, Activity activity, ContactFilter.OnUpdate update){
        this.contacts = contacts;
        UNSELECTED = activity.getResources().getColor(R.color.white);
        SELECTED = activity.getResources().getColor(R.color.primary_light);
        this.selectedContacts = new TreeMap<>();
        this.update = update;
        this.listChange = new ContactFilter.OnListChange() {
            @Override
            public void onPublishResults(List<Contact> filteredContacts) {
                setContacts(filteredContacts);
                notifyDataSetChanged();
            }
        };
        selectClick = new SelectClick() {
            @Override
            public void onSelected(int id, View v) {
                boolean isSelected = isIdSelected(id);
                if(isSelected) {
                    selectedContacts.remove(id);
                }else {
                    selectedContacts.put(id, id);
                }
                v.setBackgroundColor(!isSelected ? SELECTED : UNSELECTED);
            }
        };
    }


    public static class SelectSendPhotoViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername, mCircle;
        View container;
        int idFriend = -1;
        SelectClick listener;

        SelectSendPhotoViewHolder(View itemView, SelectClick listener) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.CONTACT_name);
            mUsername = (TextView) itemView.findViewById(R.id.CONTACT_username);
            mCircle = (TextView)itemView.findViewById(R.id.CONTACT_circle);
            container = itemView.findViewById(R.id.container);
            this.listener = listener;
        }


        public void removeClick(View v){
            v.setOnClickListener(null);
        }

        public void setClick(View v){
            v.setOnClickListener(myClickListener);
        }

        private View.OnClickListener myClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.container:
                        listener.onSelected(idFriend, v);
                        break;
                }
            }
        };

    }

    @Override
    public SelectSendPhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_simple, viewGroup, false);
        return new SelectSendPhotoViewHolder(v, selectClick);
    }



    @Override
    public void onBindViewHolder(SelectSendPhotoViewHolder viewHolder, int i) {
        // get the single element from the main array
        Contact contact = contacts.get(i);
        // Set the values
        viewHolder.mName.setText(contact.getName());
        viewHolder.mUsername.setText(contact.getUsername());
        viewHolder.idFriend = contact.getId();


        String letter = "";
        String[] names = contact.getName().split(" ");
        for (String name: names) {
            if(name.length()> 1)
                letter+= name.substring(0,1);
            else if(name.length() == 1)
                letter+= name;
        }

        viewHolder.mCircle.setText(letter.toUpperCase());


        // Set the color of the shape
        GradientDrawable bgShape = (GradientDrawable) viewHolder.mCircle.getBackground();
        bgShape.setColor(Color.parseColor("#33b5e5"));

        viewHolder.container.setBackgroundColor(isIdSelected(contact.getId()) ? SELECTED : UNSELECTED);
        viewHolder.setClick(viewHolder.container);


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    //public Map<Integer, Integer> getSelectedContacts() {
//        return selectedContacts;
//    }

    private boolean isIdSelected(int id){
        return selectedContacts.containsKey(id);
    }


    interface SelectClick{
        public void onSelected(int id, View v);
    }

    @Override
    public Filter getFilter() {
        if(contactFilter==null) {
            contactFilter=new ContactFilter(contacts, update, this.listChange);
        }
        return contactFilter;
    }

    public List<Integer> getSelectedContacts(){
        return new ArrayList<>(selectedContacts.values());
    }

}

