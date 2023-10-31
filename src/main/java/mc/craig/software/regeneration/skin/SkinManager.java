package mc.craig.software.regeneration.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkinManager {
    private List<Skin> skinList;

    public SkinManager() {
        skinList = new ArrayList<>();
    }

    public void addSkin(String name, String link) {
        Skin skin = new Skin(name, link);
        skinList.add(skin);
    }

    public Skin getRandomSkin() {
        if (skinList.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(skinList.size());
        return skinList.get(randomIndex);
    }

    public List<Skin> getAllSkins() {
        return skinList;
    }

    public int getSkinCount() {
        return skinList.size();
    }
}

