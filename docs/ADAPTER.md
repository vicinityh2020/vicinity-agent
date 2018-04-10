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

## VICINITY Client Node

Generally, Client Node consists of three components:
* Adapter - the translator between its infrastructure and VICINITY common services - implements only very basic interaction
 patterns for objects it exposes. Regarding its objects, Agent is aware only of internal identifier of its objects
* Agent - extends the functionality of Adapter, translates between internal identifies of Adapter objects and common VICINITY identifiers,
holds the credentials of objects into P2P network, except the translations, Agent provides common functionalities, such as
automatic objects discovery, log in/out of objects into P2P network or .. generally .. it provides services for Adapter
integrators to easily interact with remote objects (in another VICINITY node)
* Gateway API - providing access to VICINITY P2P network

Remote objects can access objects behind Adapter via sequence:

Remote object (in Adapter) -> its Agent -> its GTW Api ---> this GTW Api -> this Agent -> this Adapter

and vice versa. When objects behind Adapter needs to interact with remote object, the most easy way to do it is to
 use proper service of its Agent. See [Agent docs](../README.md).


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


## Discovery of Adapter objects



