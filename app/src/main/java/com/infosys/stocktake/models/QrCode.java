package com.infosys.stocktake.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class QrCode {
    private final int height;
    private final int width;
    private String data;

    public QrCode(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public QrCode(int height, int width, String data) {
        this.height = height;
        this.width = width;
        this.data = data;
    }

    public Bitmap generateQrCode() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(this.data, BarcodeFormat.QR_CODE, this.width, this.height);
            Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565);

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String qrString = Base64.encodeToString(b, Base64.DEFAULT);

        return qrString;
    }

    /*
     * @param qrString (encoded string from bitmap
     * @return bitmap (decoded from the encoded string
     */
    public Bitmap stringToBitmap(String qrString) {
        try {
            byte[] encodedByte = Base64.decode(qrString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
