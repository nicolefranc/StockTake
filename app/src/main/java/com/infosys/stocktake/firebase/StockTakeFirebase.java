package com.infosys.stocktake.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StockTakeFirebase<TEntity> {

    private static final String TAG = "StockTake Firebase Operation";
    private final Class<TEntity> entityClass;
    private final CollectionReference collectionRef;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    public StockTakeFirebase(Class<TEntity> entityClass, String collectionName){
        this.entityClass = entityClass;
        db = FirebaseFirestore.getInstance();
        this.collectionRef = db.collection(collectionName);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     *
     * @param uuid
     * @return Task<TEntity>: implement: StockTakeFirebase.query(uuid).continueWith(...)
     * Functions that make use of the query must be implemented within the continueWith scope.
     */
    public Task<TEntity> query(String uuid){
        final String documentID = uuid;
        final DocumentReference docRef = collectionRef.document(documentID);
        return docRef.get().continueWith(new Continuation<DocumentSnapshot, TEntity>() {
            @Override
            public TEntity then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot docSnap = task.getResult();
                if(docSnap.exists()){
                    return docSnap.toObject(entityClass);
                }
                else{
                    Log.w(TAG,"Document "+documentID + "does not exist");
                    return null;
                }
            }
        });
    }
    // for querying multiple documents with certain values
    public Task<ArrayList<TEntity>> compoundQuery(String field, Object value){
        final String valString = value.toString();
        Query query = collectionRef.whereEqualTo(field,value);
        return query.get().continueWith(new Continuation<QuerySnapshot, ArrayList<TEntity>>() {
            @Override
            public ArrayList<TEntity> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                QuerySnapshot querySnap = task.getResult();
                if(querySnap.isEmpty()){
                    Log.w(TAG,"Query " + valString + "does not yield any result");
                    return null;
                }
                else{
                    List<DocumentSnapshot> docList= querySnap.getDocuments();
                    ArrayList<TEntity> returnList = new ArrayList<>();
                    for(DocumentSnapshot doc:docList){
                        returnList.add(doc.toObject(entityClass));
                    }
                    return returnList;
                }
            }
        });
    }

    public Task<ArrayList<TEntity>> getCollection() {
        return collectionRef.get().continueWith(new Continuation<QuerySnapshot, ArrayList<TEntity>>() {
            @Override
            public ArrayList<TEntity> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                QuerySnapshot querySnap = task.getResult();
                if (querySnap.isEmpty()) {
                    Log.w(TAG, "Collection is empty");
                    return null;
                } else {
                    List<DocumentSnapshot> docList = querySnap.getDocuments();
                    ArrayList<TEntity> returnList = new ArrayList<>();
                    for (DocumentSnapshot doc : docList) {
                        returnList.add(doc.toObject(entityClass));
                    }
                    return returnList;
                }

            }
        });
    }

    public Task<Void> create(TEntity entity, String uuid){
        final String uuidString = uuid;
        DocumentReference docRef = collectionRef.document(uuidString);
        return docRef.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error in creating document: "+uuidString);
            }
        });
    }
    public Task<Void> update(TEntity entity, String uuid){
        final String uuidString = uuid;
        DocumentReference docRef = collectionRef.document(uuidString);
        return docRef.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error in updating document: "+uuidString);
            }
        });
    }
    public Task<Void> delete (String uuid){
        final String uuidString = uuid;
        DocumentReference docRef = collectionRef.document(uuidString);
        return docRef.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error in deleting document: "+uuidString);
            }
        });
    }
}
