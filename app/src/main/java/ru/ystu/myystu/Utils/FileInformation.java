package ru.ystu.myystu.Utils;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileInformation {

    public static String getFileType(final String url){

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .head()
                .build();

        String fileInfo = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                fileInfo = Objects.requireNonNull(response.body().contentType()).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileInfo;
    }

    public static String getExt(String fileType) {
        return parseType(fileType, 0);
    }

    public static String getFileName(String fileType){
        return parseType(fileType, 1);
    }

    private static String parseType(String fileType, int id){

        String result = null;

        if(!Objects.equals(fileType, null)){
            int startIndex = fileType.indexOf("name=") + 6;
            result = fileType.substring(startIndex, fileType.length() - 1);
            startIndex = result.lastIndexOf(".");
            switch (id){
                // Получить расширение
                case 0:
                    result = result.substring(startIndex);
                    break;
                // Поулчить имя файла
                case 1:
                    result = result.substring(0, startIndex);
                    break;
            }
        }

        return result;
    }
}

