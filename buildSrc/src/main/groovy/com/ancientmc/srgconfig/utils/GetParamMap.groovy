package com.ancientmc.srgconfig.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Util class that creates a map of all parameters from the jar inputted in the getMap() method.
 * Each map entry consists of the method name and the list of all the method's parameter index values in the LVT.
 */
class GetParamMap {

    static Map<String, Integer> map = new HashMap<>()

    static def getMap(File jar) {
        try (ZipFile jarFile = new ZipFile(jar)) {
            for(ZipEntry entry : Collections.list((Enumeration<ZipEntry>)jarFile.entries())) {
                if(!entry.name.endsWith(".class")) continue
                ClassReader reader = new ClassReader(jarFile.getInputStream(entry).bytes)
                ClassNode node = new ClassNode()
                reader.accept(node, 0)
                for(MethodNode mNode : node.methods) {
                    def mParser = new MethodParser(mNode)
                    String name = mParser.name + ' ' + mParser.desc
                    int params = mParser.params
                    map.put(name, params)
                }
            }
            return map
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    static class MethodParser {
        def name, desc, params

        MethodParser(MethodNode node) {
            name = node.name
            desc = node.desc
            params = Type.getArgumentTypes(node.desc).size()
        }
    }
}
