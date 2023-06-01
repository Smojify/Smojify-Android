package com.smojify.smojify;
import android.os.AsyncTask;

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

                    String regexPattern = "<h1><span class=\"emoji\">(.+?)</span> (.+?)</h1>";
                    Pattern pattern = Pattern.compile(regexPattern);
                    Matcher matcher = pattern.matcher(response.toString());
                    if (matcher.find()) {
                        String emojiSlugName = matcher.group(1) + " " + matcher.group(2);
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
