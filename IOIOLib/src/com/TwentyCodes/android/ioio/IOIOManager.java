/** 
 * IOIOManager.java 
 * @date Jan 21, 2012 
 * @author ricky barrette 
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
  
import ioio.lib.bluetooth.BluetoothIOIOConnectionDiscovery;  
import ioio.lib.util.IOIOConnectionDiscovery;  
import ioio.lib.util.SocketIOIOConnectionDiscovery;  
import ioio.lib.util.IOIOConnectionDiscovery.IOIOConnectionSpec;  
  
import java.util.Collection;  
import java.util.LinkedList;  
  
import android.util.Log;  
  
/** 
 * This class manages the IOIO connectivity threads. It is based on the AbstractIOIOActivity 
 * Remember that onConnected(), loop(), and onDisconnected() are called from the one of the IOIO threads 
 * @author ricky barrette 
 */  
public abstract class IOIOManager implements IOIOListener{  
  
    private static final String TAG = "IOIOManager";  
    private Collection<IOIOThread> mThreads = new LinkedList<IOIOThread>();  
  
    /** 
     * Aborts the IOIO connectivity threads and joins them 
     * @throws InterruptedException 
     * @author ricky barrette 
     */  
    public void abort() throws InterruptedException {  
        for (IOIOThread thread : mThreads)  
            thread.abort();  
        joinAllThreads();  
    }  
  
    /** 
     * Joins all the threads 
     * @throws InterruptedException 
     * @author ricky barrette 
     */  
    private void joinAllThreads() throws InterruptedException {  
        for (IOIOThread thread : mThreads)  
            thread.join();  
    }  
  
    /** 
     * Creates all the required IOIO connectivity threads 
     * @author ricky barrette 
     */  
    private void createAllThreads() {  
        mThreads.clear();  
        Collection<IOIOConnectionSpec> specs = getConnectionSpecs();  
        for (IOIOConnectionSpec spec : specs)  
            mThreads.add(new IOIOThread(spec.className, spec.args, this));  
    }  
  
    /** 
     * Starts IOIO connectivity threads 
     * @author ricky barrette 
     */  
    public void start() {  
        createAllThreads();  
        for (IOIOThread thread : mThreads)  
            thread.start();  
    }  
  
    /** 
     * @return 
     * @author Ytai Ben-Tsvi 
     */  
    private Collection<IOIOConnectionSpec> getConnectionSpecs() {  
        Collection<IOIOConnectionSpec> result = new LinkedList<IOIOConnectionSpec>();  
        addConnectionSpecs(SocketIOIOConnectionDiscovery.class.getName(),result);  
        addConnectionSpecs(BluetoothIOIOConnectionDiscovery.class.getName(), result);  
        return result;  
    }  
  
    /** 
     * @param discoveryClassName 
     * @param result 
     * @author Ytai Ben-Tsvi 
     */  
    private void addConnectionSpecs(String discoveryClassName, Collection<IOIOConnectionSpec> result) {  
        try {  
            Class<?> cls = Class.forName(discoveryClassName);  
            IOIOConnectionDiscovery discovery = (IOIOConnectionDiscovery) cls.newInstance();  
            discovery.getSpecs(result);  
        } catch (ClassNotFoundException e) {  
            Log.d(TAG, "Discovery class not found: " + discoveryClassName+ ". Not adding.");  
        } catch (Exception e) {  
            Log.w(TAG,"Exception caught while discovering connections - not adding connections of class "+ discoveryClassName, e);  
        }  
    }  
  
    /** 
     * @param isStatLedEnabled the isStatLedEnabled to set 
     * @author ricky barrette 
     */  
    public void setStatLedEnabled(boolean isStatLedEnabled) {  
        for(IOIOThread thread : mThreads)  
            thread.setStatLedEnabled(isStatLedEnabled);  
    }  
  
    /** 
     * Sets the update interval of the IOIO thread 
     * @param ms 
     * @author ricky barrette 
     */  
    public void setUpdateInverval(long ms){  
        for(IOIOThread thread : mThreads)  
            thread.setUpdateInverval(ms);  
    }  
  
}  