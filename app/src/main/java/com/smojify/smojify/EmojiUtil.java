package com.smojify.smojify;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

                    int start = response.indexOf("<h1>");
                    int end = response.indexOf("</h1>");
                    if (start != -1 && end != -1) {
                        emojiSlugName = response.substring(start + 4, end).replace(" emoji", "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return emojiSlugName;
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
