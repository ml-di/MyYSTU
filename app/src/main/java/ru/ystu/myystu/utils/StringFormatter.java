package ru.ystu.myystu.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import androidx.annotation.NonNull;

public class StringFormatter {

    public SpannableStringBuilder getFormattedString(String text){

        SpannableStringBuilder formattedText;

        // Добавление спецсимволов
        String textPost = text
                .replaceAll("<br>", "\n")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replaceAll("&quot;", "\"")
                .replaceAll("&laquo;", "«")
                .replaceAll("&raquo;", "»")
                .replaceAll("&ndash;", "–")
                .replaceAll("&mdash;", "—")
                .replaceAll("&pound;", "£")
                .replaceAll("&euro;", "€")
                .replaceAll("&sect;", "§")
                .replaceAll("&copy;", "©")
                .replaceAll("&reg;", "®")
                .replaceAll("&trade;", "™")
                .replaceAll("&amp;", "&");

        formattedText = new SpannableStringBuilder(textPost);
        // Хештеги
        if(textPost.contains("#"))
            formattedText = getHashtag(formattedText);
        // Сслыки вконтакте
        if(text.contains("[club") || text.contains("[id"))
            formattedText = getVkLink(formattedText);

        formattedText = getUrlLink(formattedText);

        return formattedText;
    }

    private SpannableStringBuilder getHashtag (SpannableStringBuilder textSpannable){

        String text = textSpannable.toString();
        SpannableStringBuilder hashText = textSpannable;
        String hash;

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

                // Если последний символ хештега перенос строки, пробел, точка или запятая удаляем его
                if(Objects.equals(hash.substring(hash.length() -1), " ")
                        || Objects.equals(hash.substring(hash.length() -1), "\n")
                        || Objects.equals(hash.substring(hash.length() -1), ".")
                        || Objects.equals(hash.substring(hash.length() -1), ",")) {
                    hash = hash.substring(0, hash.length()-1);
                    index_e--;
                }

                if(hash.length() > 2)
                    hashText.setSpan(new LinkClickableSpan(hash, 0), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                index_c++;
            }
        }

        return hashText;
    }
    private SpannableStringBuilder getVkLink (SpannableStringBuilder textSpannable){

        SpannableStringBuilder linkText = textSpannable;
        String text = textSpannable.toString();
        String linkType;
        String link;

        int index_s = 0;    // Начальный индекс
        int index_e = 0;    // Конечный индекс
        int index_c = 0;    // Индекс для количества

        int position_id;    // Позиция ссылки на профиль
        int position_club;  // Позиция ссылки на группу / паблик

        while (index_s >= 0){

            position_id = text.indexOf("[id", index_c);
            position_club = text.indexOf("[club", index_c);

            // Определение первой ссылки
            if(position_id == -1)
                linkType = "[club";
            else
            if(position_club == -1)
                linkType = "[id";
            else {
                if(position_id < position_club)
                    linkType = "[id";
                else
                    linkType = "[club";
            }

            // Начальная позиция ссылки
            index_s = text.indexOf(linkType, index_c);
            index_c = index_s + 1;

            if(index_s >= 0){

                // Конечная позиция ссылки
                index_e = text.indexOf("]", index_c) + 1;

                link = text.substring(index_s, index_e);
                String linkName = link.substring(link.indexOf("|") + 1, link.indexOf("]"));         // Текст в записи
                String linkUrl = link.substring(link.indexOf("[") + 1, link.indexOf("|"));          // Ссылка
                linkText.setSpan(new LinkClickableSpan(linkUrl, 1), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                linkText = linkText.replace(index_s, index_e, linkName);

                index_c++;
            }
        }

        return linkText;
    }
    private SpannableStringBuilder getUrlLink (SpannableStringBuilder textSpannable){

        SpannableStringBuilder linkText = textSpannable;
        String text = textSpannable.toString();
        String url;
        String[] domains = new String[]{".com", ".ru", ".cc", ".org", ".su", ".net", ".рф", ".info",
            ".club", ".me", ".biz", ".cz", ".at", ".fm", ".ly", ".be", ".re", ".co", ".gl", ".tv",
            ".io", ".de", ".in", ".it", ".us", ".cn", ".fr", ".jp"};

        int index_s = 0;    // Начальный индекс
        int index_e = 0;    // Конечный индекс
        int index_c = 0;    // Индекс для количества

        // Используются для отслеживания завершения ссылки
        int isN;            // Если перенос строки
        int isSpace;        // Если пробел

        // Проверка на начиличе ссылок в тексте
        for(int i = 0; i < domains.length; i++){
            if(text.contains(domains[i])){
                while (index_s >= 0){

                    // Начало ссылки
                    index_s = text.indexOf(domains[i], index_c);
                    index_c = index_s;

                    if(index_s >= 0){

                        // Ищем конец ссылки
                        isN = text.indexOf("\n", index_c);
                        isSpace = text.indexOf(" ", index_c);
                        index_e = sortIndex(isN, isSpace, text, false);

                        // Ещем начало ссылки
                        isN = text.lastIndexOf("\n", index_c);
                        isSpace = text.lastIndexOf(" ", index_c);
                        index_s = sortIndex(isN, isSpace, text, true);

                        url = text.substring(index_s + 1, index_e);

                        // Если последний символ ссылки перенос строки, пробел, точка или запятая удаляем его
                        if(Objects.equals(url.substring(url.length() -1), " ")
                                || Objects.equals(url.substring(url.length() -1), "\n")
                                || Objects.equals(url.substring(url.length() -1), ".")
                                || Objects.equals(url.substring(url.length() -1), ",")) {
                            url = url.substring(0, url.length()-1);
                            index_e--;
                        }

                        // Почта
                        if(url.contains("@"))
                            linkText.setSpan(new LinkClickableSpan(url, 3), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // Ссылка
                        else
                            linkText.setSpan(new LinkClickableSpan(url, 2), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        index_c++;
                    }
                }
            }
        }

        return linkText;
    }

    private int sortIndex(int isN, int isSpace, String text, boolean isStart){

        int response;

        if(isN == -1 && isSpace == -1)
            response = -1;
        else
        {
            if(isN == -1)
                response = isSpace;
            else
            if(isSpace == -1)
                response = isN;
            else {
                if(isStart){
                    if(isN > isSpace)
                        response = isN;
                    else
                        response = isSpace;
                } else {
                    if(isN < isSpace)
                        response = isN;
                    else
                        response = isSpace;
                }
            }
        }

        if(response == -1 && !isStart)
            response = text.length();

        return response;
    }

    class LinkClickableSpan extends ClickableSpan{

        /*
        *   id:
        *   0 - хештег
        *   1 - сслыка внутри вк
        *   2 - url ссылка
        *   3 - почта
        *   4 - телефон
        * */

        String link;
        int id;

        LinkClickableSpan(String link, int id) {
            this.link = link;
            this.id = id;
        }

        public void onClick(@NonNull View tv) {

            String url = null;
            Intent intent = null;

            switch (id){
                // Хештег
                case 0:

                    url = "https://vk.com/feed?section=search&q=";

                    try {
                        link = URLEncoder.encode(link, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + link));

                    break;
                // Ссылка внутри вк
                case 1:
                    url = "https://vk.com/" + link;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    break;
                // url ссылка
                case 2:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    break;
                // Почта
                case 3:
                    intent = new Intent(Intent.ACTION_SENDTO)
                            .setType("text/plain")
                            .setData(Uri.parse("mailto:" + link));
                    break;
            }

            tv.getContext().startActivity(intent);
        }

        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(ds.linkColor);
        }
    }

}
