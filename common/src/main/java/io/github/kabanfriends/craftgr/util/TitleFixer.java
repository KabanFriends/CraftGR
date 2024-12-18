package io.github.kabanfriends.craftgr.util;

import java.util.HashMap;

public class TitleFixer {

    private static final HashMap<Character, char[]> TABLE;
    static {
        TABLE = new HashMap<>();

        TABLE.put('か', new char[]{'が'});
        TABLE.put('き', new char[]{'ぎ'});
        TABLE.put('く', new char[]{'ぐ'});
        TABLE.put('け', new char[]{'げ'});
        TABLE.put('こ', new char[]{'ご'});
        TABLE.put('さ', new char[]{'ざ'});
        TABLE.put('し', new char[]{'じ'});
        TABLE.put('す', new char[]{'ず'});
        TABLE.put('せ', new char[]{'ぜ'});
        TABLE.put('そ', new char[]{'ぞ'});
        TABLE.put('た', new char[]{'だ'});
        TABLE.put('ち', new char[]{'ぢ'});
        TABLE.put('つ', new char[]{'づ'});
        TABLE.put('て', new char[]{'で'});
        TABLE.put('と', new char[]{'ど'});
        TABLE.put('は', new char[]{'ば', 'ぱ'});
        TABLE.put('ひ', new char[]{'び', 'ぴ'});
        TABLE.put('ふ', new char[]{'ぶ', 'ぷ'});
        TABLE.put('へ', new char[]{'べ', 'ぺ'});
        TABLE.put('ほ', new char[]{'ぼ', 'ぽ'});

        TABLE.put('カ', new char[]{'ガ'});
        TABLE.put('キ', new char[]{'ギ'});
        TABLE.put('ク', new char[]{'グ'});
        TABLE.put('ケ', new char[]{'ゲ'});
        TABLE.put('コ', new char[]{'ゴ'});
        TABLE.put('サ', new char[]{'ザ'});
        TABLE.put('シ', new char[]{'ジ'});
        TABLE.put('ス', new char[]{'ズ'});
        TABLE.put('セ', new char[]{'ゼ'});
        TABLE.put('ソ', new char[]{'ゾ'});
        TABLE.put('タ', new char[]{'ダ'});
        TABLE.put('チ', new char[]{'ヂ'});
        TABLE.put('ツ', new char[]{'ヅ'});
        TABLE.put('テ', new char[]{'デ'});
        TABLE.put('ト', new char[]{'ド'});
        TABLE.put('ハ', new char[]{'バ', 'パ'});
        TABLE.put('ヒ', new char[]{'ビ', 'ピ'});
        TABLE.put('フ', new char[]{'ブ', 'プ'});
        TABLE.put('ヘ', new char[]{'ベ', 'ペ'});
        TABLE.put('ホ', new char[]{'ボ', 'ポ'});
    }

    public static String fixJapaneseString(String input) {
        if (input == null) {
            return null;
        }

        //Replace or remove special characters that are visible only in Minecraft font
        input = input.replaceAll("　", "  ");
        input = input.replaceAll("\u200B", "");

        //Combine separated voicing and semi-voicing symbols
        Character last = null;
        char current;
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);

            if (current == '゙' && last != null) {
                line.append(fixCharacter(last, 0));
                last = null;
            } else if (current == '゚' && last != null) {
                line.append(fixCharacter(last, 1));
                last = null;
            } else {
                if (last != null) {
                    line.append(last);
                }
                last = current;
            }
        }
        if (last != null) {
            line.append(last);
        }

        return line.toString();
    }

    private static String fixCharacter(char base, int type) {
        if (TABLE.containsKey(base)) {
            char[] value = TABLE.get(base);
            if (value.length >= type + 1) return String.valueOf(value[type]);
        }

        if (type == 0) return base + "゛";
        else return base + "゜";
    }

}
