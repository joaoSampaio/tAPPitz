package com.tappitz.tappitz.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class SelectContactAdapter_old extends BaseAdapter {

    private Activity activity;
    private ContactAdapter.OnUpdate update; ;
    private List<ListViewContactItem> contacts;
    private Map<Integer, Integer> selectedContacts;
//    private List<ListViewContactItem> originalContacts;
    private LayoutInflater mInflater;
    private int UNSELECTED, SELECTED;


    public SelectContactAdapter_old(Activity activity, List<ListViewContactItem> contacts, ContactAdapter.OnUpdate update) {
        this.contacts = contacts;
//        this.originalContacts = contacts;
        this.update = update;
        this.selectedContacts = new TreeMap<>();
        this.activity = activity;
        mInflater=LayoutInflater.from(activity);
        UNSELECTED = activity.getResources().getColor(R.color.white);
        SELECTED = activity.getResources().getColor(R.color.primary_light);
    }

    public void setContacts(List<ListViewContactItem> contacts) {
        this.contacts = contacts;
    }

    public List<Integer> getSelectedContacts(){
        return new ArrayList<>(selectedContacts.values());
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return contacts.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderHeader holderHeader = null;
        int listViewItemType = getItemViewType(position);
        ListViewContactItem contact = (ListViewContactItem)getItem(position);
        if (convertView == null) {
            if (listViewItemType == ListViewContactItem.HASCONTACT) {
                convertView = mInflater.inflate(R.layout.contact_list_front, parent, false);
                holder = new ViewHolder();
                holder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.contact_front = ((RelativeLayout) convertView.findViewById(R.id.contact_front));
                holder.contact_email = (TextView) convertView.findViewById(R.id.contact_email);

                holder.setClick(holder.contact_front);


                convertView.setTag(holder);
            }else {
                convertView = mInflater.inflate(R.layout.contact_header, parent, false);
                holderHeader = new ViewHolderHeader();
                holderHeader.textHeader = (TextView) convertView.findViewById(R.id.textHeader);
                convertView.setTag(holderHeader);
            }
        } else {
            Log.d("myapp", "else listViewItemType: " + listViewItemType);
            if (listViewItemType == ListViewContactItem.HASCONTACT) {
                holder = (ViewHolder) convertView.getTag();
            }else {
                holderHeader = (ViewHolderHeader) convertView.getTag();
            }
        }

        Log.d("myapp", "listViewItemType: " + listViewItemType);
        if (listViewItemType == ListViewContactItem.HASCONTACT) {

            holder.pos = position;
            holder.contact_name.setText(contact.getContact().getName());
            holder.contact_email.setText("<" + contact.getContact().getEmail() + ">");
        }else {
            holderHeader.textHeader.setText(contact.getMsgSeparator());
        }

        return convertView;
    }



    private static class ViewHolderHeader {

        TextView textHeader;
        int pos;

        public ViewHolderHeader() {
        }
    }

    private class ViewHolder {
        RelativeLayout contact_front;
        TextView contact_name;
        TextView contact_email;
        int pos;

        public ViewHolder(){


        }

        public void removeClick(View v){
            v.setOnClickListener(null);
        }

        public void setClick(View v){
            v.setOnClickListener(mMyLocalClickListener);
        }

        private View.OnClickListener mMyLocalClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact;
                switch (v.getId()){
                    case R.id.contact_front:
                        contact = contacts.get(pos).getContact();
                        if(selectedContacts.containsKey(contact.getId())){
                            selectedContacts.remove(contact.getId());
                            v.setBackgroundColor(UNSELECTED);
                        }

                        else {
                            selectedContacts.put(contact.getId(),contact.getId());
                            v.setBackgroundColor(SELECTED);
                        }

                        break;


                }

            }
        };
    }





}
