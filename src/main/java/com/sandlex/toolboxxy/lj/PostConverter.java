package com.sandlex.toolboxxy.lj;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Converts files produced by livejournal export tool (csv file per month) into markdown files (file per post).
 */
public class PostConverter {

    public static void main(String[] args) throws IOException {
        String sourceDir = args[0];
        File dir = new File(sourceDir);

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        Files.list(dir.toPath())
                .filter(path -> path.toFile().isFile())
                .forEach(sourceFile -> {
                    try {
                        parseFile(mapper, sourceDir, sourceFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void parseFile(CsvMapper mapper, String sourceDir, Path sourceFile) throws IOException {
        CsvSchema schema = CsvSchema.builder().setSkipFirstDataRow(true).build();
        MappingIterator<String[]> it = mapper
                .readerFor(String[].class)
                .with(schema)
                .readValues(new InputStreamReader(new FileInputStream(sourceFile.toFile()), StandardCharsets.UTF_8));
        Iterable<String[]> iterable = () -> it;
        Stream<String[]> targetStream = StreamSupport.stream(iterable.spliterator(), false);
        targetStream.forEach(post -> {
            File nf = new File(sourceDir + "/md/" + post[0] + ".md");
            try {
                Path f = Files.createFile(nf.toPath());
                byte[] buf = getFileContent(post).getBytes();
                Files.write(f, buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static String getFileContent(String[] input) {
        String separator = System.lineSeparator();
        return String.format(
                "+++" + separator +
                        "title = \"%s\"" + separator +
                        "date = \"%s\"" + separator +
                        "tags = []" + separator +
                        "music = \"%s\"" + separator +
                        "mood = \"%s\"" + separator +
                        "+++" + separator + separator +
                        "%s", input[2], input[1].replace(" ", "T"),
                input[5], input[6], input[3]);
    }

}
