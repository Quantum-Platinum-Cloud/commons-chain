/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.chain.generic;

import static org.junit.Assert.*;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.apache.commons.chain.impl.NonDelegatingCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>Test case for the <code>DispatchLookupCommand</code> class.</p>
 *
 * @author Sean Schofield
 * @version $Revision$
 */

public class DispatchLookupCommandTestCase {


    // ---------------------------------------------------- Instance Variables

    /**
     * The instance of {@link Catalog} to use when looking up commands
     */
    protected Catalog catalog;

    /**
     * The {@link DispatchLookupCommand} instance under test.
     */
    protected DispatchLookupCommand<Context> command;

    /**
     * The {@link Context} instance on which to execute the chain.
     */
    protected Context context = null;


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp() {
        catalog = new CatalogBase();
        CatalogFactoryBase.getInstance().setCatalog(catalog);
        command = new DispatchLookupCommand<Context>();
        context = new ContextBase();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown() {
        catalog = null;
        CatalogFactoryBase.clear();
        command = null;
        context = null;
    }


    // ------------------------------------------------ Individual Test Methods


    // Test ability to lookup and execute a dispatch method on a single 
    // non-delegating command
    @Test
    public void testExecuteDispatchLookup_1a() {

        // use default catalog
        catalog.addCommand("fooCommand", new TestCommand<Context>("1"));
        
        // command should lookup the fooCommand and execute the fooMethod
        command.setName("fooCommand");
        command.setMethod("fooMethod");
        
        try {
            assertTrue("Command should return true",
                       command.execute(context));
        } catch (Exception e) {

            fail("Threw exception: " + e);
        }
        
        // command should lookup the fooCommand and execute the barMethod
        command.setMethod("barMethod");

        try {
            assertTrue("Command should return true",
                       command.execute(context));
        } catch (Exception e) {
            fail("Threw exception: " + e);
        }
        
        checkExecuteLog("1/1");
        
    }
    
    // Test IllegalArgumentException when incorrect command name specified
    @Test
    public void testExecuteDispatchLookup_2() {

        // use default catalog
        catalog.addCommand("barCommand", new TestCommand("2"));

        // command should lookup the fooCommand and execute the fooMethod
        command.setName("fooCommand");
        command.setMethod("fooMethod");

        try {
            command.execute(context);
        } catch (IllegalArgumentException e) {
            // test passed
            return;
        } catch (Exception e) {
            // this is a failure
        }
      
        fail("Expected IllegalArgumentException");
    }

    // Test ability to lookup and execute a dispatch method on a single 
    // non-delegating command (using context to specify method name)
    @Test
    public void testExecuteDispatchLookup_3() {

        // use default catalog
        catalog.addCommand("fooCommand", new TestCommand("3"));

        // command should lookup the fooCommand and execute the fooMethod
        command.setName("fooCommand");
        command.setMethodKey("methodKey");
        context.put("methodKey", "fooMethod");

        try {
            assertTrue("Command should return true",
                       command.execute(context));
        } catch (Exception e) {
            fail("Threw exception: " + e);
        }

        // command should lookup the fooCommand and execute the barMethod
        command.setMethodKey("methodKey");
        context.put("methodKey", "barMethod");


        try {
            assertTrue("Command should return true",
                       command.execute(context));
        } catch (Exception e) {
            fail("Threw exception: " + e);
        }

        checkExecuteLog("3/3");

    }


    // -------------------------------------------------------- Support Methods


    // Verify the contents of the execution log
    protected void checkExecuteLog(String expected) {
        StringBuffer log = (StringBuffer) context.get("log");
        assertNotNull("Context failed to return log", log);
        assertEquals("Context returned correct log",
                     expected, log.toString());
    }

    // ---------------------------------------------------------- Inner Classes


    class TestCommand<C extends Context> extends NonDelegatingCommand<C> {

        public TestCommand(String id)
        {
            super(id);
        }
    
        public boolean fooMethod(C context) {
            log(context, id);            
            return true;
        }
        
        public boolean barMethod(C context) {
            log(context, id);
            return true;
        }
        
    }

}