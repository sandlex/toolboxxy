package com.sandlex.toolboxxy.lj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts tags from ["tag", "tag2"] into [#tag, #tag2]
 */
public class TagsConverter {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir);
        int counter = 0;

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");

                        String content = new String(Files.readAllBytes(post));
                        String tagLine = getTagLine(content);
                        if (content.contains("tags = []")) {
                            System.out.println("no tags... skipping");
                        } else if (!tagLine.contains("\"")) {
                            System.out.println("already processed... skipping");
                        } else {
                            String processedTagLine = getProcessedTagLine(tagLine);
                            content = content.replace(tagLine, processedTagLine);
                            System.out.println("changing tags: " + tagLine + " -> " + processedTagLine);
                            Files.write(post, content.getBytes());
                        }

                        System.out.println("done with the post " + postId);
                        System.out.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("++++++ StringIndexOutOfBoundsException " + post);
                    }
                });
    }

    private static String getPostId(Path sourceFile) {
        String fileName = sourceFile.toFile().getName();
        return fileName.substring(0, fileName.indexOf("."));
    }

    private static String getTagLine(String content) throws IOException {
        int startIndex = content.indexOf("tags = [");
        int lastIndex = content.indexOf("]", startIndex + 7);

        return content.substring(startIndex + 7, lastIndex + 1);
    }

    private static String getProcessedTagLine(String tagLine) {
        return tagLine
                .replace(", \"", ",\"")
                .replace(" ", "-")
                .replace("[\"", "[#")
                .replace("\",", ",")
                .replace(",\"", ", #")
                .replace("\"]", "]");
    }
}
