package org.spiderland.Psh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * The Push language interpreter.
 */

public class Interpreter implements Serializable {
    private static final long serialVersionUID = 1L;

    protected HashMap<String, Instruction> instructions = new HashMap<>();

    // All generators

    protected HashMap<String, AtomGenerator> generators = new HashMap<>();
    protected List<AtomGenerator> randomGenerators = new ArrayList<>();

    // Create the stacks.
    protected IntStack intStack;
    protected FloatStack floatStack;
    protected BooleanStack boolStack;
    protected ObjectStack codeStack;
    protected ObjectStack nameStack;
    protected ObjectStack execStack = new ObjectStack();

    protected ObjectStack inputStack = new ObjectStack();

    // This arraylist will hold all custom stacks that can be created by the
    // problem classes
    protected List<Stack> customStacks = new ArrayList<>();

    /* Since the _inputStack will not change after initialization, it will not
     * need a frame stack.
     */
    protected ObjectStack intFrameStack = new ObjectStack();
    protected ObjectStack floatFrameStack = new ObjectStack();
    protected ObjectStack boolFrameStack = new ObjectStack();
    protected ObjectStack codeFrameStack = new ObjectStack();
    protected ObjectStack nameFrameStack = new ObjectStack();

    protected boolean useFrames;

    protected int totalStepsTaken;
    protected long evaluationExecutions = 0;

    protected int maxRandomInt;
    protected int minRandomInt;
    protected int randomIntResolution;

    protected float maxRandomFloat;
    protected float minRandomFloat;
    protected float randomFloatResolution;

    protected int maxRandomCodeSize;
    protected int maxPointsInProgram;

    protected Random random = new Random();

    protected InputPusher inputPusher = new InputPusher();

    public Interpreter() {
        useFrames = false;
        pushStacks();

        defineInstruction("integer.+", new IntegerAdd());
        defineInstruction("integer.-", new IntegerSub());
        defineInstruction("integer./", new IntegerDiv());
        defineInstruction("integer.%", new IntegerMod());
        defineInstruction("integer.*", new IntegerMul());
        defineInstruction("integer.pow", new IntegerPow());
        defineInstruction("integer.log", new IntegerLog());
        defineInstruction("integer.=", new IntegerEquals());
        defineInstruction("integer.>", new IntegerGreaterThan());
        defineInstruction("integer.<", new IntegerLessThan());
        defineInstruction("integer.min", new IntegerMin());
        defineInstruction("integer.max", new IntegerMax());
        defineInstruction("integer.abs", new IntegerAbs());
        defineInstruction("integer.neg", new IntegerNeg());
        defineInstruction("integer.ln", new IntegerLn());
        defineInstruction("integer.fromfloat", new IntegerFromFloat());
        defineInstruction("integer.fromboolean", new IntegerFromBoolean());
        defineInstruction("integer.rand", new IntegerRand());

        defineInstruction("float.+", new FloatAdd());
        defineInstruction("float.-", new FloatSub());
        defineInstruction("float./", new FloatDiv());
        defineInstruction("float.%", new FloatMod());
        defineInstruction("float.*", new FloatMul());
        defineInstruction("float.pow", new FloatPow());
        defineInstruction("float.log", new FloatLog());
        defineInstruction("float.=", new FloatEquals());
        defineInstruction("float.>", new FloatGreaterThan());
        defineInstruction("float.<", new FloatLessThan());
        defineInstruction("float.min", new FloatMin());
        defineInstruction("float.max", new FloatMax());
        defineInstruction("float.sin", new FloatSin());
        defineInstruction("float.cos", new FloatCos());
        defineInstruction("float.tan", new FloatTan());
        defineInstruction("float.exp", new FloatExp());
        defineInstruction("float.abs", new FloatAbs());
        defineInstruction("float.neg", new FloatNeg());
        defineInstruction("float.ln", new FloatLn());
        defineInstruction("float.frominteger", new FloatFromInteger());
        defineInstruction("float.fromboolean", new FloatFromBoolean());
        defineInstruction("float.rand", new FloatRand());

        defineInstruction("boolean.=", new BoolEquals());
        defineInstruction("boolean.not", new BoolNot());
        defineInstruction("boolean.and", new BoolAnd());
        defineInstruction("boolean.or", new BoolOr());
        defineInstruction("boolean.xor", new BoolXor());
        defineInstruction("boolean.frominteger", new BooleanFromInteger());
        defineInstruction("boolean.fromfloat", new BooleanFromFloat());
        defineInstruction("boolean.rand", new BoolRand());

        defineInstruction("code.quote", new Quote());
        defineInstruction("code.fromboolean", new CodeFromBoolean());
        defineInstruction("code.frominteger", new CodeFromInteger());
        defineInstruction("code.fromfloat", new CodeFromFloat());
        defineInstruction("code.noop", new ExecNoop());

        defineInstruction("exec.k", new ExecK(execStack));
        defineInstruction("exec.s", new ExecS(execStack, maxPointsInProgram));
        defineInstruction("exec.y", new ExecY(execStack));
        defineInstruction("exec.noop", new ExecNoop());

        defineInstruction("exec.do*times", new ExecDoTimes(this));
        defineInstruction("code.do*times", new CodeDoTimes(this));
        defineInstruction("exec.do*count", new ExecDoCount(this));
        defineInstruction("code.do*count", new CodeDoCount(this));
        defineInstruction("exec.do*range", new ExecDoRange(this));
        defineInstruction("code.do*range", new CodeDoRange(this));
        defineInstruction("code.=", new ObjectEquals(codeStack));
        defineInstruction("exec.=", new ObjectEquals(execStack));
        defineInstruction("code.if", new If(codeStack));
        defineInstruction("exec.if", new If(execStack));
        defineInstruction("code.rand", new RandomPushCode(codeStack));
        defineInstruction("exec.rand", new RandomPushCode(execStack));

        defineInstruction("true", new BooleanConstant(true));
        defineInstruction("false", new BooleanConstant(false));

        defineInstruction("input.index", new InputIndex(inputStack));
        defineInstruction("input.inall", new InputInAll(inputStack));
        defineInstruction("input.inallrev", new InputInRev(inputStack));
        defineInstruction("input.stackdepth", new Depth(inputStack));

        defineStackInstructions("integer", intStack);
        defineStackInstructions("float", floatStack);
        defineStackInstructions("boolean", boolStack);
        defineStackInstructions("name", nameStack);
        defineStackInstructions("code", codeStack);
        defineStackInstructions("exec", execStack);

        defineInstruction("frame.push", new PushFrame());
        defineInstruction("frame.pop", new PopFrame());

        generators.put("float.erc", new FloatAtomGenerator());
        generators.put("integer.erc", new IntAtomGenerator());
    }

    /**
     * Enables experimental Push "frames"
     * <p>
     * When frames are enabled, each Push subtree is given a fresh set of stacks
     * (a "frame") when it executes. When a frame is pushed, the top value from
     * each stack is passed to the new frame, and likewise when the frame pops,
     * allowing for input arguments and return values.
     */

    public void setUseFrames(boolean inUseFrames) {
        useFrames = inUseFrames;
    }

    /**
     * Defines the instruction set used for random code generation in this Push
     * interpreter.
     *
     * @param inInstructionList A program consisting of a list of string instruction names to
     *                          be placed in the instruction set.
     */

    public void setInstructions(Program inInstructionList) {
        randomGenerators.clear();

        for (int n = 0; n < inInstructionList.size(); n++) {
            Object o = inInstructionList.peek(n);
            String name = null;

            if (o instanceof Instruction) {
                String[] keys = instructions.keySet().toArray(new String[0]);

                for (String key : keys)
                    if (instructions.get(key) == o) {
                        name = key;
                        break;
                    }
            } else if (o instanceof String) {
                name = (String) o;
            } else
                throw new RuntimeException(
                        "Instruction list must contain a list of Push instruction names only");

            // Check for registered
            if (name.indexOf("registered.") == 0) {
                String registeredType = name.substring(11);

                if (!registeredType.equals("integer")
                        && !registeredType.equals("float")
                        && !registeredType.equals("boolean")
                        && !registeredType.equals("exec")
                        && !registeredType.equals("code")
                        && !registeredType.equals("name")
                        && !registeredType.equals("input")
                        && !registeredType.equals("frame")) {
                    System.err.println("Unknown instruction \"" + name
                            + "\" in instruction set");
                } else {
                    // Legal stack type, so add all generators matching
                    // registeredType to _randomGenerators.
                    Object[] keys = instructions.keySet().toArray();

                    for (Object value : keys) {
                        String key = (String) value;
                        if (key.indexOf(registeredType) == 0) {
                            AtomGenerator g = generators.get(key);
                            randomGenerators.add(g);
                        }
                    }

                    if (registeredType.equals("boolean")) {
                        AtomGenerator t = generators.get("true");
                        randomGenerators.add(t);
                        AtomGenerator f = generators.get("false");
                        randomGenerators.add(f);
                    }
                    if (registeredType.equals("integer")) {
                        AtomGenerator g = generators.get("integer.erc");
                        randomGenerators.add(g);
                    }
                    if (registeredType.equals("float")) {
                        AtomGenerator g = generators.get("float.erc");
                        randomGenerators.add(g);
                    }

                }
            } else if (name.indexOf("input.makeinputs") == 0) {
                String strnum = name.substring(16);
                int num = Integer.parseInt(strnum);

                for (int i = 0; i < num; i++) {
                    defineInstruction("input.in" + i, new InputInN(i));
                    AtomGenerator g = generators.get("input.in" + i);
                    randomGenerators.add(g);
                }
            } else {
                AtomGenerator g = generators.get(name);

                if (g == null) {
                    throw new RuntimeException("Unknown instruction \"" + name
                            + "\" in instruction set");
                } else {
                    randomGenerators.add(g);
                }
            }
        }
    }

    public void addInstruction(String inName, Instruction inInstruction) {
        InstructionAtomGenerator iag = new InstructionAtomGenerator(inName);
        instructions.put(inName, inInstruction);
        generators.put(inName, iag);
        randomGenerators.add(iag);
    }

    protected void defineInstruction(String inName, Instruction inInstruction) {
        instructions.put(inName, inInstruction);
        generators.put(inName, new InstructionAtomGenerator(inName));
    }

    protected void defineStackInstructions(String inTypeName, Stack inStack) {
        defineInstruction(inTypeName + ".pop", new Pop(inStack));
        defineInstruction(inTypeName + ".swap", new Swap(inStack));
        defineInstruction(inTypeName + ".rot", new Rot(inStack));
        defineInstruction(inTypeName + ".flush", new Flush(inStack));
        defineInstruction(inTypeName + ".dup", new Dup(inStack));
        defineInstruction(inTypeName + ".stackdepth", new Depth(inStack));
        defineInstruction(inTypeName + ".shove", new Shove(inStack));
        defineInstruction(inTypeName + ".yank", new Yank(inStack));
        defineInstruction(inTypeName + ".yankdup", new YankDup(inStack));
    }

    /**
     * Sets the parameters for the ERCs.
     */
    public void setRandomParameters(int minRandomInt, int maxRandomInt, int randomIntResolution, float minRandomFloat,
        float maxRandomFloat, float randomFloatResolution, int maxRandomCodeSize, int maxPointsInProgram) {

        this.minRandomInt = minRandomInt;
        this.maxRandomInt = maxRandomInt;
        this.randomIntResolution = randomIntResolution;

        this.minRandomFloat = minRandomFloat;
        this.maxRandomFloat = maxRandomFloat;
        this.randomFloatResolution = randomFloatResolution;

        this.maxRandomCodeSize = maxRandomCodeSize;
        this.maxPointsInProgram = maxPointsInProgram;
    }

    /**
     * Executes a Push program with no execution limit.
     *
     * @return The number of instructions executed.
     */

    public int execute(Program inProgram) {
        return execute(inProgram, -1);
    }

    /**
     * Executes a Push program with a given instruction limit.
     *
     * @param inMaxSteps The maximum number of instructions allowed to be executed.
     * @return The number of instructions executed.
     */

    public int execute(Program inProgram, int inMaxSteps) {
        evaluationExecutions++;
        loadProgram(inProgram); // Initializes program
        return Step(inMaxSteps);
    }

    /**
     * Loads a Push program into the interpreter's exec and code stacks.
     *
     * @param inProgram The program to load.
     */

    public void loadProgram(Program inProgram) {
        codeStack.push(inProgram);
        execStack.push(inProgram);
    }

    /**
     * Steps a Push interpreter forward with a given instruction limit.
     * <p>
     * This method assumes that the intepreter is already setup with an active
     * program (typically using \ref Execute).
     *
     * @param inMaxSteps The maximum number of instructions allowed to be executed.
     * @return The number of instructions executed.
     */

    public int Step(int inMaxSteps) {
        int executed = 0;
        while (inMaxSteps != 0 && execStack.size() > 0) {
            executeInstruction(execStack.pop());
            inMaxSteps--;
            executed++;
        }

        totalStepsTaken += executed;

        return executed;
    }

    public int executeInstruction(Object inObject) {

        if (inObject instanceof Program p) {

            if (useFrames) {
                execStack.push("frame.pop");
            }

            p.pushAllReverse(execStack);

            if (useFrames) {
                execStack.push("frame.push");
            }

            return 0;
        }

        if (inObject instanceof Integer) {
            intStack.push((Integer) inObject);
            return 0;
        }

        if (inObject instanceof Number) {
            floatStack.push(((Number) inObject).floatValue());
            return 0;
        }

        if (inObject instanceof Instruction) {
            ((Instruction) inObject).Execute(this);
            return 0;
        }

        if (inObject instanceof String) {
            Instruction i = instructions.get(inObject);

            if (i != null) {
                i.Execute(this);
            } else {
                nameStack.push(inObject);
            }

            return 0;
        }

        return -1;
    }

    /**
     * Fetch the active integer stack.
     */

    public IntStack intStack() {
        return intStack;
    }

    /**
     * Fetch the active float stack.
     */

    public FloatStack floatStack() {
        return floatStack;
    }

    /**
     * Fetch the active exec stack.
     */

    public ObjectStack execStack() {
        return execStack;
    }

    /**
     * Fetch the active code stack.
     */

    public ObjectStack codeStack() {
        return codeStack;
    }

    /**
     * Fetch the active bool stack.
     */

    public BooleanStack boolStack() {
        return boolStack;
    }

    /**
     * Fetch the active name stack.
     */

    public ObjectStack nameStack() {
        return nameStack;
    }

    /**
     * Fetch the active input stack.
     */

    public ObjectStack inputStack() {
        return inputStack;
    }

    /**
     * Fetch the indexed custom stack
     */
    public Stack getCustomStack(int inIndex) {
        return customStacks.get(inIndex);
    }

    /**
     * Add a custom stack, and return that stack's index
     */
    public int addCustomStack(Stack inStack) {
        customStacks.add(inStack);
        return customStacks.size() - 1;
    }

    protected void assignStacksFromFrame() {
        floatStack = (FloatStack) floatFrameStack.top();
        intStack = (IntStack) intFrameStack.top();
        boolStack = (BooleanStack) boolFrameStack.top();
        codeStack = (ObjectStack) codeFrameStack.top();
        nameStack = (ObjectStack) nameFrameStack.top();
    }

    public void pushStacks() {
        floatFrameStack.push(new FloatStack());
        intFrameStack.push(new IntStack());
        boolFrameStack.push(new BooleanStack());
        codeFrameStack.push(new ObjectStack());
        nameFrameStack.push(new ObjectStack());

        assignStacksFromFrame();
    }

    public void popStacks() {
        floatFrameStack.pop();
        intFrameStack.pop();
        boolFrameStack.pop();
        codeFrameStack.pop();
        nameFrameStack.pop();

        assignStacksFromFrame();
    }

    public void pushFrame() {
        if (useFrames) {
            boolean boolTop = boolStack.top();
            int intTop = intStack.top();
            float floatTop = floatStack.top();
            Object nameTop = nameStack.top();
            Object codeTop = codeStack.top();

            pushStacks();

            floatStack.push(floatTop);
            intStack.push(intTop);
            boolStack.push(boolTop);

            if (nameTop != null)
                nameStack.push(nameTop);
            if (codeTop != null)
                codeStack.push(codeTop);
        }
    }

    public void popFrame() {
        if (useFrames) {
            boolean boolTop = boolStack.top();
            int intTop = intStack.top();
            float floatTop = floatStack.top();
            Object nameTop = nameStack.top();
            Object codeTop = codeStack.top();

            popStacks();

            floatStack.push(floatTop);
            intStack.push(intTop);
            boolStack.push(boolTop);

            if (nameTop != null)
                nameStack.push(nameTop);
            if (codeTop != null)
                codeStack.push(codeTop);
        }
    }

    /**
     * Prints out the current stack states.
     */

    public void printStacks() {
        System.out.println(this);
    }

    /**
     * Returns a string containing the current Interpreter stack states.
     */

    public String toString() {
        String result = "";
        result += "exec stack: " + execStack + "\n";
        result += "code stack: " + codeStack + "\n";
        result += "int stack: " + intStack + "\n";
        result += "float stack: " + floatStack + "\n";
        result += "boolean stack: " + boolStack + "\n";
        result += "name stack: " + nameStack + "\n";
        result += "input stack: " + inputStack + "\n";

        return result;
    }

    /**
     * Resets the Push interpreter state by clearing all of the stacks.
     */

    public void clearStacks() {
        intStack.clear();
        floatStack.clear();
        execStack.clear();
        nameStack.clear();
        boolStack.clear();
        codeStack.clear();
        inputStack.clear();

        // Clear all custom stacks
        for (Stack s : customStacks) {
            s.clear();
        }
    }

    /**
     * Returns a string list of all instructions enabled in the interpreter.
     */
    public String getRegisteredInstructionsString() {
        Object[] keys = instructions.keySet().toArray();
        Arrays.sort(keys);
        String list = "";

        for (int n = 0; n < keys.length; n++)
            list += keys[n] + " ";

        return list;
    }

    /**
     * Returns a string of all the instructions used in this run.
     */
    public String getInstructionsString() {
        Object[] keys = instructions.keySet().toArray();
        List<String> strings = new ArrayList<>();
        StringBuilder str = new StringBuilder();

        for (Object o : keys) {
            String key = (String) o;

            if (randomGenerators.contains(generators.get(key))) {
                strings.add(key);
            }

        }

        if (randomGenerators.contains(generators.get("float.erc"))) {
            strings.add("float.erc");
        }
        if (randomGenerators.contains(generators.get("integer.erc"))) {
            strings.add("integer.erc");
        }

        Collections.sort(strings);
        for (String s : strings) {
            str.append(s).append(" ");
        }

        return str.substring(0, str.length() - 1);
    }

    /**
     * Returns the Instruction whose name is given in instr.
     *
     * @return the Instruction or null if no such Instruction.
     */
    public Instruction getInstruction(String instr) {
        return instructions.get(instr);
    }

    /**
     * Returns the number of evaluation executions so far this run.
     *
     * @return The number of evaluation executions during this run.
     */
    public long getEvaluationExecutions() {
        return evaluationExecutions;
    }

    public InputPusher getInputPusher() {
        return inputPusher;
    }

    public void setInputPusher(InputPusher _inputPusher) {
        this.inputPusher = _inputPusher;
    }

    /**
     * Generates a single random Push atom (instruction name, integer, float,
     * etc) for use in random code generation algorithms.
     *
     * @return A random atom based on the interpreter's current active
     * instruction set.
     */

    public Object randomAtom() {
        int index = random.nextInt(randomGenerators.size());

        return randomGenerators.get(index).generate(this);
    }

    /**
     * Generates a random Push program of a given size.
     *
     * @param inSize The requested size for the program to be generated.
     * @return A random Push program of the given size.
     */

    public Program randomCode(int inSize) {
        Program p = new Program();

        List<Integer> distribution = randomCodeDistribution(inSize - 1,
                inSize - 1);

        for (int count : distribution) {
            if (count == 1) {
                p.push(randomAtom());
            } else {
                p.push(randomCode(count));
            }
        }

        return p;
    }

    /**
     * Generates a list specifying a size distribution to be used for random
     * code.
     * <p>
     * Note: This method is called "decompose" in the lisp implementation.
     *
     * @param inCount       The desired resulting program size.
     * @param inMaxElements The maxmimum number of elements at this level.
     * @return A list of integers representing the size distribution.
     */

    public List<Integer> randomCodeDistribution(int inCount, int inMaxElements) {
        List<Integer> result = new ArrayList<>();

        randomCodeDistribution(result, inCount, inMaxElements);

        Collections.shuffle(result);

        return result;
    }

    /**
     * The recursive worker function for the public RandomCodeDistribution.
     *
     * @param ioList        The working list of distribution values to append to.
     * @param inCount       The desired resulting program size.
     * @param inMaxElements The maxmimum number of elements at this level.
     */

    private void randomCodeDistribution(List<Integer> ioList, int inCount,
            int inMaxElements) {
        if (inCount < 1)
            return;

        int thisSize = inCount < 2 ? 1 : (random.nextInt(inCount) + 1);

        ioList.add(thisSize);

        randomCodeDistribution(ioList, inCount - thisSize, inMaxElements - 1);
    }

    abstract static class AtomGenerator implements Serializable {
        private static final long serialVersionUID = 1L;

        abstract Object generate(Interpreter inInterpreter);
    }

    private static class InstructionAtomGenerator extends AtomGenerator {
        private static final long serialVersionUID = 1L;

        String _instruction;

        InstructionAtomGenerator(String inInstructionName) {
            _instruction = inInstructionName;
        }

        Object generate(Interpreter inInterpreter) {
            return _instruction;
        }
    }

    private class FloatAtomGenerator extends AtomGenerator {
        private static final long serialVersionUID = 1L;

        Object generate(Interpreter inInterpreter) {
            float r = random.nextFloat() * (maxRandomFloat - minRandomFloat);

            r -= (r % randomFloatResolution);

            return r + minRandomFloat;
        }
    }

    private class IntAtomGenerator extends AtomGenerator {
        private static final long serialVersionUID = 1L;

        Object generate(Interpreter inInterpreter) {
            int r = random.nextInt(maxRandomInt - minRandomInt);

            r -= (r % randomIntResolution);

            return r + minRandomInt;
        }
    }

}
