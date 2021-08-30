package com.infosys.stocktake.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.HomeActivity;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
//import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.loans.AddLoanActivity;
import com.infosys.stocktake.loans.LoanDetailsActivity;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.User;
import com.infosys.stocktake.nfc.parser.NdefMessageParser;
import com.infosys.stocktake.nfc.record.ParsedNdefRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NfcReaderActivity extends AppCompatActivity {
    public static final String TAG = "NFC";
    public static final String ACTIVITY_NAME="nfcReader";
    private static final StockTakeFirebase<Item> itemRepo = new StockTakeFirebase<>(Item.class, "items");
    private TextView text;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private String source;
    private ImageView mImgCheck;
    private User user;
    private Loan loan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);
        text = (TextView) findViewById(R.id.text);
        mImgCheck = (ImageView) findViewById(R.id.imageView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Accept intent to check which screen it came from: ProfileSetup or AddLoan
        source = getIntent().getStringExtra("source");
        user = (User) getIntent().getSerializableExtra("UserIntent");
        loan = (Loan) getIntent().getSerializableExtra(AddLoanActivity.LOAN_INTENT_KEY);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveProfileIntent(String nfcTag) {
        user.setNfcTag(nfcTag);
        Toast.makeText(NfcReaderActivity.this, "User's nfc tag is " + user.getNfcTag(), Toast.LENGTH_SHORT).show();;
        String documentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StockTakeFirebase<User> userRepo = new StockTakeFirebase<>(User.class, User.USER_COLLECTION);

        userRepo.create( user , documentId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"successfully succeed");
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                ((Animatable) mImgCheck.getDrawable()).start();
                new CountDownTimer(2000, 1000) {
                    public void onFinish() {
                        // When timer is finished
                        // Execute your code here
                        startActivity(intent);
                    }
                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Fail to fail");
                e.printStackTrace();
            }
        });
    }

    private void resolveLoanIntent(String nfcTag) {
        // Retrieve User by NFC Tag, extract uid
        StockTakeFirebase<User> userRepo = new StockTakeFirebase<>(User.class, User.USER_COLLECTION);
        userRepo.compoundQuery(User.NFC_TAG, nfcTag)
                .addOnSuccessListener(new OnSuccessListener<ArrayList<User>>() {
                    @Override
                    public void onSuccess(ArrayList<User> users) {
                        loan.setLoaneeID(users.get(0).getUuid());

                        // Add uuid into Loan
                        StockTakeFirebase<Loan> loanRepo = new StockTakeFirebase<>(Loan.class, Loan.LOAN_COLLECTION);
                        loanRepo.create(loan, loan.getLoanID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(NfcReaderActivity.this, R.string.create_loan_success, Toast.LENGTH_SHORT).show();
                                Intent detailsIntent = new Intent(NfcReaderActivity.this, LoanDetailsActivity.class);
                                Log.d(TAG,"PASSING LOAN ID: "+loan.getLoanID());
                                itemRepo.query(loan.getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                                    @Override
                                    public void onSuccess(Item item) {
                                        int qtyAvailable = item.getQtyStatus().get(ItemStatus.AVAILABLE.toString());
                                        qtyAvailable-=loan.getQuantity();
                                        item.setQtyStatus(ItemStatus.AVAILABLE.toString(),qtyAvailable);
                                        Log.d(TAG,"Reducing item quantity to "+ qtyAvailable);
                                        itemRepo.update(item,item.getItemID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG,"Successfully reduced item quantity!");
                                                Toast.makeText(NfcReaderActivity.this, "Successfully loaned out item", Toast.LENGTH_SHORT).show();
                                                detailsIntent.putExtra(AddLoanActivity.LOAN_INTENT_KEY, loan);
                                                detailsIntent.putExtra(LoanDetailsActivity.PREVIOUS_ACTIVITY_KEY,ACTIVITY_NAME);
                                                startActivity(detailsIntent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       e.printStackTrace();
                    }
                });

    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            completeScan(msgs);
        }
    }

    private void completeScan(NdefMessage[] message) {
        if (message == null || message.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(message[0]);
        final int size = records.size();
        Log.d(TAG, "Length of record: " + size);

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            Log.d(TAG, str);
            builder.append(str).append("\n");
        }

        Toast.makeText(NfcReaderActivity.this, "NFC Tag " + builder.toString(), Toast.LENGTH_SHORT).show();

        // Accept intent to check which screen it came from: ProfileSetup or AddLoan;
        Toast.makeText(NfcReaderActivity.this, "Source: " + source, Toast.LENGTH_SHORT).show();

        if (Objects.requireNonNull(source).equals("profile")) {
            // Do profile things
            resolveProfileIntent(builder.toString());
        } else if (Objects.requireNonNull(source).equals("loan")) {
            // Do add loan things
            // Accept loan object and add the nfcTag
            resolveLoanIntent(builder.toString());
        } else {
            // Shouldn't be opened
            Log.d(TAG, "There's something wrong with NFC");
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        return String.valueOf(toDec(id));
    }

    // UTILS
    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
}