package org.spiderland.Psh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

/**
 * A utility class for reading PushGP params.
 */

public class Params {
    public static HashMap<String, String> ReadFromFile(File inFile)
            throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        return Read(ReadFileString(inFile), map, inFile);
    }

    public static HashMap<String, String> Read(String inParams)
            throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
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
                        Read(ReadFileString(f), inMap, f);
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

    /**
     * Utility function to read a file in its entirety to a string.
     *
     * @param inPath The file path to be read.
     * @return The contents of a file represented as a string.
     */

    static String ReadFileString(String inPath) throws Exception {
        return ReadFileString(new File(inPath));
    }

    /**
     * Utility function to read a file in its entirety to a string.
     *
     * @param inFile The file to be read.
     * @return The contents of a file represented as a string.
     */

    public static String ReadFileString(File inFile) throws Exception {
        InputStream s = new FileInputStream(inFile);
        byte[] tmp = new byte[1024];
        int read;
        String result = "";

        while ((read = s.read(tmp)) > 0) {
            result += new String(tmp, 0, read);
        }

        return result;
    }
}
