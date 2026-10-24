package com.save;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class writer {
    File file = new File("save.json");
    String key;
    Object value;

    public File Writer(){
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonObject obj = new JsonObject();

        try (FileWriter writer = new FileWriter(file)) {
            obj.addProperty(key, value.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
