import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import java.applet.Applet;

public class PshApplet extends Applet {
    public static final long serialVersionUID = 2L;


    Interpreter _interpreter = new Interpreter();

    public void init() {
        try {
            System.out.println(Run(getParameter("program")));
        } catch (Exception e) {
        }
    }

    public String Run(String inValue) {
        _interpreter.clearStacks();

        try {
            Program p;
            p = new Program(_interpreter, inValue);

            _interpreter.Execute(p);

        } catch (Exception e) {

        }

        return _interpreter.toString();
    }

    public String GetInstructionString() {
        return "=> " + _interpreter.GetRegisteredInstructionsString();
    }
}
