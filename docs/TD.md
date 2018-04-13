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

# Thing (object)

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| oid | string | yes | Infrastructure specific unique identifier of the object |
| name | string | yes | Human readable name of object visible in Neighbourhood manager |
| type | string | yes | Ontology annotation: the class in VICINITY semantic model (currently one of classes in hierarchy for core:Device). **The ontology class is always provided without prefix!** e.g. use **Device**, instead of **core:Device**. See [hierarchy of devices and services](http://iot.linkeddata.es/def/core/) |
| properties | array of objects | no | The array of property interaction patterns [see Property](#property) |
| actions | array of objects | no | The array of action interaction patterns [see Action](#action) |
| events | array of objects | no | The array of event interaction patterns [see Event](#event)|

**Validity**
* Specification tells, that object interaction patterns are not mandatory.
But, object must contain at least one interaction pattern (of any type)
* Field **type** must contain the existing semantic annotation, otherwise it will be rejected


## Interaction patterns

Interaction patterns represent the resources enabling to interact with the object. Interaction pattern
can be property, action or event.

According to former thing descriptions, there is one main important change: **read_links** and **write_links**
are now changed from array of links to **read_link** and **write_link** describing just one single object the link specification.

### Property

Objects have the properties, that can be read or set.

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| pid | string | yes | Unique identifier of the property. Used by all VICINITY components as specified here. |
| monitors | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | no | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | no | Definition of interaction to set the property. [see Link](#link) |

Property may contain just one of **read_link** or **write_link**.
The links describe if property is available for reading and writing.
If property contains only the **read_link**, it can be read, but it is not allowed to set this property and vice versa.
If property contains only the **write_link**, it can be set, but it is not allowed to read it.

**Validity**
* Specification tells, that object interaction patterns are not mandatory.
But, object must contain at least one interaction pattern (of any type)
* Field **type** must contain the existing semantic annotation, otherwise it will be rejected



### Action
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| aid | string | yes | Unique identifier of the action. Used by all VICINITY components as specified here. |
| affects | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | no | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | no | Definition of interaction to set the property. [see Link](#link) |


### Event
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| eid | string | yes | Unique identifier of the event. Used by all VICINITY components as specified here. |
| monitored | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| output | object | yes | Definition of event payload. [see Data schema](#data-schema) |


### Link
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| href | string | yes | Adapter endpoint that will be used to interact with pattern. |
| input | string | no | The payload of input to interaction pattern for this link. |
| output | string | yes | The payload of output of interaction pattern for this link. Always required. |


### Data schema
See [W3C Thing Description typed system](https://www.w3.org/TR/wot-thing-description/#type-system-section)

## Serialization of Thing Descriptions

When using single adapter, it is not necessary to provide the **adapter-id**. So it is possible to use simplified serialization:

```
#!json
[
 the list of thing descriptions as described in
 common thing description format
]
```

However, **in current Agent implementation, the Client node may contain multiple adapters.** That means, one client node
may serve multiple different infrastructures (per each there must exist specific Adapter). When multiple adapters are used,
it is necessary to distinguish between them. In this case, there must exist persistent unique identifier of each Adapter.
Identifier of Adapter must be unique just within the Client node. That means: each adapter used by same agent must use unique identifier.
If using multiple adapters, the */objects* service must contain the **adapter-id** in form:

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| adapter-id | string | yes | Unique identifier of adapter within the client node. |
| input | string | no | The payload of input to interaction pattern for this link. |
| output | string | yes | The payload of output of interaction pattern for this link. Always required. |

**Example**

```
#!json
{
    "adapter-id": "unique adapter identifier",
    "thing-descriptions":
    [
     the list of thing descriptions as described in
     common thing description format
    ]
}
```

**!!!VERY IMPORTANT!!!** The **adapter-id** is used by agent to tie the objects with its adapter. It is treated
as persistent and can not be changed. Agent holds the pairs **adapter-id : object-id** and **adapter-id** is the only
clue, how to distinguish, to which Adapter the object belong. Once **adapter-id** is changed, Agent treats it
as new Adapter and in discovery process, it re-creates all objects in it and creates new VICINITY **oid**s with new credentials.
This means, objects are treated as new, so all friendships in Neighbourhood Manager of former objects will be lost.
So, just generate some hash code for each Adapter and don't change it.

