/** 
 * IOIOThread.java 
 * @date Jan 11, 2012 
 * @author ricky barrette 
 * @author Ytai Ben-Tsvi 
 * @author Twenty Codes, LLC 
 * 
 * Copyright 2012 Richard Barrette 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */  
package com.TwentyCodes.android.ioio;  
  
import android.util.Log;
import ioio.lib.api.DigitalOutput;  
import ioio.lib.api.IOIO;  
import ioio.lib.api.IOIOFactory;  
import ioio.lib.api.exception.ConnectionLostException;  
import ioio.lib.api.exception.IncompatibilityException;
  
/** 
 * This is the thread that maintains the IOIO interaction. 
 * 
 * It first creates a IOIO instance and wait for a connection to be 
 * established. 
 * 
 * Whenever a connection drops, it tries to reconnect, unless this is a 
 * result of abort(). 
 * 
 * @author Ytai Ben-Tsvi 
 * @author ricky barrette 
 */  
public class IOIOThread extends Thread{  
  
    private static final String TAG = "IOIOThread";
	private IOIO mIOIO;  
    private boolean isAborted = false;  
    private long mUpdateInterval = 10;  
    private boolean isStatLedEnabled = false;
	private IOIOListener mListener;
	private String mClassName;
	private Object[] mArgs;
    
    public IOIOThread(IOIOListener listener){
    	super();
    	mListener = listener;
    }
  
    public IOIOThread(String className, Object[] args, IOIOListener listener) {
    	super();
    	mListener = listener;
    	mClassName = className;
    	mArgs = args;
	}

	/** 
     * Abort the connection. 
     * 
     * This is a little tricky synchronization-wise: we need to be handle 
     * the case of abortion happening before the IOIO instance is created or 
     * during its creation. 
     */  
    synchronized public void abort() {  
        isAborted = true;  
        if (mIOIO != null) {  
            mIOIO.disconnect();  
        }  
    }  
  
    /** 
     * @return the isStatLedEnabled 
     */  
    public boolean isStatLedEnabled() {  
        return isStatLedEnabled;  
    }  
 
  
    /** 
     * Thread Body 
     * (non-Javadoc) 
     * @see java.lang.Thread#run() 
     */  
    @Override  
    public void run() {  
        while (true) {  
            synchronized (this) {  
                if (isAborted) {  
                    break;  
                }
                if(mClassName == null || mArgs == null)
                	mIOIO = IOIOFactory.create();
				else
					try {
						mIOIO = IOIOFactory.create(mClassName, mArgs);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
            }  
            try {  
                /* 
                 * Here we will try to connect to the IOIO board. 
                 * 
                 * the waitForConnect() is blocking until it can connect 
                 */  
                mIOIO.waitForConnect();  
  
                /* 
                 * Here we register and initialize each port 
                 */  
                DigitalOutput statLed = mIOIO.openDigitalOutput(IOIOValues.STAT_LED_PORT, true);  
                mListener.onConnected(mIOIO);  
  
                /* 
                 * Here we will update the ports every 10 ms (default) 
                 */  
                while (true) {  
                	mListener.loop();  
                    statLed.write(!isStatLedEnabled);  
                    sleep(mUpdateInterval );  
                }  
            } catch (ConnectionLostException e) {  
            	mListener.onDisconnected();  
            } catch (InterruptedException e) {  
            	Log.e(TAG, e.getMessage());
            	e.printStackTrace();  
                mIOIO.disconnect();  
                mListener.onDisconnected();  
                break;  
            } catch (IncompatibilityException e) {
            	Log.e(TAG, e.getMessage());
				e.printStackTrace();
			} finally {  
                try {  
                    mIOIO.waitForDisconnect();  
                    mListener.onDisconnected();  
                } catch (InterruptedException e) {  
                }  
            }  
        }  
    }  
  
    /** 
     * Sets the stat led on / off 
     * @param isStatLedEnabled the isStatLedEnabled to set 
     */  
    public synchronized void setStatLedEnabled(boolean isStatLedEnabled) {  
        this.isStatLedEnabled = isStatLedEnabled;  
    }  
  
    /** 
     * Sets the update interval of the IOIO thread 
     * @param ms 
     */  
    public synchronized void setUpdateInverval(long ms){  
        mUpdateInterval = ms;  
    }  
  
}  