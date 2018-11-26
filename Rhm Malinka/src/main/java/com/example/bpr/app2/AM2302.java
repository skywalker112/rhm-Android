package com.example.bpr.app2;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.*;
import java.net.SocketTimeoutException;


public class AM2302
{
    private static String ADDRESS = "89.66.60.110"; // athomelinux.ddns.net
    private static int PORT = 50001;
    private static String COMMAND = "MEASURE";
    private static int SOCKET_TIMEOUT = 8000;

    private static String[] printResponse(String text)
    {
        String PIPE = "|",	TEMP = "Temp=",	HUMIDITY = "Humidity=";
        int pipe_position = text.indexOf(PIPE);
        int temp_position = text.indexOf(TEMP);
        int humidity_position = text.indexOf(HUMIDITY);
        if(pipe_position == -1 || temp_position == -1 || humidity_position == -1)
            return new String[] {"WRONG SERVER RESPONSE"};

        //System.out.println("RESP : " + text);
        String date = text.substring(0,pipe_position-1).trim();
        String temp = text.substring(temp_position+TEMP.length(), humidity_position).trim();
        String humidity = text.substring(humidity_position+HUMIDITY.length(), text.length()).trim();
        return new String[] {date, temp.substring(0, temp.length()-1), humidity.substring(0, humidity.length()-1)};
    }

    public static String [] infoUpdate() throws IOException
    {
        byte[] buffer = new byte[100];
        String[] response;
        InetAddress IPAddress= InetAddress.getByName(ADDRESS);
/*
        try {
            IPAddress = InetAddress.getByName(ADDRESS);
        } catch (Exception e) {
            return "Cannot resolve the address!";
        }

*/
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(COMMAND.getBytes(), COMMAND.getBytes().length, IPAddress, PORT);
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);


        try {

            socket.send(sendPacket);

        } catch (Exception e) {
            return new String[]{"Socket failed while sending request!"};

        } finally {

            try {
                socket.setSoTimeout(SOCKET_TIMEOUT);
                socket.receive(receivePacket);

            } catch(SocketTimeoutException e) {
                return new String[]{"Socket timeout while waiting for response!"};

            } catch (Exception e) {
                return new String[]{"Socket while waiting for response!"};

            } finally {
                String serverResponse = new String(receivePacket.getData());
                response = printResponse(serverResponse);
            }
        }
        socket.close();
        return response;
    }
}