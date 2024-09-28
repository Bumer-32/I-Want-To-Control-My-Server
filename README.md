# I-Want-To-Control-My-Server
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

https://gist.github.com/Bumer-32/99e64287299333e370751b58cabd5e98


Plans:

| Idea                                                                                      | Ready? |
|-------------------------------------------------------------------------------------------|-------:|
| Any security? Maybe password or something else, because now anyone can connect to console |      ❌ |
| Cool remote client for server, maybe site with client or just application                 |      ❌ |
| Port to forge (Not sure)                                                                  |      ❌ |