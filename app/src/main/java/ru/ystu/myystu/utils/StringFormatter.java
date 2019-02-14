package ru.ystu.myystu.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.annotation.NonNull;

public class StringFormatter {

    public Spannable getFormattedString(String text){

        Spannable formattedText;

        // Добавление пробелов и прочих знаков
        String textPost = text
                .replaceAll("<br>", "\n")
                .replaceAll("&gt;", ">");

        formattedText = new SpannableString(textPost);

    // Хештеги
        if(textPost.contains("#"))
            formattedText = getHashtag(formattedText);



        return formattedText;
    }

    private Spannable getHashtag (Spannable textSpannable){

        String text = textSpannable.toString();
        String hash;
        Spannable hashText = textSpannable;

        int index_s = 0;    // Начальный индекс
        int index_e = 0;    // Конечный индекс
        int index_c = 0;    // Индекс для количества

        // Используются для отслеживания завершения хештега
        int isN;            // Если перенос строки
        int isSpace;        // Если пробел
        int isHash;         // Если хештег

        while (index_s >= 0){

            index_s = text.indexOf("#", index_c);
            index_c = index_s + 1;

            // Если хештег действительно существует
            if(index_s >= 0){

                // Ищем первый пробел, хештег или новую строку
                isN = text.indexOf("\n", index_c);
                isSpace = text.indexOf(" ", index_c);
                isHash = text.indexOf("#", index_c);

                // Если ничего не найдено, то конец строки
                if(isN == -1 && isSpace == -1 && isHash == -1)
                    index_e = text.length();
                else
                {
                    if(isN == -1)
                        isN = Integer.MAX_VALUE;
                    if(isSpace == -1)
                        isSpace = Integer.MAX_VALUE;
                    if(isHash == -1)
                        isHash = Integer.MAX_VALUE;

                    // Сортировка на меньший индекс
                    index_e = Math.min(Math.min(isN, isSpace), isHash);
                }

                hash = text.substring(index_s, index_e);

                // Если последний символ хештега перенос строки, пробел или точка, удаляем его
                if(Objects.equals(hash.substring(hash.length() -1), " ")
                        || Objects.equals(hash.substring(hash.length() -1), "\n")
                        || Objects.equals(hash.substring(hash.length() -1), ".")) {
                    hash = hash.substring(0, hash.length()-1);
                    index_e--;
                }

                if(hash.length() > 2)
                    hashText.setSpan(new HashClickableSpan(hash), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                index_c++;
            }
        }

        return hashText;
    }

    class HashClickableSpan extends ClickableSpan{

        String hash;
        HashClickableSpan(String string) {
            hash = string;
        }

        public void onClick(@NonNull View tv) {

            String url = "https://vk.com/feed?section=search&q=";
            
            try {
                hash = URLEncoder.encode(hash, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Intent hashIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + hash));
            tv.getContext().startActivity(hashIntent);
        }

        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(ds.linkColor);
        }
    }

}
