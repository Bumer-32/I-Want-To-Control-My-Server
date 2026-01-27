package ua.pp.lumivoid.iwtcms.server.api.requests.api

import com.charleskorn.kaml.Yaml
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import ua.pp.lumivoid.iwtcms.server.*
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import java.io.File
import kotlin.test.assertContains

@ExtendWith(SetupTestEnv::class)
class ConfigsTest {
    val authTest = object: AuthBasedTest() {
        override var url: String = Configs.path
    }

//    @Test
    internal fun `test for created permissions`() {
        val permissionsList = PermissionsList.permissionsList()
        availableConfigSettings.forEach {
            assertContains(permissionsList, "config.read.${it.value.selector_name}")
            assertContains(permissionsList, "config.write.${it.value.selector_name}")
        }
    }

//    @Test
    internal fun `test root`() = testApplication {
        application { module() }

        val response = client.get(Configs.path)
        val body = response.bodyAsText()
        val availableConfigSettings: Map<String, Configs.AvailableConfigSetting> = Yaml.default.decodeFromString(body)
        assert(availableConfigSettings.isNotEmpty())
    }

//    @Test
    internal fun `test getting file content`() = testApplication {
        verifyTestUserRole(true)

        application { module() }

        availableConfigSettings.forEach {
            val url = "${Configs.path}/${it.value.selector_name}"
            authTest.url = url
            authTest.`test for unauthorized`()
            authTest.`test for forbidden`()

            val response = client.get(url) {
                cookie("USER_SESSION", Json.encodeToString(createTestSession()))
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("TEST ${it.value.selector_name}", response.bodyAsText())
        }

    }

//    @Test
    internal fun `test uploading config file`() = testApplication {
        verifyTestUserRole(true)

        application { module() }

        availableConfigSettings.forEach {
            val url = "${Configs.path}/${it.value.selector_name}"
            authTest.url = url
            authTest.`test for unauthorized`()
            authTest.`test for forbidden`()

            val file = File("${System.getProperty("user.dir")}/test/${it.value.config_path}")
            val oldFileContent = file.readText()
            val backupFile = File("test/__BACKUP__${file.name}")

            val response = client.put(url) {
                cookie("USER_SESSION", Json.encodeToString(createTestSession()))
                setBody("UPLOADED ${it.value.selector_name}")
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assert(backupFile.exists())
            assertEquals(oldFileContent, backupFile.readText())
            assertEquals("UPLOADED ${it.value.selector_name}", file.readText())
        }
    }

//    @Test
    internal fun `test getting strategy`() = testApplication {
        verifyTestUserRole(true)

        application { module() }

        availableConfigSettings.forEach {
            val url = "${Configs.path}/${it.value.selector_name}/strategy"
            authTest.url = url
            authTest.`test for unauthorized`()
            authTest.`test for forbidden`()

            val response = client.get(url) {
                cookie("USER_SESSION", Json.encodeToString(createTestSession()))
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    companion object {
        private val availableConfigsSettingsFile = Companion::class.java.getResource(Constants.AVAILABLE_CONFIGS_SETTINGS_FILE)!!
        private val availableConfigSettings: Map<String, Configs.AvailableConfigSetting> = Yaml.default.decodeFromString(availableConfigsSettingsFile.readText())

        @JvmStatic
//        @BeforeAll
        internal fun `create config files`() {
            availableConfigSettings.forEach {
                val filePath = "${System.getProperty("user.dir")}/test/${it.value.config_path}"
                val file = File(filePath)
                println(file.absolutePath)
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText("TEST ${it.value.selector_name}")
            }
        }

        @JvmStatic
//        @AfterAll
        internal fun `delete config files`() {
            File("test").deleteRecursively()
        }
    }
}