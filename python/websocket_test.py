import asyncio
import websockets
import ssl

async def test_websocket():
    uri = "wss://127.0.0.1:25566/ws/console"

    ssl_context = ssl.create_default_context()
    ssl_context.check_hostname = False # ! IMPORTANT disable check for "handmade" certs
    ssl_context.verify_mode = ssl.CERT_NONE # ! IMPORTANT disable check for "handmade" certs


    async with websockets.connect(uri, ssl=ssl_context) as websocket:
        greeting = await websocket.recv()
        print(f"Server: {greeting}")

        while True:
            # message = input("You: ")
            #
            # await websocket.send(message)
            #
            # if message.lower() == "bye":
            #     farewell = await websocket.recv()
            #     print(f"Server: {farewell}")
            #     break

            response = await websocket.recv()
            print(f"Server: {response}")

asyncio.run(test_websocket())
