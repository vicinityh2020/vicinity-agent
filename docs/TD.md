# Thing Descriptions for integrators

| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| oid | string | yes | Infrastructure specific unique identifier of the object |
| name | string | yes | Human readable name of object visible in Neighbourhood manager |
| type | string | yes | Ontology annotation: the class in VICINITY semantic model (currently one of classes in hierarchy for core:Device). **The ontology class is always provided without prefix!** e.g. use **Device**, instead of **core:Device**. See [hierarchy of devices and services](http://iot.linkeddata.es/def/core/) |
| properties | array of objects | no | The array of property interaction patterns [see Property](#property) |
| actions | array of objects | no | The array of action interaction patterns [see Action](#action) |
| events | array of objects | no | The array of event interaction patterns [see Event](#event)|


## Interaction patterns

### Property
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| pid | string | yes | Unique identifier of the property. Used by all VICINITY components as specified here. |
| monitors | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | at least one of read_link/write_link | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | at least one of read_link/write_link | Definition of interaction to set the property. [see Link](#link) |



### Action
| Field name | JSON Construct | Mandatory | Description |
| --- | --- | --- | --- |
| aid | string | yes | Unique identifier of the action. Used by all VICINITY components as specified here. |
| affects | string | yes | Specification of what is monitored. Ontology annotation: the individual in VICINITY semantic model (currently one of individuals in hierarchy for ssn:Property). **The ontology individual is always provided without prefix!** e.g. use **Motion**, instead of **core:Motion**. See [hierarchy of properties](http://iot.linkeddata.es/def/core/). |
| read_link | object | yes | Definition of interaction to read the property. [see Link](#link) |
| write_link | object | yes | Definition of interaction to set the property. [see Link](#link) |


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
| output | string | yes | The payload of interaction pattern for this link. |
| input | string | no | The payload of interaction pattern for this link. |


### Data schema
