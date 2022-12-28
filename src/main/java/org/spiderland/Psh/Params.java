package org.spiderland.Psh;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * A utility class for reading PushGP params.
 */
public class Params {

    public static HashMap<String, String> readFromFile(File inFile) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        return Read(Files.readString(inFile.toPath(), StandardCharsets.UTF_8), map, inFile);
    }

    public static HashMap<String, String> Read(String inParams)
            throws Exception {
        HashMap<String, String> map = new HashMap<>();
        return Read(inParams, map, null);
    }

    public static HashMap<String, String> Read(String inParams,
            HashMap<String, String> inMap, File inFile) throws Exception {
        int linenumber = 0;
        String filename = "<string>";

        try {
            BufferedReader reader = new BufferedReader(new StringReader(
                    inParams));
            String line;
            String parent;

            if (inFile == null) {
                parent = null;
                filename = "<string>";
            } else {
                parent = inFile.getParent();
                filename = inFile.getName();
            }

            while ((line = reader.readLine()) != null) {
                linenumber += 1;
                int comment = line.indexOf('#');

                if (comment != -1)
                    line = line.substring(0, comment);

                if (line.startsWith("include")) {
                    int startIndex = "include".length();
                    String includefile = line.substring(startIndex
                    ).trim();

                    try {
                        File f = new File(parent, includefile);
                        Read(Files.readString(f.toPath(), StandardCharsets.UTF_8), inMap, f);
                    } catch (IncludeException e) {
                        // A previous include exception should bubble up to the
                        // top
                        throw e;
                    } catch (Exception e) {
                        // Any other exception should generate an error message
                        throw new IncludeException("Error including file \""
                                + includefile + "\" at line " + linenumber
                                + " of file \"" + filename + "\"");
                    }
                } else {
                    int split = line.indexOf('=');

                    if (split != -1) {
                        String name = line.substring(0, split).trim();
                        String value = line.substring(split + 1)
                                .trim();

                        while (value.endsWith("\\")) {
                            value = value.substring(0, value.length() - 1);
                            line = reader.readLine();
                            if (line == null)
                                break;
                            linenumber++;
                            value += line.trim();
                        }

                        inMap.put(name, value);
                    }
                }
            }

        } catch (IncludeException e) {
            // A previous include exception should bubble up to the top
            throw e;
        } catch (Exception e) {
            // Any other exception should generate an error message
            throw new IncludeException("Error at line " + linenumber
                    + " of parameter file \"" + filename + "\"");
        }

        return inMap;
    }
}
