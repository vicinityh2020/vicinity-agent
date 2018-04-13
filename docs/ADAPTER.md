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

# General concepts

## General note on interaction concepts

Before we dig into the possible interaction patterns, it is necessary (or at least good) to remind, what is the main
concept of inter-object interaction in VICINITY.

**In VICINITY, the interaction happens always between two objects!** That means:
* if object(1) needs to interact with object(2), in Neighbourhood Manager, there must be set proper permission for this interaction
* interaction is always triggered by object
* interaction must be subscribed by credentials of object triggering this interaction - object must use its credentials to perform the interaction

In some cases, objects that need to perform the interaction are passive. For example, the sensor needs to publish the event, but
the sensor itself is the passive unit. In this case, the responsibility for performing action takes some logic component of the node.
This can be value added service object or logic of Adapter. This active component acts on behalf of passive object.
For example (depending on Adapter implementation, of course), Adapter manages the events from sensors
(which are passive units, they don't trigger any interactions themselves).
Once event needs to be published, Adapter calls the proper Agent service, providing information on event itself, but also
on identifier of object, that produced the event. Agent translates this request to proper GTW API call, providing the credentials
of object, that produced the event. Adapter acts on behalf of this object.

Agent services are designed to simplify this process for the integrators.

## VICINITY Client Node

Generally, Client Node consists of three components:
* Adapter (or multiple Adapters) - the translator between its infrastructure and VICINITY common services - implements only very basic interaction
 patterns for objects it exposes. Regarding its objects, Agent is aware only of internal identifier of its objects
* Agent - extends the functionality of Adapter, translates between internal identifies of Adapter objects and common VICINITY identifiers,
holds the credentials of objects into P2P network, except the translations, Agent provides common functionalities, such as
automatic objects discovery, log in/out of objects into P2P network or .. generally .. it provides services for Adapter
integrators to easily interact with remote objects (in another VICINITY node)
* Gateway API - providing access to VICINITY P2P network

Remote objects can access objects behind Adapter via sequence:

Remote object (in Adapter) -> its Agent -> its GTW Api ---> this GTW Api -> this Agent -> this Adapter

and vice versa, when objects behind Adapter needs to interact with remote object, the most easy way to do it is to
 use proper service of its Agent. See [Agent docs](../README.md).

object in this Adapter -> this Agent -> this GTW Api ---> remote GTW API -> its Agent -> object of its Adapter


# Adapter API

## Discovery of Adapter objects

One of the most important responsibilities of Client node is ability to describe, what objects are exposed to VICINITY.
This is the responsibility of Adapter. Adapter must be able to provide the **list of object descriptions** that are
exposed to VICINITY. Objects are described in [**VICINITY common thing description**](TD.md) format.

For this purpose, Adapter **must** implement the service:

```
GET /objects
```

This service returns the list of thing descriptions exposed by this Adapter in [VICINITY Common Thing Description format](TD.md).
The output of this service is specified in Thing Description documentation, see section [Serialization of Thing Descriptions](TD.md#serialization-of-thing-descriptions).



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

Common interaction patterns in VICINITY are **read/set property** or **read status/execute action**:
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

See also [VICINITY Common Thing Description format](TD.md) for additional explanation.

### OID confusions
* **oid** on level of GTW API (or VICINITY) is always the VICINITY specific object id
* **oid** in Adapter thing description is always the internal, infrastructure specific, identifier of object behind the Adapter

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
* **mandatory**: **GET /objects** - object discovery service
* **mandatory**: endpoints with [prescribed http method](#http-methods-for-interaction-patterns) provided in **read/write_link** in thing descriptions
* optional: **PUT /objects/{subscriber-id}/events/{eid}** if Adapter needs to receive events

All other functionality, such as how to access the remote object, how to open or subscribe event channels for objects, is part of [Agent documentation](../README.md).
Use it.

