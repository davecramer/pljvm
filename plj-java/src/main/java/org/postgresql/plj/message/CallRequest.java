package org.postgresql.plj.message;

import org.postgresql.plj.Type;
import org.postgresql.plj.arg.Argument;

public class CallRequest {
    String name;
    String definition;
    int oid;
    boolean hasChanged;
    Type returnType;
    String returnName;
    boolean setReturning;
    int numArgs;
    Argument args[];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public boolean isHasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public boolean isSetReturning() {
        return setReturning;
    }

    public void setSetReturning(boolean setReturning) {
        this.setReturning = setReturning;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public void setNumArgs(int numArgs) {
        this.numArgs = numArgs;
        this.args = new Argument[numArgs];
    }

    public Argument[] getArgs() {
        return args;
    }

    public void setArg(int i, Argument arg) {
        this.args[i] = arg;
    }
}
