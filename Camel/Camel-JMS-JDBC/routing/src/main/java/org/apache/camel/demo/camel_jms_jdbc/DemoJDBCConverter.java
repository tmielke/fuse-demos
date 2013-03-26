package org.apache.camel.demo.camel_jms_jdbc;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers some conversion methods that convert a string msg 
 * contaning comma separated values into Maps or Arrays so 
 * that it can be used in camel-sql component. 
 * camel-sql wants data to be used in SQL statements to be
 * either of java.util.Map or of Object array.
 */
@Converter
public class DemoJDBCConverter {

    public static String DELIMITER = ",";
    private static Logger LOG = LoggerFactory.getLogger(DemoJDBCConverter.class);

    /**
     * Utility classes should not have a public constructor.
     */
    private DemoJDBCConverter() {
    }

    /**
     * Converts string message into java.util.Map by tokenizing 
     * the input string and trimming any leading and trailing
     * whitespace characters.
     * 
     * @param orig - the original input msg containing comma separated values
     * @return a Map of these values
     */
    @Converter
    public static Map toMap(String orig) {
        LOG.debug("Converting from String to java.util.Map.");
        Map result = new HashMap();
        if (orig != null && (!orig.equals(""))) {
            StringTokenizer tokenizer = new StringTokenizer(orig, DELIMITER);
            result.put("firstname", tokenizer.nextToken().trim());
            result.put("lastname", tokenizer.nextToken().trim());
            result.put("login", tokenizer.nextToken().trim());
            result.put("password", tokenizer.nextToken().trim());
            LOG.debug("Converted to Map with values: " +
                "firstname=" + result.get("firstname") + ", " +
                "lastname=" + result.get("lastname") + ", " +
                "login=" + result.get("login") + ", " +
                "password=" + result.get("password"));
        }
        return result;
    }

    /**
     * Converts string message into java.lang.Object[] array by tokenizing 
     * the input string and trimming any leading and trailing
     * whitespace characters.
     * 
     * @param orig - the original input msg containing comma separated values
     * @return Object array of these values
     */
    @Converter
    public static Object[] toArray(String orig) {
        LOG.debug("Converting from String to java.util.Map.");
        Object[] result = null;
        if (orig != null && (!orig.equals(""))) {
            StringTokenizer tokenizer = new StringTokenizer(orig, DELIMITER);
            result = new Object[tokenizer.countTokens()];
            result[0] = new String(tokenizer.nextToken().trim());
            result[1] = new String(tokenizer.nextToken().trim());
            result[2] = new String(tokenizer.nextToken().trim());
            result[3] = new String(tokenizer.nextToken().trim());
            LOG.debug("Converted to Object array with values: " +
                "firstname=" + result[0] + ", " +
                "lastname=" + result[1] + ", " +
                "login=" + result[2] + ", " +
                "password=" + result[3]);
        }
        return result;
    }
}
