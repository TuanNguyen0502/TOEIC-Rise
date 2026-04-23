package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.dtos.responses.AudioResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class AudioUtils {
    public static AudioResource fetchAudio(String audioUrl) throws IOException {
        URL url = URI.create(audioUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String contentType = connection.getContentType();
            InputStream inputStream = connection.getInputStream();

            return new AudioResource(inputStream, contentType);
        } else {
            throw new IOException("Failed to fetch audio. HTTP Response Code: " + responseCode);
        }
    }
}
