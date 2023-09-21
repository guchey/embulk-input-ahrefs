package com.guchey.embulk.input.ahrefs.delegate

//import com.guchey.embulk.input.ahrefs.config.PluginTask
//import org.embulk.EmbulkTestRuntime
//import org.embulk.config.ConfigSource
//import org.embulk.spi.Schema
//import org.junit.jupiter.api.BeforeEach
//import org.junit.Rule
//import kotlin.test.Test


class BackLinkStatsInputPluginTest {
//    @get:Rule
//    val runtime = EmbulkTestRuntime()
//
//    lateinit var config: ConfigSource
//    lateinit var plugin: AhrefsInputPlugin
//
//    @BeforeEach
//    fun createResources() {
//        config = runtime.exec.newConfigSource()
//        plugin = AhrefsInputPlugin()
//    }
//
//    @Test
//    fun testInput() {
//        // テストするための設定を追加
//        config.set("option1", "value1")
//        config.set("option2", "value2")
//
//        // プラグインの設定と実行
//        val task = config.loadConfig(PluginTask::class.java)
////        val schema = plugin.transaction(config, object : AhrefsInputPlugin.Control {
////            override fun run(task: PluginTask, schema: Schema, taskCount: Int) {
////                // プラグインが生成するページをチェック
////                for (i in 0 until taskCount) {
////                    val page = plugin.run(task.dump(), schema, i, null)
////                    // ページの内容をテスト
////                }
////            }
////        })
//
//        // 結果を確認
//        // assertEquals(expected, actual)
//    }
}