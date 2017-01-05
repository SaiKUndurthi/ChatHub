package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sfsu.csc780.chathub.model.ChatMessage;
import edu.sfsu.csc780.chathub.R;

public class MessageUtil {
    public static final String MESSAGES_CHILD = "messages";
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private static FirebaseStorage sStorage = FirebaseStorage.getInstance();
    private static MessageLoadListener sAdapterListener;
    private static MediaPlayer mediaPlayer;


    public interface MessageLoadListener { public void onLoadComplete(); }

    public static void send(ChatMessage chatMessage) {
        sFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public static View.OnClickListener sMessageViewListener;

        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public TextView timestampTextView;
        public View messageLayout;
        public ImageButton audioPlayView;
        public ImageButton audioStopView;
        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestampTextView);
            audioPlayView = (ImageButton) itemView.findViewById(R.id.messageAudioView);
            audioStopView = (ImageButton)  itemView.findViewById(R.id.messageAudioStopView);
            messageLayout = (View) itemView.findViewById(R.id.messageLayout);
            v.setOnClickListener(sMessageViewListener);
        }
    }


    public static FirebaseRecyclerAdapter getFirebaseAdapter(final Activity activity,
                                                             MessageLoadListener listener,
                                                             final LinearLayoutManager linearManager,
                                                             final RecyclerView recyclerView,
                                                             final View.OnClickListener clickListener
                                                            ) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        sAdapterListener = listener;
        MessageViewHolder.sMessageViewListener = clickListener;

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ChatMessage,
                MessageViewHolder>(
                ChatMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                sFirebaseDatabaseReference.child(MESSAGES_CHILD)) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              final ChatMessage chatMessage, int position) {
                sAdapterListener.onLoadComplete();

                viewHolder.messageTextView.setText(chatMessage.getText());
                viewHolder.messengerTextView.setText(chatMessage.getName());
                if (chatMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(activity,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    SimpleTarget target = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            viewHolder.messengerImageView.setImageBitmap(bitmap);
                            final String palettePreference = activity.getString(R.string
                                    .auto_palette_preference);

                            if (preferences.getBoolean(palettePreference, false)) {
                                DesignUtils.setBackgroundFromPalette(bitmap, viewHolder
                                        .messageLayout);
                            } else {
                                viewHolder.messageLayout.setBackground(
                                        activity.getResources().getDrawable(
                                                R.drawable.message_background));
                            }

                        }
                    };
                    Glide.with(activity)
                            .load(chatMessage.getPhotoUrl())
                            .asBitmap()
                            .into(target);
                }
                if(chatMessage.getImageUrl()!=null || chatMessage.getAudioUrl()!=null) {
                    // Checks for the presence of either the audio or image url. If not directly goes to the Text message view holder.
                    if (chatMessage.getImageUrl() != null && chatMessage.getAudioUrl() == null) {
                        // Checks if data contains only image url, to load Image message view holder.
                        Log.i("IMAGE URL ",chatMessage.getImageUrl());
                        viewHolder.messageImageView.setVisibility(View.VISIBLE);
                        viewHolder.messageTextView.setVisibility(View.GONE);
                        viewHolder.audioPlayView.setVisibility(View.GONE);
                        viewHolder.audioStopView.setVisibility(View.GONE);

                        try {
                            final StorageReference gsReference =
                                    sStorage.getReferenceFromUrl(chatMessage.getImageUrl());
                            Log.i("DOWNLOAD IMAGE URL ",chatMessage.getImageUrl()+" "+gsReference.getDownloadUrl() );
                            gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(activity)
                                            .load(uri)
                                            .into(viewHolder.messageImageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.i("IMAGE FAILURE", "Could not load image for message", exception);
                                }
                            });
                        } catch (IllegalArgumentException e) {
                            Log.e("IMAGE ERROR", e.getMessage() + " : " + chatMessage.getImageUrl());
                        }
                    } else {
                        // Checks if data contains only audio url, to load Audio message view holder.
                        viewHolder.messageImageView.setVisibility(View.GONE);
                        viewHolder.messageTextView.setVisibility(View.GONE);
                        viewHolder.audioPlayView.setVisibility(View.VISIBLE);
                        viewHolder.audioStopView.setVisibility(View.VISIBLE);
                        // PLAY BUTTON is drawn here.
                        viewHolder.audioPlayView.setImageDrawable(ContextCompat
                                .getDrawable(activity,
                                        R.drawable.ic_play_circle_filled_black_24dp));
                        // STOP BUTTON is drawn here.
                        viewHolder.audioStopView.setImageDrawable(ContextCompat
                                .getDrawable(activity,
                                        R.drawable.ic_stop_black_24dp));
                        // Animations to Fade out and also to disable the stop button. Because the user has not clicked Play button yet.
                        final Animation animationFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
                        viewHolder.audioStopView.setClickable(false);
                        viewHolder.audioStopView.setEnabled(false);
                        viewHolder.audioStopView.startAnimation(animationFadeOut);
                            try{
                                  final StorageReference gsReferenc =
                                    sStorage.getReferenceFromUrl(chatMessage.getAudioUrl());
                            gsReferenc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    viewHolder.audioStopView.setImageDrawable(ContextCompat.getDrawable(
                                                activity, R.drawable.ic_stop_black_24dp
                                        ));
                                    // onClick for STOP BUTTON.
                                        viewHolder.audioStopView.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                // Animations to Fade in / Fade out play and stop button. 
                                                final Animation animationFadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
                                                final Animation animationFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
                                                
                                                // Condition to check if Media Player instance is created or not.
                                                if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                                                    // Animations to Fade in play, Fade out stop and also to disable the stop button ,enable the play.
                                                    // Because the user has clicked Stop button .
                                                    viewHolder.audioPlayView.setClickable(true);
                                                    viewHolder.audioPlayView.setEnabled(true);
                                                    viewHolder.audioPlayView.startAnimation(animationFadeIn);

                                                    viewHolder.audioStopView.setClickable(false);
                                                    viewHolder.audioStopView.setEnabled(false);
                                                    viewHolder.audioStopView.startAnimation(animationFadeOut);
                                                    mediaPlayer.stop();

                                                }
                                            }
                                        });

                                    // onClick for PLAY BUTTON.
                                        viewHolder.audioPlayView.setImageDrawable(ContextCompat.getDrawable(
                                                activity, R.drawable.ic_play_circle_filled_black_24dp
                                        ));
                                        final Animation animationFadeOut = AnimationUtils.loadAnimation(activity,R.anim.fade_out);
                                        final Animation animationFadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
                                        viewHolder.audioPlayView.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                // Playing music is handled by this thread in the background.
                                                new Thread() {
                                                    public void run() {
                                                        // runOnUiThread: the play action is posted to the event queue of the UI thread.
                                                        // Helps in reducing the skipping of frames while playing music.
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    viewHolder.audioPlayView.setClickable(false);
                                                                    viewHolder.audioPlayView.setEnabled(false);
                                                                    viewHolder.audioPlayView.startAnimation(animationFadeOut);
                                                                    viewHolder.audioStopView.setClickable(true);
                                                                    viewHolder.audioStopView.setEnabled(true);
                                                                    viewHolder.audioStopView.startAnimation(animationFadeIn);
                                                                    mediaPlayer = MediaPlayer.create(activity, uri);
                                                                    mediaPlayer.start();
                                                                    // Register observers to listen for when song is completed
                                                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                                                                                                            @Override
                                                                                                            public void onCompletion(MediaPlayer mp) {
                                                                                                                // On Song completion Fade out and disable Stop button.
                                                                                                                // Fade in and enable Play button.
                                                                                                                viewHolder.audioPlayView.setClickable(true);
                                                                                                                viewHolder.audioPlayView.setEnabled(true);
                                                                                                                viewHolder.audioPlayView.startAnimation(animationFadeIn);
                                                                                                                viewHolder.audioStopView.setClickable(true);
                                                                                                                viewHolder.audioStopView.setEnabled(true);
                                                                                                                viewHolder.audioStopView.startAnimation(animationFadeIn);
                                                                                                            }
                                                                                                        });
                                                                                                    }});
                                                                 }}.start();
                                                    }});
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("ERROR ",e.toString());
                                            }
                                        });
                        }catch (IllegalArgumentException e) {
                            Log.e("IMAGE ERROR", e.getMessage() + " : " + chatMessage.getAudioUrl());
                        }
                    }
                }else  {
                    // Text message View holder.
                    viewHolder.messageImageView.setVisibility(View.GONE);
                    viewHolder.messageTextView.setVisibility(View.VISIBLE);
                    viewHolder.audioPlayView.setVisibility(View.GONE);
                    viewHolder.audioStopView.setVisibility(View.GONE);
                }

                long timestamp = chatMessage.getTimestamp();
                if (timestamp == 0 || timestamp == chatMessage.NO_TIMESTAMP ) {
                    viewHolder.timestampTextView.setVisibility(View.GONE);
                } else {
                    viewHolder.timestampTextView.setText(DesignUtils.formatTime(activity,
                            timestamp));
                    viewHolder.timestampTextView.setVisibility(View.VISIBLE);
                }

            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = adapter.getItemCount();
                int lastVisiblePosition = linearManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        return adapter;
    }



    public static StorageReference getImageStorageReference(FirebaseUser user, Uri uri) {
        //Create a blob storage reference with path : bucket/userId/timeMs/filename
        long nowMs = Calendar.getInstance().getTimeInMillis();

        return sStorage.getReference().child(user.getUid() + "/" + nowMs + "/" + uri
                .getLastPathSegment());
    }

}