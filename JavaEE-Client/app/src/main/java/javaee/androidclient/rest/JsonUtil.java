package javaee.androidclient.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Utility for Json serialization and deserialization of Beacons
 *
 * @author Henrik Drefs
 */
public class JsonUtil {

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * deserializes a list of beacons
     *
     * @param jsonString json string
     * @param <T>        return type param Beacon
     * @return deserialized list
     */
    public static <T> T deserializeBeaconList(String jsonString) {
        return gson.fromJson(jsonString, new TypeToken<List<Beacon>>() {
        }.getType());
    }

    public static <T> T deserializeBeacon(String jsonString) {
        return gson.fromJson(jsonString, new TypeToken<Beacon>() {
        }.getType());
    }

    public static <T> T deserializeLectureList(String jsonString) {
        JsonReader reader = new JsonReader(new StringReader(jsonString));
        reader.setLenient(true);
        return gson.fromJson(reader, new TypeToken<List<Lecture>>() {
        }.getType());
    }

    public static <T> T deserializeLecture(String jsonString) {
        return gson.fromJson(jsonString, new TypeToken<Lecture>() {
        }.getType());
    }

    public static String serialize(Object o) {
        return gson.toJson(o);
    }

    public static List<Lecture> serializeFromFile(File file)
            throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("")) {
                sb.append(line);
            }
        }
        return deserializeLectureList(sb.toString());
    }

}
