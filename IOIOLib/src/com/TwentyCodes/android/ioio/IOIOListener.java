/**
 * IOIOListener.java
 * @date Jan 24, 2012
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

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * This is a simple interface used to pass information from the IOIO thread to the manager 
 * @author ricky barrette
 */
public interface IOIOListener {
	
	/**
	 * Called when a IOIO is connected.
	 * here is a good time to init ports
	 * @param ioio
	 * @author ricky barrette
	 */
	public void onConnected(IOIO ioio) throws ConnectionLostException;
	
	/**
	 * Called when the IOIO thread loops.
	 * Here you want to update the ports
	 * @author ricky barrette
	 */
	public void loop() throws ConnectionLostException;
	
	/**
	 * Called when the IOIO is disconnected.
	 * @author ricky barrette
	 */
	public void onDisconnected();

}
