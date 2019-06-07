# Thing Description examples

## Light bulb object
```
{
    "oid": "bulb1",
    "name": "human reads my name here",
    "type": "core:Device",
    "located-in": [
        {
            "location_type": "s4bldg:Building",
            "location_id": "http://mybuilding.eu",
            "label": "building label"
        }
    ],
    "properties": [
        {
            "pid": "brightness",
            "monitors": "adapters:LightColor",
            "read_link": {
                "href": "/device/{oid}/property/{pid}",
                "static-value": {
                    "property": "property name",
                    "value": "property value",
                    "time": 12345
                },
                "output": {
                    "type": "object",
                    "field": [
                        {
                            "name": "property",
                            "schema": {
                                "type": "string"
                            }
                        },
                        {
                            "name": "value",
                            "predicate": "core:value",
                            "schema": {
                                "type": "integer"
                            }
                        },
                        {
                            "name": "time",
                            "predicate": "core:timestamp",
                            "schema": {
                                "type": "long"
                            }
                        }
                    ]
                }
            },
            "write_link": {
                "href": "/bulb/set-brightness/{oid}",
                "input": {
                    "type": "object",
                    "field": [
                        {
                            "name": "brightness-level",
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ]
                },
                "output": {
                    "type": "object",
                    "field": [
                        {
                            "name": "success",
                            "schema": {
                                "type": "boolean"
                            }
                        }
                    ]
                }
            }
        }
    ],
    "actions": [
        {
            "aid": "set-brightness",
            "affects": "adapters:LightColor",
            "read_link": {
                "href": "/device/{oid}/status/{aid}",
                "output": {
                    "type": "object",
                    "field": [
                        {
                            "name": "action",
                            "schema": {
                                "type": "string"
                            }
                        },
                        {
                            "name": "status",
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ]
                }
            },
            "write_link": {
                "href": "/bulb/set-brightness/{oid}",
                "input": {
                    "type": "object",
                    "field": [
                        {
                            "name": "brightness-level",
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ]
                },
                "output": {
                    "type": "object",
                    "field": [
                        {
                            "name": "success",
                            "schema": {
                                "type": "boolean"
                            }
                        }
                    ]
                }
            }
        }
    ],
    "events": [
        {
            "eid": "set-brightness",
            "monitors": "adapters:LightColor",
            "output": {
                "type": "object",
                "field": [
                    {
                        "name": "observed-property",
                        "schema": {
                            "type": "string"
                        }
                    },
                    {
                        "name": "value",
                        "schema": {
                            "type": "integer"
                        }
                    }
                ]
            }
        }
    ]
}
```