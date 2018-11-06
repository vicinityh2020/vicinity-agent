# Agent changelog by version

Agent releases are now build as agent-service-full-x.y.z.jar

All agent binaries are available under **Releases** tab, corresponding **jar** file is attached to each release.

When using new release, please update your **agent.sh** script (or any other script you use) to
new **.jar** version.

```
JAR=agent-service-full-x.y.z.jar
```


## 0.6.0 is starting agent version

## 0.6.2

* updated persistence,
    * agent credentials are now persisted
    * things and recovery data persistence are now updated with agent-id to be able to
      correctly remove unused adapters
    * adapter things must be discovered at least once, so persistence updates will have effect

* events and actions were tested on level on GTW - AGENT communication,
    * major bug fixed for invoking action on remote object

* discovery process was refined to use specific IoT thing UPDATE/UPGRADE services
on Neighbourhood manager when thing violates or does not violate the contract


## 0.6.3:

* cleanup of unused adapters: all adapters that are not presented in agent config are permanently
  removed, including things in those adapters, all credentials and friendships are lost

* improved event subscriptions defined in adapter static configuration
    * when adapter is discovered, agent tries to subscribe all specified
      objects to related event channels, however the channel must be open,
      if object needs to subscribe to it. this sequence (1. open, 2. subscribe)
      can not be assured. now, agent implements specific separate process
      continually trying to make subscriptions, until successfully subscribed to channel

* updated passing events to adapters
    * sourceOID of publisher is extracted from event published into agent and passed into adapter
    * **adapter must implement new endpoint for listening for events:**

      **PUT: /objects/{infrastructure-id}/publishers/{oid}/events/{eid}**

      **infrastructure-id** is receiver,
      **oid** is publisher and **eid** is identifier of published event

* **Fixed the major blocker bug [OP:45]**. Agent was unable to send big payload to GTW API.
    The problem was found in HTTP client implementation used by Agent Service.
    HTTP client implementation was completely replaced. [OP:45] is fixed now.

* Implemented new GTW API endpoint for updating the content of discovered
    TDs in Neighbourhood manager and Semantic repository.

* Fixed bug [OP:34] with event subscriptions for adapter with missing endpoints. If adapter
    misses endpoint, its things can not receive events. So they also can not subscribe to events.
    Applied for both static and dynamic subscriptions.

* Fixed bug with attempt to pass events to adapters without endpoints.

* Added agent service run.bat script to be able to run agent on Windows.

* Fixed very stupid bug [OP:86]. Agent tried to cleanup all adapters, which are not used in
    configuration anymore. However, if discovery fails on level of agent (e.g. json parsing error),
    agent service is no more able to decide, if adapter should be cleaned or not.
    From now on, unused adapters are cleaned for each agent separately, only if
    agent discovery process is successfull, so all required configuration information is
    clearly known.

## to next version:

* Added error propagation from Gateway API and Adapters. Now, all payloads should be uniform,
    if error happens on local node, response has status Internal Server Error 500. If
    error happens on remote node, status is 200, payload contains full description of error
    and status code.

* Query parameter of agent calls are propagated to adapter and to gtw in all directions of communication.