package com.tappitz.tappitz.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.FutureWorkList;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.OperationContactService;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.NiceColor;

import java.util.List;

/**
 * Created by joaosampaio on 06-02-2016.
 */
public class ContactManagerAdapter extends RecyclerView.Adapter<ContactManagerAdapter.ContactViewHolder> implements Filterable {

    private List<Contact> contacts;
    private ContactFilter contactFilter;
    private ContactFilter.OnUpdate update;
    private ContactFilter.OnListChange listChange;
    private Activity activity;
    private ReloadChildren reloadChildren;
    public ContactManagerAdapter(List<Contact> contacts, ContactFilter.OnUpdate update, Activity activity, ReloadChildren reloadChildren ){
        this.contacts = contacts;
        Log.d("myapp", "contacts:" + contacts.size());
        this.update = update;
        this.reloadChildren = reloadChildren;
        this.activity = activity;
        this.listChange = new ContactFilter.OnListChange() {
            @Override
            public void onPublishResults(List<Contact> filteredContacts) {
                Log.d("myapp", "listChange:" + filteredContacts.size());
                setContacts(filteredContacts);
                notifyDataSetChanged();
            }
        };
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername;
        TextView mCircle;
        Button follow, remove;
        View progressOperation, image_is_friend;
        int id;


        ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.CONTACT_name);
            mUsername = (TextView) itemView.findViewById(R.id.CONTACT_username);
            mCircle = (TextView)itemView.findViewById(R.id.CONTACT_circle);
            follow = (Button) itemView.findViewById(R.id.action_follow);
            remove = (Button) itemView.findViewById(R.id.action_remove);
            progressOperation = itemView.findViewById(R.id.progressOperation);
            image_is_friend = itemView.findViewById(R.id.image_is_friend);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_simple, viewGroup, false);
        return new ContactViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ContactViewHolder viewHolder, int i) {
        // get the single element from the main array
        viewHolder.remove.setVisibility(View.GONE);
        viewHolder.follow.setVisibility(View.GONE);
        viewHolder.progressOperation.setVisibility(View.GONE);
        final Contact contact = contacts.get(i);
        // Set the values
        viewHolder.mName.setText(contact.getName());
        viewHolder.mUsername.setText(contact.getUsername());
        viewHolder.id = contact.getId();
        //se não for amigo e e nao estiver a seguir
        viewHolder.follow.setVisibility((!contact.isAmIFollowing() && !contact.isFriend())? View.VISIBLE : View.GONE);
        viewHolder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow(viewHolder, contact);
            }
        });

        viewHolder.image_is_friend.setVisibility((contact.isFriend())? View.VISIBLE : View.GONE);

        if(contact.isFriend()){

            viewHolder.remove.setVisibility(View.VISIBLE);
            viewHolder.remove.setText("Remove");
            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Do you want to remove " + contact.getName() + " ?")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    removeFriend(viewHolder, contact);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog

                                }
                            });
                    builder.setTitle("Remove Contact");
                    builder.create();
                    builder.show();
                }
            });
        }


        //estou a seguir esta pessoa remover
        if(contact.isAmIFollowing()) {
            viewHolder.remove.setVisibility(View.VISIBLE);
            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Do you want to unfollow " + contact.getName() + " ?")
                            .setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    unfollow(viewHolder, contact);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog

                                }
                            });
                    builder.setTitle("Unfollow");
                    builder.create();
                    builder.show();
                }
            });

        }
        Log.d("myapp", "onBindViewHolder:" + contact.getName());
        viewHolder.mCircle.setText(contact.getLetters());


        // Set the color of the shape
        GradientDrawable bgShape = (GradientDrawable) viewHolder.mCircle.getBackground();
        bgShape.setColor(NiceColor.betterNiceColor(contact.getName()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
//        this.contacts.clear();
//        this.contacts.addAll(contacts);
        this.contacts = contacts;
    }

    @Override
    public Filter getFilter() {
        if(contactFilter==null) {
            contactFilter=new ContactFilter(contacts, update, this.listChange);
        }
        return contactFilter;
    }

    private void removeFriend(final ContactViewHolder viewHolder, final Contact contact){
        viewHolder.progressOperation.setVisibility(View.VISIBLE);
        viewHolder.remove.setVisibility(View.GONE);
        new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_DELETE), new CallbackMultiple<Boolean, String>() {
            @Override
            public void success(Boolean response) {
                if (contacts != null)
                    Contact.removeContact(contacts, contact.getId());
                new ModelCache<List<Contact>>().saveModel(AppController.getAppContext(), contacts, Global.FRIENDS);

                if (viewHolder != null)
                    notifyDataSetChanged();


                if(reloadChildren != null)
                    reloadChildren.onReloadChildren();

                Toast.makeText(AppController.getAppContext(), "Contact Removed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failed(String error) {
                if (viewHolder != null)
                    viewHolder.progressOperation.setVisibility(View.GONE);
                Toast.makeText(AppController.getAppContext(), " " + error, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    private void unfollow(final ContactViewHolder viewHolder, final Contact contact){
        viewHolder.progressOperation.setVisibility(View.VISIBLE);
        viewHolder.remove.setVisibility(View.GONE);
        new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_DELETE), new CallbackMultiple<Boolean, String>() {
            @Override
            public void success(Boolean response) {
                if (contacts != null)
                    Contact.removeContact(contacts, contact.getId());
                new ModelCache<List<Contact>>().saveModel(AppController.getAppContext(), contacts, Global.FRIENDS);

                if (viewHolder != null)
                    notifyDataSetChanged();

                if(reloadChildren != null)
                    reloadChildren.onReloadChildren();
                Toast.makeText(AppController.getAppContext(), "Unfollowed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failed(String error) {
                if (viewHolder != null)
                    viewHolder.progressOperation.setVisibility(View.GONE);
                Toast.makeText(AppController.getAppContext(), " " + error, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }


    private void follow(final ContactViewHolder viewHolder, final Contact contact){
        viewHolder.progressOperation.setVisibility(View.VISIBLE);
        viewHolder.follow.setVisibility(View.GONE);
        new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_ACCEPT), new CallbackMultiple<Boolean, String>() {
            @Override
            public void success(Boolean response) {
                if (contacts != null)
                    Contact.removeContact(contacts, contact.getId());
                new ModelCache<List<Contact>>().saveModel(AppController.getAppContext(), contacts, Global.MYFOLLOWERS);
                List<Contact> tmp = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(), new TypeToken<List<Contact>>() {
                }.getType(), Global.FRIENDS);
                if (tmp != null) {
                    contact.setIsFriend(true);
                    tmp.add(contact);
                    new ModelCache<List<Contact>>().saveModel(AppController.getAppContext(), tmp, Global.FRIENDS);

                }


                if (viewHolder != null && viewHolder.id == contact.getId())
                    viewHolder.progressOperation.setVisibility(View.GONE);

                if (viewHolder != null)
                    notifyDataSetChanged();

                if(reloadChildren != null)
                    reloadChildren.onReloadChildren();
                Toast.makeText(AppController.getAppContext(), "Follow successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failed(String error) {
                if (viewHolder != null)
                    viewHolder.progressOperation.setVisibility(View.GONE);
                Toast.makeText(AppController.getAppContext(), " " + error, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    public interface ReloadChildren{
        void onReloadChildren();
    }

}

