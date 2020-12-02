package com.infosys.stocktake.inventory.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.infosys.stocktake.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ViewQrActivity extends AppCompatActivity {
    ImageView ivQrCode;
    Button downloadQr;
    final static String TAG = ViewQrActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);
        Intent intent = getIntent();
        String itemName = intent.getStringExtra("ItemName");
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("QRBitMap");

        ivQrCode = findViewById(R.id.ivQrCode);
        ivQrCode.setImageBitmap(bitmap);

        downloadQr = findViewById(R.id.downloadQrBtn);

        downloadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(ViewQrActivity.this, "Downloading image...", Toast.LENGTH_SHORT).show();
//                    saveFile(getApplicationContext(), bitmap, "test pic name");
//                    saveFileToExternalStorage(bitmap);
//                    saveFile2(bitmap);
                    saveBitmap(getApplicationContext(), bitmap, Bitmap.CompressFormat.PNG, "image/jpeg", itemName);
                    Toast.makeText(
                            ViewQrActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void saveFile(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }
        ;
    }

    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void saveFileToExternalStorage(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().toString();
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        OutputStream fOut = null;

        String imageName = "yourImageName";
        File file = new File(path, imageName);
        try {
            fOut = new FileOutputStream(file);
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)) {
                Log.e("Log", "error while saving bitmap " + path + imageName);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void saveFile2(Bitmap result) {
        Context mContext = getApplicationContext();
        if (result != null) {
            File dir = new File(mContext.getFilesDir(), "MyImages");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File destination = new File(dir, "image.jpg");

            try {
                destination.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(destination);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
//                selectedFile = destination;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    private void saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                            @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
                            @NonNull final String displayName) throws IOException
    {
        final String relativeLocation = Environment.DIRECTORY_PICTURES;

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try
        {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null)
            {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null)
            {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false)
            {
                throw new IOException("Failed to save bitmap.");
            }
        }
        catch (IOException e)
        {
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
}