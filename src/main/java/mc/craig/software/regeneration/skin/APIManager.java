package mc.craig.software.regeneration.skin;

import mc.craig.software.regeneration.skin.SkinManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIManager extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final String apiURL;
    private final SkinManager skinManager;

    public APIManager(JavaPlugin plugin, String apiURL, SkinManager skinManager) {
        this.plugin = plugin;
        this.apiURL = apiURL;
        this.skinManager = skinManager;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Parse JSON response
            JSONParser parser = new JSONParser();
            JSONArray skinArray = (JSONArray) parser.parse(response.toString());

            for (Object skinObj : skinArray) {
                JSONObject skinJSON = (JSONObject) skinObj;
                String name = (String) skinJSON.get("name");
                String link = (String) skinJSON.get("link");
                skinManager.addSkin(name, link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndPopulateSkinsAsync() {
        runTaskAsynchronously(plugin);
    }
}
