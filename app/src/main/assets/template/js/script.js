

        function positionPanel() {
            const container = document.getElementById("mainContainer");
            const sidePanel = document.getElementById("sidePanel");

            const rect = container.getBoundingClientRect();
            sidePanel.style.right = rect.left + "px";
        }

        window.addEventListener("resize", positionPanel);
        window.addEventListener("load", positionPanel);

        async function callLoadFiles() {
        const directoryPath = window.location.pathname;
        await loadFiles(directoryPath);

        const dropTable = document.getElementById("dropTable");

        const uploadProgressContainer = document.getElementById('uploadProgressContainer');
        const progressTableBody = document.getElementById('progressTableBody');
        const toggleBtn = document.getElementById('toggleBtn');
        const headerTitle = document.getElementById('headerTitle');

        dropTable.addEventListener("dragover", function(e) {
            e.preventDefault();
            dropTable.style.background = "red";
        });

        
        dropTable.addEventListener("drop", function(e) {
            e.preventDefault();
            dropTable.style.background = "";
            const files = e.dataTransfer.files;
            uploadProgressContainer.style.display = 'block';
            uploadFiles(files);
            });
        }

    let fileCounter = 0;
    function uploadFiles(files) {
        uploadProgressContainer.style.display = "block";
        let uploadTasks = [];
            for (let file of files) {
                fileCounter++;
                
                 // Add row to progress table
                const row = addProgressRow(fileCounter, file.name);

                let formData = new FormData();
                formData.append("file", file);

                 let task = uploadFileWithProgress(file, row) 
                .then(data => {
                    updateProgressRow(row, 100, 'completed');
                })
                .catch(err => {
                    console.error(err);
                });

                 uploadTasks.push(task);
            }

            Promise.all(uploadTasks).then(() => {
            
                    loadFiles(window.location.pathname);
                });
    }

         // Uppoad region
     document.getElementById('uploadButton').addEventListener('click', function () {
        document.getElementById('fileInput').click();
    });


    document.getElementById('newFolderButton').addEventListener('click', function () {
        showModal();
    });

    function showModal() {
        showSuccess(false);
        const body = document.querySelector('.create-folder-body');
        body.in
            modalOverlay.classList.add('active');
            setTimeout(() => {
                folderNameInput.focus();
            }, 300);
        }
     // Hide modal
    function hideModal() {
        modalOverlay.classList.remove('active');
        setTimeout(() => {
            resetModal();
        }, 300);
    }

    // Reset modal state
    function resetModal() {
        createFolderModal.classList.remove('loading');
        folderNameInput.value = '';
        headerTitle.textContent = 'Create new folder';
    }

        // Event listeners
        createFolderCancel.addEventListener('click', hideModal);

        createFolderSubmit.addEventListener('click', async function() {
            const folderName = folderNameInput.value.trim();
            
            if (!folderName) {
                folderNameInput.focus();
                folderNameInput.style.borderColor = '#f44336';
                setTimeout(() => {
                    folderNameInput.style.borderColor = '#e1e8ed';
                }, 2000);
                return;
            }

            // Show loading state
            createFolderModal.classList.add('loading');
            
            try {
                // Simulate API call

                await fetch(`${window.location.origin}/api/files/create`,{
                    method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ folder: decodeURIComponent( window.location.pathname +"/"+ folderName) })
                });
                
                // Show success
                showSuccess();
                await loadFiles(window.location.pathname);
                
                setTimeout(() => {
                    hideModal();
                }, 1000);
                
            } catch (error) {
                console.error('Error creating folder:', error);
                createFolderModal.classList.remove('loading');
            }
        });

        // Success animation
        function showSuccess(isShow =true) {
            const success = document.getElementById('success-noti');
            const input = document.getElementById('create-folder-input');

            if (!isShow) {
                success.style.display= "none";
                input.style.display ="block";
            }else{
                success.style.display= "block";
                input.style.display ="none";
            }
        }

        // Close on overlay click
        modalOverlay.addEventListener('click', function(e) {
            if (e.target === modalOverlay) {
                hideModal();
            }
        });

    document.getElementById('fileInput').addEventListener('change', function (event) {
        const files = event.target.files;
        if (files.length > 0) {
            uploadFiles(files);
        }
    });

    function addProgressRow(stt, fileName) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${stt}</td>
                <td title="${fileName}">${fileName.length > 20 ? fileName.substring(0, 20) + '...' : fileName}</td>
                <td>
                    <div class="status-uploading">0%</div>
                    <div class="progress-bar">
                        <div class="progress-fill"></div>
                    </div>
                </td>
            `;
            progressTableBody.appendChild(row);
            return row;
        }

        function updateProgressRow(row, percentage, status = 'uploading') {
            const statusDiv = row.querySelector('td:last-child > div:first-child');
            const progressFill = row.querySelector('.progress-fill');
            
            statusDiv.textContent = percentage + '%';
            progressFill.style.width = percentage + '%';
            
            // Update status class
            statusDiv.className = `status-${status}`;
            
            if (status === 'completed') {
                statusDiv.textContent = 'Hoàn thành 100%';
                progressFill.style.background = '#4caf50';
            } else if (status === 'error') {
                statusDiv.textContent = 'Lỗi';
                progressFill.style.background = '#f44336';
            }
        }


        function uploadFileWithProgress(file, row) { 
                    return new Promise((resolve, reject) => { 
                        let xhr = new XMLHttpRequest(); 
                        let url = window.location.origin+"/api/files/upload?filename=" + window.location.pathname + "/" + file.name; 
        
                        xhr.open("POST", url, true); 
        
                        // Track upload progress 
                        xhr.upload.onprogress = function (event) { 
                            if (event.lengthComputable) { 
                                let percentComplete = ((event.loaded / event.total) * 100).toFixed(0); 
                                
                                updateProgressRow(row, percentComplete, 'uploading');
                            } 
                        }; 
        
                        xhr.onload = function () { 
                            if (xhr.status >= 200 && xhr.status < 300) { 
                                resolve(JSON.parse(xhr.responseText)); 
                            } else { 
                                reject(new Error("Upload failed: " + xhr.status)); 
                            } 
                        }; 
        
                        xhr.onerror = function () { 
                            reject(new Error("Network error")); 
                        }; 
        
                        let formData = new FormData(); 
                        formData.append("file", file); 
                        xhr.send(formData); 
                    });
                }

        let isMinimized = false;
        function toggleProgressTable() {
                    isMinimized = !isMinimized;
                    
                    if (isMinimized) {
                        uploadProgressContainer.style.display = "none";
                        
                    } else {
                        uploadProgressContainer.style.display = "block";
                    }
                }

        let toolBarMinimized = false;
        function toggleToolBar() {
            let toolBar = document.getElementById("toolBar");
            let toggleToolBarButton = document.getElementById("toggleToolBarButton");
            let icon = document.querySelector("#toggleToolBarButton i"); 
            if (toolBarMinimized) {
                toolBar.style.display = "none"
                icon.classList.remove("fa-minus");
                icon.classList.add("fa-plus");
                
                toolBarMinimized = false;
                
            }else {
                toolBar.style.display = "flex"

                icon.classList.remove("fa-plus");
                icon.classList.add("fa-minus");
                
                toggleToolBarButton.style.backgroundColor = "#ff4d4d";
                toolBarMinimized = true;
            }
        }
                
    document.querySelector('.progress-header').addEventListener('click', function(e) {
            if (e.target.classList.contains('toggle-btn')) {
                return; // Don't toggle if clicking the button directly
            }
            toggleProgressTable();
        });

    window.onload = callLoadFiles;

    (function(history){
        const pushState = history.pushState;
        history.pushState = function(state, title, url){
            const ret = pushState.apply(this, arguments);
            window.dispatchEvent(new Event('locationchange'));
            return ret;
        };
        const replaceState = history.replaceState;
        history.replaceState = function(state, title, url){
            const ret = replaceState.apply(this, arguments);
            window.dispatchEvent(new Event('locationchange'));
            return ret;
        };
    })(window.history);

    window.addEventListener('popstate', () => {
        window.dispatchEvent(new Event('locationchange'));
    });

    window.addEventListener('locationchange', callLoadFiles);

    $(document).ready(function() {
     });

        // File type definitions
        const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'heic', 'heif', 'tiff', 'svg', 'jfif', 'jpe'];
        const unSupportNativeExtensions= ['heic', 'heif'];
        const videoExtensions = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm', 'mkv', '3gp', 'ogv'];

        let listFiles = [];

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

        let currentPage = 1;
        let currentPageSize = 10;;
        let currentPath= "";
        let totalItem = 0;
        let totalPage = 1;

        async function goToPage(pageNumber, pageSize) {
            currentPage =pageNumber;
            currentPageSize = pageSize;
            await loadFiles(currentPath);
            
        }
        function generateRouter() {
            const router = document.getElementById('router');
            const pathParts = window.location.pathname.split('/').filter(p => p);

            const home = document.createElement("a");
            home.href ="/";
            home.innerHTML =`<i class="fa-solid fa-home" title="Home"></i>`;

            const sep = document.createElement('span');
            sep.textContent = '/';
            sep.className = 'separator';
            router.appendChild(home)
            router.appendChild(sep);



            let currentPath = '';
            pathParts.forEach((part, index) => {
                currentPath += '/' + part;
                const link = document.createElement('a');
                link.href = currentPath;
                link.textContent = decodeURIComponent(part);
                router.appendChild(link);

                if (index < pathParts.length - 1) {
                    const sep = document.createElement('span');
                    sep.textContent = '/';
                    sep.className = 'separator';
                    router.appendChild(sep);
                }
            });
        }

        
        async function loadFiles(path = "/") {
            generateRouter();
            const tbody = document.getElementById("fileTableBody");
            let seconds = 0;
            tbody.innerHTML = `
           <tr>
                <td colspan="5">
                    <div class="loading">
                        <div class="spinner"></div>
                        <p>Loading ${path}... <span id="loadingTimer">0</span> s</p>
                    </div>
                </td>
            </tr>
                `;

            const timerId = setInterval(() => {
                seconds++;
                const timerEl = document.getElementById("loadingTimer");
                if (timerEl) {
                    timerEl.textContent = seconds;
                }
            }, 1000);


            const decodePath = decodeURI(path);
            const res = await fetch(window.location.origin + "/api/files", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ path: decodePath, page :currentPage, size: currentPageSize })
            });

            const data = await res.json();
            clearInterval(timerId);
            if(!data.files ||data.files.length <=0){
                const noFile = document.getElementById("noFileFound");
                noFile.style.display = "block";
                const filesTable = document.getElementById("filesTable");
                filesTable.style.display = "none";
                return;
            }else {
                const noFile = document.getElementById("noFileFound");
                noFile.style.display = "none";
                const filesTable = document.getElementById("filesTable");
                filesTable.style.display = "table";
            }

            currentPath = path;
            totalItem = Number(data.total);
            totalPage = data.total === 0 ? 1 : Math.ceil(Number(data.total)/ Number(currentPageSize));
            
            getPaginationHtml();

            listFiles = data.files;
            await generateTableHtml();
            

            const totalNumberOfFile = document.getElementById("totalNumberOfFile");
            totalNumberOfFile.innerHTML = data.total
            const numberOfDir = document.getElementById("numberOfDir");
            numberOfDir.innerHTML = data.numberOfDir
            const numberOfFile = document.getElementById("numberOfFile");
            numberOfFile.innerHTML = data.numberOfFile

        }

        async function generateTableHtml(){
            const tbody = document.getElementById("fileTableBody");
            tbody.innerHTML = "";
            const trParent = document.createElement("tr");
            const pathname = window.location.pathname;
            if (pathname.length !== 1) {
                const parentDir = pathname.substring(0, pathname.lastIndexOf("/"));
                trParent.innerHTML =`
                    <tr style="cursor: pointer;">
                        <td ><span class="file-icon">⬆️</span></td>
                        <td><span class="file-name">...</span></td>
                        <td><span class="file-name">Parent</span></td>
                        <td></td>
                        <td></td>
                    </tr>
                `
                trParent.onclick = ()=>{
                    window.location.href=parentDir===""?"/":parentDir;
                }
                tbody.appendChild(trParent);
            }
            

            for (const file of listFiles) {
                const tr = document.createElement("tr");

                const type = file.isDirectory ? "📁" : "📄";
                const name = file.name;
                const ext = file.isDirectory ? "Folder" : name.split('.').pop();
                const size = file.isDirectory ? "-" : formatBytes(file.size);
                const date = new Date(file.lastModified).toLocaleString();

                const baseUrl = window.location.origin + window.location.pathname;
                let imgSrc = `${baseUrl}/${encodeURIComponent(name)}`;
                if(unSupportNativeExtensions.indexOf(ext) > -1){
                    imgSrc = await fetchImage(name);
                }
                const iconHtml = file.isDirectory ? "📁" : isImageFile(ext)?
                `<img src="${imgSrc}" class="thumb" style="height:32px;width:auto;vertical-align:middle;" />`
                :`<span class=\"file-icon\">${getFileIcon(ext)}</span>`
                ;
                tr.innerHTML = `
                <td>${iconHtml}</td>
                <td> <span class="file-name">${name} </span></td>
                <td>  <span class="file-name">${ext} </span></td>
                <td>${size}</td>
                <td>${date}</td>
            `;
                tbody.appendChild(tr);
                const params = new URLSearchParams(window.location.search);

                if (isImageFile(ext) || isVideoFile(ext)) {
                    tr.addEventListener('click', () => openFileModal(name, type, size, date));
                } else {
                    const pathname =window.location.pathname === "/"? "" : window.location.pathname;
                        const href =  pathname+"/"+ name;
                    tr.addEventListener('click', () => {
                        window.location.href = href;
                    });
                }
                
            }
        }

        function getFileIcon(extension) {
            switch (extension.toLowerCase()) {
                case "txt": return "📄";
                case "pdf": return "📕";
                case "doc":
                case "docx": return "📘";
                case "xls":
                case "xlsx": return "📗";
                case "ppt":
                case "pptx": return "📙";
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp": return "🖼️";
                case "mp4":
                case "avi":
                case "mov":
                case "mkv": return "🎬";
                case "mp3":
                case "wav":
                case "flac":
                case "aac": return "🎵";
                case "zip":
                case "rar":
                case "7z":
                case "tar": return "🗜️";
                case "html":
                case "htm": return "🌐";
                case "css": return "🎨";
                case "js": return "⚡";
                case "json": return "📋";
                case "xml": return "📰";
                case "py": return "🐍";
                case "java": return "☕";
                case "kt": return "🔷";
                case "cpp":
                case "c": return "⚙️";
                case "apk": return '<i class="fa-brands fa-android" style="color:#3DDC84; font-size:24px;"></i>'
                default: return "📄";
            }
        }

        function getPagesToShow(page, totalPages) {
            const pagesToShow = new Set();

            // Always show current page, and pages around it
            pagesToShow.add(page);
            if (page > 1) pagesToShow.add(page - 1);
            if (page < totalPages) pagesToShow.add(page + 1);

            // Show page 1 if far from current
            if (!pagesToShow.has(1)) {
                pagesToShow.add(1);
            }

            // Always show last 2 pages
            if (totalPages > 1) {
                pagesToShow.add(totalPages);
                if (totalPages - 1 > 1) pagesToShow.add(totalPages - 1);
            }

            // Convert to sorted array before returning
            return Array.from(pagesToShow).sort((a, b) => a - b);
        }

        function formatBytes(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }

        let currentFileShowOnModal = "";
        function openFileModal(filename, type, size, date) {
            const modal = document.getElementById('fileModal');
            const modalTitle = document.getElementById('modalTitle');
            const modalIcon = document.getElementById('modalIcon');
            const modalBody = document.getElementById('modalBody');
            const fileType = document.getElementById('fileType');
            const fileSize = document.getElementById('fileSize');
            const fileDate = document.getElementById('fileDate');

            // Set file info
            modalTitle.textContent = currentFileShowOnModal = filename;
            fileType.textContent = type;
            fileSize.textContent = size;
            fileDate.textContent = date;

            // Show loading
            modalBody.innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                <p>Loading ${filename}...</p>
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

        async function fetchImage(filename) {
            const apiUrl = `${window.location.origin}/api/files/image?path=${window.location.pathname}/${filename}&thumbnail=true`;
            try {
                const res = await fetch(apiUrl);
                if (!res.ok) throw new Error('Network response was not ok');
                return await res.text(); // base64 string from server
            } catch (err) {
                console.error(err);
                showError('Failed to load image preview', filename);
                return null;
            }
        }

        function loadImage(filename) {
            const modalBody = document.getElementById('modalBody');
            modalBody.innerHTML = 'Loading...';
                
            // Tạo URL gốc
            const baseUrl = window.location.origin + window.location.pathname;
            const fileUrl = `${baseUrl}/${encodeURIComponent(filename)}`;

            // Kiểm tra nếu là JFIF thì dùng fetch blob
            if (filename.toLowerCase().endsWith('.heic')|| filename.toLowerCase().endsWith('.heif')) {
                const apiUrl = window.location.origin + "/"+`api/files/image?path=${window.location.pathname + "/" +filename}`;
                fetch(apiUrl)
                    .then(res => {
                        if (!res.ok) throw new Error('Network response was not ok');
                        return res.text();
                    })
                    .then(base64String => {
                        const img = document.createElement('img');
                        img.className = 'image-viewer';
                        img.alt = filename;
                        img.src = base64String;

                        img.onload = function () {
                            modalBody.innerHTML = '';
                            modalBody.appendChild(img);

                            const controls = document.createElement('div');
                            controls.className = 'image-controls';
                            controls.innerHTML = getViewerControl(filename);
                            modalBody.appendChild(controls);
                        };

                        img.onerror = function () {
                            showError('Failed to display image preview', filename);
                        };
                    })
                    .catch(err => {
                        console.error(err);
                        showError('Failed to load image', filename);
                    });
            } else {
                // Ảnh thường thì load trực tiếp
                const img = document.createElement('img');
                img.className = 'image-viewer';
                img.alt = filename;
                img.src = fileUrl;

                img.onload = function () {
                    modalBody.innerHTML = '';
                    modalBody.appendChild(img);

                    const controls = document.createElement('div');
                    controls.className = 'image-controls';
                    controls.innerHTML = getViewerControl(filename);
                    modalBody.appendChild(controls);
                };

                img.onerror = function () {
                    showError('Failed to load image', filename);
                };
            }
}


        function loadVideo(filename) {
            const modalBody = document.getElementById('modalBody');

            // Create video element
            const video = document.createElement('video');
            video.className = 'video-player';
            video.controls = true;
            video.autoplay = false;

            video.onloadedmetadata = function () {
                modalBody.innerHTML = '';
                modalBody.appendChild(video);
                video.play();


                // Add image controls
                const controls = document.createElement('div');
                controls.className = 'image-controls';
                controls.innerHTML = getViewerControl(filename);
        modalBody.appendChild(controls);
            };

            video.onerror = function () {
                showError('Failed to load video', filename);
            };

            // Use your server endpoint to serve the file
            const baseUrl = window.location.origin + window.location.pathname; // http://localhost:8080/Download/Camera
            const imgSrc = `${baseUrl}/${encodeURIComponent(filename)}`;
            video.src = imgSrc;
        }

        function showError(message, filename) {
            const modalBody = document.getElementById('modalBody');
            modalBody.innerHTML = `
        <div class="error-message">
            <div class="error-icon">❌</div>
            <h3>Error</h3>
            <h2>${message}</h1>
        </div>
    `;

    // Add image controls
                const controls = document.createElement('div');
                controls.className = 'image-controls';
                controls.innerHTML = getViewerControl(filename);
                modalBody.appendChild(controls);
        }

        function getViewerControl(filename){
            return `
        
            <button class="control-btn" onclick="showPreviousFile()" title="Next file">
                ⬅️
            </button>
            
            <button class="control-btn" onclick="downloadFile('${filename}')" title="Download">
                📥
            </button>
            <button class="control-btn" onclick="toggleFullscreen()" title="Fullscreen">
                🔍
            </button>
             <button class="control-btn" onclick="showNextFile()" title="Next file">
                ➡️
            </button>
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
            link.href = encodeFileName(filename);
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

        async function showPreviousFile() {
            const currentIndex = listFiles.findIndex(file=> file.name === currentFileShowOnModal);
            if (currentIndex>0) {
            const file = listFiles[currentIndex - 1];
            const type = getFileExtension(file.name);
            const name = file.name;
            const ext = file.isDirectory ? "-" : name.split('.').pop();
            const size = file.isDirectory ? "-" : formatBytes(file.size);
            const date = new Date(file.lastModified).toLocaleString();
            openFileModal(name, type, size, date);
            }

            if(currentPage-1 >0 && currentIndex === 0){
                await goToPage(currentPage - 1 ,currentPageSize);
                
                const file = listFiles[0];
                const type = getFileExtension(file.name);
                const name = file.name;
                const ext = file.isDirectory ? "-" : name.split('.').pop();
                const size = file.isDirectory ? "-" : formatBytes(file.size);
                const date = new Date(file.lastModified).toLocaleString();
                openFileModal(name, type, size, date);
            }
        }

        async function showNextFile() {
            const currentIndex = listFiles.findIndex(file=> file.name === currentFileShowOnModal);
            if (currentIndex>=0&& currentIndex +1<listFiles.length) {
            const file = listFiles[currentIndex +1];
            const type = getFileExtension(file.name);
            const name = file.name;
            const ext = file.isDirectory ? "-" : name.split('.').pop();
            const size = file.isDirectory ? "-" : formatBytes(file.size);
            const date = new Date(file.lastModified).toLocaleString();
            openFileModal(name, type, size, date);
            }

            if(currentPage+1 <= totalPage && currentIndex + 1 === listFiles.length){
                await goToPage(currentPage + 1 ,currentPageSize);
                
                const file = listFiles[0];
                const type = getFileExtension(file.name);
                const name = file.name;
                const ext = file.isDirectory ? "-" : name.split('.').pop();
                const size = file.isDirectory ? "-" : formatBytes(file.size);
                const date = new Date(file.lastModified).toLocaleString();
                openFileModal(name, type, size, date);
            }
        }

        // Close modal when clicking outside
        document.getElementById('fileModal').addEventListener('click', function (e) {
            if (e.target === this) {
                closeModal();
            }
        });

        // Close modal with Escape key
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                closeModal();
            }
        });

        function getPaginationHtml(){
                    const pagination = document.getElementById("pagination");

                    //const prePath = `?page=${currentPageSize -1}&size=${currentPageSize}`
                    const previous = `
                    <button class="paginate-button" ${currentPage-1>0? `onClick="goToPage(${currentPage-1}, ${currentPageSize})"`:""}> 
                                            <a style="
                                            display: inline-flex;
                                            align-items: center;
                                            padding: 6px 10px;
                                            margin: 0 5px;
                                            background-color: ${currentPage <= 1? "gray" : "#178FFF"};
                                            border: none;
                                            border-radius: 12px;
                                            color: white;
                                            text-decoration: none;
                                            font-size: 14px;
                                            ${currentPage <= 1? "pointer-events: none; cursor: not-allowed;" : ""}
                                        ">
                                            &#8592; Previous
                                        </a>
                                        </button>`

                    const pagesToShow =getPagesToShow(currentPage, totalPage);

                    let pages="";
                    
                    for(let i of pagesToShow){
                        //const pagePath = path === "/"? `page=${i}&size=${currentPageSize}`: `${path}?page=${i}&size=${currentPageSize}`
                        const pageI = `
                        <button class="paginate-button" onClick="goToPage(${i}, ${currentPageSize})"> <a href='#'style='
                            display: inline-block;
                            width: 32px;
                            height: 32px;
                            margin: 0 1px;
                            border: 1px solid #ccc;
                            border-radius: 50%;
                            color: white;
                            background-color: ${i != currentPage? "#178FFF" : "#248f24"};
                            text-align: center;
                            line-height: 30px;
                            text-decoration: none;
                            font-weight: bold;
                            font-family: sans-serif;
                            cursor: pointer;'>
                            ${i}
                        </a>
                        </button>
                        `

                        pages += pageI;
                    }
                    //const nextPath = `?page=${currentPageSize + 1}&size=${currentPageSize}`;
                    const next = `
                    <button class="paginate-button" ${currentPage >= totalPage? "":`onClick="goToPage(${currentPage+1}, ${currentPageSize})`}">
                                    <a  style="
                                        display: inline-flex;
                                        align-items: center;
                                        padding: 6px 10px;
                                        background-color: ${currentPage >= totalPage? "gray" : "#178FFF"};
                                        border: 1px solid #ccc;
                                        border-radius: 12px;
                                        color: white;
                                        text-decoration: none;
                                        font-size: 14px;
                                        ${currentPage >= totalPage? "pointer-events: none; cursor: not-allowed;" : ""}
                                    ">
                                        Next &#8594;
                                    </a>
                    </button>
                    `

                    const sizeOptions = [5, 10, 20, 50, 100];
                    const selectOptionsHtml = sizeOptions.map(sizeOption => {
                    const selected = sizeOption === currentPageSize ? "selected" : "";
                            return `<option value="${sizeOption}" ${selected}>${sizeOption}</option>`;
                            }).join("");

                    const formHtml = `
                        <form id="pageSizeForm" method="GET" action="">
                            <select name="size" id="size"
                                    onchange="changePageSize(this.value)"
                                    style="background-color: #178FFF; color: white; border-radius:10px; padding: 6px; margin-left: 5px;">
                                ${selectOptionsHtml}
                            </select>
                            
                        </form>
                    `;
                    pagination.innerHTML = previous + "\n"+ pages+ "\n"+ next + "\n" + formHtml;
        }

        function changePageSize(newSize) {
            goToPage(1, Number(newSize));
            
        }