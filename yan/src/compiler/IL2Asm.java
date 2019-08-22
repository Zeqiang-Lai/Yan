package compiler;

public class IL2Asm {
    ILGen il_generator;
    String filename;

    StringBuilder asm_code;

    public IL2Asm(ILGen il_generator, String filename) {
        this.il_generator = il_generator;
        this.filename = filename;
        asm_code = new StringBuilder();
    }

    public String toAsm() {
        emit(".file","\"" + filename + "\"");
        translateGlobal();
        emit(".text");
        for(int i=0; i<il_generator.functions.size()-1; i++) {
            translateFunction(il_generator.functions.get(i));
        }
        return asm_code.toString();
    }

    private void translateFunction(ILGen.ILFunction ilFunction) {
        emit(".global", ilFunction.name);
        emit(".type", ilFunction.name, "@function");
        emitLabel(ilFunction.name);
        emit("pushq", "%rbp");
        emit("movq", "rsp", "rbp");

        // parameters

        // local variables

        // commands

        emit("leaveq");
        emit("retq");
    }

    private void translateGlobal() {
        
    }

    private void emit(String inst, String src, String des) {
        emit(inst + "\t" + src + ", " + des);
    }

    private void emit(String inst, String des) {
        emit(inst + "\t" + des);
    }

    private void emit(String str) {
        asm_code.append("\t").append(str).append("\n");
    }

    private void emitLabel(String label) {
        asm_code.append(label).append(":\n");
    }
}
