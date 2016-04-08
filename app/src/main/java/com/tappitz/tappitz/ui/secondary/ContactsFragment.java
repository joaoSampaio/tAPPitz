package com.tappitz.tappitz.ui.secondary;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactManagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFollowingService;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.rest.service.ListMyFollowersService;
import com.tappitz.tappitz.rest.service.OperationContactService;
import com.tappitz.tappitz.rest.service.SearchContactService;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public final static int FOLLOWERS = 0;
    public final static int FOLLOWING = 1;
    private int TYPE = 0;
    private View rootView;
//    private TextWatcher mSearchTw;
//    private EditText mSearchEdt;
//    private SwipeRefreshLayout swipeLayout;
    List<Contact> allContactsList;
    private TextView text_no_contact;
    private ContactManagerAdapter adapter;
    private Handler handler;
    private String newSearch;
    private Button action_follow;
    private ReloadContacts reloadContacts;




    private View progress_search, searchContainer, contact_search, progressOperation;
    private TextView mName, mUsername, mCircle, text_no_contact_search;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ContactsFragment.
     */
    public static ContactsFragment newInstance(int TYPE) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setTYPE(TYPE);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        Log.d("ContactsFragment", "ContactsFragment TYPE: "+TYPE );
        loadUI();
        refresh();
        return rootView;
    }

    private void loadUI(){


        mName = (TextView) rootView.findViewById(R.id.CONTACT_name);
        mUsername = (TextView) rootView.findViewById(R.id.CONTACT_username);
        mCircle = (TextView)rootView.findViewById(R.id.CONTACT_circle);
        searchContainer = rootView.findViewById(R.id.searchContainer);
        progress_search = rootView.findViewById(R.id.progress_search);
        searchContainer.setVisibility(View.GONE);
        action_follow = (Button)rootView.findViewById(R.id.action_follow);
        contact_search = rootView.findViewById(R.id.contact_search);
        text_no_contact_search = (TextView) rootView.findViewById(R.id.text_no_contact_search);
        progressOperation = rootView.findViewById(R.id.progressOperation);

        handler = new Handler();
        newSearch = "";
        allContactsList = new ArrayList<>();
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv); // layout reference

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true); // to improve performance

        this.adapter = new ContactManagerAdapter(allContactsList, new ContactFilter.OnUpdate() {
            @Override
            public void onNoContactsFound(int size) {
                checkIfHasContacts(size);
            }

            @Override
            public void reloadFromServer() {

            }

            @Override
            public void addContact(String eMail, int id, String name) {

            }
        }, getActivity(), new ContactManagerAdapter.ReloadChildren() {
            @Override
            public void onReloadChildren() {
                if(getParentFragment() != null){
                    ((ContactContainerFragment)getParentFragment()).reloadChildren();
                }
            }
        });

        rv.setAdapter(adapter); // the data manager is assigner to the RV


        ((ContactContainerFragment)getParentFragment()).addSearchListener(new ContactContainerFragment.SearchText() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", " ->" + new String(s.toString()) + "  getItemCount:"+adapter.getItemCount());
                adapter.getFilter().filter(s);
                for(Contact c : adapter.getContacts()){
                    Log.d("contact", " ---------->" + c.getEmail() + " getUsername:"+c.getUsername());
                }
                text_no_contact_search.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //se for following e nao exitir mais contactos entao vamso fazer search
//                        if (TYPE == FOLLOWING && adapter.getItemCount() == 0) {
                        if (TYPE == FOLLOWING) {
                            progress_search.setVisibility(View.VISIBLE);
                            searchContainer.setVisibility(View.VISIBLE);
                            newSearch = new String(s.toString());
                            final String search = new String(s.toString());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (newSearch.equals(search) && !search.isEmpty()) {
                                        Log.d("ADDContact", "text changed is the same as 1s ago:" + newSearch + "||||" + search);
                                        searchContact();
                                    } else {
                                        progress_search.setVisibility(View.GONE);
                                        searchContainer.setVisibility(View.GONE);
                                        Log.d("ADDContact", "text changed in less than 1s");
                                    }
                                }
                            }, 1200);

                        }else{
                            searchContainer.setVisibility(View.GONE);
                        }
                    }
                }, 300);

            }
        });

        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);


        //swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        //swipeLayout.setOnRefreshListener(this);


        Log.d("ContactsFragment", "ContactsFragment: end");
    }



    @Override
    public void onResume() {
        super.onResume();

        reloadContacts = new ReloadContacts() {
            @Override
            public void onReloadContacts() {
                loadContacts();
            }
        };
        ((ContactContainerFragment)getParentFragment()).addReloadContacts(reloadContacts);
    }


    private List<Contact> loadContactsOffline(){
        List<Contact> contactsList = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(),new TypeToken<List<Contact>>(){}.getType(), getOfflineId());
        if(contactsList == null)
            contactsList = new ArrayList<>();
        return contactsList;
    }


    @Override
    public void onPause() {
        super.onPause();
        if(reloadContacts != null)
            ((ContactContainerFragment)getParentFragment()).removeReloadContacts(reloadContacts);

    }

    @Override
    public void onRefresh() {
        refresh();
    }


    public void refresh(){
        //pede a lista de todos os contactos
        loadContacts();
    }

    private void loadContacts(){
        allContactsList.clear();
        allContactsList.addAll(loadContactsOffline());
        notifyAdapter();

        CallbackMultiple callback = new CallbackMultiple<List<Contact>, String>() {
            @Override
            public void success(List<Contact> response) {
                sortContacts(response);
                saveContactsOffline(response);




                if(!((ContactContainerFragment)getParentFragment()).getFriends().isEmpty()){
                    response.addAll(((ContactContainerFragment)getParentFragment()).getFriends());
                }
                ((ContactContainerFragment)getParentFragment()).addListenner(new ContactContainerFragment.NotifyReturnedFriends() {
                    @Override
                    public void onFriendsReceived(List<Contact> contacts) {

                        allContactsList.addAll(contacts);
                        Contact.removeDuplicates(allContactsList);
                        sortContacts(allContactsList);
                        notifyAdapter();
                    }
                });
                sortContacts(response);
                allContactsList.clear();
                allContactsList.addAll(response);


                notifyAdapter();
            }

            @Override
            public void failed(String error) {
            }
        };


        if(TYPE == FOLLOWING)
            new ListFollowingService(callback).execute();
        else
            new ListMyFollowersService(callback).execute();


    }

    private void notifyAdapter(){
        adapter.notifyDataSetChanged();
        checkIfHasContacts(allContactsList.size());
//        swipeLayout.setRefreshing(false);
    }

    private void checkIfHasContacts(int size) {
        Log.d("myapp", "checkIfHasContacts: " + allContactsList.size());
        if(TYPE != FOLLOWING)
            text_no_contact.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
    }



    private void searchContact(){
        final String searchParam = newSearch;
        if(searchParam.isEmpty())
            return;

        searchContainer.setVisibility(View.VISIBLE);
        progress_search.setVisibility(View.VISIBLE);
        contact_search.setVisibility(View.GONE);
        text_no_contact.setVisibility(View.GONE);
        new SearchContactService(searchParam, new CallbackMultiple<Contact, String>() {
            @Override
            public void success(final Contact contact) {
                Log.d("myapp", "searchContact success: ");
                if(getActivity() != null && contact != null) {
                    contact_search.setVisibility(View.VISIBLE);
                    progress_search.setVisibility(View.GONE);
                    Log.d("myapp", "searchContact success: dentro ");
                    text_no_contact_search.setVisibility(View.GONE);
                    contact.setIsFollower(false);
                    contact.setIsFriend(false);
                    mName.setText(contact.getName());
                    mUsername.setText(contact.getUsername());

                    mCircle.setText(contact.getLetters());

                    GradientDrawable bgShape = (GradientDrawable) mCircle.getBackground();
                    bgShape.setColor(Color.parseColor("#33b5e5"));

                    action_follow.setVisibility(View.VISIBLE);
                    action_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressOperation.setVisibility(View.VISIBLE);
                            action_follow.setVisibility(View.GONE);
                            new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_INVITE), new CallbackMultiple<Boolean, String>() {
                                @Override
                                public void success(Boolean response) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);


                                    if(getParentFragment() != null){
                                        ((ContactContainerFragment)getParentFragment()).reloadChildren();
                                    }

                                    Toast.makeText(AppController.getAppContext(), "Follow successful", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failed(String error) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);
                                    Toast.makeText(AppController.getAppContext(), " "+ error, Toast.LENGTH_LONG).show();
                                }
                            }).execute();
                        }
                    });

                }


            }

            @Override
            public void failed(String error) {
                if(getActivity() != null) {
                    progress_search.setVisibility(View.GONE);
                    text_no_contact_search.setText("No matches found for "+ searchParam);
                    text_no_contact_search.setVisibility(View.VISIBLE);
                    //searchContainer.setVisibility(View.GONE);

                }
            }
        }).execute();




//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }

    private void saveContactsOffline(List<Contact> contacts){

        Context ctx = AppController.getAppContext();
        new ModelCache<List<Contact>>().saveModel(ctx, contacts, getOfflineId());


    }

    public static void sortContacts(List<Contact> list){
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact obj1, Contact obj2) {
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    private String getOfflineId(){
        if(TYPE == FOLLOWING)
            return Global.FOLLOWING;
        else
            return Global.MYFOLLOWERS;
    }


    public interface ReloadContacts{
        void onReloadContacts();
    }

}
