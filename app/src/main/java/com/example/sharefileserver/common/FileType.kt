package com.example.sharefileserver.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.sharefileserver.service.TAG
import fi.iki.elonen.NanoHTTPD.Response
import fi.iki.elonen.NanoHTTPD.getMimeTypeForFile
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun serveFileList(wwwDir: File, currenPath:String,page: Int = 1, itemsPerPage: Int =10): Response {
    val startTime = System.currentTimeMillis()
    val htmlContent = generateFileListHTML(wwwDir, currenPath, page, itemsPerPage)
    val endTime = System.currentTimeMillis()
    val elapsed = endTime - startTime

    Log.d("FileListTiming", "generateFileListHTML executed in $elapsed ms")
    return newFixedLengthResponse(
        Response.Status.OK,
        "text/html",
        htmlContent
    )
}

fun serveFile(requestedFile: File): Response {
    return if (requestedFile.exists() && requestedFile.isFile) {
        val mimeType = getMimeTypeForFile(requestedFile.extension)
        newFixedLengthResponse(
            Response.Status.OK,
            mimeType,
            requestedFile.readText()
        )
    } else {
        newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            "text/html",
            "<h1>404 - File Not Found</h1><a href='/'>Back to file list</a>"
        )
    }
}

fun serveFile2(requestedFile: File): Response {
    return if (requestedFile.exists() && requestedFile.isFile) {
        val mimeType = getMimeType(requestedFile.extension)

        // For binary files (images, videos), use FileInputStream
        if (isBinaryFile(requestedFile.extension)) {
            newFixedLengthResponse(
                Response.Status.OK,
                mimeType,
                requestedFile.inputStream(),
                requestedFile.length()
            )
        } else {
            // For text files, read as text
            newFixedLengthResponse(
                Response.Status.OK,
                mimeType,
                requestedFile.readText()
            )
        }
    } else {
        newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            "text/html",
            """
            <html>
            <head>
                <title>404 - File Not Found</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                    .error { color: #dc3545; font-size: 2em; margin-bottom: 20px; }
                    .message { font-size: 1.2em; margin-bottom: 30px; }
                    .back-btn { 
                        background: #007bff; color: white; padding: 12px 24px; 
                        text-decoration: none; border-radius: 5px; 
                        display: inline-block; transition: all 0.3s ease;
                    }
                    .back-btn:hover { background: #0056b3; transform: translateY(-2px); }
                </style>
            </head>
            <body>
                <div class="error">❌</div>
                <h1>404 - File Not Found</h1>
                <p class="message">The requested file could not be found.</p>
                <a href="/" class="back-btn">🏠 Back to File List</a>
            </body>
            </html>
            """.trimIndent()
        )
    }
}


fun getMimeType(extension: String): String {
    return when (extension.lowercase()) {
        // Images
        "jpg", "jpeg", "jfif" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "webp" -> "image/webp"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        "tiff", "tif" -> "image/tiff"
        "svg" -> "image/svg+xml"
        "ico" -> "image/x-icon"

        // Videos
        "mp4" -> "video/mp4"
        "avi" -> "video/x-msvideo"
        "mov" -> "video/quicktime"
        "wmv" -> "video/x-ms-wmv"
        "flv" -> "video/x-flv"
        "webm" -> "video/webm"
        "mkv" -> "video/x-matroska"
        "3gp" -> "video/3gpp"
        "ogv" -> "video/ogg"
        "m4v" -> "video/x-m4v"

        // Audio
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "ogg" -> "audio/ogg"
        "aac" -> "audio/aac"
        "flac" -> "audio/flac"
        "m4a" -> "audio/mp4"
        "wma" -> "audio/x-ms-wma"

        // Text files
        "txt" -> "text/plain"
        "html", "htm" -> "text/html"
        "css" -> "text/css"
        "js" -> "application/javascript"
        "json" -> "application/json"
        "xml" -> "application/xml"
        "csv" -> "text/csv"

        // Documents
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "xls" -> "application/vnd.ms-excel"
        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        "ppt" -> "application/vnd.ms-powerpoint"
        "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"

        // Archives
        "zip" -> "application/zip"
        "rar" -> "application/x-rar-compressed"
        "7z" -> "application/x-7z-compressed"
        "tar" -> "application/x-tar"
        "gz" -> "application/gzip"

        else -> "application/octet-stream"
    }
}

fun isImageFile(extension: String): Boolean {
    val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif", "tiff", "svg", "ico","jfif")
    return imageExtensions.contains(extension.lowercase())
}

fun isVideoFile(extension: String): Boolean {
    val videoExtensions = setOf("mp4", "avi", "mov", "wmv", "flv", "webm", "mkv", "3gp", "ogv", "m4v")
    return videoExtensions.contains(extension.lowercase())
}

// Helper function to determine if file is binary
private fun isBinaryFile(extension: String): Boolean {
    val binaryExtensions = setOf(
        // Images
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif", "tiff", "svg", "ico", "jfif",
        // Videos
        "mp4", "avi", "mov", "wmv", "flv", "webm", "mkv", "3gp", "ogv", "m4v",
        // Audio
        "mp3", "wav", "ogg", "aac", "flac", "m4a", "wma",
        // Archives
        "zip", "rar", "7z", "tar", "gz", "bz2",
        // Executables
        "exe", "dll", "so", "dylib",
        // Documents
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    )
    return binaryExtensions.contains(extension.lowercase())
}

private fun generateFileListHTML(wwwDir: File, currentPath: String, page: Int = 1, itemsPerPage: Int =10 ): String {

    val files = wwwDir.listFiles() ?: arrayOf()



    val totalPages = (files.size + itemsPerPage - 1) / itemsPerPage // Ceiling division
    val startIndex = (page - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, files.size)

    val fileList = StringBuilder()

    // Add parent directory link if not at root
    if (wwwDir != Environment.getExternalStorageDirectory()) {
        val parentPath = wwwDir.parentFile?.absolutePath?.removePrefix(Environment.getExternalStorageDirectory().absolutePath)?.trimStart('/')
        fileList.append("""
        <tr onclick="window.location.href='/${parentPath ?: ""}?page=1&size=$itemsPerPage '" style="cursor: pointer;">
            <td>⬆️</td>
            <td>..</td>
            <td>Parent</td>
            <td></td>
            <td></td>
        </tr>
    """.trimIndent())
    }
    val startTime = System.currentTimeMillis()
    // Sort files: directories first, then files, both alphabetically
    //val sortedFiles = files.sortedWith(compareBy<File> { !it.isDirectory() }.thenBy { it.name.lowercase() })
    val paginatedFiles = files.slice(startIndex until endIndex)

    val endTime = System.currentTimeMillis()
    val elapsed = endTime - startTime

    Log.d("FileListTiming", "generateFileListHTML ${wwwDir} size ${files.size} executed in $elapsed ms")

    Log.d(TAG, "Page info page:${page} start:${startIndex} end:${endIndex} size: ${paginatedFiles.size}" )
    for ((index,file) in paginatedFiles.withIndex()) {
        val link = "${currentPath.trimEnd('/')}/${file.name}"
        val size = if (file.isDirectory()) "" else " (${formatFileSize(file.length())})"

        val iconHtml = if (file.isDirectory()) "📁" else if (isImageFile(file.extension)) {
            val base64Image = getThumbnailIcon(file)
            """<img src="$base64Image" class="thumb" style="height:32px;width:auto;vertical-align:middle;" />"""
        } else {
            "<span class=\"file-icon\">${getFileIcon(file.extension)}</span>"
        }


        val previousLink =if(index -1 > 0) "${currentPath.trimEnd('/')}/${paginatedFiles[index-1].name}"
        else ""
        val nextLink =if(index + 1 < paginatedFiles.size) "${currentPath.trimEnd('/')}/${paginatedFiles[index + 1].name}"
        else ""

        val currentObject = "{ filename: ${file.name}, type: ${if (file.isDirectory()) "Directory" else file.extension.uppercase().ifEmpty { "File" }},size: ${size}" +
                "date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(file.lastModified()))}}"

        fileList.append("""
        <tr onclick="${
            if (isImageFile(file.extension) || isVideoFile(file.extension))
                "openFileModal($currentObject,'$previousLink','$nextLink')"
            else
                "window.location.href='$link?page=1&size=$itemsPerPage'"
        }"  style="cursor: pointer; user-select: none;">
            <td> $iconHtml</td>
            <td> <span class="file-name"> ${file.name} </span></td>
            <td> <span class="file-type">${if (file.isDirectory()) "Directory" else file.extension.uppercase().ifEmpty { "File" }} </span></td>
            <td class="file-size"> ${size.ifEmpty { "---" }}</td>
            <td class="file-date"> ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(file.lastModified()))}</td>
        </tr>
    """.trimIndent())
    }

    val pagesToShow = mutableSetOf<Int>()

    // Always show current page, and pages around it
    pagesToShow.add(page)
    if (page > 1) pagesToShow.add(page - 1)
    if (page < totalPages) pagesToShow.add(page + 1)

    // Show page 1 if far from current
    if(!pagesToShow.contains(1)){
        pagesToShow.add(1)
    }

    // Always show last 2 pages
    if (totalPages > 1) {
        pagesToShow.add(totalPages)
        if (totalPages - 1 > 1) pagesToShow.add(totalPages - 1)
    }

    // Sort pages before rendering
    val sortedPages = pagesToShow.toList().sorted()
    // Generate pagination links
    val pagination = StringBuilder()
    if (totalPages > 0) {
        pagination.append("<tr><td colspan=\"5\" style=\"text-align: center; padding: 10px;\"> <div style=\"margin: 10px;display: flex; justify-content: center\">")

        val prevPage = page - 1
        val prevPath = if (currentPath.isEmpty()) "?page=$prevPage&size=$itemsPerPage" else "$currentPath?page=$prevPage&size=$itemsPerPage"
        pagination.append("<a href=\"$prevPath\" style=\"\n" +
                "    display: inline-flex;\n" +
                "    align-items: center;\n" +
                "    padding: 6px 10px;\n" +
                "    margin: 0 5px;\n" +
                "    background-color: #178FFF;\n" +
                "    border: none;\n" +
                "    border-radius: 12px;\n" +
                "    color: white;\n" +
                "    text-decoration: none;\n" +
                "    font-size: 14px;\n" +
                "    ${if(page<=1) "background-color: gray; pointer-events: none;  cursor: not-allowed;  text-decoration: none;" else ""}\n" +
                "\">\n" +
                "    &#8592; Previous\n" +
                "</a>")

        for (i in sortedPages) {
            val pagePath = if (currentPath.isEmpty()) "?page=$i&size=$itemsPerPage" else "$currentPath?page=$i&size=$itemsPerPage"
            pagination.append("<a href='$pagePath' style='display: inline-block; width: 32px; height: 32px; " +
                    "margin: 0 1px; border: 1px solid #ccc; border-radius: 50%; color: white; background-color:  ${if (i != page) "#178FFF" else " #248f24"}; " +
                    "text-align: center; line-height: 30px; text-decoration: none; font-weight: bold; font-family: sans-serif; cursor: pointer;'>$i</a>")
        }

        val nextPage = page + 1
        val nextPath = if (currentPath.isEmpty()) "?page=$nextPage&size=$itemsPerPage" else "$currentPath?page=$nextPage&size=$itemsPerPage&size=$itemsPerPage"
        pagination.append("<a href=\"$nextPath\" style=\"\n" +
                "    display: inline-flex;\n" +
                "    align-items: center;\n" +
                "    padding: 6px 10px;\n" +
                "    margin: 0 5px;\n" +
                "    background-color: #178FFF;\n" +
                "    border: 1px solid #ccc;\n" +
                "    border-radius: 12px;\n" +
                "    color: white;\n" +
                "    text-decoration: none;\n" +
                "    font-size: 14px;\n" +
                "    ${if(page >= totalPages) "background-color: gray; pointer-events: none;  cursor: not-allowed;  text-decoration: none;" else ""}\n" +
                "\">\n" +
                "    Next &#8594;\n" +
                "</a>")



        pagination.append("<form id=\"pageSizeForm\" method=\"GET\" action=\"\">\n" +

                "            <select name=\"size\" id=\"size\" onchange=\"document.getElementById('pageSizeForm').submit()\" style=\"background-color: #248f24;  color: white; border-radius:10px; padding: 6px; margin-left: 5px;\">\n" +
                "                ${
                    listOf(5, 10, 20, 50, 100).joinToString("") { sizeOption ->
                        val selected = if (sizeOption == itemsPerPage) "selected" else ""
                        "<option value='$sizeOption' $selected>$sizeOption</option>"
                    }
                }\n" +
                "            </select>\n" +
                "            <input type=\"hidden\" name=\"page\" value=\"$page\" />\n" +
                "        </form>")

        pagination.append("</div></td></tr>")
    }

    return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Server - Directory Listing</title>
    ${getIndexStyle()}
    
    ${getFileModalStyle()}
</head>
<body>
    <div class="container">
        <div class="header">
            <h3>📁 File Server</h1>
            <p>Browse and access your stored files</p>
        </div>
        
        <div class="stats">
            <div class="stat-item">
                <div class="stat-number">${files.size}</div>
                <div class="stat-label">Total Items</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">${files.count { it.isDirectory() }}</div>
                <div class="stat-label">Directories</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">${files.count { it.isFile() }}</div>
                <div class="stat-label">Files</div>
            </div>
        </div>
        
        ${if (files.isEmpty()) """
        <div class="no-files">
            <div class="icon">📂</div>
            <h3>No files found</h3>
            <p>Upload some files to get started!</p>
        </div>
        """ else """
            
        """}
        
        $pagination
        <table class="file-table">
            <thead>
                <tr>
                    <th>Type</th>
                    <th>Name</th>
                    <th>Format</th>
                    <th>Size</th>
                    <th>Modified</th>
                </tr>
            </thead>
            <tbody>
                $fileList
            </tbody>
        </table>
        
        <div class="footer">
            <p>HTTP File Server • Running on port 8080</p>
        </div>
    </div>
    
    ${getFileModalHtml()}
    ${getModalScript()}
</body>
</html>
    """.trimIndent()

    // JavaScript for modal functionality (add this to your HTML template)

}

private fun getFileIcon(extension: String): String {
    return when (extension.lowercase()) {
        "txt" -> "📄"
        "pdf" -> "📕"
        "doc", "docx" -> "📘"
        "xls", "xlsx" -> "📗"
        "ppt", "pptx" -> "📙"
        "jpg", "jpeg", "png", "gif", "bmp" -> "🖼️"
        "mp4", "avi", "mov", "mkv" -> "🎬"
        "mp3", "wav", "flac", "aac" -> "🎵"
        "zip", "rar", "7z", "tar" -> "🗜️"
        "html", "htm" -> "🌐"
        "css" -> "🎨"
        "js" -> "⚡"
        "json" -> "📋"
        "xml" -> "📰"
        "py" -> "🐍"
        "java" -> "☕"
        "kt" -> "🔷"
        "cpp", "c" -> "⚙️"
        else -> "📄"
    }
}
private fun getBase64ForSvg(file: File): String {
    val svgBytes = file.readBytes()
    val base64 = Base64.encodeToString(svgBytes, Base64.NO_WRAP)
    return "data:image/svg+xml;base64,$base64"
}
private fun getThumbnailIcon(file: File): String? {
    return try {
        // svg
        if(file.extension == "svg") return getBase64ForSvg(file)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        val scaleFactor = maxOf(1, minOf(options.outWidth / 64, options.outHeight / 64))

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = scaleFactor
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)

        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
        val byteArray = outputStream.toByteArray()

        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/png;base64,$base64String"
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun heicToPngBase64(file: File, maxWidth: Int = 800, maxHeight: Int = 800, quality: Int= 80): String {
    // Load scaled bitmap
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, this)

        val scale = maxOf(1,
            minOf(outWidth / maxWidth, outHeight / maxHeight)
        )
        inJustDecodeBounds = false
        inSampleSize = scale
    }
    val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
        ?: throw IllegalArgumentException("Cannot decode HEIC")

    // Compress to JPEG (smaller) instead of PNG
    val outStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream) // 80% quality

    val jpegBytes = outStream.toByteArray()
    return Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
}

private fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    return if (size == size.toInt().toDouble()) {
        "${size.toInt()} ${units[unitIndex]}"
    } else {
        "%.1f ${units[unitIndex]}".format(size)
    }
}

private fun encodeFileName(filename: String): String {
    return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
        .replace("+", "%20") // Replace + with %20 for spaces
}

fun getModalScript(): String{
    // JavaScript for modal functionality (add this to your HTML template)
    val fileNameText ="\${filename}".trimIndent()
    val encodeURIComponentString ="\${window.location.origin}/\${encodeFileName(filename)}"
    val modalScript = """
<script>
// File type definitions
const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'heic', 'heif', 'tiff', 'svg', 'jfif', 'jpe' ];
const videoExtensions = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm', 'mkv', '3gp', 'ogv'];

function getFileExtension(filename) {
    return filename.split('.').pop().toLowerCase();
}

function isImageFile(filename) {
    const ext = getFileExtension(filename);
    return imageExtensions.includes(ext);
}

function isVideoFile(filename) {
    const ext = getFileExtension(filename);
    return videoExtensions.includes(ext);
}

function encodeFileName(filename) {
    return encodeURIComponent(filename).replace(/\+/g, '%20');
}
let previousFilePath= "";
let nextFilePath = "";
function openFileModal(filename, previousFile, nextFile) {
    const modal = document.getElementById('fileModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalIcon = document.getElementById('modalIcon');
    const modalBody = document.getElementById('modalBody');
    const fileType = document.getElementById('fileType');
    const fileSize = document.getElementById('fileSize');
    const fileDate = document.getElementById('fileDate');
    
     try {
        // Lấy metadata từ server
        const response = await fetch(`${encodeURIComponentString}`, { method: 'HEAD' });

console.log(response);
        if (!response.ok) {
            throw new Error('Failed to fetch file info');
        }

        const type = response.headers.get('Content-Type') || 'Unknown';
        const size = formatBytes(parseInt(response.headers.get('Content-Length')) || 0);
        const date = response.headers.get('Last-Modified') || 'Unknown';

        
    } catch (err) {
        showError('Could not load file metadata');
    }


    // Set file info
    modalTitle.textContent = filename;
    fileType.textContent = type;
        fileSize.textContent = size;
        fileDate.textContent = date;
    previousFilePath =previousFile;
    nextFilePath = nextFile;

    // Show loading
    modalBody.innerHTML = `
        <div class="loading">
            <div class="spinner"></div>
            <p>Loading ${fileNameText}...</p>
        </div>
    `;

    // Show modal
    modal.classList.add('show');

    // Check if it's an image or video file
    if (isImageFile(filename)) {
        modalIcon.textContent = '🖼️';
        loadImage(filename);
    } else if (isVideoFile(filename)) {
        modalIcon.textContent = '🎬';
        loadVideo(filename);
    } else {
        modalIcon.textContent = '📄';
        showError('Preview not available for this file type');
    }
}

function loadImage(filename) {
    const modalBody = document.getElementById('modalBody');
    
    // Create image element
    const img = document.createElement('img');
    img.className = 'image-viewer';
    img.alt = filename;
    
    img.onload = function() {
        modalBody.innerHTML = '';
        modalBody.appendChild(img);
        
        // Add image controls
        const controls = document.createElement('div');
        controls.className = 'image-controls';
        controls.innerHTML = `
        
            <button class="control-btn" onclick="showPreviousFile()" title="Fullscreen">
                ⬅️
            </button>
            
            <button class="control-btn" onclick="downloadFile('${fileNameText}')" title="Download">
                📥
            </button>
            <button class="control-btn" onclick="toggleFullscreen()" title="Fullscreen">
                🔍
            </button>
             <button class="control-btn" onclick="showNextFile()" title="Fullscreen">
                ➡️
            </button>
        `;
        modalBody.appendChild(controls);
    };
    
    img.onerror = function() {
        showError('Failed to load image');
    };
    
    // Use your server endpoint to serve the file
    img.src = `${encodeURIComponentString}`;
}

function loadVideo(filename) {
    const modalBody = document.getElementById('modalBody');
    
    // Create video element
    const video = document.createElement('video');
    video.className = 'video-player';
    video.controls = true;
    video.autoplay = false;
    
    video.onloadedmetadata = function() {
        modalBody.innerHTML = '';
        modalBody.appendChild(video);
        video.play();
    };
    
    video.onerror = function() {
        showError('Failed to load video');
    };
    
    // Use your server endpoint to serve the file
    video.src = `${encodeURIComponentString}`;
}

function showError(message) {
    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = `
        <div class="error-message">
            <div class="error-icon">❌</div>
            <h3>Error</h3>
            <p>$ {message}</p>
        </div>
    `;
}

function closeModal() {
    const modal = document.getElementById('fileModal');
    modal.classList.remove('show');
    
    // Stop any playing video
    const video = modal.querySelector('video');
    if (video) {
        video.pause();
        video.currentTime = 0;
    }
}

function downloadFile(filename) {
    // Trigger download
    const link = document.createElement('a');
    link.href = `/file/${encodeURIComponentString}`;
    link.download = filename;
    link.click();
}

function toggleFullscreen() {
    const modal = document.getElementById('fileModal');
    if (!document.fullscreenElement) {
        modal.requestFullscreen();
    } else {
        document.exitFullscreen();
    }
}

function showPreviousFile(){

}

function showNextFile(){

}

// Close modal when clicking outside
document.getElementById('fileModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});
</script>
""".trimIndent()





    return modalScript;
}

// CSS styles for modal (add this to your HTML template)
fun getFileModalStyle(): String{
    val modalStyles = """
<style>
/* Modal Styles */
.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.9);
    animation: fadeIn 0.3s ease;
}

.modal.show {
    display: flex;
    align-items: center;
    justify-content: center;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

@keyframes slideIn {
    from { transform: scale(0.8) translateY(20px); opacity: 0; }
    to { transform: scale(1) translateY(0); opacity: 1; }
}

.modal-content {
    position: relative;
    width: 800px;
    height: 600px;
    background: white;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.5);
    animation: slideIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-header {
    background: linear-gradient(135deg, #2196F3, #21CBF3);
    color: white;
    padding: 5px 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-title {
    font-size: 1.2em;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 10px;
}

.close-btn {
    background: none;
    border: none;
    color: red;
    font-size: 2em;
    cursor: pointer;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: rotate(90deg);
}

.modal-body {
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 300px;
    background: #f8f9fa;
    position: relative;
    height: 70%;
}

/* Image Viewer */
.image-viewer {
    width: 100%;
    height: auto;
    max-width: 800px;
    max-height: 600px;
    object-fit: contain;
    border-radius: 0 0 15px 15px;
}

/* Video Player */
.video-player {
    width: 100%;
    height: auto;
    max-width: 800px;
    max-height: 600px;
    border-radius: 0 0 15px 15px;
}

/* Loading Spinner */
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 50px;
    color: #666;
}

.spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #2196F3;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 20px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* Error Message */
.error-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 50px;
    color: #dc3545;
}

.error-icon {
    font-size: 3em;
    margin-bottom: 20px;
}

/* Image Controls */
.image-controls {
    position: absolute;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(0, 0, 0, 0.7);
    border-radius: 25px;
    padding: 10px 20px;
    display: flex;
    gap: 15px;
}

.control-btn {
    background: none;
    border: none;
    color: white;
    font-size: 1.2em;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 50%;
    transition: all 0.3s ease;
}

.control-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.1);
}

/* File Info */
.file-info {
    background: white;
    padding: 5px 25px;
    border-top: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 15px;
    font-size: 0.9em;
    color: #666;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.info-label {
    font-weight: 600;
    color: #333;
}

/* Row animations */
.file-row {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
}

.file-row:hover {
    background: linear-gradient(135deg, #f8f9ff, #e3f2fd);
    transform: translateY(-2px) scale(1.01);
    box-shadow: 0 5px 15px rgba(33, 150, 243, 0.15);
}

.file-icon {
    font-size: 1.4em;
    transition: all 0.3s ease;
    display: inline-block;
}

.file-row:hover .file-icon {
    transform: scale(1.1) rotate(5deg);
}

.file-name {
    font-weight: 500;
    transition: all 0.3s ease;
}

.file-row:hover .file-name {
    color: #2196F3;
    font-weight: 600;
}

.file-type {
    background: #e9ecef;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.8em;
    font-weight: 500;
    color: #495057;
    transition: all 0.3s ease;
}

.file-row:hover .file-type {
    background: #2196F3;
    color: white;
    transform: scale(1.05);
}

/* Responsive */
@media (max-width: 768px) {
    .modal-content {
        max-width: 95%;
        max-height: 95%;
        margin: 10px;
    }


    .modal-title {
        font-size: 1.1em;
    }

    .file-info {
        padding: 5px 20px;
        flex-direction: column;
        gap: 10px;
    }
}
</style>
""".trimIndent()
    return modalStyles
}

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

fun getFileModalHtml(): String{
    // HTML for modal (add this to your HTML template)
    val modalHtml = """
<!-- File Modal -->
<div id="fileModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <div class="modal-title">
                <span id="modalIcon"></span>
                <span id="modalTitle"></span>
            </div>
            <button class="close-btn" onclick="closeModal()">&times;</button>
        </div>
        <div class="modal-body" id="modalBody">
            <div class="loading">
                <div class="spinner"></div>
                <p>Loading file...</p>
            </div>
        </div>
        <div class="file-info" id="fileInfo">
            <div class="info-item">
                <span class="info-label">Type:</span>
                <span id="fileType"></span>
            </div>
            <div class="info-item">
                <span class="info-label">Size:</span>
                <span id="fileSize"></span>
            </div>
            <div class="info-item">
                <span class="info-label">Modified:</span>
                <span id="fileDate"></span>
            </div>
        </div>
    </div>
</div>
""".trimIndent()
    return modalHtml
}