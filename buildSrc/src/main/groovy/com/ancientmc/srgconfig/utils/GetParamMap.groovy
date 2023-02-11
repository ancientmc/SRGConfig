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

    static Map<String, List<Integer>> map = new HashMap<>()

    static def getMap(File jar) {
        try (ZipFile jarFile = new ZipFile(jar)) {
            for(ZipEntry entry : Collections.list((Enumeration<ZipEntry>)jarFile.entries())) {
                if(!entry.name.endsWith(".class")) continue
                ClassReader reader = new ClassReader(jarFile.getInputStream(entry).bytes)
                ClassNode node = new ClassNode()
                reader.accept(node, 0)
                for(MethodNode mNode : node.methods) {
                    def mParser = new MethodParser(mNode)
                    String name = mParser.name
                    List<Integer> params = mParser.params
                    map.put(name, params)
                }
            }
            return map
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    // Partially adapted from https://github.com/ModCoderPack/MCInjector/blob/c6073894bb80f96f30d35a945f4d427fb4d7b39f/src/main/java/de/oceanlabs/mcp/mcinjector/adaptors/ApplyMap.java#L94
    static class MethodParser {
        def name, desc, params

        MethodParser(MethodNode node) {
            name = node.name
            desc = node.desc

            List<Integer> lParams = new ArrayList<>()
            List<Type> types = new ArrayList<>()

            if((node.access & Opcodes.ACC_STATIC) == 0) {
                types.add(Type.getType("L" + node.getClass().name.replace(".", "/") + ";"))
                lParams.add(0) // "this" local variable
            }

            types.addAll(Arrays.asList(Type.getArgumentTypes(node.desc)))

            // fancy shmancy formula for calculating the LVT index values
            // Kudos to LexManos for making this, I'm not smart enough to.
            for(int i = lParams.size(), j = i; i < types.size(); i++) {
                lParams.add(j)
                j += types.get(i).getSize()
            }

            // Removes the "this" variable from non-statics since we don't need it anymore
            // Without this all non-statics would have a ghost param entry with index value "0" in the TSRG file
            if((node.access & Opcodes.ACC_STATIC) == 0) lParams.remove(0)

            params = lParams
        }
    }
}
