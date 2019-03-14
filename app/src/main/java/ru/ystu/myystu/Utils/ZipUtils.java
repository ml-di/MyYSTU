package ru.ystu.myystu.Utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Observable;

public class ZipUtils {

    public Observable<String> unzipFile (File zipFile, File dirOut) {

        return Observable.create(emitter -> {

            try {

                if(!dirOut.exists())
                    dirOut.mkdirs();
                else {
                    File[] files = dirOut.listFiles();
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }

                ZipFile zipFiles = null;
                List<FileHeader> headers = null;
                try {
                    zipFiles = new ZipFile(zipFile);
                    zipFiles.setFileNameCharset("CP866");
                    headers = zipFiles.getFileHeaders();

                } catch (ZipException e) {
                    if(!emitter.isDisposed())
                        emitter.onError(e);
                }

                if(headers != null) {
                    for (int i = 0; i < headers.size(); i++) {
                        try {
                            zipFiles.extractFile(headers.get(i), dirOut.getAbsolutePath());

                            final String fileName = headers.get(i).getFileName();
                            final long fileSize = headers.get(i).getUncompressedSize();

                            final Date date = new Date(Zip4jUtil.dosToJavaTme(headers.get(i).getLastModFileTime()));
                            final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                            mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
                            final String stringTimeTemp = mSimpleDateFormat.format(date);

                            if(!emitter.isDisposed())
                                emitter.onNext(fileName + ":" + fileSize + "*" + stringTimeTemp);

                        } catch (ZipException e) {
                            if (!emitter.isDisposed())
                                emitter.onError(e);
                        }
                    }
                }

                if(!emitter.isDisposed())
                    emitter.onComplete();

            } catch (Exception e){

                if(!emitter.isDisposed())
                    emitter.onError(e);

            }
        });
    }
}
