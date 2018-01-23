package sk.intersoft.vicinity.agent.utils;

public class Dump {
    StringBuffer out = new StringBuffer();

    public String indent(int tab) {
        String out = "";
        for(int i = 0; i < tab; i++){
            out += "  ";
        }
        return out;
    }


    public void add(String string, int tab) {
        out.append(indent(tab) + string);
        nl();
    }

    public void add(String string) {
        out.append(string);
    }

    public void nl() {
        out.append(System.lineSeparator());
    }

    public String toString(){
        return out.toString();
    }
}
