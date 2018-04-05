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

# Auto discovery/configuration

Agent always holds the actual configuration of objects behind its Adapter(s).
The configuration always contains the actual list of objects presented by Adapter and their services (consumption/eventing).
For each object, there is specified unique mapping between object's **infrastructure-id** and VICINITY **oid**.
Agent also holds the object's credentials, that are **mandatory** for interaction between any pair of objects in VICINITY.

Auto Discovery process is launched, when Agent starts (by default) and is composed of following steps:
1. read the last active configuration of node from Neighbourhood Manager
2. read the actual thing descriptions from all its Adapters
3. make DIFF. Last active configuration and actual list of objects is compared, the result is
..* the list of missing objects to delete
..* the list of new objects to create
..* the list of objects to update; update is the change of any of mandatory properties and is interpreted as the violence to contract, thus the update is in Neighbourhood Manager executed as: delete/create object + drop all friendships; the result of update operation is the brand new object with new **oid** and credentials
..* the list of unchanged objects
4. the CRUD dance, delete/create/update objects
5. actualize the actual configuration

So, the result of auto discovery is the list of all active objects with actual credentials and their consumption/eventing services.
Using the actual configuration, the Agent can translate between common VICINITY services and Adapter services, using the proper credentials.


```
```

