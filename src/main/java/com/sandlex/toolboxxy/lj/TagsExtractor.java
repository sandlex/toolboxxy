package com.sandlex.toolboxxy.lj;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Extracts tags from page metadata and adds to markdown files.
 */
public class TagsExtractor {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");

                        String tags = getTags(postId);

                        if (StringUtils.EMPTY.equals(tags)) {
                            System.out.println("no tags... skipping");
                        } else {
                            String content = new String(Files.readAllBytes(post));
                            if (content.contains("tags = []")) {
                                content = content.replace("tags = []", String.format("tags = [%s]", tags));
                                System.out.println("adding tags -> " + tags);
                                Files.write(post, content.getBytes());
                            } else {
                                System.out.println("already processed... skipping");
                            }
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

    private static String getTags(String postId) throws IOException {
        Set<String> set = new HashSet<>();
        Document doc = Jsoup.connect("https://sandlex.livejournal.com/" + postId + ".html").get();
        Elements tagElements = doc.select("meta[property=article:tag]");
        tagElements.forEach(tag -> set.add("\"" + tag.attr("content") + "\""));

        return set.isEmpty() ? StringUtils.EMPTY : StringUtils.join(set, ", ");
    }

}
