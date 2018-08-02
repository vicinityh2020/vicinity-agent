# Agent changelog by version

Agent releases are now build as agent-service-full-x.y.z.jar

When using new release, please update your **agent.sh** script (or any other script you use) to
new **.jar** version.

```
JAR=agent-service-full-x.y.z.jar
```


## 0.6.0 is starting agent version

## currently added to next version:

* updated persistence,
    * agent credentials are now persisted
    * things and recovery data persistence are now updated with agent-id to be able to
      correctly remove unused adapters
    * adapter things must be discovered at least once, so persistence updates will have effect

* cleanup of unused adapters: all adapters that are not presented in agent config are permanently
  removed, including things in those adapters, all credentials and friendships are lost