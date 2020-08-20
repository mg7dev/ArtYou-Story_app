package com.dw.artyou.helper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    private static String failReason;

    static String errorReason() {
        return failReason;
    }

    public static String getPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return getRealPathFromURI_BelowAPI19(context, uri);
        } else {
            return getRealPathFromURI_API19(context, uri);
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {


        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            } else if (isRawDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                String subFolderName = getSubFolders(uri);

                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + subFolderName + fileName;
                }
                String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);

                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }
                if (id.startsWith("raw%3A%2F")) {
                    id = id.replaceFirst("raw%3A%2F", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            } else {
                return getDriveFilePath(uri, context);
            }

        } else if (isGoogleDriveUri(uri)) {
            return getDriveFilePath(uri, context);
        } else if (isOneDriveDocument(uri)) {
            return getDriveFilePath(uri, context);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                getDriveFilePath(uri, context);
                return uri.getLastPathSegment();
            }
            if (getDataColumn(context, uri, null, null) == null) {
                failReason = "dataReturnedNull";
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else if ("files".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else if ("files".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    public static String getSubFolders(Uri uri) {
        String replaceChars = String.valueOf(uri).replace("%2F", "/").replace("%20", " ").replace("%3A", ":");
        String[] bits = replaceChars.split("/");
        String sub5 = bits[bits.length - 2];
        String sub4 = bits[bits.length - 3];
        String sub3 = bits[bits.length - 4];
        String sub2 = bits[bits.length - 5];
        String sub1 = bits[bits.length - 6];
        if (sub1.equals("Download")) {
            return sub2 + "/" + sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub2.equals("Download")) {
            return sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub3.equals("Download")) {
            return sub4 + "/" + sub5 + "/";
        } else if (sub4.equals("Download")) {
            return sub5 + "/";
        } else {
            return "";
        }
    }

    public static String getRealPathFromURI_BelowAPI19(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            failReason = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public static String getFilePath(Context context, Uri uri) {
        Cursor cursor = null;
        final String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME};
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);

                return cursor.getString(index);
            }
        } catch (Exception e) {
            failReason = e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isRawDownloadsDocument(Uri uri) {
        String uriToString = String.valueOf(uri);
        return uriToString.contains("com.android.providers.downloads.documents/document/raw");
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isOneDriveDocument(Uri uri) {
        return "com.microsoft.skydrive.content".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }


    public static String getDriveFilePath(Uri uri, Context context) {
        try {
            Uri returnUri = uri;
            Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
                   move to the first row in the Cursor, get the data,
                   and display it.
              */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            String name = (returnCursor.getString(nameIndex));
            String size = (Long.toString(returnCursor.getLong(sizeIndex)));
            File file = new File(context.getCacheDir(), name);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read = 0;
                int maxBufferSize = 1 * 1024 * 1024;
                int bytesAvailable = inputStream.available();

                //int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                Log.e("File Size", "Size " + file.length());
                inputStream.close();
                outputStream.close();
                Log.e("File Path", "Path " + file.getPath());
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
                return "";

            }
            return file.getPath();
        } catch (Exception e) {
            return "";
        }
    }
}