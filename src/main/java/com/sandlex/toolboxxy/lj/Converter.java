package com.sandlex.toolboxxy.lj;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Converter {

    public static void main(String[] args) throws IOException {
        File dir = new File(args[0]);

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        Files.list(dir.toPath())
                .forEach(path -> {
                    try {
                        parseFile(mapper, path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void parseFile(CsvMapper mapper, Path file) throws IOException {
        MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(file.toFile());
        Iterable<String[]> iterable = () -> it;
        Stream<String[]> targetStream = StreamSupport.stream(iterable.spliterator(), false);
        targetStream.forEach(post -> System.out.println(post[0]));
    }
}
