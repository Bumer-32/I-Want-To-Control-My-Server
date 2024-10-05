import socket
import hashlib
import ssl
import threading

IP = "127.0.0.1"
PORT = 25566

def receive_messages(client_socket):
    while True:
        try:
            message = client_socket.recv(1024).decode('utf-8')
            if message:
                if message.lower() == 'iwtcms_shutdown':
                    print("Server shut down. Exiting...")
                    client_socket.close()
                    exit(1)

                print(message, end="")
            else:
                break
        except:
            print("An error occurred while receiving messages.")
            client_socket.close()
            break

def main():
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    context = ssl.create_default_context()
    context.minimum_version = ssl.TLSVersion.TLSv1_2
    context.check_hostname = False  # ! IMPORTANT disable check for "handmade" certs
    context.verify_mode = ssl.CERT_NONE  # ! IMPORTANT disable check for "handmade" certs

    client_socket = context.wrap_socket(client_socket, server_hostname=IP)

    try:
        client_socket.connect((IP, PORT))
        print(f"Connected to server at {IP}:{PORT}")

        receive_thread = threading.Thread(target=receive_messages, args=(client_socket,))
        receive_thread.start()

        while True:
            message = input()

            if message.startswith("iwtcms_login"):
                _, password = message.split(" ", 1)
                password_hash = hashlib.sha256(password.encode()).hexdigest()
                message = f"iwtcms_login {password_hash}"
            message = message + "\n"
            client_socket.send(message.encode('utf-8'))

            if message.lower() == 'exit':
                break
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        client_socket.close()

if __name__ == "__main__":
    main()
