<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>뉴스 관리 - 파이낸셜 프리덤</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="/static/admin/css/admin-common.css" rel="stylesheet">
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="common/sidebar.jsp"/>

        <div class="admin-content">
            <c:set var="pageTitle" value="뉴스 관리"/>
            <c:set var="pageIcon" value="fas fa-newspaper"/>
            <c:set var="addButton" value="뉴스 동기화"/>
            <jsp:include page="common/header.jsp"/>

            <main class="page-content">
                <!-- 액션 버튼들 -->
                <div class="row mb-3">
                    <div class="col-md-12 text-end">
                        <button class="btn btn-success" onclick="syncNews()" id="syncNewsBtn">
                            <i class="fas fa-sync me-2"></i>최신 뉴스 동기화
                        </button>
                    </div>
                </div>

                <!-- 뉴스 목록 테이블 -->
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th width="5%" class="text-center">번호</th>
                                <th width="45%">제목</th>
                                <th width="20%">AI 요약</th>
                                <th width="10%">기관</th>
                                <th width="12%">게시일</th>
                                <th width="8%">관리</th>
                            </tr>
                        </thead>
                        <tbody id="newsTableBody">
                            <tr>
                                <td colspan="6" class="text-center text-muted py-4">
                                    <div class="spinner-border me-2" role="status"></div>
                                    뉴스 데이터를 불러오는 중입니다...
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- 페이지네이션 -->
                <nav class="mt-4">
                    <ul class="pagination justify-content-center" id="pagination">
                        <!-- 페이지네이션 동적 생성 -->
                    </ul>
                </nav>
            </main>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/static/admin/js/admin-common.js"></script>
    
    <script>
        let currentPage = 0;
        const pageSize = 10;

        $(document).ready(function() {
            $('[data-page="news"]').addClass('active');
            loadNewsList();
        });

        // 뉴스 목록 로드
        function loadNewsList(page = 0) {
            currentPage = page;
            
            $.ajax({
                url: '/admin/api/news',
                method: 'GET',
                data: { page: page, size: pageSize },
                success: function(response) {
                    displayNewsList(response.content);
                    displayPagination(response);
                },
                error: function(xhr) {
                    console.error('뉴스 목록 로드 실패:', xhr);
                    $('#newsTableBody').html(
                        '<tr><td colspan="6" class="text-center text-danger py-4">' +
                        '뉴스 목록을 불러오는데 실패했습니다.</td></tr>'
                    );
                }
            });
        }

        // 뉴스 목록 표시
        function displayNewsList(newsList) {
            const tbody = $('#newsTableBody');
            
            if (newsList.length === 0) {
                tbody.html('<tr><td colspan="6" class="text-center text-muted py-4">등록된 뉴스가 없습니다.</td></tr>');
                return;
            }

            let html = '';
            newsList.forEach(function(news, index) {
                const summary = news.aiSummary && news.aiSummary.length > 50 
                    ? news.aiSummary.substring(0, 50) + '...' 
                    : (news.aiSummary || '요약 없음');
                
                // 페이지 번호를 고려한 순번 계산 (현재 페이지 * 페이지 사이즈 + 인덱스 + 1)
                const rowNumber = currentPage * pageSize + index + 1;
                
                // 날짜 형식을 시간까지 포함하여 변환
                const approveDate = news.approvalDate ? formatDateTime(news.approvalDate) : '-';
                console.log(approveDate);
                html += '<tr>';
                html += '<td class="text-center">' + rowNumber + '</td>';
                html += '<td><a href="javascript:void(0)" onclick="viewNewsDetail(' + news.id + ')" class="text-decoration-none">' + news.newsTitle + '</a></td>';
                html += '<td><small>' + summary + '</small></td>';
                html += '<td><small>' + (news.organizationName || '기관 정보 없음') + '</small></td>';
                html += '<td>' + approveDate + '</td>';
                html += '<td><button class="btn btn-sm btn-primary" onclick="viewNewsDetail(' + news.id + ')">상세</button></td>';
                html += '</tr>';
            });
            
            tbody.html(html);
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
            console.log(year, month, day, hours, minutes);
            return year+'-'+month+'-'+day+' '+hours+':'+minutes;
        }

        // 페이지네이션 표시
        function displayPagination(pageData) {
            const pagination = $('#pagination');
            let html = '';
            
            // 이전 페이지
            if (pageData.first) {
                html += '<li class="page-item disabled"><span class="page-link">이전</span></li>';
            } else {
                html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadNewsList(' + (currentPage - 1) + ')">이전</a></li>';
            }
            
            // 페이지 번호들
            const startPage = Math.max(0, currentPage - 2);
            const endPage = Math.min(pageData.totalPages - 1, currentPage + 2);
            
            for (let i = startPage; i <= endPage; i++) {
                if (i === currentPage) {
                    html += '<li class="page-item active"><span class="page-link">' + (i + 1) + '</span></li>';
                } else {
                    html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadNewsList(' + i + ')">' + (i + 1) + '</a></li>';
                }
            }
            
            // 다음 페이지
            if (pageData.last) {
                html += '<li class="page-item disabled"><span class="page-link">다음</span></li>';
            } else {
                html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadNewsList(' + (currentPage + 1) + ')">다음</a></li>';
            }
            
            pagination.html(html);
        }

        // 뉴스 상세 보기
        function viewNewsDetail(newsId) {
            window.location.href = '/admin/news/' + newsId;
        }

        // 뉴스 삭제
        function deleteNews(newsId) {
            if (confirm('정말로 이 뉴스를 삭제하시겠습니까?')) {
                $.ajax({
                    url: '/admin/api/news/' + newsId,
                    method: 'DELETE',
                    success: function() {
                        alert('뉴스가 성공적으로 삭제되었습니다.');
                        loadNewsList(currentPage);
                    },
                    error: function(xhr) {
                        console.error('뉴스 삭제 실패:', xhr);
                        alert('뉴스 삭제에 실패했습니다.');
                    }
                });
            }
        }

        // 뉴스 동기화
        function syncNews() {
            const btn = $('#syncNewsBtn');
            const originalHtml = btn.html();
            
            btn.prop('disabled', true);
            btn.html('<span class="spinner-border spinner-border-sm me-2"></span>동기화 중...');
            
            $.ajax({
                url: '/admin/api/news/sync',
                method: 'POST',
                success: function() {
                    alert('뉴스 동기화가 완료되었습니다.');
                    loadNewsList(0); // 첫 페이지로 이동
                },
                error: function(xhr) {
                    console.error('뉴스 동기화 실패:', xhr);
                    alert('뉴스 동기화에 실패했습니다.');
                },
                complete: function() {
                    btn.prop('disabled', false);
                    btn.html(originalHtml);
                }
            });
        }
    </script>
</body>
</html>
