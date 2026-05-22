package com.hcmute.fit.toeicrise.dtos.responses;

import java.io.InputStream;

public record AudioResource(InputStream inputStream, String contentType) {
}
