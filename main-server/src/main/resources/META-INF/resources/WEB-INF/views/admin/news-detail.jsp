<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>뉴스 상세 - 파이낸셜 프리덤</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="/static/admin/css/admin-common.css" rel="stylesheet">
    
    <style>
        .news-detail-container {
            max-width: 900px;
            margin: 0 auto;
        }
        
        .news-header {
            background: white;
            border-radius: 12px;
            padding: 2rem;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            margin-bottom: 2rem;
        }
        
        .news-title {
            font-size: 1.8rem;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1.4;
            margin-bottom: 1rem;
        }
        
        .news-meta {
            display: flex;
            gap: 2rem;
            color: #6c757d;
            font-size: 0.9rem;
            margin-bottom: 1.5rem;
            flex-wrap: wrap;
        }
        
        .news-meta-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .news-summary {
            background: #f8f9fa;
            border-left: 4px solid #3498db;
            padding: 1.5rem;
            border-radius: 0 8px 8px 0;
            margin-bottom: 1.5rem;
        }
        
        .news-summary h6 {
            color: #3498db;
            font-weight: 600;
            margin-bottom: 1rem;
        }
        
        .news-content {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }
        
        .news-content-header {
            background: #f8f9fa;
            padding: 1.5rem 2rem;
            border-bottom: 1px solid #e9ecef;
        }
        
        .news-content-body {
            padding: 2rem;
        }
        
        .content-block {
            margin-bottom: 2rem;
            line-height: 1.7;
        }
        
        .content-block:last-child {
            margin-bottom: 0;
        }
        
        .content-block h3 {
            color: #2c3e50;
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 1rem;
        }
        
        .content-block p {
            color: #495057;
            font-size: 1rem;
            margin-bottom: 1rem;
        }
        
        .image-block {
            margin: 2rem 0;
            text-align: center;
        }
        
        .image-block img {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;
        }
        
        .image-block img:hover {
            transform: scale(1.02);
        }
        
        .action-buttons {
            position: sticky;
            bottom: 2rem;
            text-align: center;
            margin-top: 3rem;
        }
        
        .btn-back {
            background: linear-gradient(135deg, #6c757d, #495057);
            border: none;
            color: white;
            padding: 0.75rem 2rem;
            border-radius: 25px;
            font-weight: 500;
            margin-right: 1rem;
        }
        
        .btn-delete {
            background: linear-gradient(135deg, #dc3545, #c82333);
            border: none;
            color: white;
            padding: 0.75rem 2rem;
            border-radius: 25px;
            font-weight: 500;
        }
        
        .loading-container {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
        }
        
        .error-container {
            text-align: center;
            padding: 3rem;
            color: #dc3545;
        }
    </style>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="common/sidebar.jsp"/>

        <div class="admin-content">
            <c:set var="pageTitle" value="뉴스 상세"/>
            <c:set var="pageIcon" value="fas fa-newspaper"/>
            <jsp:include page="common/header.jsp"/>

            <main class="page-content">
                <div class="news-detail-container">
                    <!-- 로딩 상태 -->
                    <div id="loadingContainer" class="loading-container">
                        <div class="spinner-border text-primary mb-3" role="status"></div>
                        <div>뉴스 상세 정보를 불러오는 중입니다...</div>
                    </div>
                    
                    <!-- 에러 상태 -->
                    <div id="errorContainer" class="error-container d-none">
                        <i class="fas fa-exclamation-triangle fa-3x mb-3"></i>
                        <h5>뉴스를 불러올 수 없습니다</h5>
                        <p id="errorMessage"></p>
                        <button class="btn btn-primary" onclick="loadNewsDetail()">다시 시도</button>
                    </div>
                    
                    <!-- 뉴스 상세 내용 -->
                    <div id="newsDetailContainer" class="d-none">
                        <!-- 뉴스 헤더 -->
                        <div class="news-header">
                            <h1 class="news-title" id="newsTitle"></h1>
                            
                            <div class="news-meta">
                                <div class="news-meta-item">
                                    <i class="fas fa-building"></i>
                                    <span id="newsOrganization"></span>
                                </div>
                                <div class="news-meta-item">
                                    <i class="fas fa-calendar-alt"></i>
                                    <span id="newsDate"></span>
                                </div>
                                <div class="news-meta-item">
                                    <i class="fas fa-link"></i>
                                    <a href="#" id="newsUrl" target="_blank">원문 보기</a>
                                </div>
                            </div>
                            
                            <div class="news-summary">
                                <h6><i class="fas fa-robot me-2"></i>AI 요약</h6>
                                <p id="aiSummary" class="mb-0"></p>
                            </div>
                        </div>
                        
                        <!-- 뉴스 본문 -->
                        <div class="news-content">
                            <div class="news-content-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-file-alt me-2"></i>뉴스 본문
                                </h5>
                            </div>
                            <div class="news-content-body" id="newsContentBlocks">
                                <!-- 콘텐츠 블록들이 동적으로 생성됩니다 -->
                            </div>
                        </div>
                        
                        <!-- 액션 버튼들 -->
                        <div class="action-buttons">
                            <button class="btn btn-back" onclick="goBackToList()">
                                <i class="fas fa-arrow-left me-2"></i>목록으로
                            </button>
                            <button class="btn btn-delete" onclick="deleteCurrentNews()">
                                <i class="fas fa-trash me-2"></i>삭제
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/static/admin/js/admin-common.js"></script>
    
    <script>
        const newsId = ${newsId};
        let newsData = null;

        $(document).ready(function() {
            $('[data-page="news"]').addClass('active');
            loadNewsDetail();
        });

        // 뉴스 상세 정보 로드
        function loadNewsDetail() {
            showLoading();
            
            $.ajax({
                url: '/admin/api/news/' + newsId,
                method: 'GET',
                success: function(response) {
                    if (response) {
                        newsData = response;
                        displayNewsDetail(newsData);
                        showNewsDetail();
                    } else {
                        showError('뉴스를 불러오는데 실패했습니다.');
                    }
                },
                error: function(xhr) {
                    console.error('뉴스 상세 로드 실패:', xhr);
                    let errorMessage = '뉴스를 불러오는데 실패했습니다.';
                    if (xhr.status === 404) {
                        errorMessage = '존재하지 않는 뉴스입니다.';
                    }
                    showError(errorMessage);
                }
            });
        }

        // 뉴스 상세 정보 표시
        function displayNewsDetail(news) {
            console.log('뉴스 데이터:', news); // 디버깅용
            
            $('#newsTitle').text(news.newsTitle || '제목 없음');
            $('#newsOrganization').text(news.organizationName || '기관 정보 없음');
            
            const formattedDate = formatDateTime(news.approvalDate);

            $('#newsDate').text(formattedDate);
            
            if (news.newsUrl) {
                $('#newsUrl').attr('href', news.newsUrl).show().parent().show();
            } else {
                $('#newsUrl').parent().hide();
            }
            
            $('#aiSummary').text(news.aiSummary || 'AI 요약이 없습니다.');
            
            // 콘텐츠 블록들 표시
            const contentContainer = $('#newsContentBlocks');
            contentContainer.empty();
            
            if (news.contentBlocks && news.contentBlocks.length > 0) {
                news.contentBlocks.forEach(function(block) {
                    let blockHtml = '<div class="content-block">';
                    
                    const content = block.content || '';
                    console.log('블록 타입:', block.blockType, '내용:', content); // 디버깅용
                    if (block.blockType === 'image') {
                        if (block.url) {
                            blockHtml += '<div class="image-block text-center mb-3">';
                            blockHtml += '<img src="' + (block.url) + '" ';
                            blockHtml += 'alt="' + (block.altText || '뉴스 이미지') + '" ';
                            blockHtml += 'class="img-fluid" style="max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);" />';
                            blockHtml += '</div>';
                        } else if (content) {
                            blockHtml += '<p class="text-muted">[이미지: ' + escapeHtml(content) + ']</p>';
                        }
                    } else {
                        blockHtml += '<p>' + content + '</p>';
                    }
                    
                    blockHtml += '</div>';
                    contentContainer.append(blockHtml);
                });
            } else {
                contentContainer.html('<p class="text-muted">본문 내용이 없습니다.</p>');
            }
        }

        // 날짜 시간 포맷팅 함수
        function formatDateTime(dateString) {
            if (!dateString) return '-';
            
            const date = new Date(dateString);
            if (isNaN(date.getTime())) return dateString;
            
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            return year+'-'+month+'-'+day+' '+hours+':'+minutes;
        }

        // 로딩 상태 표시
        function showLoading() {
            $('#loadingContainer').removeClass('d-none');
            $('#errorContainer').addClass('d-none');
            $('#newsDetailContainer').addClass('d-none');
        }

        // 뉴스 상세 표시
        function showNewsDetail() {
            $('#loadingContainer').addClass('d-none');
            $('#errorContainer').addClass('d-none');
            $('#newsDetailContainer').removeClass('d-none');
        }

        // 에러 상태 표시
        function showError(message) {
            $('#loadingContainer').addClass('d-none');
            $('#newsDetailContainer').addClass('d-none');
            $('#errorMessage').text(message);
            $('#errorContainer').removeClass('d-none');
        }

        // 목록으로 돌아가기
        function goBackToList() {
            window.location.href = '/admin/news';
        }

        // 현재 뉴스 삭제
        function deleteCurrentNews() {
            if (confirm('정말로 이 뉴스를 삭제하시겠습니까?')) {
                $.ajax({
                    url: '/admin/api/news/' + newsId,
                    method: 'DELETE',
                    success: function() {
                        alert('뉴스가 성공적으로 삭제되었습니다.');
                        window.location.href = '/admin/news';
                    },
                    error: function(xhr) {
                        console.error('뉴스 삭제 실패:', xhr);
                        alert('뉴스 삭제에 실패했습니다.');
                    }
                });
            }
        }
    </script>
</body>
</html>
