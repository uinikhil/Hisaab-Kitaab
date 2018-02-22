package in.aadara.hisaabkitaab.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.aadara.hisaabkitaab.localDB.User;

/**
 * Created by umashankarpathak on 24/01/18.
 */

public class AppIndexingUpdateService extends JobIntentService {

    private static final int UNIQUE_JOB_ID = 42;

    public static void enqueueWork(Context context) {
        enqueueWork(context, AppIndexingUpdateService.class, UNIQUE_JOB_ID, new Intent());
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final ArrayList<Indexable> indexableNotes = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Indexable noteToIndex = Indexables.noteDigitalDocumentBuilder()
                                .setName(user.getName())
                                .setText(user.getRemark())
                                .setUrl(user.getAddress())
                                .build();

                        indexableNotes.add(noteToIndex);
                    }
                }
                if (indexableNotes.size() > 0) {
                    Indexable[] notesArr = new Indexable[indexableNotes.size()];
                    notesArr = indexableNotes.toArray(notesArr);

                    // batch insert indexable notes into index
                    FirebaseAppIndex.getInstance().update(notesArr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
