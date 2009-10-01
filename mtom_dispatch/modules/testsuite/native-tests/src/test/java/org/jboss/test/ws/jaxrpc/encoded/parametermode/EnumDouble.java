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

// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.2_01, build R40)
// Generated source version: 1.1.2

package org.jboss.test.ws.jaxrpc.encoded.parametermode;


import java.util.HashMap;
import java.util.Map;

public class EnumDouble {
    private double value;
    private static Map valueMap = new HashMap();
    public static final double _value1 = (double)-1.0000000000000;
    public static final double _value2 = (double)3.0000000000000;
    
    public static final EnumDouble value1 = new EnumDouble(_value1);
    public static final EnumDouble value2 = new EnumDouble(_value2);
    
    protected EnumDouble(double value) {
        this.value = value;
        valueMap.put(this.toString(), this);
    }
    
    public double getValue() {
        return value;
    }
    
    public static EnumDouble fromValue(double value)
        throws java.lang.IllegalStateException {
        if (value1.value == value) {
            return value1;
        } else if (value2.value == value) {
            return value2;
        }
        throw new IllegalArgumentException();
    }
    
    public static EnumDouble fromString(String value)
        throws java.lang.IllegalStateException {
        EnumDouble ret = (EnumDouble)valueMap.get(value);
        if (ret != null) {
            return ret;
        }
        if (value.equals("-1.0000000000000")) {
            return value1;
        } else if (value.equals("3.0000000000000")) {
            return value2;
        }
        throw new IllegalArgumentException();
    }
    
    public String toString() {
        return new Double(value).toString();
    }
    
    private Object readResolve()
        throws java.io.ObjectStreamException {
        return fromValue(getValue());
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof EnumDouble)) {
            return false;
        }
        return ((EnumDouble)obj).value == value;
    }
    
    public int hashCode() {
        return new Double(value).toString().hashCode();
    }
}