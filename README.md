# akka-http-inviter

A bare minimum service based on high-level server-side Akka HTTP API and Akka Actor.

Serves GET and POST for:

```POST /invitation HTTP/1.1
Host: 127.0.0.1:9000
Content-Type: application/json

{
	"name": "name",
	"email": "email@mail.com"
}
```