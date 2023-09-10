package com.smojify.smojify;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtil {
    String emojiSlugName;

    public interface EmojiNameListener {
        void onEmojiNameFetched(String emojiName);
    }

    public String getEmojiSlugName(String emoji, EmojiNameListener listener) {
        String emojiName = null;
        String urlString = "https://emojipedia.org/" + emoji + "/";

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    StringBuilder response = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String regexPattern1 = "<span class=\"Emoji_emoji__P7Lkz __variable_344bdf Emoji_emoji-large__fRM8m !bg-transparent transform active:scale-75 transition-transform\">(.+?)</span>";
                    Pattern pattern1 = Pattern.compile(regexPattern1);
                    Matcher matcher1 = pattern1.matcher(response.toString());
                    String regexPattern2 = "<div class=\"SingleEmojiDescription_single-emoji-description-wrapper__6n_lB \"><div><i>(.+?)</i>";
                    Pattern pattern2 = Pattern.compile(regexPattern2);
                    Matcher matcher2 = pattern2.matcher(response.toString());
                    if (matcher1.find() && matcher2.find()) {
                        String emojiSlugName = matcher1.group(1) + " " + matcher2.group(1);
                        return emojiSlugName;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(String emojiName) {
                listener.onEmojiNameFetched(emojiName);
            }
        };
        task.execute(urlString);
        return emojiSlugName;
    }
}
