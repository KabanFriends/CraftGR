package io.github.kabanfriends.craftgr.util;

import java.util.HashMap;

public class TitleFixer {

    private static final HashMap<String, String[]> TABLE;
    static {
        TABLE = new HashMap<>();

        TABLE.put("か", new String[]{"が"});
        TABLE.put("き", new String[]{"ぎ"});
        TABLE.put("く", new String[]{"ぐ"});
        TABLE.put("け", new String[]{"げ"});
        TABLE.put("こ", new String[]{"ご"});
        TABLE.put("さ", new String[]{"ざ"});
        TABLE.put("し", new String[]{"じ"});
        TABLE.put("す", new String[]{"ず"});
        TABLE.put("せ", new String[]{"ぜ"});
        TABLE.put("そ", new String[]{"ぞ"});
        TABLE.put("た", new String[]{"だ"});
        TABLE.put("ち", new String[]{"ぢ"});
        TABLE.put("つ", new String[]{"づ"});
        TABLE.put("て", new String[]{"で"});
        TABLE.put("と", new String[]{"ど"});
        TABLE.put("は", new String[]{"ば", "ぱ"});
        TABLE.put("ひ", new String[]{"び", "ぴ"});
        TABLE.put("ふ", new String[]{"ぶ", "ぷ"});
        TABLE.put("へ", new String[]{"べ", "ぺ"});
        TABLE.put("ほ", new String[]{"ぼ", "ぽ"});

        TABLE.put("カ", new String[]{"ガ"});
        TABLE.put("キ", new String[]{"ギ"});
        TABLE.put("ク", new String[]{"グ"});
        TABLE.put("ケ", new String[]{"ゲ"});
        TABLE.put("コ", new String[]{"ゴ"});
        TABLE.put("サ", new String[]{"ザ"});
        TABLE.put("シ", new String[]{"ジ"});
        TABLE.put("ス", new String[]{"ズ"});
        TABLE.put("セ", new String[]{"ゼ"});
        TABLE.put("ソ", new String[]{"ゾ"});
        TABLE.put("タ", new String[]{"ダ"});
        TABLE.put("チ", new String[]{"ヂ"});
        TABLE.put("ツ", new String[]{"ヅ"});
        TABLE.put("テ", new String[]{"デ"});
        TABLE.put("ト", new String[]{"ド"});
        TABLE.put("ハ", new String[]{"バ", "パ"});
        TABLE.put("ヒ", new String[]{"ビ", "ピ"});
        TABLE.put("フ", new String[]{"ブ", "プ"});
        TABLE.put("ヘ", new String[]{"ベ", "ペ"});
        TABLE.put("ホ", new String[]{"ボ", "ポ"});
    }

    public static String fixJapaneseString(String input) {

        //Replace full-width space with two half-width spaces (Full-width space appears as IDSP in Minecraft)
        String spaced = input.replaceAll("　", "  ");

        //Combine separated voicing and semi-voicing symbols
        String last = "";
        String current = "";
        StringBuilder line = new StringBuilder();

        for ( int i = 0; i < spaced.length(); i++ ) {
            current = spaced.substring(i, i + 1);

            if (current.equals("゙")) {
                line.append(getFixedChar(last, 0));
                last = "";
            } else if (current.equals("゚")) {
                line.append(getFixedChar(last, 1));
                last = "";
            } else {
                line.append(last);
                last = current;
            }
        }
        line.append(last);

        return line.toString();
    }

    private static String getFixedChar(String base, int type) {
        if (TABLE.containsKey(base)) {
            String[] value = TABLE.get(base);
            if (value.length >= type + 1) return value[type];
        }

        if (type == 0) return base + "゛";
        else return base + "゜";
    }

}
