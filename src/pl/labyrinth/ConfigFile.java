package pl.labyrinth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 2/8/13
 * Time: 3:00 PM
 */
public class ConfigFile {
    private Map<String, String> mValues = new HashMap<String, String>();
    private String mCurrentFile = null;

    public ConfigFile() {
    }

    public ConfigFile(String filename) {
        load(filename);
    }

    public boolean load(String filename) {
        mValues.clear();

        try {
            Scanner scanner = new Scanner(new File(filename));
            Pattern linePattern = Pattern.compile("(\\w+)\\s*=\\s*([^\n]*)");
            Pattern commentPattern = Pattern.compile("\\s*#[^\n]*");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() == 0)
                    continue;

                Matcher matcher = commentPattern.matcher(line);
                if (matcher.matches())
                    continue;

                matcher = linePattern.matcher(line);
                if (!matcher.matches()) {
                    System.out.printf("Invalid line: '%s'\n", line);
                    continue;
                }

                String key = matcher.group(1);
                String value = matcher.group(2);
                System.out.printf("Found: %s = %s\n", key, value);

                if (mValues.containsKey(key))
                    System.out.printf("Duplicate key: %s\n", key);
                else
                    mValues.put(key, value);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        mCurrentFile = filename;
        return true;
    }

    public boolean save() {
        if (mCurrentFile == null)
            return false;

        return save(mCurrentFile);
    }

    public boolean save(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);

            for (Map.Entry<String, String> entry: mValues.entrySet()) {
                writer.write(String.format("%s = %s\n", entry.getKey(), entry.getValue()));
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean hasKey(String key) {
        return mValues.containsKey(key);
    }

    public float getFloat(String key) {
        if (mValues.containsKey(key)) {
            return new Float(mValues.get(key));
        }

        return 0.f;
    }

    public Vector3 getVector3(String key) {
        if (mValues.containsKey(key)) {
            String floatPat = "\\s*(\\d+.?\\d*)\\s*,\\s*(\\d+.?\\d*)\\s*,\\s*(\\d+.?\\d*)";
            Pattern pat = Pattern.compile(floatPat);
            Matcher matcher = pat.matcher(mValues.get(key));

            if (!matcher.matches()) {
                System.out.printf("Invalid vector value: '%s'\n", mValues.get(key));
                return new Vector3(0.f);
            }

            return new Vector3(
                    new Float(matcher.group(1)),
                    new Float(matcher.group(2)),
                    new Float(matcher.group(3))
            );
        }

        return new Vector3(0.f);
    }

    public void setFloat(String key, float value) {
        mValues.put(key, new Float(value).toString());
    }

    public static void main(String[] args) {
        ConfigFile config = new ConfigFile();
        config.load("data/jbullet.config");
        System.out.println(config.getVector3("ball_inertia"));
        //config.save("data/jbullet.config2");
    }
}
