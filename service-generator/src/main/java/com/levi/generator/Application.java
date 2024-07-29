package com.levi.generator;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Application {
    public static void main(String[] args) throws IOException {
        List<String> moduleTypes = Arrays.asList("microservice", "util", "consumer", "producer");
        String moduleType = args[0];
        String serviceName = args[1];
        String root = "../service-generator/src/main/resources/templates";
        copyDirectory(root + File.separator + moduleType, "../" + serviceName);
        updateFiles("../settings.gradle", "{{serviceName}}", String.format("%s", serviceName), "");

    }

    private static void updateFiles(String fileName, String textToSearch, String textToReplace, String escapeSpace) throws IOException {
        List<String> newLines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8)) {
            if (line.contains(textToSearch)) {
                newLines.add(line);
                String replace = line.replace(textToSearch, textToReplace).replace("//", escapeSpace);
                newLines.add(replace.replaceAll("'serviceName'", textToReplace.replace("-", "")));
            } else {
                newLines.add(line);
            }
        }
        Files.write(Paths.get(fileName), newLines, StandardCharsets.UTF_8);
    }


    public static Path createDirectories(String folderPath) throws IOException {
        Path path = Paths.get(folderPath);
        return Files.createDirectories(path);
    }


    private static void generateFile(HashMap<String, Object> scopes, MustacheFactory mf, String name, String a) throws IOException {
        Mustache mustache = mf.compile(name);
        Writer writer = new OutputStreamWriter(new FileOutputStream(new File(a)));
        mustache.execute(writer, scopes);
        writer.flush();
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
