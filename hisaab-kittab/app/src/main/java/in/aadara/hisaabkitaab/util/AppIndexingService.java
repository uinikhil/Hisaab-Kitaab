package in.aadara.hisaabkitaab.util;

import android.app.IntentService;
import android.content.Intent;
import android.provider.ContactsContract;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;

import java.util.ArrayList;

import in.aadara.hisaabkitaab.localDB.User;

/**
 * Created by umashankarpathak on 24/01/18.
 */

public class AppIndexingService extends IntentService {
    public AppIndexingService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<Indexable> indexableNotes = new ArrayList<>();

//        for (User recipe : getAllRecipes()) {
//            ContactsContract.CommonDataKinds.Note note = recipe.getNote();
//            if (note != null) {
//                Indexable noteToIndex = Indexables.noteDigitalDocumentBuilder()
//                        .setName(recipe.getTitle() + " Note")
//                        .setText(note.getText())
//                        .setUrl(recipe.getNoteUrl())
//                        .build();
//
//                indexableNotes.add(noteToIndex);
//            }
//        }
//
//        if (indexableNotes.size() > 0) {
//            Indexable[] notesArr = new Indexable[indexableNotes.size()];
//            notesArr = indexableNotes.toArray(notesArr);
//
//            // batch insert indexable notes into index
//            FirebaseAppIndex.getInstance().update(notesArr);
//        }
    }
}
