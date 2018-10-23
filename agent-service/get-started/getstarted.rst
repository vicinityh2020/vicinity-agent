===========
Get started
===========

This "simple:)" get started guide provides step by step approach to integrate IoT infrastructure in VICINITY.

-----------------------------------------------
Install the VICINITY Gateway API
-----------------------------------------------
We will start with simple VICINITY Gateway API installation.

1. Clone the github repository

  ::

    cd /path/to/the/directory
    git clone git@github.com:vicinityh2020/vicinity-gateway-api.git

2. Compile the VICINITY Gateway API using maven

  ::

    cd vicinity-gateway-api
    mvn clean compile assembly:single

3. Create dedicated system user for VICINITY Gateway API

  ::

    adduser --system --group --no-create-home --shell /bin/sh ogwapi


4. Putt it all in the right place

  ::

    mkdir /opt/ogwapi
    mkdir /opt/ogwapi/log
    mkdir /opt/ogwapi/config
    cp -r ./config /opt/ogwapi
    cp target/ogwapi-jar-with-dependencies.jar /opt/ogwapi/gateway.jar
    chown -R ogwapi:ogwapi /opt/ogwapi
    chmod u+x /opt/ogwapi/gateway.jar

5. Running VICINITY Gateway API.

  ::

    cd /opt/ogwapi
    su - ogwapi -c "nohup java -jar gateway.jar &"

6. Checking the running API
  In nohup.out you should see:

  ::

    CONFIG: HTTP Basic challenge authentication scheme configured.
    Sep 08, 2018 2:19:45 PM org.restlet.engine.connector.NetServerHelper start
    INFO: Starting the internal [HTTP/1.1] server on port 8181
    Sep 08, 2018 2:19:45 PM org.restlet.Application start
    INFO: Starting eu.bavenir.ogwapi.restapi.Api application
    Sep 08, 2018 2:19:45 PM org.restlet.engine.application.ApplicationHelper start
    FINE: By default, an application should be attached to a parent component in order to let application's outbound root handle calls properly.
    Sep 08, 2018 2:19:45 PM eu.bavenir.ogwapi.restapi.RestletThread run
    FINE: HTTP server component started.


-----------------------------------------------
Install the VICINITY Example Adapter
-----------------------------------------------

We provide very simple example Adapter as a playground for first run and testing.

Please, download prepared Adapter example from VICINITY Agent GitHub. In releases tab,
find last release and download attached file **adapter-build-x.y.z.zip**, where
**x.y.z** is the version of actual release. Unzip it.

Example Adapter exposes two **things**: *example-thing-1* and *example-thing-2*.
You can find their thing descriptions in file

::

    adapter-build-x.y.z/objects/example-objects.json

In command line, start example Adapter.

::

    ./adapter.sh

Your Adapter is now running. In console, you should see:

::

    Oct 23, 2018 2:32:36 PM org.restlet.engine.connector.NetServerHelper start
    INFO: Starting the internal [HTTP/1.1] server on port 9998
    Oct 23, 2018 2:32:36 PM org.restlet.Application start
    INFO: Starting sk.intersoft.vicinity.adapter.testing.service.TestingAdapterApplication application
    starting


-----------------------------------------------
Install the VICINITY Agent Service
-----------------------------------------------

We provide very Agent Service build preconfigured to work with example Adapter.
Later, you can reconfigure of Agent Service to work with any other adapters.

Please, download prepared Agent Service from VICINITY Agent GitHub. In releases tab,
find last release and download attached file **agent-build-x.y.z.zip**, where
**x.y.z** is the version of actual release. Unzip it.

**VIKTOR: TOTO PREFORMULUJ: START**

In order to be able to run Agent Service, you will need to configure the credentials.

**VIKTOR: TOTO PREFORMULUJ: END**

Update Agent Service credentials in configuration file

::

     agent-build-x.y.z/config/agents/example-agent.json

Fill in correct values in:

::

    "credentials": {
        "agent-id": "agent id goes here",
        "password": "agent password goes here"
    }



-----------------------------------------------
Register your device
-----------------------------------------------

.. todo::  Add registration of device with VICINITY Agent

-----------------------------------------------
Register your device
-----------------------------------------------

.. todo:: Add registration of device without VICINITY Agent

-----------------------------------------------
Read data from your device
-----------------------------------------------

.. todo:: Add VICINITY Agent installation