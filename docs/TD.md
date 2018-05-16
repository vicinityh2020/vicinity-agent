# Thing Descriptions for integrators

VICINITY Common Thing Description format is based on [W3C Wot Thing Description working draft](https://www.w3.org/TR/wot-thing-description).
For purposes of VICINITY it was necessary to slightly change it, but we try to follow it (what is quite tricky sometimes,
as this draft is still continually changing :) )

Thing Descriptions are provided by [Adapter mandatory **/objects** service](ADAPTER.md#discovery-of-adapter-objects).
It is the backbone service for VICINITY auto discovery of objects.
Frankly speaking, Adapter must be able to describe all objects it exposes to VICINITY, together with their interaction
patterns.

Each Thing Description is the JSON object fully describing one object exposed by Adapter. The validation
of objects is the part of auto discovery process and is performed in semantic repository, when creating the semantic model of object.

Lets look inside the thing description. For each part of it, it will be explained, how it is interpreted,
when it is (in)valid and how to understand the *mandatory* parts of description.

## Object (the Thing)

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| oid | string | yes | Infrastructure specific unique identifier of the object |
| name | string | yes | Human readable name of object visible in Neighbourhood manager |
| type | string | yes | Ontology annotation: the class in VICINITY semantic model (currently one of classes in hierarchy for core:Device). **The ontology class is always provided without prefix!** e.g. use **Device**, instead of **core:Device**. See [hierarchy of devices and services](http://iot.linkeddata.es/def/core/) |
| version | string | yes |Defines the version of the service. It is possible to have registered different version of the services |
| properties | array of objects | no | The array of property interaction patterns [see Property](#property) |
| actions | array of objects | no | The array of action interaction patterns [see Action](#action) |
| events | array of objects | no | The array of event interaction patterns [see Event](#event)|
| requirements | object | no | The requirement object [see Requirements](#requirements)|

**Validity**
* Specification tells, that object interaction patterns are not mandatory.
But, object must contain at least one interaction pattern (of any type).
* Field **type** must contain the existing semantic annotation in VICINITY ontology.


## Interaction patterns

Interaction patterns represent the resources enabling to interact with the object. Interaction pattern
can be property, action or event.


Interaction patterns contain the links, that enable the physical interaction with object.
Links represent the REST endpoints implemented by Adapter to manage the interaction pattern.
The **read_link** represents the resource to read the value, **write_link** represents the resource to write value or execute action.
Pattern may contain both **read_link** or **write_link** or just one of them.
If pattern contains only the **read_link**, it can be read, but it is not allowed to set/execute it.
If pattern contains only the **write_link**, it can be set/executed, but it is not allowed to read it.

According to former thing descriptions, there is one main important change: **read_links** and **write_links**
are now changed from array of links to **read_link** and **write_link** describing just one single object with the link specification.

### Property

Objects have the properties, that can be read or set. For example: the luminiscence of the lamp,
actual energy consumption of appliance, actual value observed by sensor, etc.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| pid | string | yes | Unique identifier of the property. Used by all VICINITY components as specified here. |
| monitors | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | no | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | no | Definition of interaction to set the property. [see Link](#link) |


**Validity**
* Specification tells, that read/write links are not mandatory.
But, object must contain at least one of read/write links.
* Field **monitors** must contain the existing semantic annotation in VICINITY ontology.



### Action

The action triggers changes or processes on a object that may take a certain time to complete,
(i.e., actions cannot be applied instantaneously like property writes).
For example: LED fade in, self-destroying drone, switch on the lamp, etc.
In VICINITY Gateway API, ongoing actions are modelled as task resources, which are created when an action invocation is received by the object.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| aid | string | yes | Unique identifier of the action. Used by all VICINITY components as specified here. |
| affects | string | yes | Specification of what is affected by action. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | no | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | no | Definition of interaction to set the property. [see Link](#link) |


**Validity**
* Again, specification tells, that read/write links are not mandatory.
But, object must contain at least one of read/write links.
* Field **affects** must contain the existing semantic annotation in VICINITY ontology.


### Event

The event enables a mechanism for events to be published or notified by a object on a certain condition.
For example sensor value change reaches the certain threshold, periodic notification of sensor value, etc.

In VICINITY, the event pattern has different interpretation as properties or actions. Event can not be reached
via write or read link. It is handled by Gateway API and Agent automatically, in a VICINITY specific way.

In VICINITY, eventing is implemented as publish/subscribe pattern.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| eid | string | yes | Unique identifier of the event. Used by all VICINITY components as specified here. |
| monitors | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| output | object | yes | Definition of event payload. [see Data schema](#data-schema) |

**Validity**
* Field **monitors** must contain the existing semantic annotation in VICINITY ontology.


### Link

Link represents the resource for interaction with the object. It is specified as the REST endpoint, that must be implemented in Adapter in order to interact with the pattern, which uses the link.


| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| href | string | yes | Adapter endpoint that will be used to interact with pattern. |
| input | object | no | The payload of input to interaction pattern for this link. Mandatory for **write_link** |
| output | object | yes | The payload of output of interaction pattern for this link. Always required. |


In actual implementation of Agent, the **href** should always start with the  **/**. Agent executes link as
```
**{adapter-endpoint}/href**
```

The link specified in **href** may contain the properties, that are automatically translated by the Agent.
This enables for example to specify the same link for all patterns. For example well known

```
/object/{oid}/properties/{pid}
```

See [Adapter Interaction patterns](ADAPTER.md#interaction-patterns) for explanation, how Agent
works with the links.

The important change according former thing description is, that link contains the **input** and **output** fields.
It was necessary to move inputs/outputs into links, because different link may produce different outputs (e.g. reading the property value produces different payload as setting this property).

Links for writing must contain mandatory **input** field. **input** describes the schema of payload required to set the property or execute the action.

### Requirements

The requirements are the list of interaction resources that are needed by the service to function properly (e.g. to calculate average energy consumption by the service the monitoring of action energy consumption is required). The requirements are specified as list of property, action and event requirements.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | ---|
| properties | array of objects | no | Required list of properties |
| actions | array of objects | no | Required list of actions  |
| events | array of objects | no | Required list of acceptable events |

Each required interaction resource is specified by the interaction pattern fragment. The service defines only what it is necessary not how it should be accessible. For example, the service specify that it needs to monitor current energy consumption with the output data structure. However, it does not specify how to get it from a device. The interaction patterns fragments are as follow:

##### Property requirement
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | ---|
| monitors | array of objects | yes | One of the [property classes](http://iot.linkeddata.es/def/core/index-en.html) being monitored |
| output | object | yes | Definition of event payload. [see Data schema](#data-schema) |
| required | array of strings | no | Reference to `pid`, `aid` or `eid` which requires this required. If missing required for all interaction patterns|

##### Action requirement
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | ---|
| affects | array of objects | yes | One of the [property classes](http://iot.linkeddata.es/def/core/index-en.html) being affected by the property |
| input | object | yes | Definition of event payload. [see Data schema](#data-schema) |
| output | object | yes | Definition of event payload. [see Data schema](#data-schema) |
| required | array of strings | no | Reference to `pid`, `aid` or `eid` which requires this required. If missing required for all interaction patterns|


##### Event requirement
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| monitors | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| output | object | yes | Definition of event payload. [see Data schema](#data-schema) |

### Data schema
DataSchema describes the payload for input or  output of the link or event in human readable way.
DataSchema enables the developer to understand, what payload to expect, how the payload will look like
and how to process/interpret it. It has only informal purposes for developers using the
interaction patterns.

DataSchema is based on [W3C Thing Description typed system](https://www.w3.org/TR/wot-thing-description/#type-system-section).
W3C working specification enables the ambiguous DataSchema interpretation.
As it must be possible to unambiguously transform the DataSchema into semantic representation,
we provide the set of restrictions (and updates) to it.

**!!IMPORTANT!!!**
Root DataSchema attached to input/output must always have **"type": "object" or "array"**.
It is to ensure, that payload will be the valid JSON (not just simple type)!

DataSchema is always the simple type, object of array. Definition is recursive.
The base properties for data schema are the **type** and **description**.
Depending of the type, the schema may have different structure.

Each DataSchema must provide following properties:

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| type | string | yes | Enumerator, one of simple data types: [boolean, integer, double, string] or one of complex types [object, array]. |
| description | string | no | Human readable description of the DataSchema. |

#### DataSchema "type": simple data type
No additional fields are provided. Payload is simple data type.


#### DataSchema "type": "object"
If DataSchema is object, there must be provided the set of its properties.
Object properties are provided in additional **field** array. Each **field** stands for one property.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| field | array | yes | Array of object properties. Field must be array also if only one property is provided. |

Field structure:

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| name | string | yes | The name of the property. |
| schema | DataSchema | yes | Specification of property structure. |

Fast example of object with two fields:
* **person-name**: string value of the name
* **address**: object with street and city information

```
#!json
{
    "type": "object",
    "description": "person name and address",
    "field": [
        {
            "name": "person-name",
            "description": "person name",
            "schema": {
                "type": "string"
            }
        },
        {
            "name": "address",
            "description": "person address",
            "schema": {
                "type": "object",
                "field": [
                    {
                        "name": "street",
                        "schema": {
                            "type": "string"
                        }
                    },
                    {
                        "name": "country",
                        "schema": {
                            "type": "string"
                        }
                    }
                ]
            }
        }
    ]
}
```



## Serialization of Thing Descriptions


**In current Agent implementation, the Client node may contain multiple adapters.** That means, one client node
may serve multiple different infrastructures (per each there must exist specific Adapter). When multiple adapters are used,
it is necessary to distinguish between them. In this case, there must exist persistent unique identifier of each Adapter.
Identifier of Adapter must be unique just within the Client node. That means: each adapter used by same agent must use unique identifier.


When using single adapter, it is not necessary to provide the **adapter-id**. So it is possible to use simplified serialization in Adapter */objects* service:

```
#!json
[
 array of thing descriptions
]
```


If using multiple adapters, the */objects* service must contain the **adapter-id** in form:

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| adapter-id | string | yes | Unique identifier of adapter within the client node. |
| thing-descriptions | array of objects | yes | The array of thing descriptions. |

**Example**

```
#!json
{
    "adapter-id": "unique adapter identifier",
    "thing-descriptions":
    [
     array of thing descriptions
    ]
}
```

**!!!VERY IMPORTANT!!!** The **adapter-id** is used by agent to tie the objects with its adapter. It is treated
as persistent and can not be changed. Agent holds the pairs **adapter-id : object-id** and **adapter-id** is the only
clue, how to distinguish, to which Adapter the object belong (especially in cases, when multiple adapters may contain objects with same identifiers).
Once **adapter-id** is changed, Agent treats it
as new Adapter and in discovery process, it re-creates all objects in it and creates new VICINITY **oid**s with new credentials.
This means, objects are treated as new, so all friendships in Neighbourhood Manager of former objects will be lost.
So, just generate some hash code for each Adapter and don't change it.

