# I-Want-To-Control-My-Server
### or just IWTCMS



Simple minecraft mod, which allows you to connect to your server and control it

Configuration
```json
{
    "ip": "127.0.0.1",
    "port": 25566,
    "logLevel": "INFO"
}
```
``ip: any ip you want to connect. By default it's 127.0.0.1 (localhost)``

``port: any free port (DONT USE 25565). By default it's 25566``

``logLevel: how much logs you get in client. can be TRACE, DEBUG, INFO, WARN, ERROR``

There's also simple example of client written on <a href="https://python.org">python</a>:

https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/iwtcms_client.py


## Custom commands

These commands available only through connecting to IWTCMS internal server, and didn't available from default minecraft console.

To "execute" this command just send it to server.

##### Note: All these command starts with "iwtcms_"

| command         | function                                     | response          | responce type |
|-----------------|----------------------------------------------|-------------------|---------------|
| iwtcms_ping     | Pings server, e.g. you can check is it alive | iwtcms_pong\n     | text          |
| iwtcms_shutdown | Shutdown connection with **this** client     | iwtcms_shutdown\n | text          |    


## Plans:

| Idea                                                                                      | Ready? |
|-------------------------------------------------------------------------------------------|-------:|
| Any security? Maybe password or something else, because now anyone can connect to console |      ❌ |
| Cool remote client for server, maybe site with client or just application                 |      ❌ |
| Port to forge (Not sure)                                                                  |      ❌ |