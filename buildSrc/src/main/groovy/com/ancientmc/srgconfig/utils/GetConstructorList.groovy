package com.ancientmc.srgconfig.utils

import groovy.json.JsonSlurper

class GetConstructorList {

    /** Each line in the list is equivalent to a line in the constructors.txt file inputted into MCInjector
     * (That text file isn't going to be used in ACP, though)
     * {id} {class & method} {desc}
     */
    static List<String> list = new ArrayList<>()

    static def getList(File json) {
        def jsonObj = new JsonSlurper().parse(json)
        jsonObj.each { clazz ->
            String className = clazz.name

            // Constructors require an id for
            int id = 1
            clazz.methods.each { method ->

            }
        }
    }
}
