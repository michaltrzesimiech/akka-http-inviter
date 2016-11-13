# Inviter

Delivered to Evojam as a recruitment challenge. Based on requirements sent by email and gathered in this [document] for convenience. This is a bare minimum version of the specified service.

[document]: https://docs.google.com/document/d/1y25ZUjLjdCZAvolD0Ah5jmb7rgpbkHKjqPkQEsJoTO0/edit

## How it's made

- Based on **high-level server-side Akka HTTP API** as the most productive, concise and readable solution to deliver on the requirements.

- Since I have chosen to go with bare minimum, I also decided to keep all the code in a single file for increased readability.

- There is actually another file in ```src/test/scala```. It covers basic checks against the DSL routes. It can be run with  ```sbt test```.

### Details

- There are 2 queries that I used to test both routes manually.

[curl Win32 package]: https://curl.haxx.se/download.html

For **GET**: ```curl -v 127.0.0.1:8099/invitation```, which results in

``` 
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to 127.0.0.1 (127.0.0.1) port 8099 (#0)
> GET /invitation HTTP/1.1
> Host: 127.0.0.1:8099
> User-Agent: curl/7.51.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Server: akka-http/2.4.11
< Date: Sun, 13 Nov 2016 19:16:01 GMT
< Content-Type: application/json
< Content-Length: 48
<
{"invitee":"John Smith","email":"john@smith.mx"}* Curl_http_done: called premature == 0
* Connection #0 to host 127.0.0.1 left intact
```

For **POST**, i.e.: ```curl -v -H "Content-Type: application/json" -X POST http://127.0.0.1:8099/invitation -d '{"""invitee""":"""Colonel Sanders""", """email""": """colonel@kfc.sad"""}'```, which results in:

```
*   Trying 127.0.0.1...
* TCP_NODELAY set
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
< Date: Sun, 13 Nov 2016 19:15:47 GMT
< Content-Type: application/json
< Content-Length: 55
<
[{"invitee":"John Smith","email":"john@smith.mx"}]* Curl_http_done: called premature == 0
* Connection #0 to host 127.0.0.1 left intact
```

- There could have been an additional scenario servicing unspecified POST call like: ```curl -v -X POST 127.0.0.1:8099/invitation```, which results in: 

```
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to 127.0.0.1 (127.0.0.1) port 8099 (#0)
> POST /invitation HTTP/1.1
> Host: 127.0.0.1:8099
> User-Agent: curl/7.51.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Server: akka-http/2.4.11
< Date: Sun, 13 Nov 2016 19:15:53 GMT
< Content-Type: application/json
< Content-Length: 50
[{"invitee":"John Smith","email":"john@smith.mx"}]* Curl_http_done: called premature == 0
* Connection #0 to host 127.0.0.1 left intact
```

The route would then look like this:

```
  def routes: Route = {
    pathPrefix("invitation") {
      get {
        complete(invitations.head)
      }
    } ~
      post {
        entity(as[JsValue]) { invitation =>
          complete(invitation)
        } 
      } ~ complete(invitations)
  }
```

Similarly, there could've been just 1 POST scenario to imitate the result, but I made it into a moving part for functional reasons. To just imitate the required result with ```curl -v -X POST 127.0.0.1:8099/invitation``` and ```curl -v 127.0.0.1:8099/invitation``` for GET, the route could have been as simple as this:

```
  val sample = Invitation("John Smith", "john@smith.mx")
  
   def routes: Route = {
    path("invitation") {
      get {
        complete(sample)
      }
    } ~
      post {
        complete(List(sample))
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