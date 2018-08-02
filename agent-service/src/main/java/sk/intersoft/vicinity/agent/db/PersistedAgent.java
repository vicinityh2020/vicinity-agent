package sk.intersoft.vicinity.agent.db;

public class PersistedAgent {
    public String agentId = null;
    public String password = null;


    public PersistedAgent(String agentId,
                          String password){
        this.agentId = agentId;
        this.password = password;
    }

    public String toString(){
        return "[Persisted Agent: ["+agentId+": "+password+"]]";
    }
}
