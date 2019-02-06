package ru.ystu.myystu.function;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FileInformation {

    public static String getFileType(final String url){
        String result = null;

        try {
            result = new LoadFileInfo().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
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
            startIndex = result.indexOf(".");
            switch (id){
                // Получить расширение
                case 0:
                    result = result.substring(startIndex, result.length());
                    break;
                // Поулчить имя файла
                case 1:
                    result = result.substring(0, startIndex);
                    break;
            }
        }

        return result;
    }

    static class LoadFileInfo extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(final String... url) {

            final String url_s = url[0];
            String fileInfo = null;

            URL url_file = null;
            try {
                url_file = new URL(url_s);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection conn = null;
            try {
                if (url_file != null) {
                    conn = url_file.openConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert conn != null;
            fileInfo = conn.getContentType();

            return fileInfo;
        }
    }

}

