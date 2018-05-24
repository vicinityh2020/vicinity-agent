# Auto discovery/configuration

Agent service always holds the actual configuration of all objects behind it.
The agent service is designed to handle multiple client nodes, that means, it
is able to manage multiple agent configurations. For each agent, there may be defined the
set of adapters, each adapter exposes the set of objects.

The whole structure is illustrated here:

![](img/agent-service.svg)

Agent service holds the whole mapping, so it knows, where each object is located (in which agent/adapter),
how to access it, which specific endpoints to use, which credentials to use (on both agent and object level, depending
on operation to be performed). Of course, the Agent service must hold the mappings
between object **OIDs** (unique object identifiers commonly shared in VICINITY) and
**infrastructure-ids** (unique - internal - object identifiers known to specific Adapter,
which expose this object). When Adapters operate with their own objects, they,
of course, use their internal **infrastructure-ids**.

Multi-agent approach required to make the **adapter-id** mandatory and unique for whole
agent service. So, speaking very simply, from point of view of accessing the concrete objects,
the result of mappings for are pairs
**adapter-id:object-id**, which uniquely identify the location of the each object.

Objects are discovered always using the Adapters, as only Adapters know, which objects
they expose.

Following the requirements of pilots, some Adapters can not be accessed by
Agent service, because they don't have the endpoint (e.g. adapters on mobile devices).
For this reason, discovery may be performed in two ways, from Adapter point of view:
* **passive**: Agent service asks Adapter for its objects
* **active**: Adapter proactively sends its objects to Agent service

Active discovery influenced the whole discovery process to be asynchronous. Adapters
may perform the discovery by exposing its objects to Agent service any time
(when Adapter starts, when Adapters needs to change the list of its objects, ...).

So now, how the whole circus works.

## Auto discovery/configuration process core

Agent service tries to process the configuration files for all included
Agents (one per client node). In each particular Agent configuration,
there is specification of all Adapters included in the Agent.

For each Agent, service asks Neighbourhood manager for the last active configuration
of its Agent. It will be used to compare the last active configuration, with
real actual configuration.

For each Adapter, if Adapter is configured for passive discovery,
Adapter is requested for its objects and object discovery is performed (see below).

Whole discovery process uses the very optimistic strategy. That means,
if some part of process fails, The process itself does not stop.
Agent service tries to configure as much of the underlying structure as possible.
If some of structure fails to be configured, it is leaved as unconfigured,
but there still exist the mapping in Agent service, so it can be configured later.
Later mean by request for unconfigured Agents, or for Adapters, proactively (active discovery).

## Auto discovery/configuration of Adapter

The process is same in both active/passive discovery case. Agent service:
1. Acquires the list of object descriptions of this Adapter
    * in case of passive discovery, Agent service asks this list from Adapter
    * in case of active discovery, this whole process is triggered by Adapter posting this list of objects into Agent service
2. Takes the last configuration for this Adapter (takes relevant part from configuration already acquired from Neighbourhood Manager).
3. Makes the DIFF. Last active configuration and actual list of objects is compared, the result is
    * the list of missing objects to delete
    * the list of new objects to create
    * the list of objects to update; update is the change of any of mandatory properties and is interpreted as the violence to contract, thus the update is in Neighbourhood Manager executed as: delete/create object + drop all friendships; the result of update operation is the brand new object with new **oid** and credentials
    * the list of unchanged objects
4. Performs the CRUD dance with Neighbourhood Manager, delete/create/update objects and their semantic models. For unchanged objects, at least the content of semantic model is actualized (there are information that could be changed, but do not trigger the update operation itself).
5. Actualizes the actual configuration for this Adapter together with all object mappings
6. Logs-in all actual objects into P2P network

The result of Adapter auto discovery is the list of all active objects with actual credentials and their consumption/eventing services.




