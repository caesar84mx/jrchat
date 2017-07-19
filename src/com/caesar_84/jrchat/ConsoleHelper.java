package com.caesar_84.jrchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by caesar-84 on 11/30/16.
 */
public class ConsoleHelper
{
    private static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message)
    {
        System.out.println(message);
    }

    public static String readString()
    {
        while (true)
        {
            try
            {
                return console.readLine();
            }
            catch (IOException ex)
            {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
    }

    public static int readInt()
    {
        while (true)
        {
            try
            {
                return Integer.parseInt(readString());
            }
            catch (NumberFormatException ex)
            {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
    }
}
