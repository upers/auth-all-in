package com.msavchuk.tool;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileHelper {

    public String getProjectDir() {
        try {
            Class<?> callingClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            URL url = callingClass.getProtectionDomain().getCodeSource().getLocation();
            URI parentDir = url.toURI().resolve("..");
            return parentDir.getPath();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public File getFileFromResourceResource(String path) {
        String realPath = FileHelper.class.getResource(path).getFile();
        System.out.println(realPath);
        return new File(realPath);
//        String projectDir = getProjectDir();
//        String filePath = "resources/path";

//        return new File(projectDir + filePath);
    }
}
