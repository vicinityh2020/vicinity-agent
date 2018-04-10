# Adapter Overview

[Agent docs](../README.md)


VICINITY Adapter serves as the proxy between common VICINITY services and underlying infrastructure.
For each specific infrastructure to be integrated, there must exist specific VICINITY Adapter.
The role of Adapter is to translate VICINITY services into infrastructure specific services.
VICINITY Adapter provides the API, which must be implemented, when integrating new infrastructure.

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

this Adapter -> this Agent -> this GTW Api ---> remote GTW API -> its Agent -> object of its Adapter


# Adapter API

## Discovery of Adapter objects

One of the most important responsibilities of Client node is ability to describe, what objects are contained.
This is the responsibility of Adapter. Adapter must be able to provide the **list of object descriptions** that are
exposed to VICINITY. Objects are described in **VICINITY common thing description** format.

For this purpose, Adapter **must** implement the service:

```
GET /objects
```

This service returns the list of thing descriptions exposed by this Adapter.

Common Thing Description Format: TBD soon.

**In current Agent implementation, the Client node may contain multiple adapters.** That means, one client node
may serve multiple different infrastructures (per each there must exist specific Adapter). When multiple adapters are used,
it is necessary to distinguish between them. In this case, there must exist persistent unique identifier of each Adapter.
If using multiple adapters, the */objects* service must contain the **adapter-id** in form:

```
#!json
{
    "adapter-id": "unique adapter identifier",
    "thing-descriptions":
    [
     the list of thing descriptions as described in
     common thing description format
    ]
}
```

Identifiers of adapters must be unique within the VICINITY node.

**!!!VERY IMPORTANT!!!** The **adapter-id** is used by agent to tie the objects with its adapter. It is treated
as persistent and can not be changed. Agent holds the pairs **adapter-id : object-id** and **adapter-id** is the only
clue, how to distinguish, to which Adapter the object belong. Once **adapter-id** is changed, Agent treats it
as new Adapter and in discovery process, it re-creates all objects in it and creates new VICINITY **oid**s with new credentials.
This means, objects are treated as new, so all friendships in Neighbourhood Manager of former objects will be lost.


## Interaction patterns

Adapter provides the information, which endpoints are implemented to interact with objects in their thing descriptions.
There is no specific prescription for Adapter API. Agent will always use the endpoints provided in thing descriptions.

Before looking into interaction patterns, it is important to explain, how Agent uses and interpretes the interaction endpoints
used in Adapter.

In Thing Description format, the interaction patterns always contain the keys **read_links** and **write_links**.
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
* In thing description, Agent looks for **read_links** or **write_links** depending on the request (get property/action status, read property/execute action). The result is the **link** to be executed on Adapter.
* Agent calls the Adapter with corresponding **link** and passes back the result as the response to request

That means, Adapter is free to specify any read/write link in thing description. Agent will use it.

If needed, **read/write_links** may contain properties, which Agent automatically translates before executing the link, namely:
* **{oid}** is translated into **infrastructure id** of object
* **{pid}/{aid}/{eid}** is translated into identifier of property/action/event, to which the link belongs

### Examples:

Lets take the reading property interaction pattern to explain, how Agent interprets the read/write links. Skeleton of Adapter thing
description with one interaction pattern:
```
#!json
{
    "oid": "adapter-object-id",
    "properties": [
        {
            "pid": "property-unique-identifier",
            "read_links": [{
                "href": "link implemented by Adapter"
            }]
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

The same pattern is applied for both **read/write_links** and for all interaction patterns.

The **!!!IMPORTANT!!!** thing is, that thing description allows to specify the links as the array of **href** objects.
In current implementation, Agent uses only the first link!


