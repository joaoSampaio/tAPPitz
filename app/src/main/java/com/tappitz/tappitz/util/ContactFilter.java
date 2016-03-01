package com.tappitz.tappitz.util;

import android.widget.Filter;

import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaosampaio on 07-02-2016.
 */
public class ContactFilter  extends Filter {

    private OnUpdate update;
    private OnListChange listChange;
    private List<Contact> originalContacts;

    public ContactFilter(List<Contact> originalContacts, OnUpdate update, OnListChange listChange) {
        this.originalContacts = originalContacts;
        this.update = update;
        this.listChange = listChange;
    }

    //Invoked in a worker thread to filter the data according to the constraint.
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toLowerCase();
            ArrayList<Contact> filterList = new ArrayList<Contact>();
            Contact item;
            for (int i = 0; i < originalContacts.size(); i++) {
                item = originalContacts.get(i);

                if (item.getName().toLowerCase().contains(constraint)) {
                    filterList.add(item);
                }
                if(item.getUsername().toLowerCase().contains(constraint) && !originalContacts.contains(item)){
                    filterList.add(item);
                }

            }

            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = originalContacts.size();
            results.values = originalContacts;
        }

        return results;
    }

    //Invoked in the UI thread to publish the filtering results in the user interface.
    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        listChange.onPublishResults((ArrayList<Contact>)results.values);
//        contacts = (ArrayList<ListViewContactItem>) results.values;
//
//        notifyDataSetChanged();
        update.onNoContactsFound(results.count);

    }

    public interface OnUpdate{
        public void onNoContactsFound(int size);

        public void reloadFromServer();

        public void addContact(String eMail, int id, String name);

    }

    public interface OnListChange{
        public void onPublishResults(List<Contact> contacts);
    }

}