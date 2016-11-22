# akka-http-inviter

- An example service based on **high-level server-side Akka HTTP API**. 
- Serves 2 endpoints:

**POST**, i.e.: ```curl -v -H "Content-Type: application/json" -X POST http://127.0.0.1:8099/invitation -d '{"""invitee""":"""Colonel Sanders""", """email""": """colonel@kfc.sad"""}'```:

```
* Connected to 127.0.0.1 (127.0.0.1) port 8099 (#0)
> POST /invitation HTTP/1.1
> Host: 127.0.0.1:8099
> User-Agent: curl/7.51.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 57
>
* upload completely sent off: 57 out of 57 bytes
< HTTP/1.1 200 OK
< Server: akka-http/2.4.11
< Date: Mon, 14 Nov 2016 18:45:41 GMT
< Content-Type: application/json
< Content-Length: 55
<
{"invitee":"Colonel Sanders","email":"colonel@kfc.sad"}* Curl_http_done: called premature == 0
```

**GET**, i.e. ```curl -v 127.0.0.1:8099/invitation```:

``` 
* Connected to 127.0.0.1 (127.0.0.1) port 8099 (#0)
> GET /invitation HTTP/1.1
> Host: 127.0.0.1:8099
> User-Agent: curl/7.51.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Server: akka-http/2.4.11
< Date: Mon, 14 Nov 2016 18:47:08 GMT
< Content-Type: application/json
< Content-Length: 106
<
[{"invitee":"John Smith","email":"john@smith.mx"},{"invitee":"Colonel Sanders","email":"colonel@kfc.sad"}]* Curl_http_do
ne: called premature == 0
* Connection #0 to host 127.0.0.1 left intact
```