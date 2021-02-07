package ru.mugattarov.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mugattarov.proto.DictOuterClass;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@org.springframework.stereotype.Controller
public class Controller {
    private final Random R = new Random();
    private DictOuterClass.Dict dict;

    {
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream("proto_dict"))) {
            dict = DictOuterClass.Dict.parseFrom(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary", e);
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        int listSize = dict.getNounsList().size();
        for (int i = 1; i <= 5; i++) {
            DictOuterClass.Noun noun = dict.getNouns(R.nextInt(listSize));
            setNoun(model, noun, "word_" + i, "def_" + i);
        }
        return "index";
    }

    private void setNoun(Model model, DictOuterClass.Noun noun, String word, String def) {
        model.addAttribute(word, noun.getNoun());
        model.addAttribute(def, noun.getDef());

    }
}
