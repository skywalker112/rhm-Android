package com.example.bpr.app2;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class AM2302
{
    private static String ADDRESS = "89.66.60.110"; // athomelinux.ddns.net
    private static int PORT = 50001;
    private static String COMMAND = "MEASURE";
    private static int SOCKET_TIMEOUT = 8000;

    private List<Listener> listeners = new ArrayList<Listener>();

    private void addListener(Listener obj){
        listeners.add(obj);
    }
    private void triggerListenerEvent(String[] response){
        for(Listener listener : listeners){
            listener.onResponseReceived(response);
        }
    }

    public AM2302(MainActivity mainActivity){
        addListener(mainActivity);
    }

    private static String[] printResponse(String text)
    {
        String PIPE = "|",	TEMP = "Temp=",	HUMIDITY = "Humidity=";
        int pipe_position = text.indexOf(PIPE);
        int temp_position = text.indexOf(TEMP);
        int humidity_position = text.indexOf(HUMIDITY);
        if(pipe_position == -1 || temp_position == -1 || humidity_position == -1)
            return new String[] {"WRONG SERVER RESPONSE"};

        String date = text.substring(0,pipe_position-1).trim();
        String temp = text.substring(temp_position+TEMP.length(), humidity_position).trim();
        String humidity = text.substring(humidity_position+HUMIDITY.length(), text.length()).trim();
        return new String[] {date, temp.substring(0, temp.length()-1), humidity.substring(0, humidity.length()-1)};
    }

    public void infoUpdate() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    infoUpdateNewThread();

                } catch (Exception e) {
                    triggerListenerEvent(new String[]{"Exception while creating new thread!"});
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public  void infoUpdateNewThread() throws IOException
    {
        byte[] buffer = new byte[100];
        String[] response;
        InetAddress IPAddress= InetAddress.getByName(ADDRESS);
/*
        try {
            IPAddress = InetAddress.getByName(ADDRESS);
        } catch (Exception e) {z
            return "Cannot resolve the address!";
        }

*/
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(COMMAND.getBytes(), COMMAND.getBytes().length, IPAddress, PORT);
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);


        try {
            socket.send(sendPacket);

        } catch (Exception e) {
            triggerListenerEvent(new String[]{"Socket failed while sending request!"});

        } finally {

            try {
                socket.setSoTimeout(SOCKET_TIMEOUT);
                socket.receive(receivePacket);

            } catch(SocketTimeoutException e) {
                triggerListenerEvent(new String[]{"Socket timeout while waiting for response!"});

            } catch (SocketException e) {
                triggerListenerEvent(new String[]{"SocketException  while waiting for response!"});

            }catch (Exception e) {
                triggerListenerEvent(new String[]{"Socket failed while waiting for response! "});

            } finally {
                String serverResponse = new String(receivePacket.getData());
                response = printResponse(serverResponse);
            }
        }
        socket.close();
        triggerListenerEvent(response);
    }
}