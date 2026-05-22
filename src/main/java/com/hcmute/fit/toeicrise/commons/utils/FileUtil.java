package com.hcmute.fit.toeicrise.commons.utils;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    public static boolean isValidFile(MultipartFile file) {
        String filePath = file.getOriginalFilename();
        if (filePath == null)
            return false;
        return filePath.endsWith(".xlsx") || filePath.endsWith(".xls") || filePath.endsWith(".xlsm");
    }
}
