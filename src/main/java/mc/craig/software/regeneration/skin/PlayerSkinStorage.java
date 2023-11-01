package mc.craig.software.regeneration.skin;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerSkinStorage {
    private final File dataFile;
    private Map<String, String> skinURLs;

    public PlayerSkinStorage(String dataFilePath) {
        dataFile = new File(dataFilePath);
        skinURLs = new HashMap<>();

        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(dataFile)) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);

                if (obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    for (Object key : jsonObject.keySet()) {
                        String playerUUID = (String) key;
                        String skinURL = (String) jsonObject.get(playerUUID);
                        skinURLs.put(playerUUID, skinURL);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : skinURLs.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            writer.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeSkinURL(String playerUUID, String skinURL) {
        skinURLs.put(playerUUID, skinURL);
        saveData();
    }

    public String getSkinURL(String playerUUID) {
        return skinURLs.get(playerUUID);
    }
}
