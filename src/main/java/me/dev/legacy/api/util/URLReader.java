package me.dev.legacy.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLReader {
    public static List readURL() {
        ArrayList s = new ArrayList();

        try {
            URL url = new URL("https://pastebin.com/raw/p5qX2zAQ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            String hwid;
            while ((hwid = bufferedReader.readLine()) != null) {
                s.add(hwid);
            }
        } catch (Exception var4) {
            ;
        }

        return s;
    }
}
