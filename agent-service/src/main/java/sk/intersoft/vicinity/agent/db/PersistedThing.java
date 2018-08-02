package sk.intersoft.vicinity.agent.db;

import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class PersistedThing {
    public String oid = null;
    public String agentId = null;
    public String adapterId = null;
    public String adapterInfrastructureId = null;
    public String password = null;

    public PersistedThing(String oid,
                          String agentId,
                          String adapterId,
                          String adapterInfrastructureId,
                          String password){
        this.oid = oid;
        this.adapterId = adapterId;
        this.agentId = agentId;
        this.adapterInfrastructureId = adapterInfrastructureId;
        this.password = password;
    }

    public PersistedThing(ThingDescription thing){
        this.oid = thing.oid;
        this.agentId = thing.agentId;
        this.adapterId = thing.adapterId;
        this.adapterInfrastructureId = thing.adapterInfrastructureID;
        this.password = thing.password;
    }

    public PersistedThing(String oid, String agentId, String adapterId){
        this.oid = oid;
        this.agentId = agentId;
        this.adapterId = adapterId;
    }

    public String toString(){
        return "[Persisted Thing: ["+oid+"]["+agentId+" :: "+adapterId+" :: "+adapterInfrastructureId+"]["+password+"]]";
    }

}
