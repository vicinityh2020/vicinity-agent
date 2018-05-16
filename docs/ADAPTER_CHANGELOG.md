# 2018-05-16

## Agent service now accepts multiple agent configs

Agent service now has one GTW-API endpoint shared by all included agents.
GTW-API endpoint is no more used in agent configs.
Agent service is configured from file:

```
agent/config/service-config.json
```

Agetn service configuration file now contains only endpoint to GTW-API:

```
service-config.json:
{
    "gateway-api-endpoint": "http://localhost:8181/api"
}

```

Agent configuration files are all placed in one folder, e.g.
```
agent/config/agents/
```

This folder now may contain configuration files for multiple agents.

```
/agent/
    /config/
        db/
        agents/
            agent-1-config.json
            ..
            agent-x-config.json
        service-config.json
```

Update **agent.sh** script to latest version enabling this feature.
**agent.sh** script will enable to specify the both service configuration file
and folder with agent configurations.


## agent-id is now mandatory

In one agent, now we support multiple agents, each of them may have multiple adapters.
Each of adapters may have multiple objects.

Agent service must be able to unambiguously decide, which object belongs to which adapter
and which adapter belongs to which agent.

**IMPORTANT!!!**

**adapter-id must be specific for whole client node.**
Generate **adapter-id** as **unique hash code** and **store it in your adapter code**.
It will be possibly required, when talking to agent, if there will be ambiguity in
object identifiers (case, when different adapters contain objects with
identical infrastructure-id.

Changes in agent config:

* property "adapters" / "adapter-id" is now mandatory

```
#!json
    "adapters": [
        {
            "adapter-id": "adapter-1",
            "endpoint": "http://localhost:9995/adapter"
        }
    ]
```

Changes in adapter **objects/** service response:

* **adapter-id** must be provided, matching **adapter-id** in configuration of related agent

update **objects/** service response to:

```
#!json
{
    "adapter-id": "unique adapter identifier having match in one of agent configs",
    "thing-descriptions": [
        array of thing descriptions
    ]
}
```

## Agent service allows active discovery of objects triggered by adapter

Discovery may be triggered by agent, but now, also by adapter proactively.
If you want your adapter to trigger the discovery, in agent config "adapters", use property
**"active-discovery": true/false**.

If set to true, agent will not try to invoke discovery, agent will wait for agent to do this.
When running node, adapter must be started as last component!

If set to false, agent will invoke discovery when started.
When running node, agent must be started as last component!

Default is : **"active-discovery": false**.

```
#!json
    "adapters": [
        {
            "adapter-id": "adapter-1",
            "active-discovery": true
            "endpoint": "http://localhost:9995/adapter"
        }
    ]
```


## Agent service allows to miss "endpoint" in agent config / "adapters"

When endpoint is not provided, that means, that only communication allowed
is **adapter -> agent**. In this case, agent can not speak to adapter and
discovery must be invoked by adapter.

For this reason, **"active-discovery"** must be set to **true**.
If **"active-discovery"** is missing, it is set to **true** by default.


## Specification of "input"/"output" in thing desc. links (and event) are now mandatory

DataSchema specifying the payload of "input"/"output" has to be defined.
Now, "input"/"output" in links (and "output" in event) must be provided, not empty and
must be valid according specification [see DataSchema specs](docs/TD.md#data-schema).
