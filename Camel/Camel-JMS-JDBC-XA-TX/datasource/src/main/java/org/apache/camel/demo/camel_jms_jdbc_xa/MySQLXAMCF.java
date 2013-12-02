/*
 * Copyright (c) 2005 - 2007, Tranql project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.camel.demo.camel_jms_jdbc_xa;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import org.tranql.connector.jdbc.AbstractXADataSourceMCF;

/**
 * ManagedConnectionFactory for connecting to a MySQL database with XA transactions.
 * Taken from org.tranql.connector.mysql.XAMCF but supplying our own 
 * MysqlExceptionSorter class. The default MysqlExceptionSorter (which is 
 * hard-coded in MySQLXAMCF) considers every SQLException non-fatal.
 * As a result a database restart does not cause the JDBC connection to get
 * refreshed.
 * This version of MySQLXAMCF uses its own CustomMysqlExceptionSorter which 
 * currently considers all SQLExceptions fatal, causing the transaction 
 * to be rolled back. CustomMysqlExceptionSorter should get more fine tuned
 * to only consider certain SQLExceptions as fatal.
 */
public class MySQLXAMCF extends AbstractXADataSourceMCF<MysqlXADataSource> {

    private static final long serialVersionUID = 8547770200041477798L;
    private String password;

    /**
     * Default constructor for an MySQL XA DataSource.
     *
     */
    public MySQLXAMCF() {
        super(new MysqlXADataSource(), new CustomMysqlExceptionSorter());
    }

    public void setDatabaseName(String dbname) {
        xaDataSource.setDatabaseName(dbname);
    }

    public String getDatabaseName() {
        return xaDataSource.getDatabaseName();
    }

    public void setPassword(String pwd) {
        this.password = pwd;
        xaDataSource.setPassword(pwd);
    }

    public String getPassword() {
        return password;
    }

    public void setPortNumber(Integer pn) {
        xaDataSource.setPortNumber(pn == null ? 0 : pn.intValue());
    }

    public Integer getPortNumber() {
        return new Integer(xaDataSource.getPortNumber());
    }

    public void setServerName(String sn) {
        xaDataSource.setServerName(sn);
    }

    public String getServerName() {
        return xaDataSource.getServerName();
    }

    public void setUserName(String user) {
        xaDataSource.setUser(user);
    }

    public String getUserName() {
        return xaDataSource.getUser();
    }

    /**
     * Equality is defined in terms of the serverName, portNumber and databaseName properties
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MySQLXAMCF) {
            MysqlXADataSource other = ((MySQLXAMCF) obj).xaDataSource;
            return equals(xaDataSource.getServerName(), other.getServerName()) &&
                    xaDataSource.getPortNumber() == other.getPortNumber() &&
                    equals(xaDataSource.getDatabaseName(), other.getDatabaseName());
        } else {
            return false;
        }
    }

    private static boolean equals(String a, String b) {
        return a == b || a != null && a.equals(b);
    }

    public int hashCode() {
        return hashCode(xaDataSource.getServerName()) ^ xaDataSource.getPortNumber() ^ hashCode(xaDataSource.getDatabaseName());
    }

    private static int hashCode(String s) {
        return s == null ? 0 : s.hashCode();
    }

    public String toString() {
        return "MySQLXAMCF[" + getDatabaseName() + ']';
    }
}
