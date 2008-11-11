/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.tools.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.concurrent.CopyJob; 

/**
 * JBWS-1793: Provide a test case for the tools scripts that reside under JBOSS_HOME/bin
 * 
 * @author Heiko.Braun@jboss.com
 */
public class ScriptTestCase extends JBossWSTest
{
   private static final String FS = System.getProperty("file.separator"); // '/' on unix, '\' on windows
   private static final String PS = System.getProperty("path.separator"); // ':' on unix, ';' on windows
   private static final String LS = System.getProperty("line.separator"); // '\n' on unix, '\r\n' on windows
   private String TOOLS_CONFIG = getResourceFile("tools" + FS + "scripts" + FS + "wstools-config.xml").getAbsolutePath();
   private static final String EXT = ":".equals( PS ) ? ".sh" : ".bat";

   private String JBOSS_HOME;
   private String JDK_HOME;
   private String TEST_EXEC_DIR;
   private String OS;

   protected void setUp() throws Exception
   {
      super.setUp();

      JBOSS_HOME = System.getProperty("jboss.home");
      TEST_EXEC_DIR = createResourceFile(".").getPath();
      JDK_HOME = System.getProperty("java.home");
      OS = System.getProperty("os.name").toLowerCase();
   }

   public void testWSToolsFromCommandLine() throws Exception
   {
      // use absolute path for the output to be re-usable      
      File dest = createResourceFile("wstools" + FS + "java");
      dest.mkdirs();

      String command = JBOSS_HOME + FS + "bin" + FS + "wstools" + EXT + " -config " + TOOLS_CONFIG + " -dest "+ dest.getAbsolutePath();
      Process p = executeCommand(command);

      // check status code
      assertStatusCode(p, "wstools");

      File javaSource = getResourceFile("wstools" + FS + "java" + FS + "org" + FS + "jboss" + FS + "test" + FS + "ws" + FS + "jbws810" + FS + "PhoneBookService.java");

      assertTrue("Service endpoint interface not generated", javaSource.exists());
   }

   private Process executeCommand(String command) throws IOException
   {
      // be verbose
      System.out.println("cmd: " + command);
      System.out.println("test execution dir: " + TEST_EXEC_DIR);

      Process p = Runtime.getRuntime().exec(command, new String[] { "JBOSS_HOME=" + JBOSS_HOME, "JAVA_HOME=" + JDK_HOME });
      return p;
   }

   private void assertStatusCode(Process p, String s) throws InterruptedException, IOException
   {
	   CopyJob job = new CopyJob(p.getInputStream(), System.out);
      // unfortunately the following thread is needed (otherwise this test will not work on windows)
	   new Thread( job ).start();
	   int status = -1;
	   try
	   {
	      status = p.waitFor();
	   }
	   finally
	   {
	      job.kill();
         p.destroy();
      }

      // check status code
      if (status != 0)
      {
         System.out.println("Error stream");
         printStream(p.getErrorStream());
      }
      assertTrue(s + " did exit with status " + status, status == 0);
   }

   private void printStream(InputStream is) throws IOException
   {
      System.out.println();
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      StringBuffer buffer = new StringBuffer();
      String line;
      while ((line = in.readLine()) != null) {
        buffer.append(line + LS);
      }
      System.out.println(buffer.toString());
      System.out.println();
      System.out.println();
   }

}