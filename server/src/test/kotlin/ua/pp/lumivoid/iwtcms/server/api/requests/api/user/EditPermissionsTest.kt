package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.pp.lumivoid.iwtcms.server.*
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class EditPermissionsTest: AuthBasedTest() {
    override var url: String = EditPermissions.path

    @ParameterizedTest
    @MethodSource("params")
    internal fun `test EditPermissions`(testPayload: EditPermissions.EditPermissionsPayload, expectedStatus: HttpStatusCode) = testApplication {
        verifyTestUserRole(true)

        application { module() }

        val response = client.put(url) {
            cookie("USER_SESSION", Json.encodeToString(createTestSession()))
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(testPayload))
        }

        assertEquals(expectedStatus, response.status)

        if (response.status == HttpStatusCode.OK) {
            transaction {
                val user = UserEntity.find { UsersTable.username eq "test" }.single()
                val permissions = user.permissions.map { it.permissionName }

                assertEquals(testPayload.admin, user.admin)
                assertContains(permissions, "one")
                assertContains(permissions, "two")
                assertContains(permissions, "three")
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        internal fun `insert users for test`(): Unit = runBlocking {
                CreateUser.create("test", "test", false, emptyList())
        }

        @JvmStatic
        @AfterAll
        internal fun `delete users for test`(): Unit = runBlocking {
            transaction {
                UsersTable.deleteWhere { UsersTable.username eq "test" }
            }
        }
        
        @JvmStatic
        fun params(): List<Arguments> = runBlocking {
            return@runBlocking listOf(
                Arguments.of(EditPermissions.EditPermissionsPayload("notExists", false,emptyList()), HttpStatusCode.NotFound),
                Arguments.of(EditPermissions.EditPermissionsPayload("test", false,listOf("one", "two", "three")), HttpStatusCode.OK),
                Arguments.of(EditPermissions.EditPermissionsPayload("test", true,listOf("one", "two", "three")), HttpStatusCode.OK),
            )
        }
    }
}