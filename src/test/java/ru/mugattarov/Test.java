package ru.mugattarov;

import ru.mugattarov.proto.DictOuterClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Test {
    private static final Pattern NOUN_DEF_BEGIN = Pattern.compile("( (1\\. )?м\\.)|( (1\\. )?ж\\.)|( (1\\. )?ср\\.)");
    @org.junit.jupiter.api.Test
    public void write() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dict.txt"));
        String noun = null;
        String defBegin = null;
        Iterator<String> iterator = lines.iterator();
        List<DictOuterClass.Noun> nouns = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            noun = defBegin;
            defBegin = line;
            if (NOUN_DEF_BEGIN.matcher(line).matches()) {
                List<String> def = new ArrayList<>();
                while(!line.isBlank()) {
                    def.add(line.strip());
                    line = iterator.next();
                }
                DictOuterClass.Noun protoNoun = DictOuterClass.Noun.newBuilder()
                        .setNoun(noun.strip())
                        .setDef(String.join("", def))
                        .build();
                nouns.add(protoNoun);
            }
        }
        DictOuterClass.Dict dict = DictOuterClass.Dict.newBuilder()
                .addAllNouns(nouns)
                .build();
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("proto_dict"))) {
            dict.writeTo(out);
        }
    }

    @org.junit.jupiter.api.Test
    public void read() throws IOException {
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream("proto_dict"))) {
            DictOuterClass.Dict dict = DictOuterClass.Dict.parseFrom(in);
            List<DictOuterClass.Noun> nouns = dict.getNounsList();
            for (DictOuterClass.Noun noun: nouns) {
                System.out.println(noun.getNoun());
                System.out.println(noun.getDef());
            }
        }
    }
}
