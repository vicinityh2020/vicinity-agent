# Agent Service API

The full list of implemented Agent Service endpoints.

## Auto Discovery / Configuration

### The actual Agent Service configuration

Dumps the Agent Service actual known mapping, in the moment when this service was called.
Formerly developed for debugging purposes, but seems to be generally useful
to be able to check the actual Agent Service state.

Contains the complete dump from several mapping perspectives:
* the list of known agent configurations, per each there is list of adapters attached, per each adapter, the list of discovered things attached
* the list of known adapter configurations, per each there is list of discovered things attached
* the mapping of things for particular adapters
* the mapping of things based on their VICINITY oid

Endpoint:

```
GET : /configuration
```

Example response:

```
#!json
{
  "agents": [
    {
      "agent-id": "4aaf6042-9888-4cd1-bc56-f42a84204101",
      "adapters": [
        {
          "adapter-id": "adapter-1",
          "things": [{
            "adapter-infra-id": "adapter-1---!---test",
            "infra-id": "test",
            "oid": "70807275-e563-4238-8ece-a68572e415fe",
            "adapter-oid": "adapter-1---!---70807275-e563-4238-8ece-a68572e415fe"
          }]
        }
      ]
    }
  ],
  "adapters": [
    {
      "adapter-id": "adapter-1",
      "things": [{
        "adapter-infra-id": "adapter-1---!---test",
        "infra-id": "test",
        "oid": "70807275-e563-4238-8ece-a68572e415fe",
        "adapter-oid": "adapter-1---!---70807275-e563-4238-8ece-a68572e415fe"
      }]
    }
  ],
  "things-by-adapter": [
    {
      "for-adapter": "adapter-1",
      "adapter-things": [{
        "adapter-infra-id": "adapter-1---!---test",
        "infra-id": "test",
        "oid": "70807275-e563-4238-8ece-a68572e415fe",
        "adapter-oid": "adapter-1---!---70807275-e563-4238-8ece-a68572e415fe"
      }]
    }
  ],
  "things-by-oid": [{
    "adapter-infra-id": "adapter-1---!---test",
    "infra-id": "test",
    "oid": "70807275-e563-4238-8ece-a68572e415fe",
    "adapter-oid": "adapter-1---!---70807275-e563-4238-8ece-a68572e415fe"
  }]
}
```

### Re-configuration of agent

Agent Service must ensure, that new agents may be added and configured, also
existing agents may be reconfigured. All of this on the fly without the need
of whole Agent Service restart.

Agent Service provides endpoint to enable on the fly (re)configurations of particular
agent components.

The endpoint:

```
PUT : /agents/{agid}/configure
```

Parameters:
* {agid}: the agent-id of agent to be (re)configured

**The payload (the request body) is not provided!**

The response is simple notification on operation success, e.g.:

```
#!json
{
    "status": "success",
    "data": "Agent [{agid}] was successfully configured!"
}

{
    "status": "error",
    "reason": "what went banana message"
}
```

Agent service search for configuration file containing the agent component with **{agid}**
parameter. For adding the new agent, just place the new agent config file into
agents config folder (by default */config/agents/*) and call configuration service.

**IMPORTANT NOTE: ** The first thing the Agent Service does is the complete
clearance of existing mappings for agent with {agid}, including clearance
of attached adapter components and their things. If anything in agent configuration
fails (parsing errors, unavailable adapters, ...), the mapping for this agent remains empty, until the configuration
runs correctly.

### Re-configuration of adapter

Agent Service enables the so-called *active discovery* of Adapters, this means,
the Adapters may proactively send their discovery information. There are two reasons
for this:
* Adapters, which are not available via static endpoints (e.g. mobile phones do not have the IP) must announce their discovery information proactively
* It is possible to reconfigure any Adapter discovery information by request, for example if the list of thing descriptions behind the Adapter needs to be actualized (added new/removed/changed things)

The endpoint:
```
POST: /objects
```

The payload is the [Adapter discovery data](TD.md#serialization-of-thing-descriptions).


**IMPORTANT NOTE: ** The first thing the Agent Service does is the complete
clearance of existing mappings for adapter and its things. If anything in adapter (re)configuration
fails (parsing errors, unavailable adapters, ...), the mapping for this adapter remains empty, until the configuration
runs correctly.
