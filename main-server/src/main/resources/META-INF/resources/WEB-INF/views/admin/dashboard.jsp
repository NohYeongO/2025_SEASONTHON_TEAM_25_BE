<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메인 대시보드 - 파이낸셜 프리덤</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="/static/admin/css/admin-common.css" rel="stylesheet">
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="common/sidebar.jsp"/>

        <div class="admin-content">
            <c:set var="pageTitle" value="메인 대시보드"/>
            <c:set var="pageIcon" value="fas fa-tachometer-alt"/>
            <jsp:include page="common/header.jsp"/>

            <main class="page-content">
                <div class="welcome-card">
                    <h2><i class="fas fa-home me-3"></i>관리자 대시보드에 오신 것을 환영합니다!</h2>
                    <p>파이낸셜 프리덤 서비스를 효율적으로 관리할 수 있습니다.</p>
                </div>

                <!-- 통계 카드들 -->
                <div class="row mb-4">
                    <div class="col-lg-4 col-md-6 mb-4">
                        <div class="stat-card primary">
                            <div class="stat-icon"><i class="fas fa-newspaper"></i></div>
                            <div class="stat-content">
                                <h3 id="newsCount">0</h3>
                                <p>총 뉴스 수</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-6 mb-4">
                        <div class="stat-card success">
                            <div class="stat-icon"><i class="fas fa-question-circle"></i></div>
                            <div class="stat-content">
                                <h3 id="quizCount">0</h3>
                                <p>총 퀴즈 수</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-6 mb-4">
                        <div class="stat-card warning">
                            <div class="stat-icon"><i class="fas fa-users"></i></div>
                            <div class="stat-content">
                                <h3 id="userCount">0</h3>
                                <p>총 사용자 수</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 시스템 정보 -->
                <div class="admin-card">
                    <div class="card-header">
                        <h5><i class="fas fa-clock"></i>시스템 시간</h5>
                    </div>
                    <div class="card-body text-center">
                        <div class="system-time" id="systemTime">로딩 중...</div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/static/admin/js/admin-common.js"></script>
    
    <script>
        $(document).ready(function() {
            // 현재 페이지 메뉴 활성화
            $('[data-page="dashboard"]').addClass('active');
            loadDashboardData();
        });

        function loadDashboardData() {
            // TODO: 실제 API 호출로 데이터 로드
            $('#newsCount').text('0');
            $('#quizCount').text('0');  
            $('#userCount').text('0');
        }
    </script>
</body>
</html>
