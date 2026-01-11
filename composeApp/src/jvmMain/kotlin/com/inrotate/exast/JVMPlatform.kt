package com.inrotate.exast

import com.inrotate.exast.utils.Platform

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

