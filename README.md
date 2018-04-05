# VICINITY Agent

## Overview

VICINITY Agent serves as the interface between VICINITY GateWay and VICINITY Adapter.

### Adapter role

Adapter represents the *driver* to specific infrastructure and
provides just very basic interactions with objects behind it, namely:
* provide discovery information in *Common VICINITY Thing Description* format
* get/set object property
* get status/execute object action
* open/subscribe to event channel
* publish/receive event

Every object in VICINITY has **its unique VICINITY identifier (oid)**,
but adapter **always uses the internal object identifiers (infrastructure-id)**, specific for its infrastructure.

### Agent role

Agent is the functional extension of Adapter. The responsibility of agent is:
* to translate between object infrastructure identifiers (**infrastructure-id**) used by Adapter
and VICINITY identifiers (**oid**).
* to automatize common tasks, like **discovery/configuration** and **opening/subscribing to event channels**
* to translate between common VICINITY consumption/eventing services into specific Adapter services, depending
 how they are described in Thing Descriptions
* translating Adapter requests for consumption/event services into common VICINITY services (implemented in GTW API)



```
```

