package org.apache.camel.demo.camel_jms_jdbc_xa;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tranql.connector.mysql.MysqlExceptionSorter;

/**
 * Extends org.tranql.connector.mysql.MySqlExceptionSorter
 * so that some SQLExceptions can be considered fatal.
 * This allows transaction roll back on any errors.
 * Further fine tuning of the different error codes is recommended.
 */
public class CustomMysqlExceptionSorter extends MysqlExceptionSorter {

    Logger log = LoggerFactory.getLogger(CustomMysqlExceptionSorter.class.getName());


    /**
     * Return true if the exception is an SQLException.
     * Further fine tuning might be needed.
     *
     * @param e the exception to check
     * @return true if the connection should be discarded
     */
    @Override
    public boolean  isExceptionFatal(Exception e) {
        if (e instanceof SQLException) {
            // TODO: check error codes and decide whether the ex is fatal.
            log.debug("Received exception {}", ((SQLException)e).getMessage());
            return true;
        }
        return false;
    }


    /**
     * Whether to roll back on fatal exceptions or not.
     * @return trues - always roll back
     */
    @Override
    public boolean rollbackOnFatalException() {
        return true;
    }
}
