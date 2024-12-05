# I-Want-To-Control-My-Server
### or just IWTCMS



Simple minecraft mod, which allows you to connect to your server and control it

### miniDocumentation structure:
[About Clients](#clients-)

[About SSL](#SSL)

[About Users](#Users)

[About Api](#Api)

---

## Clients 
[Go to top](#minidocumentation-structure)

&nbsp;

### [Official IWTCMS CLI client](https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/src/main/python/iwtcms_client.py)

To launch an official client, type: ``python iwtcms_client.py --help``

### [Unofficial IWTCMS GUI client by AXCWG](https://github.com/AXCWG/IWTCMS-Client)

&nbsp;


<details>
    <summary>You also can download an official client if you go to the main page of server in the browser</summary>
    <img src="https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/doc/main page showcase.png?raw=true" alt="Main Page">
</details>

---

## SSL
[Go to top](#minidocumentation-structure)

&nbsp;

I will not tell you how to generate a certificate here, I will only tell you how to use it with IWTCMS. let's be brief

In the config file, we have ssl section:

```hocon
// Other settings

# read more about setting ssl here: https://modrinth.com/mod/i-want-to-control-my-server
ssl {
  # use SSL: enabling SSL for embedded server, clients must connect with SSL or they can't send/receive any data. By default it's disabled(false) BUT highly recommended to enable(true) it and setup SSL connection.
  use SSL = false // boolean
  # custom Sertificate: if enabled IWTCMS won't generating new sertificate, instead it reads sertificates from config/iwtcms/keystore.jks
  custom Sertificate = false // boolean
  # Actually idk what do this shit, but alias must be entered here. Usually you use it for custom generated certificates. By default it's "iwtcms". You can read more here: https://security.stackexchange.com/questions/123944/what-is-the-purpose-role-of-the-alias-attribute-in-java-keystore-files
  ssl Alias = "iwtcms" // string
  # sslPass: password for certificates, certs will be automaticly generated with this password or if you use any other certificates it will use pass to read it.
  ssl Pass = "keystorePassword" // string
}

// Other settings
```

In fact, everything is already written here in the comments, but I want to add:

``use SSL`` - I always recommend enabling it even if you don't have generated certificate, if ``custom Sertificate`` disabled IWTCMS will generate new certificate every launch and SSL should work fine.

Btw if ``use SSL`` enabled non ssl connections disabled at all, you cant connect to it.
Use https:// instead of http:// and wss:// instead of ws://

&nbsp;

``custom Sertificate`` - Just disables auto generation of certificates and instead in reads certificate from config/iwtcms/keystore.jks

&nbsp;

``ssl Alias`` and ``ssl Pass`` must match with alias and password in generated certificates

---

## Users
[Go to top](#minidocumentation-structure)

&nbsp;

IWTCMS supports user profiles, very simple but useful

```hocon
// Other settings

auth {
  # useAuthentication: enabling password for clients, client can't receive/send messages from/to server before login. By default it's disabled(false) BUT highly recommended to enable(true) it password.
  use Authentication = false // boolean

  # needs ONLY IF useAuthentication enabled
  # here you can configure users
  # you can add custom users or remove existing
  users : [ // list
    {
      # anonymous
      name : anonymous // string # do not change if you wanna keep permits for anonymous
      # password : anonymous # doesn't matter
      permits : {
        read real time logs : false // boolean
        read logs history : false // boolean
        execute commands : false // boolean
      }
    }
    {
      # admin
      name : admin // string
      password : iwtcms // string
      permits : {
        read real time logs : true // boolean
        read logs history : true // boolean
        execute commands : true // boolean
      }
    }
    {
      # guest
      name : guest // string
      password : guest // string
      permits : {
        read real time logs : true // boolean
        read logs history : false // boolean
        execute commands : false // boolean
      }
    }
  ]
}

// Other settings
```

The auth block in the config is responsible for all authentications

``use Authentication`` - enables auth, if it disabled anyone can connect, listen, send commands, and any other shit with server with IWTCMS can do.

&nbsp;

``users`` - list of all users, there's no limits for users count

Every user has their own permits, False forbids action for user, True allows action.

If any permit is not defined, IWTCMS will use it analog from anonymous user.

If an anonymous user or anonymous permit doesn't exist, then follow this pattern: not defined? - forbidden

If a user does requests without cookies (without logining in), it uses permits from anonymous.

Anonymous didn't need password if his password exists, IWTCMS will ignore this password.

---

## Api
[Go to top](#minidocumentation-structure)

&nbsp;

Anchors for api:

### Fan fact: IWTCMS follows REST API, cool yup?

[https://127.0.0.1/apiList | ApiListGET](#if-you-want-to-see-all-avail-pages-for-requests-and-websockets-also-go-to)

[https://127.0.0.1/api/logsHistory | LogsHistoryGET](#if-you-want-to-see-the-history-of-all-logs-since-server-launch-go-to)

[https://127.0.0.1/api/login | LoginPOST](#if-you-want-to-log-in-go-to)

[https://127.0.0.1/api/permits/{username} | PermitsGET](#if-you-want-to-see-all-permits-of-user-go-to)

[wss://127.0.0.1/ws/console | WsConsole](#if-you-want-to-reach-the-server-console-go-to)

[Static files](#static-files)

This part of documentation written for developers who want to write their OWN client

Btw if written cool client you can [open issue](https://github.com/Bumer-32/I-Want-To-Control-My-Server/issues) for promoting,
if your client works well, your client can be added to [About Clients](#clients-) as client or example code

Btw 2
if you need example
of client look to [About Clients](#clients-) and [Official IWTCMS CLI client
(written on python)](https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/python/iwtcms_client.py)

### Fucking API (I'm already tired of writing this documentation, but I still need to write a whole chapter about Api)

Here as examples, I will use enabled SSL and localhost with port 25566 (127.0.0.1:25566),
replace 127.0.0.1 with your ip, 25566 with your port and use appropriate prefixes for the appropriate protocols
(see [About SSL](#SSL))

Let's start:

### If you want to see all avail pages for requests (and websockets also), go to:
``https://127.0.0.1/apiList``

I'd recommend finding links in your clients here, names of pages unlikely to be changed,
but urls can be changed (sorry but IWTCMS still WIP)

| page name  | type | response type | uses auth api (needs cookies)? | needs body |
|:----------:|:----:|:-------------:|:------------------------------:|:----------:|
| ApiListGET | GET  |     JSON      |               No               |     No     |

&nbsp;

### If you want to see the history of all logs since server launch, go to:
``https://127.0.0.1/api/logsHistory``

Returns list of all logs since server launch

|   page name    | type | response type | uses auth api (needs cookies)? | needs body |
|:--------------:|:----:|:-------------:|:------------------------------:|:----------:|
| LogsHistoryGET | GET  |     JSON      |              Yes               |     No     |

&nbsp;

### If you want to log in, go to:
``https://127.0.0.1/api/login``

Put to your request body JSON with username and password and get logged

body example:
```json
{
    "username": "guest",
    "password": "guest"
}
```

| page name | type |              response type              | uses auth api (needs cookies)? | needs body |
|:---------:|:----:|:---------------------------------------:|:------------------------------:|:----------:|
| LoginPOST | POST | Plain (Login successful / Login failed) |       No (sets cookies)        |    Yes     |

&nbsp;

### If you want to see all permits of user, go to:
``https://127.0.0.1/api/permits/{username}``

replace "{username}" with the name of the user you want to learn permissions from

| page name  | type | response type | uses auth api (needs cookies)? | needs body |
|:----------:|:----:|:-------------:|:------------------------------:|:----------:|
| PermitsGET | GET  |     JSON      |               No               |     No     |

&nbsp;

### If you want to reach the server console, go to:
``wss://127.0.0.1/ws/console``

Main feature of IWTCMS, connect to websocket and get all logs!
If you want to execute minecraft command, send command as plain text to server through websocket,
and it will be executed by IWTCMS

| page name |   type    |          response type          | uses auth api (needs cookies)? | needs body |
|:---------:|:---------:|:-------------------------------:|:------------------------------:|:----------:|
| WsConsole | Websocket | Websocket plain messages (logs) |              Yes               |     No     |

&nbsp;

### Static files

Since IWTCMS integrates a real ktor web server, it hosts a lot of static files with it, here is their list:

|     file name     |        path         |       alternative paths        | file type |
|:-----------------:|:-------------------:|:------------------------------:|:---------:|
|     style.css     |     /style.css      |                                |    css    |
|     404.html      |      /404.html      | any path witch not found (404) |   html    |
|    index.html     |     /index.html     |               /                |   html    |
|    favicon.ico    |                     |                                |    ico    |
| icon_clearbg.png  | /  icon_clearbg.png |                                |    png    |
| iwtcms_client.zip | /iwtcms_client.zip  |                                |    zip    |

---