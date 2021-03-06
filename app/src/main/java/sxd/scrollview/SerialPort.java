package sxd.scrollview;

/**
 * Created by apple on 17/2/23.
 */
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;
public class SerialPort {
    private static final String TAG = "Serial_Port";


    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate) throws SecurityException, IOException {

        if (!device.canRead() && !device.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");

                System.out.println("路径"+device.getAbsolutePath());

                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                /*String cmd = "chmod 777 /dev/s3c_serial0" + "\n"
                + "exit\n";*/
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }
    // JNI
    private native static FileDescriptor open(String path, int baudrate);
    public native void close();
    static {
        System.loadLibrary("serial_port");
    }


}
