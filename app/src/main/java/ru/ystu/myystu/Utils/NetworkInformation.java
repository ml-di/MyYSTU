package ru.ystu.myystu.Utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class NetworkInformation {

    public static boolean hasConnection() {
        try {
            return new ConnectWebsite().execute().get();
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
    }

    static class ConnectWebsite extends AsyncTask<Void, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                socket.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}

