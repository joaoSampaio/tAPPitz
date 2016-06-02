package com.tappitz.app.util;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tappitz.app.R;
import com.tappitz.app.ui.MainActivity;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

/**
 * Created by Sampaio on 30/04/2016.
 */
public class EmojiManager {

    EmojiconEditText emojiconEditText;
    View rootView;
    ImageView emojiButton;
    ImageView submitButton ;
    private MainActivity activity;
    EmojiconsPopup popup;

    public EmojiManager(MainActivity activity) {
        this.activity = activity;
        rootView = getActivity().findViewById(R.id.activity_root);
        popup = new EmojiconsPopup(rootView, activity);








        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_insert_emoticon);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });



    }


    public EmojiconEditText getEmojiconEditText() {
        return emojiconEditText;
    }

    public void setEmojiconEditText(EmojiconEditText emojiconEditText) {
        this.emojiconEditText = emojiconEditText;
    }

    public void setEmojiButton(ImageView openEmoji) {
        if(openEmoji == null && this.emojiButton != null){
            emojiButton.setOnClickListener(null);
            emojiButton = null;
            return;
        }
        this.emojiButton = openEmoji;
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if(!popup.isShowing()){

                    //If keyboard is visible, simply show the emoji popup
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard_black);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else{
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard_black);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }



    public MainActivity getActivity() {
        return activity;
    }
}
