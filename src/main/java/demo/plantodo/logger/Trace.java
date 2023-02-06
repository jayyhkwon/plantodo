package demo.plantodo.logger;

public interface Trace {
    void simpleLog(String msg);
    void complexLog(String msg, String type, String detail);
}
