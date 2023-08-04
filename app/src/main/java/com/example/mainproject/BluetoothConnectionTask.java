package com.example.mainproject;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothConnectionTask extends HandlerThread {
    private static final String TAG = "BluetoothConnectionThread";
    private BluetoothSocket socket;
    private InputStream inputStream;
    private Handler handler;

    private Context context;

    public BluetoothConnectionTask(Context context) {
        super("BluetoothConnectionTask");
        this.context = context;
    }

    public void connectToDevice(BluetoothDevice targetDevice) {
        Log.d("Arduino", "Inside task");
        handler = new Handler(getLooper());
        handler.post(() -> {
            try {
                // Retrieve the UUIDs from the device
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                ParcelUuid[] uuids = targetDevice.getUuids();
                Log.d("Arduino", uuids[0].toString());
                // Make sure the device has provided UUIDs
                if (uuids != null && uuids.length > 0) {
                    // Use the first UUID from the array (you may adjust this based on your specific use case)
                    UUID targetUUID = uuids[0].getUuid();
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    Log.d("Arduino UUID", targetUUID.toString());
                    // Create a Bluetooth socket using the retrieved UUID

                    do {
                        socket = targetDevice.createInsecureRfcommSocketToServiceRecord(targetUUID);
                        Log.d("Arduino", "init socket");

                        try {
                            // Connect to the socket
                            socket.connect();
                        } catch (Exception e) {
                            try {

                                Log.d("Arduino", "Fallback");
                                socket = (BluetoothSocket) targetDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(targetDevice, 1);
                            } catch (Exception e1) {

                            }
                        }
                    } while (!socket.isConnected());
                    Log.d("Arduino", "Connect");
                    // Get the input stream from the socket to receive data
                    inputStream = socket.getInputStream();
                    //inputStream.skip(inputStream.available());

                    Log.d("Arduino", "init inputstr");
                    // Now you can read data from the input stream as explained in the previous responses

                    String x = new String();
                    String y = new String();

                    while(true) {

                        byte[] buffer = new byte[1024];
                        int bytesRead = inputStream.read(buffer);
                        int offset = 0;
                        while ((buffer[offset] != 'x')&&(buffer[offset] != 'y')) {
                            offset++;
                        }

                        /**
                        if(buffer[offset] == 'x'){
                            x = new String(buffer, offset+1, 2);

                        } else if(buffer[offset] == 'y'){
                            y = new String(buffer, offset+1, 2);
                        }
                         */

                        String receivedData = new String(buffer, offset, 3);
                        Log.d("Arduino", receivedData);

                        //int xint = Integer.parseInt(x);
                        //int yint = Integer.parseInt(y);
                        //move(xint,yint);

                    }


                } else {
                    // The device did not provide any UUIDs, handle the error or try again
                    Log.d("Arduino", "No UUID");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Arduino", e.toString());
                // Handle connection or read errors here
            } finally {
                // Close the socket and input stream when you're done
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.d("Arduino", "IOexception");
                    e.printStackTrace();
                }
            }
        });
    }

    public void cancel() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }



}