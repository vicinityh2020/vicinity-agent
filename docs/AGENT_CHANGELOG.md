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

      **GET: /objects/{infrastructure-id}/publishers/{oid}/events/{eid}**

      **infrastructure-id** is receiver,
      **oid** is publisher and **eid** is identifier of published event

## to next version:
