package ru.ystu.myystu.Utils;

import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileInformation {

    public static String getFileType(final String url){

        final OkHttpClient client = new OkHttpClient();
        final Request mRequest = new Request.Builder()
                .url(url)
                .head()
                .build();

        String fileInfo = null;
        try (Response mResponse = client.newCall(mRequest).execute()) {
            if (mResponse.body() != null) {

                fileInfo = Objects.requireNonNull(mResponse.body().contentType()).toString();

                mResponse.close();
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

    public String getFileSize(long size){

        String response;

        if(size < 1024)
            response = size + " БАЙТ";
        else if(size < 1000000)
            response = (size / 1024) + " КБ";
        else if (size < 1000000000)
            response = (size / 1024 / 1024) + " МБ";
        else
            response = (size / 1024 / 1024 / 1024) + " ГБ";

        return response;
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

