import requests
import websockets
import asyncio
import ssl
import json

session = requests.Session()

login_url = "https://127.0.0.1:25566/api/login/"
login_data = {
    "username": "admin",
    "password": "iwtcms"
}

response = session.post(login_url, json=login_data, verify=False)  # ! verify=False IMPORTANT disable check for "handmade" certs

if response.status_code == 200:
    print("Login success.")
else:
    print(f"Login error: {response.status_code}")
    exit(1)

async def listen_ws():
    uri = "wss://127.0.0.1:25566/ws/console"

    ssl_context = ssl.create_default_context()
    ssl_context.check_hostname = False  # ! IMPORTANT disable check for "handmade" certs
    ssl_context.verify_mode = ssl.CERT_NONE  # ! IMPORTANT disable check for "handmade" certs

    cookies = session.cookies.get_dict()
    cookie_header = "; ".join([f"{key}={value}" for key, value in cookies.items()])
    headers = {
        "Cookie": cookie_header
    }

    async with websockets.connect(uri, ssl=ssl_context, extra_headers=headers) as websocket:
        greeting = await websocket.recv()
        print(f"Server: {greeting}")

        while True:
            response = await websocket.recv()
            print(f"Server: {response}")

asyncio.run(listen_ws())
