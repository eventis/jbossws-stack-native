package org.jboss.test.ws.interop.microsoft.soapwsdl.BaseDataTypesRpcLit;

import junit.framework.Test;
import org.jboss.test.ws.JBossWSTestSetup;
import org.jboss.test.ws.interop.microsoft.soapwsdl.BaseDataTypesProxy;
import org.jboss.test.ws.interop.microsoft.soapwsdl.BaseDataTypesSEI;
import org.jboss.test.ws.interop.microsoft.soapwsdl.BaseDataTypesSupport;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 17-Feb-2006
 */
public class BaseDataTypesRpcLitTestCase extends BaseDataTypesSupport {

   IBaseDataTypesRpcLit targetPort;
   BaseDataTypesSEI proxy;

   public static Test suite()
   {
      return JBossWSTestSetup.newTestSetup(BaseDataTypesRpcLitTestCase.class, "jbossws-interop-BaseDataTypesRpcLit-client.jar");
   }

    protected void setUp() throws Exception
   {
      super.setUp();

      if (targetPort == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/interop/BaseDataTypesRpcLitService");
         this.targetPort = (IBaseDataTypesRpcLit)service.getPort(IBaseDataTypesRpcLit.class);
         this.proxy = (BaseDataTypesSEI)BaseDataTypesProxy.newInstance(targetPort);
      }      
   }

   protected BaseDataTypesSEI getTargetPort() throws Exception {
      return this.proxy;
   }
}