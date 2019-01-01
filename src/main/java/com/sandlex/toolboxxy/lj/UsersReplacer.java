package com.sandlex.toolboxxy.lj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Replaces livejournal user tags with the direct markdown link to the site:
 *
 * <lj user="sandlex"> -> [sandlex](https://sandlex.livejournal.com)
 */
public class UsersReplacer {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");
                        List<String> links = extracUserTags(post);
                        if (links.isEmpty()) {
                            System.out.println("no links");
                        } else {
                            replaceLinks(post, links);
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

    private static List<String> extracUserTags(Path sourceFile) throws IOException {
        List<String> result = new ArrayList<>();

        String content = new String(Files.readAllBytes(sourceFile));

        int searchPosition = 0;

        while (true) {
            int tagStart = content.indexOf("<lj user", searchPosition);
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

    private static void replaceLinks(Path post, List<String> linkTags) throws IOException {
        String content = new String(Files.readAllBytes(post));

        try {
            for (String userTag : linkTags) {
                String user = userTag.substring(userTag.indexOf("\"") + 1, userTag.lastIndexOf("\""));
                String mdTag = String.format("[%s](https://%s.livejournal.com)", user, user);
                content = content.replace(userTag, mdTag);
                System.out.println(userTag + " -> " + mdTag);
            }
            Files.write(post, content.getBytes());
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
