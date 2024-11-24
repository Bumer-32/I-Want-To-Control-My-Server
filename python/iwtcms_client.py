import asyncio
import ssl
import json
import os
import websockets
import requests
import aioconsole

# Settings
# Url can be found at https://127.0.0.1:25566/apiList
logs_history_url = "https://127.0.0.1:25566/api/logsHistory"
login_url = "https://127.0.0.1:25566/api/login"
permits_url = "https://127.0.0.1:25566/api/permits/"
ws_url = "wss://127.0.0.1:25566/ws/console"

username = "admin"
password = "iwtcms"

async def handle_input(websocket):
    while True:
        message = await aioconsole.ainput()

        if message == "iwtcms_bye":
            os._exit(0)

        await websocket.send(message)

async def handle_output(websocket):
    try:
        while True:
            response = await websocket.recv()
            print(f"> {response}")
    except websockets.exceptions.ConnectionClosedOK:
        print("Connection closed by server")
    return

async def main():
    ssl_context = ssl.create_default_context()
    ssl_context.check_hostname = False # ! IMPORTANT disable check for "handmade" certs
    ssl_context.verify_mode = ssl.CERT_NONE # ! IMPORTANT disable check for "handmade" certs

    session = requests.Session()
    session.verify = False # ! IMPORTANT disable check for "handmade" certs

    # Check for permits
    print("Checking for permits...")
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
        print("Failed to get permits")
        return

    # Login
    print("Logging in...")
    login_data = {"username": username, "password": password}
    login_response = session.post(login_url, json=login_data)
    if login_response.status_code == 200:
        print("Login successful")
    else:
        print("Login failed")
        return

    # Prepare cookies
    session_cookies = session.cookies.get_dict()
    cookies = {
        "Cookie": "; ".join([f"{key}={value}" for key, value in session_cookies.items()])
    }

    print("Connecting to websocket...")


    async with websockets.connect(ws_url, additional_headers=cookies, ssl=ssl_context) as websocket:
        greeting = await websocket.recv()
        print(f"? {greeting}")

        # print logs history
        logs_history_response = session.get(logs_history_url)
        if logs_history_response.status_code == 200:
            logs_history = logs_history_response.json()
            for log in logs_history:
                print(f"> {log}")

        listener = asyncio.create_task(handle_output(websocket))

        if permits["execute commands"]:
            await handle_input(websocket)
        await listener

if __name__ == "__main__":
    asyncio.run(main())
