package com.tappitz.tappitz.adapter;

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

import java.util.List;

/**
 * Created by joaosampaio on 06-02-2016.
 */
public class ContactManagerAdapter extends RecyclerView.Adapter<ContactManagerAdapter.ContactViewHolder> implements Filterable {

    private List<Contact> contacts;
    private List<Contact> originalContacts;
    private ContactFilter contactFilter;
    private ContactFilter.OnUpdate update;
    private ContactFilter.OnListChange listChange;
    public ContactManagerAdapter(List<Contact> contacts, ContactFilter.OnUpdate update ){
        this.contacts = contacts;
        Log.d("myapp", "contacts:" + contacts.size());
        this.originalContacts = contacts;
        this.update = update;
        this.listChange = new ContactFilter.OnListChange() {
            @Override
            public void onPublishResults(List<Contact> filteredContacts) {
                setContacts(filteredContacts);
                notifyDataSetChanged();
            }
        };
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername;
        TextView mCircle;
        Button invite;


        ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.CONTACT_name);
            mUsername = (TextView) itemView.findViewById(R.id.CONTACT_username);
            mCircle = (TextView)itemView.findViewById(R.id.CONTACT_circle);
            invite = (Button) itemView.findViewById(R.id.action_follow);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_simple, viewGroup, false);
        return new ContactViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int i) {
        // get the single element from the main array
        Contact contact = contacts.get(i);
        // Set the values
        viewHolder.mName.setText(contact.getName());
        viewHolder.mUsername.setText(contact.getUsername());

        //se não for amigo e e nao estiver a seguir
        viewHolder.invite.setVisibility((!contact.isAmIFollowing() && !contact.isFriend())? View.VISIBLE : View.GONE);


        Log.d("myapp", "onBindViewHolder:" + contact.getName());
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

    @Override
    public Filter getFilter() {
        if(contactFilter==null) {
            contactFilter=new ContactFilter(contacts, update, this.listChange);
        }
        return contactFilter;
    }


}

