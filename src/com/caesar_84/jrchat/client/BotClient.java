package com.caesar_84.jrchat.client;

import com.caesar_84.jrchat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by caesar-84 on 12/13/16.
 */
public class BotClient extends Client
{

    public static void main(String[] args)
    {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole()
    {
        return false;
    }

    private static int counter = 0;
    @Override
    protected String getUserName()
    {
        if (counter == 99) counter = 0;
        return "date_bot_" + counter++;
    }

    public class BotSocketThread extends SocketThread
    {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);

            String senderName = "";
            String senderMessageText;

            if (message.contains(": ")) {
                senderName = message.split(": ")[0];
                senderMessageText = message.split(": ")[1];
            }
            else {
                senderMessageText = message;
            }

            SimpleDateFormat format = null;

            if (senderMessageText.equals("дата")) format = new SimpleDateFormat("d.MM.YYYY");
            else if (senderMessageText.equals("день")) format = new SimpleDateFormat("d");
            else if (senderMessageText.equals("месяц")) format = new SimpleDateFormat("MMMM");
            else if (senderMessageText.equals("год")) format = new SimpleDateFormat("YYYY");
            else if (senderMessageText.equals("время")) format = new SimpleDateFormat("H:mm:ss");
            else if (senderMessageText.equals("час")) format = new SimpleDateFormat("H");
            else if (senderMessageText.equals("минуты")) format = new SimpleDateFormat("m");
            else if (senderMessageText.equals("секунды")) format = new SimpleDateFormat("s");

            if (format != null) sendTextMessage("Информация для " + senderName + ": " + format.format(Calendar.getInstance().getTime()));
        }
    }
}
