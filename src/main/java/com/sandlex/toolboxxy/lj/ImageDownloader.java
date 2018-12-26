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
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");
                        List<String> imageLinks = extractImageUrls(post);
                        if (!imageLinks.isEmpty()) {
                            String imagesDir = contentDir + "/images";
                            File imageDir = new File(imagesDir + "/" + postId);
                            if (Files.exists(imageDir.toPath())) {
                                System.out.println("image dir exists, skipping");
                            } else {
                                downloadImages(imagesDir, postId, imageLinks);
                            }
                        } else {
                            System.out.println("no images");
                        }
                        System.out.println("done with the post " + postId);
                        System.out.println();
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

        System.out.println("found images: " + result.size());
        return result;
    }

    private static void downloadImages(String imagesDir, String postId, List<String> imageUrls) throws IOException {
        File imageDir = new File(imagesDir + "/" + postId);
        Files.createDirectory(imageDir.toPath());
        int index = 0;
        int total = imageUrls.size();
        for (String imageUrl : imageUrls) {
            System.out.println("downloading image " + (index + 1) + "/" + total);
            downloadImage(imagesDir, postId, index, imageUrl);
            index++;
        }
        System.out.println("all images downloaded");
    }

    private static void downloadImage(String imagesDir, String postId, int index, String imageUrl) {
        Path targetFile = Paths.get(getTargetFileName(imagesDir, postId, index, imageUrl));
        try (InputStream is = new URL(imageUrl).openStream()) {
            Files.copy(is, targetFile);
        } catch (FileNotFoundException e) {
            copyImagePlaceholder(postId, imageUrl, imagesDir, targetFile);
        } catch (IOException e) {
            copyImagePlaceholder(postId, imageUrl, imagesDir, targetFile);
            e.printStackTrace();
        }
    }

    private static void copyImagePlaceholder(String postId, String imageUrl, String imagesDir, Path targetFile) {
        System.out.println("file not found: " + postId + " -> " + imageUrl);
        try {
            Files.copy(Paths.get(imagesDir + "/image-not-found.png"), targetFile);
        } catch (IOException e1) {
            e1.printStackTrace();
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
