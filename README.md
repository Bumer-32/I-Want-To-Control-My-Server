# I-Want-To-Control-My-Server
### or just IWTCMS



Simple minecraft mod, which allows you to connect to your server and control it

---

## Configuration
```json
{
  "ip": "127.0.0.1",
  "port": 25566,
  "logLevel": "INFO",
  "useSSL": true
}
```
``ip: any ip you want to connect. By default it's 127.0.0.1 (localhost)``

``port: any free port (DONT USE 25565). By default it's 25566``

``logLevel: how much logs you get in client. can be TRACE, DEBUG, INFO, WARN, ERROR. By default it's INFO``

``useSSL: enabling SSL for internal server, clients must connect with SSL or they can't send/receive any data. By default it's disabled BUT highly recommended to enable it and setup SSL connection.``

---

There's also a simple example of client written on <a href="https://python.org">python</a>:

https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/iwtcms_client.py

Or if you have enabled SSL:

https://github.com/Bumer-32/I-Want-To-Control-My-Server/blob/main/iwtcms_ssl_client.py

---

## Generating SSL key

If you have enabled SSL, you should to generate SSL keys

In fact,
if you have installed java
(if you playing minecraft or running minecraft server, I'm pretty sure you have installed java (or you alien))
**you already have installed *keygen***

In my case, I have installed java at C:\Program Files\Java\jdk-21,
so *keygen* in my case located at C:\Program Files\Java\jdk-21\bin\keytool.exe

So just locate your java, and replace YOUR_JAVA_HERE in one of the commands below,
and launch it in CMD/BASH/POWERSHELL

#### Example
#### *Before*
``& "YOUR_JAVA_HERE\bin\keytool.exe" -genkeypair -alias selfsigned -keyalg RSA -keystore keystore.jks -storepass keystorePassword -validity 365 -keysize 2048 -dname "CN=YourCommonName, OU=Test, O=YourOrganizationalUnit, L=YourCity, S=YourState, C=YourCountryCode"``
#### *After*
``& "C:\Program Files\Java\jdk-21\bin\keytool.exe" -genkeypair -alias selfsigned -keyalg RSA -keystore keystore.jks -storepass keystorePassword -validity 365 -keysize 2048 -dname "CN=YourCommonName, OU=Test, O=YourOrganizationalUnit, L=YourCity, S=YourState, C=YourCountryCode"``

Oh, I almost forgot!
Don't forget to enter your data at the end of script, data must be written in file for better security.

#### Example
```
CN=John Doe 
OU=Development
O=Tech Innovations Inc
L=Springfield
S=Illinois
C=US
```
<sub>btw. Such a person does not exist</sub>

#### Result
``& "C:\Program Files\Java\jdk-21\bin\keytool.exe" -genkeypair -alias selfsigned -keyalg RSA -keystore keystore.jks -storepass keystorePassword -validity 365 -keysize 2048 -dname "CN=John Doe, OU=Development, O=Tech Innovations Inc, L=Springfield, S=Illinois, C=US"``

After *keygen* generates a file with the name "keystore.jks"
at directory which CMD/BASH/POWERSHELL been launched, just copy this file to **YOUR_SERVER/config/iwtcms/**

### Commands HERE
#### CMD, BASH
```cmd
"YOUR_JAVA_HERE\bin\keytool.exe" -genkeypair -alias selfsigned -keyalg RSA -keystore keystore.jks -storepass keystorePassword -validity 365 -keysize 2048 -dname "CN=YourCommonName, OU=YourOrganizationalUnit, O=YourOrganization, L=YourCity, S=YourState, C=YourCountryCode"
```
#### POWERSHELL
```powershell
& "YOUR_JAVA_HERE\bin\keytool.exe" -genkeypair -alias selfsigned -keyalg RSA -keystore keystore.jks -storepass keystorePassword -validity 365 -keysize 2048 -dname "CN=YourCommonName, OU=Test, O=YourOrganizationalUnit, L=YourCity, S=YourState, C=YourCountryCode"
```

---

## Custom commands

These commands are available only through connecting to IWTCMS internal server,
and didn't available from the default minecraft console.

To "execute" this command, just send it to server.

##### Note: All these commands start with "iwtcms_"

| command         | function                                     | response          | response type |
|-----------------|----------------------------------------------|-------------------|---------------|
| iwtcms_ping     | Pings server, e.g. you can check is it alive | iwtcms_pong\n     | text          |
| iwtcms_shutdown | Shutdown connection with **this** client     | iwtcms_shutdown\n | text          |    

---

## Plans:

| Idea                                                                                      | Ready? |
|-------------------------------------------------------------------------------------------|-------:|
| Any security? Maybe password or something else, because now anyone can connect to console |    50% |
| Cool remote client for server, maybe site with client or just application                 |      ❌ |
| Port to forge (Not sure)                                                                  |      ❌ |

---