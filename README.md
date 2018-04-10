# Overview

VICINITY Agent serves as the interface between VICINITY GateWay and VICINITY Adapter.

## Adapter role

Adapter represents the *driver* to specific infrastructure and
provides just very basic interactions with objects behind it, namely:
* provide discovery information in *Common VICINITY Thing Description* format
* get/set object property
* get status/execute object action
* open/subscribe to event channel
* publish/receive event

Every object in VICINITY has **its unique VICINITY identifier (oid)**,
but adapter **always uses the internal object identifiers (infrastructure-id)**, specific for its infrastructure.

## Agent role

Agent is the functional extension of Adapter. The role of Agent is to make the life of Adapter developers easier.

The responsibility of agent is:
* to translate between object infrastructure identifiers (**infrastructure-id**) used by Adapter
and VICINITY identifiers (**oid**).
* to automatize common tasks, like **discovery/configuration** and **opening/subscribing to event channels**
* to translate between common VICINITY consumption/eventing services (implemented in GTW API) into specific Adapter services, depending
 how they are described in Thing Descriptions
* translating Adapter requests for consumption/event services into common VICINITY services (implemented in GTW API), with proper credentials


Now, in details.

# Installation guide

## Deployment and re-deployment

Agent comes with and requires the following directory structure:
```
config/
    db/
    agent-config.json
logs/
agent.jar
agent.sh
```

**!!!VERY IMPORTANT!!!** folder **config/db** contains local agent configuration storage, with all mappings and credentials.
Once any content in this folder is manually removed or edited, local configuration may be lost and all objects in
this node may be treated as new. This leads to dropping whole configuration also in Neighbourhood manager, including
friendships and permissions.

*config/agent-config.json* contains the agent configuration
*logs/* contains all agent logs files
*agent.jar* is the executable
*agent.sh* is the script to run/stop the agent

Run the agent:
```
#!shell

./agent.sh
```

Stop the agent:
```
#!shell

./agent.sh stop
```

To redeploy the Agent, simply stop the agent, replace the **agent.jar** file and run it again.


## Configuration

The agent configuration can be found in *config/agent-config.json* file. The config looks as follows:
```
#!json
{
    "credentials": {
        "agent-id": "agent id generated by Neighbourhood Manager",
        "password": "agent id generated by Neighbourhood Manager"
    },
    "gateway-api-endpoint": "http://localhost:8181/api",
    "adapters": [
        {
            "endpoint": "http://localhost:9995/adapter"
        }
    ],
    "events": {
        "channels": [
            {
                "infrastructure-id": "internal identifier of object publishing the event",
                "adapter-id": "adapter identifier of this object",
                "eid": "object event identifier",
            }
        ],
        "subscriptions": [
            {
                "oid": "VICINITY object id",
                "infrastructure-id": "internal identifier of object subscribing for the event",
                "adapter-id": "adapter identifier of subscriber object",
                "eid": "object event identifier",
            }
        ]
    }
}
```

Set agent credentials to **agent id** and **password** generated by Neighbourhood Manager, when VICINITY node
was created. Set the endpoint to GTW API. Add the list of Adapters to be handled by this agent.

**!!!VERY IMPORTANT!!!** For each adapter it is mandatory to provide its unique **adapter-id** in discovery service! **adapter-id** is treated
as persistent, in Agent, internally, each object identifier is tied with adapter identifier. In current
implementation, the **adapter-id** is the only clue, how to tie the concrete object with concrete adapter
(especially in cases, when multiple adapters may contain objects with same identifiers). Once adapter identifiers are
changed, all existing objects behind this adapter are treated as new. They will receive new credentials, all friendships
will be lost.

Additional configuration can be changed in **agent.sh** script. If needed, it is possible to change the port, where Agent will run,
the log folder destination and the path to agent config json file. Just edit following variables in **agent.sh** script:

```
SERVER_PORT=9997
CONFIG_FILE=config/agent-config.json
LOGS_FOLDER=logs
```


## Agent response
The response of agent is always wrapped into following structure:

Success:
```
#!json
{
    "status": "success"
    "data": {
        untouched payload object returned by requested component
    }
}
```

Failure:
```
#!json
{
    "status": "failure"
    "reason": "the reason of failure, as much known as possible",
}
```


# Auto discovery/configuration

Agent always holds the actual configuration of objects behind its Adapter(s).
The configuration always contains the actual list of objects presented by Adapter and their services (consumption/eventing).
For each object, there is specified unique mapping between object's **infrastructure-id** and VICINITY **oid**.
Agent also holds the object's credentials, that are **mandatory** for interaction between any pair of objects in VICINITY.

Auto Discovery process is launched, when Agent starts (by default) and is composed of following steps:
1. read the last active configuration of node from Neighbourhood Manager
2. read the actual thing descriptions from all its Adapters
3. make DIFF. Last active configuration and actual list of objects is compared, the result is
    * the list of missing objects to delete
    * the list of new objects to create
    * the list of objects to update; update is the change of any of mandatory properties and is interpreted as the violence to contract, thus the update is in Neighbourhood Manager executed as: delete/create object + drop all friendships; the result of update operation is the brand new object with new **oid** and credentials
    * the list of unchanged objects
4. the CRUD dance with Neighbourhood Manager, delete/create/update objects and their semantic models
5. actualize the actual configuration
6. log in all objects into P2P network
7. open channels to publish events
8. subscribe to channels to receive events

The result of auto discovery is the list of all active objects with actual credentials and their consumption/eventing services.
Using the actual configuration, the Agent can translate between common VICINITY services and Adapter services, using the proper credentials.

# General note on interaction concepts

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


# Data consumption services

Data consumption services allows to read/set property value or execute/read status of action on the object.

**Important!** If object needs to use consumption services on another object, the proper permissions must be set in Neighbourhood manager.
Otherwise this interaction will be rejected on level of GTW API.

## Object properties



### Consuming property of remote object (from another VICINITY node)

**Consuming of remote object property is always invoked by object behind the Adapter.** Thus, object must
have permissions to consume the remote object property and proper VICINITY credentials
of this object must be provided.

To read remote object property value, the Agent implements the endpoint:
```
GET : /remote/objects/{oid}/properties/{pid}
```

To set remote object property value, the Agent implements the endpoint:
```
PUT : /remote/objects/{oid}/properties/{pid}
```
PUT operation requires the payload with data structure specified in thing description for this property input to set the value.


If **object from this infrastructure** (for which this Agent runs) wants to get/set property of **remote object** (another VICINITY node),
in both calls, **the request header must contain key-value pairs**:
```
infrastucture-id=infrastructure-id of requesting object
adapter-id=identifier of adapter for this object
```
**adapter-id** header is optional, if using single adapter, but is **mandatory**, if using **multiple adapters** or Adapter explicitly provides it.

Agent finds the corresponding **oid** for requesting object matching **infrastructure-id** in header and translates
this request into corresponding GTW API call, setting the proper VICINITY credentials for requesting object.



### How agent processes the consumption of local object property (in this VICINITY node)

This is the description of process performed by Agent, when there is request for local property consumption.
Adapters of local node do not use this endpoints. This endpoints serve as the access to Adapter object properties.

To read local object property value, the Agent implements the endpoint:
```
GET : /objects/{oid}/properties/{pid}
```

To set local object property value, the Agent implements the endpoint:
```
PUT : /objects/{oid}/properties/{pid}
```
PUT operation requires the payload with data structure specified in thing description for this property input to set the value.


Agent translates:
* **oid** in request into **infrastructure-id** known in Adapter
* the endpoint to be called on Adapter

Property interaction pattern specification in Common Thing Description format:

```
#!json
{
    "pid": "property-unique-identifier",
    "monitors": "OntologyPropertyInstance",
    "input": {
        "tbd": "when datatype schema will be completed .. this is input definition to SET the property"
    },
    "output": {
        "units": "OntologyUnitInstance"
    },
    "read_links": [{
        "href": "/any-endpoint/in-adapter/to-read/{pid}"
    }],
    "write_links": [{
        "href": "/any-endpoint/in-adapter/to-set/{pid}"
    }]
}
```

Adapter is free to specify the endpoints to read/set property in form it requires. Endpoint to read property is specified
in field *read_links*, endpoint to set property in *write_links*. Endpoint can contain variables **{oid}** and **{pid}**, which are
replaced:
* **{oid}** -> **infrastructure-id** of this object
* **{pid}** -> property unique identifier of property having this endpoint

In both cases, *read_links* and *write_links* are specified as arrays, however in actual implementation Agent takes and uses
only the first value in array.

Once transformations are done, the endpoint is executed on Adapter.

## Object actions

TBD

# Eventing

Consumption services enable to read object properties by request. The eventing mechanism enables objects to publish
the values of their properties (or whatever needs to be published) once, the value changes. So other object can be notified with new value
automatically, without explicitly requesting it.

The VICINITY eventing mechanism is implemented as publish/subscribe pattern.

## Event channel management

The name of event channel for object is always specified as: **/objects/{oid}/event/{eid}**. Any object can open its channel for
concrete event and publish data into it. Once the channel is open, other objects may subscribe to this channel. Once the object is subscribed to the channel,
Adapter of this object will receive events from this channel, when they appear.

Opening the channels and subscriptions may be done statically, using Agent configuration file; or dynamically on the fly.

### Static channel management

Opening the channels and subscriptions may be declared in Agent configuration file, field **events**.
```
#!json
    "events": {
        "channels": [
            {
                "infrastructure-id": "internal identifier of object publishing the event",
                "adapter-id": "adapter identifier of this object",
                "eid": "object event identifier",
            }
        ],
        "subscriptions": [
            {
                "oid": "VICINITY object id producing event",
                "eid": "object event to listen subscribe",

                "infrastructure-id": "internal identifier of object subscribing for the event",
                "adapter-id": "adapter identifier of subscriber object",
            }
        ]
    }

```

Field **channels** contains the array of declarations, for which object and its event the channel should be open.
As the channel is to be open for local object (within this VICINITY node), the **infrastructure-id** of object behind the Adapter
is used.
The channel **/objects/{oid}/events/{eid}** is open, where **oid** is VICINITY identifier of object with **infrastructure-id**.

Field **subscriptions** contains the array of declarations, to which channels the Adapter objects will listen.
The **infrastructure-id** specifies object behind the Adapter, which is subscribed to channel, the **oid** specifies the object producing event.
The object with **infrastructure-id** is subscribed to channel **/objects/{oid}/events/{eid}**, where **oid** is VICINITY identifier of object producing event.

**adapter-id** specifies the adapter of local object. This key is optional, if using single adapter, but is **mandatory**, if using **multiple adapters** or Adapter explicitly provides it.

Using this declaration, the Agent will create and subscribe to channels on when it starts.

### Dynamic channel management

In some cases, some object in Adapter needs to open or subscribe to channel on the fly (depending on its internal logic).

To **open channel**, Agent provides the service:
```
POST /objects/{infrastructure-id}/events/{eid}/open
headers:
adapter-id=adapter for this object
```

Request parameter **infrastructure-id** specifies the identifier of object, which will publish the events.
The channel is open for this object.

Header **adapter-id** specifies the adapter of this object. This header is optional, if using single adapter, but is **mandatory**, if using **multiple adapters** or Adapter explicitly provides it.

The body of this request must be empty.

Agent translates this request into proper GTW API call, using credentials for object with **infrastructure-id**.

Just to note, the channel of object with **infrastructure-id** may be open by another object (e.g. value added service
may open the channel for sensor). The credentials will be provided for object with **infrastructure-id**, as if object
opened the channel by itself.


To **subscribe to channel**, Agent provides the service:
```
POST /objects/{oid}/events/{eid}/subscribe
headers:
infrastructure-id=internal object id
adapter-id=adapter for this object
```
The request parameter **oid** specifies the VICINITY oid of object, to which channel this subscription applies.


The request must contain the header with key **infrastructure-id**, which specifies the internal object,
that will listen to this channel.

**adapter-id** header is optional, if using single adapter, but is **mandatory**, if using **multiple adapters** or Adapter explicitly provides it.

The body of this request must be empty.

Agent translates this request into proper GTW API call, using credentials for object with **infrastructure-id**.

## Event management

### Publishing the event

Object, for which the channel is opened may publish data into this channel by using Agent service:
```
PUT /objects/{infrastructure-id}/events/{eid}/publish
headers:
adapter-id=adapter for this object
```
**infrastructure-id** in request is the identifier of local object publishing the event.

**adapter-id** header is optional, if using single adapter, but is **mandatory**, if using **multiple adapters** or Adapter explicitly provides it.

Body of this request must be JSON payload with published event data.

Agent translates this request into proper GTW API call, using credentials for object with **infrastructure-id**.


### Consuming the event

Agent automatically pass the events into Adapter, as they appear. If Adapter needs to listen to events, **Adapter
must implement the service**:
```
PUT /objects/{infrastructure-id}/events/{eid}
```
When event appear, Agent will pass it to this Adapter endpoint. Parameters of the call:
* **infrastructure-id** is the internal id of subscriber - the object, that listens to event
* **eid** id of event
* **payload** will be wrapped into object containing **oid** of object that produced the event

In current implementation, the Adapter is responsible for processing the events and passing them further to subscribed objects.


# Current implementation status
* auto discovery/configuration: implemented
* consumption services:
    * get/set property: implemented
    * execute/read status of action: tbd
* eventing: in progress
