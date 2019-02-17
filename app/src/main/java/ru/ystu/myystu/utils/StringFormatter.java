package ru.ystu.myystu.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // Url ссылки
        formattedText = getUrlLink(formattedText);
        // Телефонные номера
        formattedText = getPhoneNumber(formattedText);
        // Электронные почты
        if(text.contains("@"))
            formattedText = getEmail(formattedText);

        return formattedText;
    }

    private SpannableStringBuilder getHashtag (SpannableStringBuilder textSpannable){

        String text = textSpannable.toString();

        Pattern pattern = Pattern.compile("#[а-яА-Яa-zA-Z0-9\\-_!@%$&*()=+\"№;:?{}]{3,}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()){
            int index_s = matcher.start();
            int index_e = matcher.end();
            String hash = text.substring(index_s, index_e);

            textSpannable.setSpan(new LinkClickableSpan(hash, 0), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpannable;
    }
    private SpannableStringBuilder getVkLink (SpannableStringBuilder textSpannable){

        SpannableStringBuilder linkText = textSpannable;
        String text = textSpannable.toString();

        Pattern pattern = Pattern.compile("\\[(id|club)\\d+\\|[^]]+]");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()){

            int index_s = matcher.start();
            int index_e = matcher.end();
            String link = text.substring(index_s, index_e);

            String linkName = link.substring(link.indexOf("|") + 1, link.indexOf("]"));         // Текст в записи
            String linkUrl = link.substring(link.indexOf("[") + 1, link.indexOf("|"));          // Ссылка

            linkText.setSpan(new LinkClickableSpan(linkUrl, 1), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkText = linkText.replace(index_s, index_e, linkName);

        }

        return linkText;
    }
    private SpannableStringBuilder getUrlLink (SpannableStringBuilder textSpannable){

        String text = textSpannable.toString();

        Pattern pattern = Pattern.compile("(https?|ftp|file)://[a-zA-Zа-яА-Я0-9+&#/%?=~_-|!:,.;]+\\.[a-zA-Zа-яА-Я0-9+&@#/%=~_\\-|]+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()){
            int index_s = matcher.start();
            int index_e = matcher.end();
            String url = text.substring(matcher.start(), matcher.end());

            textSpannable.setSpan(new LinkClickableSpan(url, 2), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpannable;
    }
    private SpannableStringBuilder getEmail (SpannableStringBuilder textSpannable){

        String text = textSpannable.toString();

        Pattern pattern = Pattern.compile("[a-zA-Z0-9+_\\-.]+@[a-zA-Z0-9+_\\-.]+\\.[a-zA-Z0-9+_\\-]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            int index_s = matcher.start();
            int index_e = matcher.end();
            String email = text.substring(index_s, index_e);

            textSpannable.setSpan(new LinkClickableSpan(email, 3), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpannable;
    }
    private SpannableStringBuilder getPhoneNumber (SpannableStringBuilder textSpannable){

        String text = textSpannable.toString();

        Pattern pattern = Pattern
                .compile("(\\+?([78])(-|\\s)?\\(?([89])\\d{2}\\)?(\\s|-)?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2})|(\\+?\\s?(([78])?\\s?\\(\\d{3,4}\\))?\\s?\\d{1,3}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            int index_s = matcher.start();
            int index_e = matcher.end();
            String number = text.substring(index_s, index_e);

            textSpannable.setSpan(new LinkClickableSpan(number, 4), index_s, index_e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpannable;
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
                    if(URLUtil.isValidUrl(link))
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    break;
                // Почта
                case 3:
                    intent = new Intent(Intent.ACTION_SENDTO)
                            .setType("text/plain")
                            .setData(Uri.parse("mailto:" + link));
                    break;
                case 4:
                    intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", link, null));
                    break;
            }

            if(intent != null)
                tv.getContext().startActivity(intent);
        }

        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(ds.linkColor);
        }
    }

}
