package com.ancientmc.srgconfig.utils

import net.minecraftforge.srgutils.IMappingFile

/**
 * General utilities class full of miscellaneous methods that will probably need to be used more than once.
 */
class Utils {

    /**
     * Method that parses through a mapping file via SRGUtils to return a class map.
     * The map keys are the original notch names, while the values are the SRG class names.
     * Since this uses SRGUtils this method only supports the SRG format or later formats. It does not support RGS.
     */
    static Map<String, String> getClassMap(File file) {
        Map<String, String> classMap = new HashMap<>()
        IMappingFile mapping = IMappingFile.load(file)
        for(IMappingFile.IClass clazz : mapping.getClasses()) {
            classMap.put(clazz.original, clazz.mapped)
        }
        return classMap
    }
}
