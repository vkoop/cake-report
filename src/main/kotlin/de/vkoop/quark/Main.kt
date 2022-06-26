package de.vkoop.quark

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.annotations.QuarkusMain


@QuarkusMain
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Quarkus.run(MyApp::class.java, *args)
        }
    }
}
