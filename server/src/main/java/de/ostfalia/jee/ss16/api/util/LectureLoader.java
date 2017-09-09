package de.ostfalia.jee.ss16.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.ostfalia.jee.ss16.api.model.Lecture;

import java.io.*;
import java.util.List;

/**
 * Created by Henrik on 6/8/2016.
 *
 * @author Henrik Drefs
 */
public class LectureLoader {

    private static Gson gson = new GsonBuilder().create();

    public static <T> T deserializeList(String jsonString) {
        JsonReader reader = new JsonReader(new StringReader(jsonString));
        reader.setLenient(true);
        return gson.fromJson(reader, new TypeToken<List<Lecture>>() {
        }.getType());
    }

    public static <T> T deserialize(String jsonString) {
        return gson.fromJson(jsonString, new TypeToken<Lecture>() {
        }.getType());
    }

    public static String serialize(Object o) {
        return gson.toJson(o);
    }

    public static List<Lecture> serializeFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("")) {
                sb.append(line);
            }
        }
        return deserializeList(sb.toString());
    }

    public static void main(String[] args) throws IOException {
//        File file = new File("src/main/resources/252.txt");
//        List<Lecture> list = serializeFromFile(file);
//        System.out.println(list.get(0));
//        System.out.println(list);

        List<Lecture> list2 = deserializeList("[{\n" +
                "\n" +
                "\"room\":\"252\",\n" +
                "\"time\":\"8:15\",\n" +
                "\"day\":\"mo\",\n" +
                "\"subject\":\"Algorithmen und Datenstrukturen - VL\",\n" +
                "\"tutor\":\"Prof. Dr. M. Huhn\"\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\"room\":\"252\",\n" +
                "\"time\":\"8:15\",\n" +
                "\"day\":\"di\",\n" +
                "\"subject\":\"Grundlagen des Programmierens - VL\",\n" +
                "\"tutor\":\"Prof. Dr. I. Schiering\"\n" +
                "}]");
        System.out.println(list2.toString());
    }


}
