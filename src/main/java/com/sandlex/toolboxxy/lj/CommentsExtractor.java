package com.sandlex.toolboxxy.lj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Extracts comments from posts and writes them into json files.
 * <p>
 * File name: postId.json
 * <p>
 * Structure:
 * <p>
 * {
 * "postId" : "206175",
 * "comments" : [
 * ...
 * , {
 * "parent" : "813919",
 * "thread" : "814175",
 * "level" : "2",
 * "dname" : "sandlex",
 * "article" : "text",
 * "ctime" : "February 3 2007, 13:25:02 UTC"
 * },
 * ...
 * ]
 * }
 * <p>
 * Fields:
 * - parent - parent thread in hierarchy
 * - thread - thread id
 * - level - comment level
 * - dname - name lj name of the commenter or openID
 * - article - comment text (may contain html tags)
 * - ctime - date/time
 */
public class CommentsExtractor {

    public static void main(String[] args) throws IOException {
        String contentDir = args[0];
        File postsDir = new File(contentDir + "/posts");

        Files.list(postsDir.toPath())
                .filter(post -> post.toFile().isFile())
                .forEach(post -> {
                    try {
                        String postId = getPostId(post);
                        System.out.println("Processing post " + postId + "...");

                        if (Files.exists(new File(contentDir + "/comments/" + postId + ".json").toPath())) {
                            System.out.println("already exported...");
                        } else {
                            extractComments(contentDir, postId);
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

    private static void extractComments(String contentDir, String postId) throws IOException {
        Document doc = Jsoup.connect("https://sandlex.livejournal.com/" + postId + ".html").get();
        Elements scriptElements = doc.getElementsByTag("script");
        for (Element element : scriptElements) {
            for (DataNode node : element.dataNodes()) {
                String content = node.getWholeData();
                if (content.contains("Site.page = ")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CommentsHolder holder = deserializeComments(objectMapper, content);
                    serializeCommentsIntoFile(objectMapper, holder, postId, contentDir);

                    // NPE here indicates hidden post... halting to write it down and process manually
                    System.out.println("exported comments: " + holder.comments.size());
                    return;
                }
            }
        }
        System.out.println("no comment section found... weird");
    }

    private static CommentsHolder deserializeComments(ObjectMapper objectMapper, String content) throws IOException {
        int commentsJsonSectionStart = content.indexOf("Site.page = ") + 12;
        String commentsJsonSection = content.substring(commentsJsonSectionStart,
                content.indexOf("};", commentsJsonSectionStart) + 1);
        return objectMapper.readValue(commentsJsonSection, CommentsHolder.class);
    }

    private static void serializeCommentsIntoFile(ObjectMapper objectMapper, CommentsHolder holder, String postId, String contentDir) throws IOException {
        holder.setPostId(postId);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(new File(contentDir + "/comments/" + postId + ".json"), holder);
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CommentsHolder {
        String postId;
        List<Comment> comments;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Comment {
        String parent;
        String thread;
        String level;
        String dname;
        String article;
        String ctime;
    }

}
