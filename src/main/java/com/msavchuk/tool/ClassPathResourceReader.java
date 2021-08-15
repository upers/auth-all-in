package com.msavchuk.tool;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ClassPathResourceReader {

    public byte[] readResourceBytes(String path) throws IOException {
//        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (var is =  getClass().getClassLoader().getResourceAsStream(path)) {
            return is.readAllBytes();
        }
    }
}
