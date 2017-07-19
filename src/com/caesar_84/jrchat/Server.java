package com.caesar_84.jrchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by caesar-84 on 11/30/16.
 */
public class Server
{
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args)
    {
        ConsoleHelper.writeMessage("Enter the port number:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            ConsoleHelper.writeMessage("Server started");

            while (true)
            {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }

        }

        catch (IOException ex)
        {
            ConsoleHelper.writeMessage(ex.getMessage());
        }
    }

    public static void sendBroadcastMessage(Message message)
    {
        for (Map.Entry<String, Connection> entry: connectionMap.entrySet())
        {
            try
            {
                entry.getValue().send(message);
            }
            catch (IOException ex)
            {
                ConsoleHelper.writeMessage("Failed to send a message");
            }
        }
    }

    private static class Handler extends Thread
    {
        private Socket socket;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            ConsoleHelper.writeMessage("Connection to a remote address: " + socket.getRemoteSocketAddress() + " has been established");
            String username = null;

            try (Connection connection = new Connection(socket))
            {
                username = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, username));
                sendListOfUsers(connection, username);
                serverMainLoop(connection, username);
            }
            catch (IOException | ClassNotFoundException ex)
            {
                ConsoleHelper.writeMessage("An error occurred while exchanging data with remote address");
            }

            connectionMap.remove(username);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, username));
            ConsoleHelper.writeMessage("Connection with the server has been closed.");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException
        {
            while (true)
            {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message response = connection.receive();

                if (response.getType() == MessageType.USER_NAME)
                {
                    String username = response.getData();
                    if (username != null && !username.isEmpty())
                    {
                        if (connectionMap.get(username) == null)
                        {
                            connectionMap.put(username, connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return username;
                        } else ConsoleHelper.writeMessage("User with this name already exists in this chat. Try another name.");
                    } else ConsoleHelper.writeMessage("The username can not be empty. Try again.");
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException
        {
            for (String user: connectionMap.keySet())
            {
                Message message = new Message(MessageType.USER_ADDED, user);
                if (!user.equals(userName)) connection.send(message);
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException
        {
            while (true)
            {
                Message receivedMessage = connection.receive();
                if (receivedMessage.getType() == MessageType.TEXT)
                {
                    StringBuffer text = new StringBuffer();
                    text.append(userName).append(": ").append(receivedMessage.getData());
                    sendBroadcastMessage(new Message(MessageType.TEXT, text.toString()));
                }
                else ConsoleHelper.writeMessage("There is an error");
            }
        }
    }
}
