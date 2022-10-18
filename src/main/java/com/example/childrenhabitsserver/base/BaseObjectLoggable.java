package com.example.childrenhabitsserver.base;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseObjectLoggable {
    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    protected Logger getLogger() {
        return this.logger;
    }
}
