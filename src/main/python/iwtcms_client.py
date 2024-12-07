import asyncio
import ssl
import json
import sys
import time
import websockets
import requests
import aioconsole

base_url: str
username: str
password: str
logs_history_url: str
login_url: str
permits_url: str
ws_url: str
use_ssl: bool
use_login: bool
permits: dict = {
    "read real time logs" : True,
    "read logs history" : True,
    "execute commands" : True
}

async def handle_input(websocket):
    while True:
        message = await aioconsole.ainput("")

        if message == "iwtcms_bye":
            # noinspection PyProtectedMember
            quit(0)

        await websocket.send(message)

async def handle_output(websocket):
    try:
        while True:
            response = await websocket.recv()
            print(f"> {response}")
    except websockets.exceptions.ConnectionClosedOK:
        print("Connection closed by server")
        # noinspection PyProtectedMember
        quit(0)
    return


# noinspection t
async def main():
    global permits
    ssl_context = None

    if use_ssl:
        ssl_context = ssl.create_default_context()
        ssl_context.check_hostname = False # ! IMPORTANT disable check for "handmade" certs
        ssl_context.verify_mode = ssl.CERT_NONE # ! IMPORTANT disable check for "handmade" certs

    session = requests.Session()
    session.verify = False # ! IMPORTANT disable check for "handmade" certs

    if use_login:
        # Check for permits
        print("Checking for permits...")
        try:
            permits_response = session.get(permits_url + username)
            if permits_response.status_code == 200:
                permits = permits_response.json()

                allowed_permits_count = 0

                print("Permits: ")
                for permit in permits:
                    print(f"    {permit} = {permits[permit]}")
                    if permits[permit]:
                        allowed_permits_count =+ 1

                if allowed_permits_count < 1:
                    print("There's no permits that allows you to do something more than connect to server")
                    return
            else:
                print(f"Failed to get permits: {permits_response.status_code}")
                return
        except Exception as e:
            print(f"Failed connection: {e}")
            return

        time.sleep(1)

        # Login
        print("Logging in...")
        try:
            login_data = {"username": username, "password": password}
            login_response = session.post(login_url, json=login_data)
            if login_response.status_code == 200:
                print("Login successful")
            else:
                print(f"Login failed: {login_response.status_code}")
                return
        except Exception as e:
            print(f"Failed connection: {e}")
            return

        time.sleep(1)

    # Prepare cookies
    session_cookies = session.cookies.get_dict()
    cookies = {
        "Cookie": "; ".join([f"{key}={value}" for key, value in session_cookies.items()])
    }

    print("Connecting to websocket...")
    time.sleep(1)
    try:
        async with websockets.connect(ws_url, additional_headers=cookies, ssl=ssl_context) as websocket:
            # print logs history
            if permits["read logs history"] or not use_login:
                logs_history_response = session.get(logs_history_url)
                if logs_history_response.status_code == 200:
                    logs_history = logs_history_response.json()
                    for log in logs_history:
                        print(f"> {log}")

            listener = asyncio.create_task(handle_output(websocket))

            if permits["execute commands"] or not use_login:
                await handle_input(websocket)
            await listener
    except Exception as e:
        print(f"Failed connection: {e}")
        return

if __name__ == "__main__":
    # Settings
    # Url can be found at https://127.0.0.1:25566/apiList
    for i, v in enumerate(sys.argv):
        if v == "-u" or v == "-user":
            username = sys.argv[i + 1]
        elif v == "-p" or v == "-password":
            password = sys.argv[i + 1]
        elif v == "-h" or v == "-host":
            base_url = sys.argv[i + 1]
        elif v == "--help" or v == "--man":
            print(f"Usage: {sys.argv[0]} -u <username> -p <password> -h <host>")
            print(f"Example:  {sys.argv[0]} -u admin -p iwtcms -h https://localhost:25566")
            # noinspection PyProtectedMember
            quit(0)

    try:
        # noinspection PyUnboundLocalVariable
        base_url
    except NameError:
        print(f"Usage: {sys.argv[0]} -u <username> -p <password> -h <host>")
        print(f"Example:  {sys.argv[0]} -u admin -p iwtcms -h https://localhost:25566")
        # noinspection PyProtectedMember
        os._exit(1)

    try:
        # noinspection PyUnboundLocalVariable,PyStatementEffect
        username
        # noinspection PyUnboundLocalVariable,PyStatementEffect
        password

        use_login = True
    except NameError:
        use_login = False


    logs_history_url = f"{base_url}/api/logsHistory"
    login_url = f"{base_url}/api/login"
    permits_url = f"{base_url}/api/permits/"
    if base_url.startswith("https://"):
        url_without_prefix = base_url.replace("https://", "")
        use_ssl = True
        ws_url = f"wss://{url_without_prefix}/ws/console"
    else:
        url_without_prefix = base_url.replace("http://", "")
        use_ssl = False
        # noinspection HttpUrlsUsage
        ws_url = f"ws://{url_without_prefix}/ws/console"

    asyncio.run(main())
