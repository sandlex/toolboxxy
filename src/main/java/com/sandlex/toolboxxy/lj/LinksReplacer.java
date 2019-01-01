package com.sandlex.toolboxxy.lj;

import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Replaces html link tags with markdown tags and applies additional rule for local posts:
 *
 * example:
 *
 * Processing post 522143...
 * <a href="http://sandlex.livejournal.com/521560.html">Perskindol Triathlon</a> -> [Perskindol Triathlon](521560.md)
 * <a href="http://triathlonhaarlemmermeer.nl/">Triathlon Vereniging Haarlemmermeer</a> -> [Triathlon Vereniging Haarlemmermeer](http://triathlonhaarlemmermeer.nl/)
 * done with the post 522143
 */
public class LinksReplacer {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");
                        List<String> links = extractLinks(post);
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

    private static List<String> extractLinks(Path sourceFile) throws IOException {
        List<String> result = new ArrayList<>();

        String content = new String(Files.readAllBytes(sourceFile));

        int searchPosition = 0;

        while (true) {
            int tagStart = content.indexOf("<a ", searchPosition);
            if (tagStart == -1) {
                break;
            } else {
                String tag = content.substring(tagStart, content.indexOf("/a>", tagStart) + 3);
                result.add(tag);
                searchPosition = tagStart + 1;
            }
        }

        return result;
    }

    private static void replaceLinks(Path post, List<String> linkTags) throws IOException {
        String content = new String(Files.readAllBytes(post));

        try {
            for (String htmlTag : linkTags) {
                Link link = parseLink(htmlTag);
                String mdTag = String.format("[%s](%s)", link.text, link.url);
                content = content.replace(htmlTag, mdTag);
                System.out.println(htmlTag + " -> " + mdTag);
            }
            Files.write(post, content.getBytes());
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static Link parseLink(String htmlTag) {
        int urlPos = htmlTag.indexOf("href=\"") + 6;
        Link link = Link.builder()
                .url(htmlTag.substring(urlPos, htmlTag.indexOf("\"", urlPos)))
                .text(htmlTag.substring(htmlTag.indexOf(">") + 1, htmlTag.indexOf("</a>")))
                .build();

        if (link.url.contains("http://sandlex.livejournal.com/")) {
            link.url = link.url.replace("http://sandlex.livejournal.com/", "")
                    .replace(".html", ".md");
        }

        return link;
    }

    @Builder
    private static class Link {
        private String url;
        private String text;
    }

}
