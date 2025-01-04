package ua.pp.lumivoid.iwtcms.ktor.api.dev

import io.github.irgaly.kfswatch.KfsDirectoryWatcherEvent
import io.github.irgaly.kfswatch.KfsEvent
import ua.pp.lumivoid.iwtcms.Constants
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit

object WebCompile {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    private var devRunnerFile: String? = null

    init {
        devRunnerFile = if (System.getProperty("os.name").startsWith("Win")) {
            "devRunner.bat"
        } else {
            "devRunner.sh"
        }

        npmInstall()
    }

    fun String.runCommand() {
        ProcessBuilder(*split(" ").toTypedArray())
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
    }

    private fun npmInstall() {
        logger.info("Installing npm packages")
        "${Constants.IWTCMS_DEV_FOLDER}/$devRunnerFile npmInstall".runCommand()
    }

    private fun compileSASS() {
        logger.info("Compiling SASS")
        "${Constants.IWTCMS_DEV_FOLDER}/$devRunnerFile compileSASS".runCommand()
    }

    private fun compileTS() {
        logger.info("Compiling TS")
        "${Constants.IWTCMS_DEV_FOLDER}/$devRunnerFile compileTS".runCommand()
    }

    fun modifyHtml(file: String) {
        // This function must to add js script at and of file to connect to reload ws
        val html = File(file)
        val htmlContent = html.readLines()
        val newContent = htmlContent.toMutableList()
        val bodyCloserIndex = htmlContent.indexOf("</body>")

        if (bodyCloserIndex == -1) {
            logger.error("Can't find </body> tag in ${html.name}")
            return
        }

        val jsScript = """
            <!--INSERTED BY IWTCMS DEV MODE-->
            <script>
                let socket;
                let reconnectInterval = 5000; // 5 seconds
            
                function createWebSocket() {
                    socket = new WebSocket(`${'$'}{document.baseURI}/dev/reloadWS`);
                    
                    socket.onopen = () => {
                        console.log("Live reloading started");
                    };
                    
                    socket.onmessage = (event) => {
                        if (event.data === "reload") {
                            socket.close();
                            window.location.reload();
                        }
                    };
                    
                    socket.onerror = (error) => {
                        console.log("Live reloading error:", error);
                    };
                    
                    socket.onclose = () => {
                        console.log("Live reloading ws closed. Reconnecting...");
                        setTimeout(createWebSocket, reconnectInterval); // Try to reconnect
                    };
                }
            
                createWebSocket(); // Initial connection
            </script>
            <!--INSERTED BY IWTCMS DEV MODE-->
        """.trimIndent().lines()

        for (i in jsScript.indices) {
            newContent.add(bodyCloserIndex + i, jsScript[i].toString())
        }

        File("${Constants.DEV_WEB_FOLDER}/${html.name}").printWriter().use { out ->
            newContent.forEach { out.println(it) }
        }

        DevReloadWS.asWs()?.sendMessage("reload")
        logger.info("Modified ${html.name}")
    }

    fun compileAll() {
        val devFolder = File(Constants.DEV_WEB_FOLDER)
        devFolder.delete()
        File(Constants.KFSW_SRC_WEB_FOLDER).copyRecursively(devFolder, true)
        devFolder.walkTopDown().forEach { file ->
            if (file.name.endsWith(".html")) {
                modifyHtml(file.absolutePath)
            }
        }
        compileSASS()
        compileTS()
    }

    fun compile(event: KfsDirectoryWatcherEvent) {
        val file = File("${event.targetDirectory}/${event.path}")

        if (file.name.endsWith("~")) return // idk what is it, maybe intellij idea

        if (file.isDirectory) {
            // With deleting folder we also can delete another files, so compile all
            if (event.event == KfsEvent.Delete) compileAll()
            return
        }

        if (file.name.endsWith(".ts")) {
            compileTS()
        } else if (file.name.endsWith(".scss") || file.name.endsWith(".sass")) {
            compileSASS()
        } else if (file.name.endsWith(".html") && event.event != KfsEvent.Delete) {
            modifyHtml(file.absolutePath)
            return
        }

        // and copy it
        if (event.event == KfsEvent.Create || event.event == KfsEvent.Modify) {
            file.copyTo(File("${Constants.DEV_WEB_FOLDER}/${event.path}"), true)
        } else if (event.event == KfsEvent.Delete) {
            File("${Constants.DEV_WEB_FOLDER}/${event.path}").delete()
        }

        DevReloadWS.asWs()?.sendMessage("reload")
    }
}