package mc.craig.software.regeneration.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkinManager {

    public static final Random RAND = new Random();


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
        int randomIndex = RAND.nextInt(skinList.size());
        return skinList.get(randomIndex);
    }

    public List<Skin> getAllSkins() {
        return skinList;
    }

    public int getSkinCount() {
        return skinList.size();
    }

    public static class Skin {
        private String name;
        private String link;

        public Skin(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public String getLink() {
            return link;
        }
    }
}

