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

**IMPORTANT NOTE:** The first thing the Agent Service does is the complete
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

The response is simple notification on operation success, e.g.:

```
#!json
{
    "status": "success",
    "data": "Discovery for adapter [{adapterId from posted payload}] successfully done!"
}

{
    "status": "error",
    "reason": "what went banana message"
}
```

**IMPORTANT NOTE:** The first thing the Agent Service does is the complete
clearance of existing mappings for adapter and its things. If anything in adapter (re)configuration
fails (parsing errors, unavailable adapters, ...), the mapping for this adapter remains empty, until the configuration
runs correctly.

## Accessing object properties

### Accessing the property of remote object

Accessing the object property is always triggered by concrete object behind
the concrete adapter. Remote objects are those, which are in another Client Node as
the requesting Adapter. And remote objects are always available via their VICINITY **oid**.

#### Read remote object property

Endpoint:

```
GET: /remote/objects/{oid}/properties/{pid}
```

Parameters:
* **oid** - VICINITY oid of requested object
* **pid** - the property identifier

To access the remote object, it is mandatory to provide identifier of object,
which is requesting this data. This is always object in concrete Adapter,
identified by its internal ID.

Request headers:
* **infrastructure-id** infrastructure-id of requesting object
* **adapter-id** identifier of adapter for this object

This request is forwarded to GTW API with proper VICINITY credentials of requesting object.
The response is passed back to GTW API, forwarded into Agent Service, which returns it
as response to this request, without touching it. The response payload should
follow the specification of the [DataSchema](TD.md#data-schema) of property *output*
provided in thing description of requested remote object.


#### Set remote object property

Endpoint:

```
PUT: /remote/objects/{oid}/properties/{pid}
```

Parameters:
* **oid** - VICINITY oid of requested object
* **pid** - the property identifier

To access the remote object, it is mandatory to provide identifier of object,
which is requesting this data. This is always object in concrete Adapter,
identified by its internal ID.

Request headers:
* **infrastructure-id** infrastructure-id of requesting object
* **adapter-id** identifier of adapter for this object

Payload is the JSON object, which should follow
the specification of the [DataSchema](TD.md#data-schema) of property *input* provided in thing description of requested remote object.


This request is forwarded to GTW API with proper VICINITY credentials of requesting object.
The response is passed back to GTW API, forwarded into Agent Service, which returns it
as response to this request, without touching it. The response payload should
follow the specification of the [DataSchema](TD.md#data-schema) of property *output*
provided in thing description of requested remote object.


### Accessing the property of local object

The reverse as accessing of remote objects. If some remote object accesses the
property of some of local objects within this Agent Service. Basically, if
remote object wants to access local object in Agent Service, it must use the
remote object access using its Agent Service, as described above.

If request is received, the local object with requested VICINITY **oid**
is found and the request is translated to call of related Adapter
(responsible this local object) using the proper Adapter endpoint.

#### Read local object property

Endpoint:

```
GET: /objects/{oid}/properties/{pid}
```

Parameters:
* **oid** - VICINITY oid of requested object
* **pid** - the property identifier

The call is translated into proper call on Adapter responsible for this object.

The response payload should
follow the specification of the [DataSchema](TD.md#data-schema) of property *output*
provided in thing description of requested local object.


#### Set local object property

Endpoint:

```
PUT: /objects/{oid}/properties/{pid}
```

Parameters:
* **oid** - VICINITY oid of requested object
* **pid** - the property identifier

The call is translated into proper call on Adapter responsible for this object.

The payload into service should
follow the specification of the [DataSchema](TD.md#data-schema) of property *input*
provided in thing description of requested local object.

The response payload should
follow the specification of the [DataSchema](TD.md#data-schema) of property *output*
provided in thing description of requested local object.
