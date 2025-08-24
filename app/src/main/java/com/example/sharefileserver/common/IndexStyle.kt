package com.example.sharefileserver.common

fun getIndexStyle(): String{
    val style :String ="""
        <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #2196F3, #21CBF3);
            color: white;
            padding: 60px 30px;
            text-align: center;
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        /* Animated wave background */
        .header::before {
            content: '';
            position: absolute;
            top: 0;
            left: -50%;
            width: 200%;
            height: 100%;
            background: linear-gradient(
                90deg,
                transparent,
                rgba(255,255,255,0.1),
                transparent,
                rgba(255,255,255,0.05),
                transparent
            );
            animation: wave 4s ease-in-out infinite;
            z-index: 1;
        }

        .header::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            height: 100px;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 120" preserveAspectRatio="none"><path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z" fill="%23ffffff" fill-opacity="0.1"/></svg>') repeat-x;
            background-size: 1200px 120px;
            animation: waveMove 10s linear infinite;
            z-index: 1;
        }

        @keyframes wave {
            0% {
                transform: translateX(-50%) skewX(0deg);
            }
            25% {
                transform: translateX(-25%) skewX(2deg);
            }
            50% {
                transform: translateX(0%) skewX(0deg);
            }
            75% {
                transform: translateX(-25%) skewX(-2deg);
            }
            100% {
                transform: translateX(-50%) skewX(0deg);
            }
        }

        @keyframes waveMove {
            0% {
                background-position-x: 0;
            }
            100% {
                background-position-x: 1200px;
            }
        }

        /* Wave text animation */
        .header h3 {
            font-size: 3em;
            margin-bottom: 15px;
            font-weight: 300;
            position: relative;
            z-index: 2;
            letter-spacing: 2px;
            animation: textWave 3s ease-in-out infinite;
        }

        .header h3 .wave-char {
            display: inline-block;
            animation: charWave 2s ease-in-out infinite;
        }

        .header h3 .wave-char:nth-child(1) { animation-delay: 0s; }
        .header h3 .wave-char:nth-child(2) { animation-delay: 0.1s; }
        .header h3 .wave-char:nth-child(3) { animation-delay: 0.2s; }
        .header h3 .wave-char:nth-child(4) { animation-delay: 0.3s; }
        .header h3 .wave-char:nth-child(5) { animation-delay: 0.4s; }
        .header h3 .wave-char:nth-child(6) { animation-delay: 0.5s; }
        .header h3 .wave-char:nth-child(7) { animation-delay: 0.6s; }
        .header h3 .wave-char:nth-child(8) { animation-delay: 0.7s; }
        .header h3 .wave-char:nth-child(9) { animation-delay: 0.8s; }
        .header h3 .wave-char:nth-child(10) { animation-delay: 0.9s; }
        .header h3 .wave-char:nth-child(11) { animation-delay: 1s; }
        .header h3 .wave-char:nth-child(12) { animation-delay: 1.1s; }

        @keyframes textWave {
            0%, 100% {
                transform: translateY(0px);
            }
            50% {
                transform: translateY(-10px);
            }
        }

        @keyframes charWave {
            0%, 100% {
                transform: translateY(0px) rotateZ(0deg);
            }
            25% {
                transform: translateY(-15px) rotateZ(5deg);
            }
            75% {
                transform: translateY(-5px) rotateZ(-3deg);
            }
        }

        .header p {
            opacity: 0.9;
            font-size: 1.2em;
            position: relative;
            z-index: 2;
            font-weight: 300;
            letter-spacing: 0.5px;
            animation: fadeWave 4s ease-in-out infinite;
        }

        @keyframes fadeWave {
            0%, 100% {
                opacity: 0.9;
                transform: translateY(0px);
            }
            50% {
                opacity: 1;
                transform: translateY(-5px);
            }
        }

        /* File icon wave animation */
        .file-icon {
            font-size: 1.5em;
            display: inline-block;
            animation: iconWave 3s ease-in-out infinite;
            margin-right: 15px;
        }

        @keyframes iconWave {
            0%, 100% {
                transform: translateY(0px) rotate(0deg) scale(1);
            }
            25% {
                transform: translateY(-20px) rotate(10deg) scale(1.1);
            }
            50% {
                transform: translateY(-10px) rotate(-5deg) scale(1.05);
            }
            75% {
                transform: translateY(-15px) rotate(8deg) scale(1.08);
            }
        }

        /* Wave overlay effect */
        .wave-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: 
                radial-gradient(circle at 30% 20%, rgba(255,255,255,0.1) 0%, transparent 50%),
                radial-gradient(circle at 70% 80%, rgba(255,255,255,0.08) 0%, transparent 50%),
                radial-gradient(circle at 50% 50%, rgba(255,255,255,0.05) 0%, transparent 50%);
            animation: overlayWave 6s ease-in-out infinite;
            z-index: 1;
        }

        @keyframes overlayWave {
            0%, 100% {
                transform: scale(1) rotate(0deg);
                opacity: 0.8;
            }
            33% {
                transform: scale(1.1) rotate(2deg);
                opacity: 1;
            }
            66% {
                transform: scale(0.9) rotate(-1deg);
                opacity: 0.9;
            }
        }

        /* Hover wave intensification */
        .header:hover .file-icon {
            animation-duration: 1s;
        }

        .header:hover::before {
            animation-duration: 2s;
        }

        .header:hover .wave-char {
            animation-duration: 1s;
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .header {
                padding: 40px 20px;
            }
            
            .header h3 {
                font-size: 2.2em;
            }
            
            .header p {
                font-size: 1.1em;
            }
        }

        /* Content area */
        .content {
            padding: 40px 20px;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .stats {
            display: flex;
            justify-content: space-around;
            padding: 0px;
            background: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
        }
        
        .stat-item {
            text-align: center;
        }
        
        .stat-number {
            font-size: 2em;
            font-weight: bold;
            color: #2196F3;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.9em;
            margin-top: 2px;
        }
        
        .file-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .file-table th {
            background: #f8f9fa;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid #dee2e6;
        }
        
        .file-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #dee2e6;
            transition: background-color 0.2s;
        }
        
        
        
        .no-files {
            text-align: center;
            padding: 50px;
            color: #6c757d;
        }
        
        .no-files .icon {
            font-size: 4em;
            margin-bottom: 20px;
        }
        
        .footer {
            padding: 20px;
            text-align: center;
            background: #f8f9fa;
            color: #6c757d;
            border-top: 1px solid #dee2e6;
        }
        
        @media (max-width: 768px) {
            .stats {
                flex-direction: row;
                gap: 10px;
            }
            
            .file-table {
                font-size: 0.9em;
            }
            
            .file-table th,
            .file-table td {
                padding: 8px;
            }
        }
        
        
        
        thead {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
        }

        th {
            padding: 20px 15px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            font-size: 0.9em;
            text-transform: uppercase;
            letter-spacing: 1px;
            border-bottom: 2px solid #dee2e6;
        }

        /* Animated table rows */
        tr {
            cursor: pointer;
            user-select: none;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            background: white;
            opacity: 0;
            transform: translateY(20px);
            animation: slideInRow 0.6s ease-out forwards;
        }

        /* Staggered animation delay for each row */
        tr:nth-child(1) { animation-delay: 0.1s; }
        tr:nth-child(2) { animation-delay: 0.2s; }
        tr:nth-child(3) { animation-delay: 0.3s; }
        tr:nth-child(4) { animation-delay: 0.4s; }
        tr:nth-child(5) { animation-delay: 0.5s; }
        tr:nth-child(6) { animation-delay: 0.6s; }
        tr:nth-child(7) { animation-delay: 0.7s; }
        tr:nth-child(8) { animation-delay: 0.8s; }
        tr:nth-child(9) { animation-delay: 0.9s; }
        tr:nth-child(10) { animation-delay: 1.0s; }

        @keyframes slideInRow {
            from {
                opacity: 0;
                transform: translateY(20px) scale(0.95);
            }
            to {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
        }

        /* Hover effects */
        tbody tr:hover {
            background: linear-gradient(135deg, #f8f9ff, #e3f2fd);
            transform: translateY(-3px) scale(1.02);
            box-shadow: 
                0 10px 25px rgba(33, 150, 243, 0.15),
                0 0 0 1px rgba(33, 150, 243, 0.1);
            z-index: 10;
        }

        /* Active/Click effect */
        tbody tr:active {
            transform: translateY(-1px) scale(1.01);
            transition: all 0.1s ease;
        }

        /* Ripple effect on click */
        tr::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            background: radial-gradient(circle, rgba(33, 150, 243, 0.3) 0%, transparent 70%);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: all 0.6s ease;
            pointer-events: none;
            z-index: -1;
        }

        tr:active::before {
            width: 100%;
            height: 100%;
        }

        td {
            padding: 18px 15px;
            border-bottom: 1px solid #f1f3f4;
            font-size: 0.95em;
            color: #333;
            transition: all 0.3s ease;
        }
        
        .file-icon {
            font-size: 1.4em;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            display: inline-block;
        }

        tr:hover .file-icon {
            transform: scale(1.2) rotate(5deg);
            filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
        }

        /* File name animation */
        .file-name {
            font-weight: 500;
            transition: all 0.3s ease;
        }

        tr:hover .file-name {
            color: #2196F3;
            font-weight: 600;
        }

        /* Stripe effect for better readability */
        tbody tr:nth-child(even) {
            background: rgba(248, 249, 250, 0.5);
        }

        tbody tr:nth-child(even):hover {
            background: linear-gradient(135deg, #f8f9ff, #e3f2fd);
        }

        /* File type badge */
        .file-type {
            background: #e9ecef;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            font-weight: 500;
            color: #495057;
            transition: all 0.3s ease;
        }

        tr:hover .file-type {
            background: #2196F3;
            color: white;
            transform: scale(1.05);
        }

        /* Size and date styling */
        .file-size, .file-date {
            color: #6c757d;
            font-size: 0.9em;
            transition: all 0.3s ease;
        }

        tr:hover .file-size,
        tr:hover .file-date {
            color: #495057;
            font-weight: 500;
        }

        /* Loading animation for new rows */
        @keyframes pulse {
            0%, 100% {
                background-color: #f8f9fa;
            }
            50% {
                background-color: #e9ecef;
            }
        }

        .loading-row {
            animation: pulse 1.5s ease-in-out infinite;
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .table-container {
                padding: 10px;
            }
            
            th, td {
                padding: 12px 8px;
                font-size: 0.85em;
            }
            
            .file-icon {
                font-size: 1.2em;
            }
        }

        /* Smooth scroll behavior */
        html {
            scroll-behavior: smooth;
        }

        /* Focus styles for accessibility */
        tr:focus {
            outline: 2px solid #2196F3;
            outline-offset: -2px;
        }
        
        
    </style>
    """.trimIndent()
    return style
}