package org.spiderland.Psh;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Push program.
 */

public class Program extends ObjectStack implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern PATTERN = Pattern.compile("#.*$", Pattern.MULTILINE);

    /**
     * Constructs an empty Program.
     */
    public Program() {
    }

    /**
     * Constructs a copy of an existing Program.
     *
     * @param inOther The Push program to copy.
     */
    public Program(Program inOther) {
        inOther.copyTo(this);
    }

    /**
     * Constructs a Push program by parsing a String.
     *
     * @param inString The Push program string to parse.
     */
    public Program(String inString) {
        parse(inString);
    }

    /**
     * Sets this program to the parsed program string.
     *
     * @param inString The Push program string to parse.
     */
    private void parse(String inString) {
        clear();

        inString = withoutComments(inString).replace("(", " ( ");
        inString = inString.replace(")", " ) ");

        String[] tokens = inString.split("\\s+");

        parse(tokens, 0);
    }

    private int parse(String[] inTokens, int inStart) {
        boolean first = (inStart == 0);

        for (int n = inStart; n < inTokens.length; n++) {
            String token = inTokens[n];

            if (!token.equals("")) {
                if (token.equals("(")) {

                    // Found an open paren -- begin a recursive Parse, though
                    // the very first
                    // token in the list is a special case -- no need to create
                    // a sub-program

                    if (!first) {
                        Program p = new Program();

                        n = p.parse(inTokens, n + 1);

                        push(p);
                    }
                } else if (token.equals(")")) {
                    // End of the program -- return the advanced token index to
                    // the caller

                    return n;

                } else if (Character.isLetter(token.charAt(0))) {
                    push(token);
                } else {
                    Object number;

                    if (token.indexOf('.') != -1)
                        number = Float.parseFloat(token);
                    else
                        number = Integer.parseInt(token);

                    push(number);
                }

                first = false;
            }
        }

        // If we're here, there was no closing brace for one of the programs

        throw new RuntimeException("no closing brace found for program");
    }

    /**
     * Returns the size of the program and all subprograms.
     *
     * @return The size of the program.
     */
    public int programSize() {
        int size = this.size;

        for (int n = 0; n < this.size; n++) {
            Object o = stack[n];
            if (o instanceof Program)
                size += ((Program) o).programSize();
        }

        return size;
    }

    /**
     * Returns the size of a subtree.
     *
     * @param inIndex The index of the requested subtree.
     * @return The size of the subtree.
     */

    public int subtreeSize(int inIndex) {
        Object sub = subtree(inIndex);

        if (sub == null)
            return 0;

        if (sub instanceof Program)
            return ((Program) sub).programSize();

        return 1;
    }

    /**
     * Returns a subtree of the program.
     *
     * @param inIndex The index of the requested subtree.
     * @return The program subtree.
     */

    public Object subtree(int inIndex) {
        if (inIndex < size) {
            return stack[inIndex];
        } else {
            int startIndex = size;

            for (int n = 0; n < size; n++) {
                Object o = stack[n];

                if (o instanceof Program sub) {
                    int length = sub.programSize();

                    if (inIndex - startIndex < length)
                        return sub.subtree(inIndex - startIndex);

                    startIndex += length;
                }
            }
        }

        return null;
    }

    /**
     * Replaces a subtree of this Program with a new object.
     *
     * @param inIndex       The index of the subtree to replace.
     * @param inReplacement The replacement for the subtree
     * @return True if a replacement was made (the index was valid).
     */

    public boolean replaceSubtree(int inIndex, Object inReplacement) {
        if (inIndex < size) {
            stack[inIndex] = cloneForProgram(inReplacement);
            return true;
        } else {
            int startIndex = size;

            for (int n = 0; n < size; n++) {
                Object o = stack[n];

                if (o instanceof Program sub) {
                    int length = sub.programSize();

                    if (inIndex - startIndex < length)
                        return sub.replaceSubtree(inIndex - startIndex,
                                inReplacement);

                    startIndex += length;
                }
            }
        }

        return false;
    }

    public void flatten(int inIndex) {
        if (inIndex < size) {
            // If here, the index to be flattened is in this program. So, push
            // the rest of the program onto a new program, and replace this with
            // that new program.

            Program replacement = new Program(this);
            clear();

            for (int i = 0; i < replacement.size; i++) {
                if (inIndex == i) {

                    if (replacement.stack[i] instanceof Program p) {
                        for (int j = 0; j < p.size; j++)
                            this.push(p.stack[j]);
                    } else {
                        this.push(replacement.stack[i]);
                    }
                } else {
                    this.push(replacement.stack[i]);
                }
            }
        } else {
            int startIndex = size;

            for (int n = 0; n < size; n++) {
                Object o = stack[n];

                if (o instanceof Program sub) {
                    int length = sub.programSize();

                    if (inIndex - startIndex < length) {
                        sub.flatten(inIndex - startIndex);
                        break;
                    }

                    startIndex += length;
                }
            }
        }
    }

    /**
     * Copies this program to another.
     *
     * @param inOther The program to receive the copy of this program
     */

    public void copyTo(Program inOther) {
        for (int n = 0; n < size; n++)
            inOther.push(stack[n]);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("(");

        for (int n = 0; n < size; n++) {
            if (result.charAt(result.length() - 1) == '(')
                result.append(stack[n]);
            else
                result.append(" ").append(stack[n]);
        }

        result.append(")");

        return result.toString();
    }

    /**
     * Creates a copy of an object suitable for adding to a Push Program. Java's
     * clone() is unfortunately useless for this task.
     */

    private Object cloneForProgram(Object inObject) {
        // Java clone() is useless :(

        if (inObject instanceof String)
            return inObject;

        if (inObject instanceof Integer)
            return inObject;

        if (inObject instanceof Float)
            return inObject;

        if (inObject instanceof Program)
            return new Program((Program) inObject);

        if (inObject instanceof Instruction)
            return inObject; // no need to copy; instructions are singletons

        return null;
    }

    private static String withoutComments(final String program) {
        final var matcher = PATTERN.matcher(program);
        return matcher.replaceAll("");
    }
}
