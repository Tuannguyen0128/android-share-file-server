package com.example.sharefileserver.service
import android.content.Intent
import android.os.IBinder
import fi.iki.elonen.NanoHTTPD
import java.io.File
import android.app.Service
import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import androidx.activity.ComponentActivity
import android.util.Log
import com.example.sharefileserver.common.SERVER_STOPPED
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sharefileserver.common.getMimeType
import com.example.sharefileserver.common.serveFile2
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT
import fi.iki.elonen.NanoHTTPD.Response
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse
import com.example.sharefileserver.common.heicToPngBase64


public val TAG = "HttpFileServer"
class HttpFileServerService: Service() {
    private var server: NanoHTTPD? = null
    private  var isRunning: Boolean = false

    override fun onCreate() {
        super.onCreate()
        //startServer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val port = intent?.getIntExtra("PORT", 8080) ?: 8080
        if (!isRunning) {
            startServer(this, port)
        }
        // START_STICKY: service restarts if killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
        Log.d(TAG, "Server stoped...")
        val action = packageName + SERVER_STOPPED
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        Log.d("HttpFileServerService", "Sending broadcast with action: $action")
        // update main activity state here
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private lateinit var assetManager: AssetManager
    private fun startServer(context: Context, port: Int) {
        assetManager = context.assets
        try {
            Log.d(TAG, "Server starting...")
            server = object : NanoHTTPD("0.0.0.0", port) {
                override fun serve(session: IHTTPSession): Response {
                    val rootDir = Environment.getExternalStorageDirectory()
                    val requestedPath = File(rootDir, session.uri.removePrefix("/"))
                    val uri = session.uri
                    val method = session.method

                    return when{
                            //uri == "/" -> serveStaticFile(context, "/index.html")
                            uri.startsWith("/css/") || uri.startsWith("/js/") ->
                            serveStaticFile(context, uri)
                            uri == "/api/files" && method == Method.POST -> serveFileListAPI(session)
                            uri == "/api/files/image" && method == Method.GET -> serveImage(session,rootDir)
                            uri.startsWith("/api/files/create") && method == Method.POST -> handleCreate(session, rootDir)
                            uri.startsWith("/api/files/upload") && method == Method.POST -> handleUpload(session, rootDir)
                            uri.startsWith("/api/files/delete") && method == Method.POST -> handleDelete(session, rootDir)
                            uri.startsWith("/api/files/rename") && method == Method.POST -> handleRename(session, rootDir)
                            else->{
                                when{
                                    //requestedPath.exists() && requestedPath.isDirectory -> serveFileList(requestedPath, session.uri, page, itemsPerPage)
                                    requestedPath.exists() && requestedPath.isDirectory -> {
                                        val indexFile = context.assets.open("template/index.html")
                                            .bufferedReader().use { it.readText() }

                                        if (indexFile.isNotEmpty()) {
                                            newFixedLengthResponse(Response.Status.OK, "text/html", indexFile)
                                        } else {
                                            newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "index.html not found")
                                        }
                                    }
                                    requestedPath.exists() && requestedPath.isFile -> serveFile2(requestedPath)
                                    else -> newFixedLengthResponse(
                                        Response.Status.NOT_FOUND,
                                        "text/html",
                                        "<h1>404 - Not Found</h1><a href='/'>Back to file list</a>"
                                    )
                                }

                            }
                        }


                    }

            }
            server?.start()
            isRunning = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopServer() {
        server?.stop()
        server = null
        isRunning =false
    }

    private fun serveStaticFile(context: Context, uri: String): NanoHTTPD.Response {
        val filePath = "template$uri" // e.g. "/css/style.css" -> "template/css/style.css"
        return try {
            val inputStream = context.assets.open(filePath.removePrefix("/"))
            val mimeType = getMimeTypeFromPath(filePath)
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                mimeType,
                inputStream,
                inputStream.available().toLong()
            )
        } catch (e: Exception) {
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.NOT_FOUND,
                "text/plain",
                "File not found: $uri"
            )
        }
    }

    private fun getMimeTypeFromPath(path: String): String {
        return when {
            path.endsWith(".html") -> "text/html"
            path.endsWith(".css") -> "text/css"
            path.endsWith(".js") -> "application/javascript"
            path.endsWith(".png") -> "image/png"
            path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
            path.endsWith(".gif") -> "image/gif"
            else -> "application/octet-stream"
        }
    }

    private fun serveFileListAPI(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        session.parseBody(mutableMapOf())
        val postParams = session.parameters

        val pathParam = postParams["path"]?.firstOrNull() ?: "/"
        val page = postParams["page"]?.firstOrNull()?.toIntOrNull() ?: 1
        val size = postParams["size"]?.firstOrNull()?.toIntOrNull() ?: 10

        val dir = File(Environment.getExternalStorageDirectory(), pathParam)
        if (!dir.exists() || !dir.isDirectory) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", """{"error":"Directory not found"}""")
        }

        val allFiles = dir.listFiles()?.sortedBy { !it.isDirectory } ?: emptyList()
        val total = allFiles.size
        val fromIndex = ((page - 1) * size).coerceAtLeast(0)
        val toIndex = (fromIndex + size).coerceAtMost(total)

        val pageFiles = if (fromIndex < total) allFiles.subList(fromIndex, toIndex) else emptyList()

        var numberOfFile = allFiles.count { it.isFile() }
        val numberOfDir = allFiles.count { it.isDirectory() }

        val jsonArray = pageFiles.map { file ->
            mapOf(
                "name" to file.name,
                "isDirectory" to file.isDirectory,
                "size" to if (file.isDirectory) 0 else file.length(),
                "lastModified" to file.lastModified()
            )
        }

        val responseJson = mapOf(
            "page" to page,
            "size" to size,
            "total" to total,
            "files" to jsonArray,
            "numberOfDir" to numberOfDir,
            "numberOfFile" to numberOfFile,
            "totalPage" to total/size
        )

        val jsonText = Gson().toJson(responseJson)
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonText)
    }

    fun serveImage(session: NanoHTTPD.IHTTPSession, rootDir: File): NanoHTTPD.Response {
        val start = System.currentTimeMillis()
        val pathParam = session.parameters["path"]?.firstOrNull()
        val path = pathParam?.toString()?: return newFixedLengthResponse(
        Response.Status.BAD_REQUEST,
        "text/plain",
        "Missing path parameter"
        )

        val isThumbnailsParam = session.parameters["thumbnail"]?.firstOrNull()
        val isThumbnail = isThumbnailsParam?.toBoolean()?:false

        val requestedFile = File( rootDir, path);
        val mimeType = getMimeType(requestedFile.extension)

        return if (requestedFile.exists() && requestedFile.isFile) {
            val ext = requestedFile.extension.lowercase()
            val supportedTypes = mapOf(
                "jpg" to "image/jpeg",
                "jpeg" to "image/jpeg",
                "jfif" to "image/jpeg",
                "png" to "image/png",
                "gif" to "image/gif",
                "webp" to "image/webp",
                "avif" to "image/avif",
                "svg" to "image/svg+xml",
                "bmp" to "image/bmp",
                "ico" to "image/x-icon"
            )

            if(supportedTypes.containsKey(ext)){
                newFixedLengthResponse(
                    Response.Status.OK,
                    mimeType,
                    requestedFile.inputStream(),
                    requestedFile.length()
                )
            }else if (ext == "heic" || ext == "heif") {

                try {

                    val base64String =if(isThumbnail) heicToPngBase64(requestedFile, 100, 100, 40) else heicToPngBase64(requestedFile)
                    val dataUrl = "data:image/jpeg;base64,$base64String"
                    val end = System.currentTimeMillis()
                    println("serveImage Execution time: ${end - start} ms")
                    newFixedLengthResponse(
                        Response.Status.OK,
                        "text/plain",
                        dataUrl
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    newFixedLengthResponse(
                        Response.Status.INTERNAL_ERROR,
                        "text/plain",
                        "Error converting image"
                    )
                }
            } else newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "text/plain",
                "Error converting image"
            )

            }
        else {
            return newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                "text/plain",
                "File not found",
            )
        }
    }

    fun handleCreate(session: NanoHTTPD.IHTTPSession, rootDir: File): Response {
        session.parseBody(mutableMapOf())

        val folderPath = session.parameters["folder"]?.firstOrNull()

        if (folderPath != null) {
            val newFile = File(rootDir, folderPath)

            return try {
                val result = newFile.mkdirs()
                if(result) newFixedLengthResponse("Created: ${newFile.absolutePath}")
                else newFixedLengthResponse(NanoHTTPD.Response.Status.CONFLICT, "text/plain","Failed to create: ${newFile.absolutePath}")
            } catch (e: Exception) {
                newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Error: ${e.message}")
            }
        }

        return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Missing 'path' param")
    }

    fun handleDelete(session: NanoHTTPD.IHTTPSession, rootDir: File): NanoHTTPD.Response {
        session.parseBody(mutableMapOf())

        val filePath = session.parameters["path"]?.firstOrNull()

        if (filePath != null) {
            val file = File(rootDir, filePath)
            return try {
                if (file.exists()) {
                    if (file.isDirectory) file.deleteRecursively() else file.delete()
                    newFixedLengthResponse("Deleted: ${file.absolutePath}")
                } else {
                    newFixedLengthResponse("File not found")
                }
            } catch (e: Exception) {
                newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Error: ${e.message}")
            }
        }

        return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Missing 'path' param")
    }

    fun handleRename(session: NanoHTTPD.IHTTPSession, rootDir: File): NanoHTTPD.Response {
        session.parseBody(mutableMapOf())

        val oldPath = session.parameters["oldPath"]?.firstOrNull()
        val newPath = session.parameters["newPath"]?.firstOrNull()

        if (oldPath != null && newPath != null) {
            val oldFile = File(rootDir, oldPath)
            val newFile = File(rootDir, newPath)

            return try {
                if (oldFile.exists()) {
                    oldFile.renameTo(newFile)
                    newFixedLengthResponse("Renamed to: ${newFile.absolutePath}")
                } else {
                    newFixedLengthResponse("Original file not found")
                }
            } catch (e: Exception) {
                newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Error: ${e.message}")
            }
        }

        return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Missing parameters")
    }

    private fun handleUpload(session: NanoHTTPD.IHTTPSession, rootDir: File): NanoHTTPD.Response {
        return try {
            val files = HashMap<String, String>()
            session.parseBody(files)

            val fileField = "file" // Tên field từ form-data
            val tmpFilePath = files[fileField]
            if (tmpFilePath == null) {
                return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "File field not found")
            }

            val fileName = session.parameters["filename"]?.firstOrNull() ?: File(tmpFilePath).name
            val destFile = File(rootDir, fileName)

            File(tmpFilePath).copyTo(destFile, overwrite = true)

            newFixedLengthResponse(NanoHTTPD.Response.Status.OK,"application/json", """{"status":"Upload successful: ${destFile.name}"}""")
        } catch (e: Exception) {
            e.printStackTrace()
            newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Upload failed: ${e.message}")
        }
    }


}

// Extension to start/stop service
fun ComponentActivity.startHttpServer(port: Int) {
    val serviceIntent = Intent(this, HttpFileServerService::class.java)
    serviceIntent.putExtra("PORT", port)
    startService(serviceIntent)
}

fun ComponentActivity.stopHttpServer() {
    stopService(Intent(this, HttpFileServerService::class.java))
}
