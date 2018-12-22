package com.sandlex.toolboxxy.lj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * /Users/sandlex/dev/sandlex.com/blog/content
 */
public class ImageDownloader {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().getName().equals("524093.md"))
                .forEach(post -> {
                    List<String> imageLinks = extractImageUrls(post);
                    try {
                        downloadImages(contentDir + "/images", getPostId(post), imageLinks);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static List<String> extractImageUrls(Path sourceFile) {
//        return Collections.singletonList("https://ic.pics.livejournal.com/sandlex/4594610/227262/227262_original.jpg");
        return Collections.singletonList("http://images24.fotki.com/v872/photos/9/918209/5011253/IMG_9445_hf_hf-vi.jpg");
    }

    private static void downloadImages(String imagesDir, String postId, List<String> imageUrls) throws IOException {
        prepareDirectory(imagesDir, postId);
        int index = 0;
        for (String imageUrl : imageUrls) {
            downloadImage(imagesDir, postId, index, imageUrl);
            index++;
        }
    }

    private static void prepareDirectory(String imagesDir, String postId) throws IOException {
        File imageDir = new File(imagesDir + "/" + postId);
        try {
            Files.deleteIfExists(imageDir.toPath());
        } catch (DirectoryNotEmptyException ex) {
            cleanAndDeleteDir(imageDir);
        }
        Files.createDirectory(imageDir.toPath());
    }

    private static void cleanAndDeleteDir(File imageDir) throws IOException {
        Files.list(imageDir.toPath()).forEach(file -> {
            try {
                Files.delete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.delete(imageDir.toPath());
    }

    private static void downloadImage(String imagesDir, String postId, int index, String imageUrl) {
        Path targetFile = Paths.get(getTargetFileName(imagesDir, postId, index, imageUrl));
        try (InputStream is = new URL(imageUrl).openStream()) {
            Files.copy(is, targetFile);
        } catch (FileNotFoundException e) {
            System.out.println(postId + " -> " + imageUrl);
            try {
                Files.copy(Paths.get(imagesDir + "/image-not-found.png"), targetFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTargetFileName(String imagesDir, String postId, int index, String imageUrl) {
        return imagesDir + "/" + postId + "/" + postId + String.format("_%03d", index) + getFileExtension(imageUrl);
    }

    private static String getFileExtension(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("."));
    }

    private static String getPostId(Path sourceFile) {
        String fileName = sourceFile.toFile().getName();
        return fileName.substring(0, fileName.indexOf("."));
    }

}
