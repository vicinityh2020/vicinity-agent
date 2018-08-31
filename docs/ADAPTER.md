# Agent -> Multi-Agent migration:
Agent is actually in heavy refactoring to enable multi-agent client node.
Here are actual important changes, please, adjust your adapter implementation according to it.

[Click to see the Migration Guide](ADAPTER_MIGRATION.md)

# Adapter for integrators


Adapter is the first step into interoperability in VICINITY. Its main purpose is to serve as the translator
between infrastructure specific language and VICINITY language. Adapter is kind of software *driver*
enabling VICINITY to interact with all specific infrastructures in same, uniform way.

VICINITY Adapter serves as the proxy between common VICINITY services and underlying infrastructure.
For each specific infrastructure to be integrated, there must exist specific Adapter.
The role of Adapter is to translate VICINITY services into infrastructure specific services.
This document specifies the API that must be implemented in Adapter, when integrating infrastructure.

All identifiers of objects used in adapter are local - infrastructure specific. The only exception is, when Adapter
needs to access remote objects (another VICINITY node).

Adapter represents the *driver* to specific infrastructure and
provides services just fir very basic interactions with objects behind it, namely:
* provide discovery information in *Common VICINITY Thing Description* format
* get/set object property
* get status/execute object action
* receive event

Lets look at them in details.

# Agent Service endpoint

**Agent Service runs at default endpoint**:

```
http://localhost:9997/agent
resp.
http://IP:PORT/agent
```


# Adapter API

## Discovery of Adapter objects

One of the most important responsibilities of Client node is ability to describe, what objects are exposed to VICINITY.
This is the responsibility of Adapter. Adapter must be able to provide the **list of object descriptions** that are
exposed to VICINITY.

This list of thing descriptions is exposed by Adapter in [VICINITY Common Thing Description format](TD.md).
See [Serialization of Thing Descriptions](TD.md#serialization-of-thing-descriptions).
Lets call it **Adapter discovery data**.

Discovery of Adapter object can be:
* **passive** - Agent asks Adapter for discovery data
* **active** - Adapter triggers the discovery process itself by calling specific Agent service

The choice, if discovery is passive or active is configured in related agent configuration
file (see [Configuration of adapters in Agent](AGENT.md#configuration-of-adapters)).

### Passive discovery

Passive discovery is triggered by Agent. When Agent service starts, it configures all
included Agent components and their Adapters. Part of configuration is the discovery
of objects inside the adapters.

If Adapter is configured for passive discovery, Agent proactively asks Adapter for
discovery data. For this purpose, Adapter must implement the following service, where
discovery data will be available:

```
GET: /objects
```

### Active discovery

Active discovery is triggered by Adapter itself. It is useful in case, when Adapter
does not have the endpoint (e.g. running on mobile phone) or when Adapter just needs
to reconfigure itself (refresh the list of exposed objects).

Active discovery is performed by calling the Agent service:

```
POST: /objects
```

The payload of the post is the same discovery data, as if it was provided by passive
discovery.

Agent reconfigures the objects exposed by Adapter.

**If Adapter is configured for active discovery, it is mandatory to call this service on Agent.
Agent will not perform the discovery of this Adapter.**

However, Adapter may perform active discovery anytime it needs, Agent will refresh the
actual Adapter content, does not matter how it is configured.


## Interaction patterns

Adapter provides the information, which endpoints are implemented to interact with objects in their thing descriptions.
There is no specific prescription for Adapter API. Agent will always use the endpoints provided in thing descriptions.

Before looking into interaction patterns, it is important to explain, how Agent uses and interpretes the interaction endpoints
used in Adapter.

In Thing Description format, the interaction patterns always contain the keys **read_link** and **write_link**.
In thing description at least one of **read_link** or **write_link** must be presented. If **read_link** is missing,
 the pattern is only for writing and vice versa, if **write_link** is missing, the pattern is only for reading.
These hold the information for Agent, what Adapter endpoint should be used to read property (or action status) and
what Adapter endpoint should be used to set the value of property or execute the action.

**For each link in thing descriptions there must be implemented endpoint in Adapter!**

### How agent interpretes the links in interaction patterns

On the level of Agent, the common interaction patterns in VICINITY are **read/set property** or **read status/execute action**:
```
GET/PUT  /objects/{oid}/properties/{pid}
GET/POST /objects/{oid}/actions/{aid}
```

**oid** in request is always VICINITY identifier of object. When such a request arrives into Agent, Agent must translate it
into proper call of Adapter endpoint. When Agent receives one of common VICINITY interaction requests (above), it is interpreted as follows:
* Agent holds the mapping between VICINITY **oid**s and Adapter's internal ids of objects.
* Agent searches its configuration, looks for Adapter object mapped to **oid** and finds its corresponding thing description
* In thing description, Agent looks for **read_link** or **write_link** depending on the request (get property/action status, read property/execute action). The result is the **link** to be executed on Adapter.
* Agent calls the Adapter with corresponding **link** and passes back the result as the response to request

That means, Adapter is free to specify any read/write link in thing description. Agent will use it.

If needed, **read/write_link** may contain properties, which Agent automatically translates before executing the link, namely:
* **{oid}** is translated into **infrastructure id** of object
* **{pid}/{aid}/{eid}** is translated into identifier of property/action/event, to which the link belongs


### OID confusions
From Adapter point of view:
* **oid** of objects inside of Adapter: always the internal id of object behind the Adapter is used. Adapter does not (and does not need to) know the VICINITY identifiers of its own objects.
* **oid** of remote objects (objects outside of Adapter): always VICINITY oid is used. Adapter can not know internal identifiers of objects behind other Adapters.

Adapter always uses the internal, infrastructure specific, identifiers of objects it manages. The only exception is, when
some of Adapter objects needs to interact with remote object (from another VICINITY node). For example,
if object representing the value added service needs to access remote objects. In this case, of course,
the VICINITY **oid** of this remote object must be used.


### HTTP Methods for interaction patterns

To follow the REST specification and W3C WoT recommendations, Adapters **must always** implement the following
http methods in endpoints of the interaction patterns:

| Pattern | link | method |
| --- | --- | --- |
| Property | read_link | GET |
| Property | write_link | PUT |
| Action | read_link | GET |
| Action | write_link | POST |


### Examples:

Lets take the reading property interaction pattern to explain, how Agent interprets the read/write link. Skeleton of Adapter thing
description with one interaction pattern:
```
#!json
{
    "oid": "adapter-object-id",
    "properties": [
        {
            "pid": "property-unique-identifier",
            "read_link": {
                "href": "link implemented by Adapter"
            }
        }
    ]
}
```

In thing description provided by Adapter, the **oid** always refers to internal, infrastructure specific, identifier
of object.


The link interpretation examples:
```
"href": "/custom/path"

Agent executes:
${adapter-endpoint}/custom/path
```

```
"href": "/custom/{oid}/path-to/{pid}"

Agent executes:
${adapter-endpoint}/custom/adapter-object-id/path-to/property-unique-identifier
```

The same pattern is applied for both **read/write_link** and for all interaction patterns.


## Consumption of events

If Adapter tends to receive the events, it must implement the following endpoint:
```
PUT /objects/{subscriber-id}/events/{eid}
```

The rule is, that always **object subscribes to event channel**.
The event channel name is always
```
/objects/{oid}/events/{eid}
```

The **oid** is VICINITY identifier of object producing the event **eid**.


Once Agent receives the event, it propagates it into Adapter by calling its endpoint above. **subscriber-id** is the internal
idetifier of Adapter object subscribed to event **eid** of object **oid**. The **oid** of object that produced the event
will be included in the event payload. Event payload structure is yet TBD.

# Adapter API requirements summarized

Adapter implements following services:
* **GET /objects** - object discovery service - **mandatory** if adapter is configured for passive discovery
* **mandatory**: endpoints with [prescribed http method](#http-methods-for-interaction-patterns) provided in **read/write_link** in thing descriptions
* optional: **PUT /objects/{subscriber-id}/events/{eid}** if Adapter needs to receive events

All other functionality, such as how to access the remote object, how to open or subscribe event channels for objects, is part of [Agent documentation](AGENT.md).
Use it.

