package io.github.guchey.embulk.input.ahrefs.e2e

import com.google.inject.Binder
import com.google.inject.Module
import org.embulk.EmbulkEmbed
import org.embulk.spi.InputPlugin
import kotlin.test.Test
import kotlin.test.fail


class AhrefsInputPluginTest {

    @Test
    fun `preview backlink_stats`() {
        val bootstrap = EmbulkEmbed.Bootstrap();
        bootstrap.addModules(object : Module {
            override fun configure(binder: Binder) {
//                InjectedPluginSource.registerPluginTo(binder, InputPlugin::class.java, "ahrefs", AhrefsInputPlugin::class.java)
                // ...
            }
        })

//		val embulk = bootstrap.initialize();           // シャットダウンフックを登録する
//        try {
//            fail("not implemented")
//        } finally {
//            embulk.destroy();
//        }
    }
}