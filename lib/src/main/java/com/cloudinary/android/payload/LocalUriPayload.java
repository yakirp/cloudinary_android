package com.cloudinary.android.payload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.cloudinary.utils.Base64Coder;

import java.io.FileNotFoundException;

public class LocalUriPayload extends Payload<Uri> {
    static final String URI_KEY = "uri";

    public LocalUriPayload(Uri data) {
        super(data);
    }

    public LocalUriPayload(){
    }

    @Override
    public String toUri() {
        return URI_KEY + "://" + Base64Coder.encodeString(data.toString());
    }

    @Override
    void fromUri(String uri) {
        data = Uri.parse(Base64Coder.decodeString(Uri.parse(uri).getHost()));
    }

    @Override
    public long getLength(Context context) {
        return fetchFileSizeFromUri(context);
    }

    @Override
    public Object prepare(Context context) throws NotFoundException {
        try {
            return context.getContentResolver().openInputStream(data);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Local uri could not be found");
        }
    }

    private long fetchFileSizeFromUri(Context context) {
        Cursor returnCursor = null;
        long size = -1;

        try {
            returnCursor = context.getContentResolver().query(data, null, null, null, null);
            if (returnCursor != null && returnCursor.moveToNext()) {
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                size = returnCursor.getLong(sizeIndex);
            }
        } finally {
            if (returnCursor != null) {
                returnCursor.close();
            }
        }

        return size;
    }
}