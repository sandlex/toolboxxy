package com.sandlex.toolboxxy.lj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Replaces img tags with markdown links to downloaded images.
 */
public class ImageReplacer {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");
                        List<String> imageTags = extractImageTags(post);
                        if (imageTags.isEmpty()) {
                            System.out.println("no images");
                        } else {
                            Path postImagesDir = Paths.get(contentDir + "/images/" + postId);
                            replaceImageTags(post, postId, postImagesDir, imageTags);
                        }
                        System.out.println("done with the post " + postId);
                        System.out.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static String getPostId(Path sourceFile) {
        String fileName = sourceFile.toFile().getName();
        return fileName.substring(0, fileName.indexOf("."));
    }

    private static List<String> extractImageTags(Path sourceFile) throws IOException {
        List<String> result = new ArrayList<>();

        String content = new String(Files.readAllBytes(sourceFile));

        int searchPosition = 0;

        while (true) {
            int tagStart = content.indexOf("<img", searchPosition);
            if (tagStart == -1) {
                break;
            } else {
                String tag = content.substring(tagStart, content.indexOf(">", tagStart) + 1);
                result.add(tag);
                searchPosition = tagStart + 1;
            }
        }

        return result;
    }

    private static void replaceImageTags(Path post, String postId, Path postImagesDir, List<String> imageTags) throws IOException {
        String content = new String(Files.readAllBytes(post));

        if (Files.list(postImagesDir)
                .filter(file -> file.toFile().isFile()
                        && file.toFile().getName().startsWith(postId + "_")).count() != imageTags.size()) {
            System.out.println("ERROR: number of images doesn't match number of tags");
            return;
        }

        try {
            int index = 0;
            for (String imageTag : imageTags) {
                String mdTag = "![[" + getImageFileName(postImagesDir, postId, index) + "]]";
                content = content.replace(imageTag, mdTag);
                System.out.println(imageTag + " -> " + mdTag);
                index++;
            }
            Files.write(post, content.getBytes());
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static String getImageFileName(Path postImagesDir, String postId, int index) throws RuntimeException, IOException {
        String partialFileName = postId + String.format("_%03d", index);
        Optional<Path> image = Files.list(postImagesDir)
                .filter(file -> file.toFile().getName().startsWith(partialFileName))
                .findFirst();

        if (image.isPresent()) {
            return "images/" + postId + "/" + image.get().toFile().getName();
        }

        throw new RuntimeException("no file matching " + partialFileName);
    }

}
