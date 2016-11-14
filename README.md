# Inviter

Delivered to Evojam as a recruitment challenge. Based on requirements sent by email and gathered in this [document] for convenience. This is a bare minimum version of the specified service.

[document]: https://docs.google.com/document/d/1y25ZUjLjdCZAvolD0Ah5jmb7rgpbkHKjqPkQEsJoTO0/edit

## How it's made

- Based on **high-level server-side Akka HTTP API** as the most productive and concise solution to deliver on the requirements.

- Since I went with bare minimum, I also decided to keep all the code in a single file for compactness.

- There are tests available under ```src/test/scala```, covering basic checks against the DSL routes.

### Details

- There are 2 curl queries that I used to test the routes manually (via PowerShell 2 and [curl]).

[curl]: https://curl.haxx.se/download.html

For **POST**, i.e.: ```curl -v -H "Content-Type: application/json" -X POST http://127.0.0.1:8099/invitation -d '{"""invitee""":"""Colonel Sanders""", """email""": """colonel@kfc.sad"""}'```, which results in:

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

For **GET**: ```curl -v 127.0.0.1:8099/invitation```, which results in

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

- To just imitate the required result with ```curl -v -X POST 127.0.0.1:8099/invitation``` and ```curl -v 127.0.0.1:8099/invitation```, the route could have been as simple as this:

```
  val sample = Invitation("John Smith", "john@smith.mx")
  
   def routes: Route = {
    path("invitation") {
      get {
        complete(List(sample))
      }
    } ~
      post {
        complete(sample)
      }
  }
```

- The ```Content-Type: application/json``` does not print as ```application/json;charset=utf-8```, but according to [RFC7158]:

> "JSON text SHALL be encoded in UTF-8, UTF-16, or UTF-32.  The default
   encoding is UTF-8."

[RFC7158]: https://tools.ietf.org/html/rfc7158#section-11

### Thanks

Thank you for the challenge. I do hope this work has met your expectations. In case of suggestions or questions, I'm looking forward to hear from you at any time.

Best regards, 

Michal Trzesimiech (michal.trzesimiech@gmail.com)