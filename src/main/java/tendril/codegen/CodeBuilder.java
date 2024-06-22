package tendril.codegen;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;

public class CodeBuilder {

    private final TextStringBuilder builder = new TextStringBuilder();
    private int numIndents = 0;
    
    public void indent() {
        numIndents++;
    }
    
    public void deIndent() {
        if(numIndents == 0)
            return;
        numIndents--;
    }
    
    public void append(String line) {
        builder.append(StringUtils.repeat(' ', numIndents * 4));
        builder.appendln(line);
    }
    
    public void blankLine() {
        builder.appendln("");
    }

    public String get() {
        return builder.get();
    }
}
