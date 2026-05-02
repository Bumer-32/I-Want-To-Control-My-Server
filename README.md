> [!CAUTION]
> PROJECT WAS ABARDONED
>
> I started this project because i didn't know any normal way to easy administrate my mc servers for friends
> So I decided to create IWTCMS, but it didn't released because shitcode (but actually I was going to rewrite project normally)
> And WEB, I really bad frontend-er.
>
> So after I found out about Crafty Controller, I don't really see the point in IWTCMS.
>
> So since 02.05.2026 IWTCMS is abardoned project and I'll archive repos.
> Bumer_32

<div style="display: flex; align-items: center;">
    <img style="height: 80px" src="https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/dev/fabric/src/main/resources/assets/iwtcms/icon.png?raw=true" alt="Main Page">
    <span style="font-size: 60px; margin-left: 35px; font-weight: bold">I-Want-To-Control-My-Server</span>
</div>

### a.k.a **IWTCMS**


A Minecraft server mod that provides a **web-based admin panel** and a **powerful API** to control your server.

---

## About

**I want to control my server** - is a Minecraft server mod (Fabric supported) that exposes server functionality through a web interface and API.
It allows you to manage your server remotely or build custom clients on top of IWTCMS (or integrate your own tools in future as plugins).

<details>
    <summary>Here some features that IWTCMS provides as api</summary>
    <ul>
        <li>Console access - you can read logs, and execute commands</li>
        <li>Users - IWTCMS uses user model with permissions, so you can create multiple users with different permissions</li>
        <li>Players management - you can kick, kill, ban, see coords of players</li>
        <li>Configs management - you can change configs of server using iwtcms api</li>
        <li>And more...</li>
    </ul>
</details>

---

## Download


- **Modrinth**  
  https://modrinth.com/mod/i-want-to-control-my-server

- **Official Maven repository (releases)**  
  https://maven.lumivoid.pp.ua/#/releases/ua/pp/lumivoid/iwtcms/iwtcms-fabric

- **Unstable builds**  
  https://maven.lumivoid.pp.ua/#/unstable/ua/pp/lumivoid/iwtcms/iwtcms-fabric

> ⚠️ **Unstable builds are UNSTABLE** ⚠️  
> Please be careful with unstable builds, they can contain bugs and may not work properly or can contain breaking changes that will break your old configs  
> Unstable builds have different versioning than releases, releases follows format `major.minor.patch`, while unstable builds follows `year.day_of_year`  
> eg. release: 2.0, unstable: 26.30

---

## Configuration

> IWTCMS uses [hocon](https://github.com/lightbend/config/blob/master/HOCON.md) format for configs  
> all configs are located in `YOUR_SERVER_FOLDER/iwtcms/iwtcms.hocon`  
> it will be generated automatically if it doesn't exist (eg on first start or after deleting it)  
> also you can take it from [here](https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/dev/server/src/main/resources/iwtcms.conf)  
> almost all props are self-explanatory and commented
> 
> There's also some files in iwtcms folder that you probably don't need to touch:
> - `db.mv.db` - h2 database file, delete it if you forgot password for iwtcms
> - `iwtcms.keystore.jks` - keystore for SSL
>
> Also you can find __BACKUP__ files in iwtcms folder, they are creates when you changing server configs through iwtcms api

>  ## SSL
> 
> 
> I will not tell you how to generate a certificate here, I will only tell you how to use it with IWTCMS. let's be brief
> 
> In the config file, we have ssl section:
> 
> ```hocon
>   // Other settings
>   
>   # read more about setting ssl here: https://modrinth.com/mod/i-want-to-control-my-server
>   ssl {
>       # use SSL: enabling SSL for embedded server, clients must connect with SSL or they can't send/receive any data. By default it's disabled(false) BUT highly recommended to enable(true) it and setup SSL connection.
>       use SSL = false // boolean
>       # custom Sertificate: if enabled IWTCMS won't generating new sertificate, instead it reads sertificates from config/iwtcms/keystore.jks
>       custom Sertificate = false // boolean
>       # Actually idk what do this shit, but alias must be entered here. Usually you use it for custom generated certificates. By default it's "iwtcms". You can read more here: https://security.stackexchange.com/questions/123944/what-is-the-purpose-role-of-the-alias-attribute-in-java-keystore-files
>       ssl Alias = "iwtcms" // string
>       # sslPass: password for certificates, certs will be automaticly generated with this password or if you use any other certificates it will use pass to read it.
>       ssl Pass = "keystorePassword" // string
>   }
>   
>   // Other settings
> ```
> 
> In fact, everything is already written here in the comments, but I want to add:
> 
> `use SSL` - I always recommend enabling it even if you don't have generated certificate, if `custom Sertificate` disabled IWTCMS will generate new certificate every launch and SSL should work fine.
> 
> Btw if `use SSL` enabled non ssl connections disabled at all, you cant connect to it.
> Use https:// instead of http:// and wss:// instead of ws://
> 
> `custom Sertificate` - Just disables auto generation of certificates and instead in reads certificate from config/iwtcms/keystore.jks
> 
> `ssl Alias` and `ssl Pass` must match with alias and password in generated certificates

---

## Clients
> Coming soon
---

## Future plans
> Coming soon
---

## For developers

> ## Maven
> IWTCMS is available on maven repository  
> releases: https://maven.lumivoid.pp.ua/#/releases/ua/pp/lumivoid/iwtcms  
> unstable: https://maven.lumivoid.pp.ua/#/unstable/ua/pp/lumivoid/iwtcms  
> 
> ## To use it in your project add this to your `build.gradle`  
> 
> ### For releases:
> ```groovy
>    maven {
>        name "lumivoidReleases"
>        url "https://maven.lumivoid.pp.ua/releases"
>    }
>
> ```
> 
> ### For unstable: 
> ```groovy
>    maven {
>        name "lumivoidUnstable"
>        url "https://maven.lumivoid.pp.ua/unstable"
>    }
>
> ```
> 
> ### After add this dependency to your project as any other:
> ```groovy
>   dependencies {
>       implementation("ua.pp.lumivoid:iwtcms-server:${iwtcms_version}")
>   }
> ```

> ## Project structure
> Iwtcms split for multiple modules:
> - `iwtcms-server` - main module, contains almost all code of iwtcms, teoretically it not depends on minecraft, you can use it in your own project as base
> - `iwtcms-fabric` - fabric module, implements few interfaces to make it work with fabric and contains fabric.mod.json, you probably don't need it in your own project
> - `front` - frontend of iwtcms, written in [svelte](https://svelte.dev/) and integrated with gradle using [vite](https://vite.dev/), so you don't really need to write node commands
> - `cli` - command line interface for iwtcms, written in go
> - `static` - contains static files for iwtcms, iwtcms uses it if web panel is disabled


> ## How to build
> First you need to install jdk 21 and nodejs with npm
> golang technically optional, but then you don't be able to build cli
> 
> Every module has build.gradle with build task, so just run `./gradlew :module:build`
> Or `./gradlew build` to build all
> 
> Frontend builds in front/build
> Server builds in server/build/libs
> Fabric builds in fabric/build/libs - iwtcms-***-all.jar is your builded mod

> ## Dev mode
> It provides some useful features for development
> Like:
> - `Auto vite opening` - after iwtcms run it will be run vite automatically
> - `External db` - you can use external database for development (I think I need to move it from developer functions)
> - `H2 web server` - so you can manually do something in embedded database
> - `Some tweaks in web`
> 
> ⚠️ Note: IWTCMS WILL NOT LAUNCH IF SERVER LAUNCHED IN NOT "dev environment" (technically it search for gradle file)  
> So DO NOT USE IT IF YOU JUST WANT TO USE IWTCMS
> 
> To enable dev mode you need to add this to your `iwtcms.conf`
>
> ```hocon
> dev {
>   # used for developing, DO NOT CHANGE IF YOU ARE NO DEVELOPER
>   # CAN CAUSES CRASH IF YOU USE COMPILED JAR
>   # ENABLE IT IF YOU KNOW WHAT YOU DO
>   # to know more read README.md on github or modrinth of IWTCMS
>   dev mode = true
>   auto open vite = false
>   enable h2 web server = false
>   use external db = true
>   external db driver = PostgreSQL // Can be: Oracle, H2, MariaDB, MSSQL, MySQL, PostgreSQL
>   external db iwtcms name = iwtcms
>   external db ip = 192.168.0.162
>   external db port = 5432
> }
> ```

> ## Documentation
> I'm working on it
