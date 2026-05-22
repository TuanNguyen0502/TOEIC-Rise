package com.hcmute.fit.toeicrise.dtos.responses;

import java.io.InputStream;

public record ImageResource(InputStream inputStream, String contentType) {
}
