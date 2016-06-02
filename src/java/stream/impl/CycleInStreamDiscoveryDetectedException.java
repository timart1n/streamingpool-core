/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

public class CycleInStreamDiscoveryDetectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CycleInStreamDiscoveryDetectedException() {
        super();
    }

    public CycleInStreamDiscoveryDetectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleInStreamDiscoveryDetectedException(String message) {
        super(message);
    }

    public CycleInStreamDiscoveryDetectedException(Throwable cause) {
        super(cause);
    }

}