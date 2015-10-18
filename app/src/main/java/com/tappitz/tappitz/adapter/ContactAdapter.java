package com.tappitz.tappitz.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.service.AnswerContactRequestService;
import com.tappitz.tappitz.rest.service.BlockContactService;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.DeleteContactService;
import com.tappitz.tappitz.rest.service.InviteContactService;
import com.tappitz.tappitz.rest.service.UndoInviteContactService;

import java.util.ArrayList;
import java.util.List;


public class ContactAdapter extends BaseAdapter implements Filterable {

    private Activity activity;
    private OnUpdate update; ;
    private ContactFilter contactFilter;
    private List<ListViewContactItem> contacts;
    private List<ListViewContactItem> originalContacts;
    private LayoutInflater mInflater;

    public ContactAdapter(Activity activity, List<ListViewContactItem> contacts, OnUpdate update) {
        this.contacts = contacts;
        this.originalContacts = contacts;
        this.update = update;
        this.activity = activity;
        mInflater=LayoutInflater.from(activity);
        getFilter();
    }

    public void removeFilter(){
        contactFilter = null;
    }


    public void setContacts(List<ListViewContactItem> contacts) {
        this.contacts = contacts;
    }

    public void removeIfFound(String email){
        ListViewContactItem tmp = null;
        for(ListViewContactItem c: contacts){
            if(c.getType() == ListViewContactItem.HASCONTACT && c.getContact().getEmail().equals(email)){
                tmp = c;
                break;
            }
        }
        if(tmp != null){
            contacts.remove(tmp);
            originalContacts.remove(tmp);
        }else {

            for (ListViewContactItem c : originalContacts) {
                if(c.getType() == ListViewContactItem.HASCONTACT && c.getContact().getEmail().equals(email)){
                    tmp = c;
                    break;
                }
            }
            if (tmp != null) {
                contacts.remove(tmp);
                originalContacts.remove(tmp);
                return;
            }
        }
        notifyDataSetChanged();

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
                convertView = mInflater.inflate(R.layout.contact_list, parent, false);
                holder = new ViewHolder();
                holder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.contact_front = ((RelativeLayout) convertView.findViewById(R.id.contact_front));
                holder.contact_email = (TextView) convertView.findViewById(R.id.contact_email);
                holder.contact_back_invite = (LinearLayout) convertView.findViewById(R.id.contact_back_invite);
                holder.contact_back_invite_undo = (LinearLayout) convertView.findViewById(R.id.contact_back_invite_undo);
                holder.contact_back = (LinearLayout) convertView.findViewById(R.id.contact_back);
                holder.contact_request = (LinearLayout) convertView.findViewById(R.id.contact_request);
                holder.setClick(convertView.findViewById(R.id.button_cancel));
                holder.setClick(convertView.findViewById(R.id.button_cancel_inv));
                holder.setClick(convertView.findViewById(R.id.button_invite));
                holder.setClick(convertView.findViewById(R.id.button_cancel_inv_undo));
                holder.setClick(convertView.findViewById(R.id.button_invite_undo));

                holder.setClick(convertView.findViewById(R.id.button_block));
                holder.setClick(convertView.findViewById(R.id.button_delete));

                holder.setClick(convertView.findViewById(R.id.add_contact));
                holder.setClick(convertView.findViewById(R.id.dont_add_contact));

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
            if (contact.getContact().isInviteRequest()) {
                holder.removeClick(holder.contact_front);
                holder.contact_request.setVisibility(View.VISIBLE);
            } else {
                holder.setClick(holder.contact_front);
                holder.contact_request.setVisibility(View.GONE);
            }
        }else {
            holderHeader.textHeader.setText(contact.getMsgSeparator());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(contactFilter==null) {
            contactFilter=new ContactFilter();
        }
        return contactFilter;
    }

    private static class ViewHolderHeader {

        TextView textHeader;
        int pos;

        public ViewHolderHeader() {
        }
    }

    private class ViewHolder {
        RelativeLayout contact_front;
        LinearLayout contact_back, contact_back_invite, contact_back_invite_undo, contact_request;
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
                        if(contact.isFriend()){
                            contact_back.setVisibility(View.VISIBLE);
                        }else if(contact.isInvited()){
                            //Ja convidado
                            contact_back_invite_undo.setVisibility(View.VISIBLE);
                        }else {
                            //convidar
                            contact_back_invite.setVisibility(View.VISIBLE);
                        }
                        contact_front.setVisibility(View.GONE);
                        break;
                    case R.id.button_cancel_inv:
                        contact_front.setVisibility(View.VISIBLE);
                        contact_back_invite.setVisibility(View.GONE);
                        break;
                    case R.id.button_cancel:
                        contact_front.setVisibility(View.VISIBLE);
                        contact_back.setVisibility(View.GONE);
                        break;
                    case R.id.button_cancel_inv_undo:
                        contact_front.setVisibility(View.VISIBLE);
                        contact_back_invite_undo.setVisibility(View.GONE);
                        break;
                    case R.id.button_invite:
                        contact = contacts.get(pos).getContact();
                        Log.d("myapp", "button_invitebutton_invite: " + contact.getEmail());
                        new InviteContactService(new ContactSendId(contact.getEmail()), new CallbackMultiple<Boolean>() {
                            @Override
                            public void success(Boolean response) {
                                if(response){
                                    Toast.makeText(activity, "Invitation received", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(activity, "There was a problem with the invitation.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failed(Object error) {
                                Toast.makeText(activity, "There was a problem with the invitation.", Toast.LENGTH_SHORT).show();
                            }
                        }).execute();
                        break;
                    case R.id.button_invite_undo:
                        contact = contacts.get(pos).getContact();
                        Log.d("myapp", "button_invite_undo: " + contact.getEmail() );
                        new UndoInviteContactService(new ContactSendId(contact.getEmail()), new CallbackMultiple<Boolean>() {
                            @Override
                            public void success(Boolean response) {
                                if(response){
                                    Toast.makeText(activity, "Invitation removed", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(activity, "We could not undo the invitation.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failed(Object error) {
                                Toast.makeText(activity, "We could not undo the invitation.", Toast.LENGTH_SHORT).show();
                            }
                        }).execute();
                        break;
                    case R.id.button_block:
                        contact = contacts.get(pos).getContact();
                        new BlockContactService(new ContactSendId(contact.getEmail()), new CallbackMultiple<Boolean>() {
                            @Override
                            public void success(Boolean response) {
                                if(response){
                                    Toast.makeText(activity, "Contact blocked!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(activity, "We could not block contact.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failed(Object error) {
                                Toast.makeText(activity, "We could not block contact.", Toast.LENGTH_SHORT).show();
                            }
                        }).execute();
                        break;
                    case R.id.button_delete:
                        final String emailId = contacts.get(pos).getContact().getEmail();
                        new DeleteContactService(new ContactSendId(emailId), new CallbackMultiple<Boolean>() {
                            @Override
                            public void success(Boolean response) {
                                if(response){
                                    Toast.makeText(activity, "Contact removed!", Toast.LENGTH_SHORT).show();

                                    removeIfFound(emailId);


                                }else{
                                    Toast.makeText(activity, "We could not remove contact.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failed(Object error) {
                                Toast.makeText(activity, "We could not remove contact.", Toast.LENGTH_SHORT).show();
                            }
                        }).execute();
                        break;
                    case R.id.add_contact:
                        answerContactRequest(true, contacts.get(pos).getContact().getEmail());
                        break;
                    case R.id.dont_add_contact:
                        answerContactRequest(false, contacts.get(pos).getContact().getEmail());
                        break;

                }

            }
        };
    }

    private void answerContactRequest(boolean answer, final String id){
        new AnswerContactRequestService(id, answer, new CallbackMultiple<Boolean>() {
            @Override
            public void success(Boolean response) {
                if(response) {
                    Toast.makeText(activity, "Contact " + id + " added.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity, "Contact " + id + " not added.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(Object error) {
                Toast.makeText(activity, "Contact " + id + " not added.", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    public interface OnUpdate{
        public void onNoContactsFound(int size);

        public void reloadFromServer();
    }

    private class ContactFilter extends Filter {


        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toLowerCase();
                ArrayList<ListViewContactItem> filterList = new ArrayList<ListViewContactItem>();
                ListViewContactItem item;
                for (int i = 0; i < originalContacts.size(); i++) {
                    item = originalContacts.get(i);
                    if(item.getType() == ListViewContactItem.HEADER){
                        filterList.add(item);
                        continue;
                    }
                    if (item.getContact().getName().toLowerCase().contains(constraint)) {
                        filterList.add(originalContacts.get(i));
                    }
                }

                //se nao houver pedidos de amizade remover cabeçalho
                if(filterList.size() >= 2 && filterList.get(1).getType() == ListViewContactItem.HEADER ){
                    filterList.remove(0);
                }

                //se nao tiver contactos remover cabeçalho
                if(filterList.size() > 0 && filterList.get(filterList.size()-1).getType() == ListViewContactItem.HEADER ){
                    filterList.remove(filterList.size()-1);
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

            contacts = (ArrayList<ListViewContactItem>) results.values;

            notifyDataSetChanged();
            update.onNoContactsFound(contacts.size());

        }
    }
}
