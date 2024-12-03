package org.spiderland.Psh;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A utility class to help read PshInspector input files.
 */

public class InspectorInput {

    Program program;
    int executionLimit;
    Interpreter interpreter;

    /**
     * Constructs an InspectorInput from a filename string
     *
     * @param inFilename The file to input from.
     */
    public InspectorInput(String inFilename) throws Exception {
        InitInspectorInput(new File(inFilename));
    }

    /**
     * Constructs an InspectorInput from a filename string
     *
     * @param inFile The file to input from.
     */
    public InspectorInput(File inFile) throws Exception {
        InitInspectorInput(inFile);
    }

    /**
     * Initializes an InspectorInput. The file should be organized as follows:
     * <p>
     * 1 Program to execute
     * 2 Execution limit
     * 3 Integer, float, or boolean inputs
     * 4 Available instructions. This is only used for creating random code
     * with code.rand or exec.rand
     *
     * @param inFile The file to input from.
     */
    private void InitInspectorInput(File inFile) throws Exception {
        interpreter = new Interpreter();

        // Read fileString
        String fileString = Files.readString(inFile.toPath(), StandardCharsets.UTF_8);

        // Get programString
        int lastProgramCharIndex = lastProgramCharacter(fileString);
        String programString = fileString.substring(0, lastProgramCharIndex + 1).trim();
        fileString = fileString.substring(lastProgramCharIndex + 1).trim();

        // Get executionLimit
        int indexNewline = fileString.indexOf("\n");
        if (indexNewline != -1) {
            String limitString = fileString.substring(0, indexNewline).trim();

            executionLimit = Integer.parseInt(limitString);
            fileString = fileString.substring(indexNewline + 1);
        } else {
            // If here, no inputs to be pushed were included
            executionLimit = Integer.parseInt(fileString);
            fileString = "";
        }

        // Get inputs and push them onto correct stacks. If fileString = ""
        // at this point, then can still do the following with correct result.
        indexNewline = fileString.indexOf("\n");
        if (indexNewline != -1) {
            String inputsString = fileString.substring(0, indexNewline).trim();
            fileString = fileString.substring(indexNewline + 1);

            // Parse the inputs and load them into the interpreter
            parseAndLoadInputs(inputsString);
        } else {
            parseAndLoadInputs(fileString);
            fileString = "";
        }

        // Get the available instructions for random code generation
        indexNewline = fileString.indexOf("\n");
        if (!fileString.trim().equals("")) {
            interpreter.setInstructions(new Program(fileString.trim()));
        }

        // Check for input.inN instructions
        checkForInputIn(programString);

        // Add random integer and float parameters
        interpreter.minRandomInt = -10;
        interpreter.maxRandomInt = 10;
        interpreter.randomIntResolution = 1;
        interpreter.minRandomFloat = -10.0f;
        interpreter.maxRandomFloat = 10.0f;
        interpreter.randomFloatResolution = 0.01f;

        interpreter.maxRandomCodeSize = 50;

        // Load the program
        program = new Program(programString);
        interpreter.loadProgram(program); // Initializes program
    }

    /**
     * Could use Program itself to count the characters while it is parsing, but this also works as a quick temporary solution.
     */
    private static int lastProgramCharacter(String input) {
        int pointer = 0;
        while (pointer < input.length() && input.charAt(pointer) != '(') {
            pointer++;
        }
        if (pointer == input.length()) {
            throw new RuntimeException("No program found in input file");
        }
        pointer++;
        int numberOfOpenBrackets = 1;
        while (numberOfOpenBrackets > 0) {
            if (input.charAt(pointer) == '(') {
                numberOfOpenBrackets++;
            } else if (input.charAt(pointer) == ')') {
                numberOfOpenBrackets--;
            }
            pointer++;
        }
        return pointer;
    }

    /**
     * Returns the initialized interpreter
     *
     * @return The initialized interpreter
     */
    public Interpreter getInterpreter() {
        return interpreter;
    }

    public Program getProgram() {
        return program;
    }

    /**
     * Returns the execution limit
     *
     * @return The execution limit
     */
    public int getExecutionLimit() {
        return executionLimit;
    }

    private void parseAndLoadInputs(String inputs) throws Exception {
        String[] inputTokens = inputs.split("\\s+");

        for (String token : inputTokens) {
            if (token.equals("")) {
            } else if (token.equals("true")) {
                interpreter.boolStack().push(true);
                interpreter.inputStack().push(true);
            } else if (token.equals("false")) {
                interpreter.boolStack().push(false);
                interpreter.inputStack().push(false);
            } else if (token.matches("(([-+])?[0-9]+(\\.[0-9]+)?)+")) {
                if (token.indexOf('.') != -1) {
                    interpreter.floatStack().push(Float.parseFloat(token));
                    interpreter.inputStack().push(Float.parseFloat(token));
                } else {
                    interpreter.intStack().push(Integer.parseInt(token));
                    interpreter.inputStack().push(Integer.parseInt(token));
                }
            } else {
                throw new Exception(
                        "Inputs must be of type int, float, or boolean. \""
                                + token + "\" is none of these.");
            }
        }
    }

    private void checkForInputIn(String programString) {
        String added = "";
        String numstr = "";
        int index = 0;
        int numindex = 0;
        int spaceindex = 0;
        int parenindex = 0;
        int endindex = 0;

        while (true) {

            index = programString.indexOf("input.in");
            if (index == -1)
                break;

            // System.out.println(programString + "    " + index);

            numindex = index + 8;
            if (!Character.isDigit(programString.charAt(numindex))) {
                programString = programString.substring(numindex);
                continue;
            }

            spaceindex = programString.indexOf(' ', numindex);
            parenindex = programString.indexOf(')', numindex);
            if (spaceindex == -1)
                endindex = parenindex;
            else if (parenindex == -1)
                endindex = spaceindex;
            else
                endindex = Math.min(spaceindex, parenindex);

            numstr = programString.substring(numindex, endindex);

            // Check for doubles in added
            if (added.indexOf(" " + numstr + " ") == -1) {
                added = added + " " + numstr + " ";
                interpreter.addInstruction("input.in" + numstr, new InputInN(
                        Integer.parseInt(numstr)));
            }

            programString = programString.substring(numindex);
        }

    }

}