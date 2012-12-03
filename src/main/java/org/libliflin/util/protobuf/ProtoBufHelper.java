/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.util.protobuf;

import com.google.protobuf.AbstractMessage;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author wjlaffin
 */
public class ProtoBufHelper {

    public static void exploreInnerClassesAddingToBuilderMap(Map<String, AbstractMessage.Builder> builderMap, Class<?> cls) {
        try {
            if (AbstractMessage.Builder.class.isAssignableFrom(cls)) {
                String canonicalName = cls.getCanonicalName();
                // return if this is one of the inner classes of builder
                if (canonicalName.startsWith("com.google.protobuf")) {
                    return;
                }
                String afterPackage = StringUtils.substringAfter(canonicalName, cls.getPackage().getName() + ".");
                String afterPackageAndBeforeBuilder = StringUtils.substringBefore(afterPackage, ".Builder");
                String[] classChain = StringUtils.split(afterPackageAndBeforeBuilder, '.');
                String className = classChain[classChain.length - 1];
                String lowerCaseClassName = StringUtils.lowerCase(className);
                Class<?> message = Class.forName(cls.getPackage().getName() + "." + StringUtils.replace(afterPackageAndBeforeBuilder, ".","$"));
                Object builder = message.getMethod("newBuilder", null).invoke(null);

//            System.out.println("found class: " + lowerCaseClassName);
                builderMap.put(lowerCaseClassName, (AbstractMessage.Builder) builder);

            } else {
                Class<?>[] subs = cls.getClasses();
                if (subs == null || subs.length == 0) {
                    return;
                }
                for (Class<?> sub : subs) {
                    if (sub != cls) {
                        exploreInnerClassesAddingToBuilderMap(builderMap, sub);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }
}
