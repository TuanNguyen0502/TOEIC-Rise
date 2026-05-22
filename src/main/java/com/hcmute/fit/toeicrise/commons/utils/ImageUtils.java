package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.dtos.responses.ImageResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ImageUtils {
    public static ImageResource fetchImage(String imageUrl) throws IOException {
        URL url = URI.create(imageUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String contentType = connection.getContentType();
            InputStream inputStream = connection.getInputStream();

            return new ImageResource(inputStream, contentType);
        } else {
            throw new IOException("Failed to fetch image. HTTP Response Code: " + responseCode);
        }
    }
}
