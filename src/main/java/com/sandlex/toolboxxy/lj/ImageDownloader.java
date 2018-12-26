package com.sandlex.toolboxxy.lj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Downloads images from posts.
 */
public class ImageDownloader {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().getName().equals("524093.md"))
                .forEach(post -> {
                    try {
                        List<String> imageLinks = extractImageUrls(post);
                        if (!imageLinks.isEmpty()) {
                            String postId = getPostId(post);
                            String imagesDir = contentDir + "/images";
                            File imageDir = new File(imagesDir + "/" + postId);
                            if (Files.exists(imageDir.toPath())) {
                                System.out.println("Image dir exists, skipping post " + postId);
                            } else {
                                downloadImages(imagesDir, postId, imageLinks);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static List<String> extractImageUrls(Path sourceFile) throws IOException {
        List<String> result = new ArrayList<>();

        String content = new String(Files.readAllBytes(sourceFile));

        int searchPosition = 0;

        while (true) {
            int foundPosition = content.indexOf("<img", searchPosition);
            if (foundPosition == -1) {
                break;
            } else {
                int srcPosition = content.indexOf("src=\"", foundPosition) + 5;
                String src = content.substring(srcPosition, content.indexOf("\"", srcPosition));
                result.add(src);
                searchPosition = foundPosition + 1;
            }
        }

        return result;
    }

    private static void downloadImages(String imagesDir, String postId, List<String> imageUrls) throws IOException {
        File imageDir = new File(imagesDir + "/" + postId);
        Files.createDirectory(imageDir.toPath());
        int index = 0;
        for (String imageUrl : imageUrls) {
            downloadImage(imagesDir, postId, index, imageUrl);
            index++;
        }
    }

    private static void downloadImage(String imagesDir, String postId, int index, String imageUrl) {
        Path targetFile = Paths.get(getTargetFileName(imagesDir, postId, index, imageUrl));
        try (InputStream is = new URL(imageUrl).openStream()) {
            Files.copy(is, targetFile);
        } catch (FileNotFoundException e) {
            System.out.println("file not found: " + postId + " -> " + imageUrl);
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
