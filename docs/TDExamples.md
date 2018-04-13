# Thing Description examples

## Light bulb object
```
{
    "oid": "bulb1",
    "name": "human reads my name here",
    "type": "PHLightBulb",
    "properties": [
        {
            "pid": "brightness",
            "monitors": "Brightness",
            "read_link": {
                "href": "/device/{oid}/property/{pid}",
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
    "actions": [
        {
            "aid": "set-brightness",
            "affects": "Brightness",
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
            "monitors": "Brightness",
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