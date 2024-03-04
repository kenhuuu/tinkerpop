# HTTP API

## Assumptions

This new /gremlin path assumes that we will be removing the OpProcessor and Session concepts from the server. Because of their removal, the request syntax can be simplified as there is no longer a need to specify which op and processor needed. Also, it gets rid of the optional args map as the information necessary to process a query is known (since you can't set an arbitrary op anymore).

In place of the OpProcessor and Session, it is assumed that there will be a new Transaction API with available at /tx. The server will work with TransactionIds (accepted in the query) instead of routing multiple requests to the same session. This idea is still highly experimental and will likely change. Another assumption is that we are getting rid of ScriptEngines and will be using gremlin-lang exclusively. 

One item that is missing at this time is the handling of AuthN and AuthZ. The current SASL mechanism probably needs to get reworked in order to work better with HTTP. If possible, we should prefer an authorization header.


## /gremlin (Query URL)

### Request Syntax
```
POST /gremlin HTTP/1.1
Accept: serializer
Content-Type: application/json
Paginate: boolean (FUTURE)
{
  "requestId": "string"
  "transactionId": "string"
  "gremlin": "string"
  "gremlinType": "string"
  "evaluationTimeout": "number"
  "bindings": "JSON value"
  "aliases": "JSON value"
}
```

### URI Request Parameters
```
  serializer - the serialization accepted for the response.
    Required: No. Defaults to "application/vnd.gremlin-v3.0+json;types=false"
```

### Request Body
```
requestId - A unique Id for this request.
  Type: String
  Required: No. If not present, then the server will generate a random UUID to use.

transactionId - The transaction to execute this query under.
  Type: String
  Required: No. If not present, then query will be executed and committed in its own transaction.

gremlin - The Gremlin query to execute.
  Type: String
  Required: Yes.

gremlinType - The type or form of the Gremlin query. Must be one of "bytecode" or "script".
  Type: String
  Required: Yes.

evaluationTimeout - The maximum time a query is allowed to execute in milliseconds.
  Type: Number
  Required: No. If not present, then server default is used.

bindings - Any bindings used to execute the query.
  Type: JSON Object
  Required: No.

aliases - A map of the traversal source to graph.
  Type: JSON Object
  Required: Yes, if more than a single graph exists on the server.
```

### Response Syntax
```
HTTP/1.1 200
Content-type: serializer
{
  "requestId": "string"
  "result": "JSON value"
}
```

### Response Elements
```
requestId - The unique identifier for the request.
  Type: String

result - A list of objects that represent the result of the query.
  Type: JSON Object
```


## /gremlin/requestId (Query Status URL) (FUTURE)

### Request Syntax
```
GET /gremlin/requestId HTTP/1.1
```

### URI Request Parameters
```
  requestId - The unique identifier for the request.
```

### Request Body
```
  none
```

### Response Syntax
```
HTTP/1.1 200
Content-type: application/json
{
  "requestId": "string"
  "status": "string"
}
```

### Response Elements
```
requestId - The unique identifier for the request.
  Type: String

status - The state of the request. One of "QUEUED", "RUNNING", or "COMPLETED"
  Type: String
```

## /gremlin/requestId (Query Cancellation URL) (FUTURE)

### Request Syntax
```
DELETE /gremlin/requestId HTTP/1.1
```

### URI Request Parameters
```
  requestId - The unique identifier for the request.
```

### Request Body
```
  none
```

### Response Syntax
```
HTTP/1.1 200
```

### Response Elements
```
  none
```


## /gremlin/requestId (Get Paginated Results URL) (FUTURE)

### Request Syntax
```
POST /gremlin/requestId HTTP/1.1
Content-Type: application/json
{
  "requestId": "string"
  "nextPageToken": "string"
}
```

### URI Request Parameters
```
  none
```

### Request Body
```
requestId - A unique Id for this request.
  Type: String`
  Required: No. If not present, then the server will generate a random UUID to use.

nextPageToken - A unique token that represents the next set of results to retrieve.
  Type: String
  Required: No. Empty results signify request for first set of results.
```

### Response Syntax
```
HTTP/1.1 200
Content-type: serializer
{
  "requestId": "string"
  "nextPageToken": "string"
  "result": "JSON value"
}
```

### Response Elements
```
requestId - The unique identifier for the request.
  Type: String

nextPageToken - The token to use in the subsequent request for the next set of results. If this is not present, it means all results have been retrieved.
  Type: String

result - A list of objects that represent the result of the query.
  Type: JSON Object
```


## /tx (very experimental)

### Request Syntax
```
POST /tx HTTP/1.1
Content-Type: application/json
{
  "action": "string"
  "transactionId": "string"
}
```

### URI Request Parameters
```
  none
```

### Request Body
```
action - The action you want taken for this. Must be one of "open", "status", "commit", or "rollback"
  Type: String
  Required: Yes

transactionId - The ID of the transaction that is to be committed or rolled back.
  Type: String
  Required: Yes if action is "status", "commit" or "rollback", otherwise optional.
```

### Response Syntax
```
HTTP/1.1 200
Content-type: application/json
{
  "transactionId": "string"
  "status": "string"
}
```

### Response Elements
```
transactionId - The unique identifier for this transaction.
  Type: String

status - The status of the transaction. One of "Open" or "Closed".
  Type: String
```

### Errors
```
InvalidArgumentException - Raised when a non-existent transaction ID is provided or action is not one of the allowed values.
  HTTP Status Code: 400
```