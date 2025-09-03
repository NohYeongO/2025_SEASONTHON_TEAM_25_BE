<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>퀴즈 관리 - 파이낸셜 프리덤</title>
    <link rel="icon" href="/static/favicon.ico" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="/static/admin/css/admin-common.css" rel="stylesheet">
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="common/sidebar.jsp"/>

        <div class="admin-content">
            <c:set var="pageTitle" value="퀴즈 관리"/>
            <c:set var="pageIcon" value="fas fa-question-circle"/>
            <c:set var="addButton" value="퀴즈 추가"/>
            <jsp:include page="common/header.jsp"/>

            <main class="page-content">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>질문</th>
                                <th>카테고리</th>
                                <th>생성일</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">
                                    퀴즈 데이터를 불러오는 중입니다...
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/static/admin/js/admin-common.js"></script>
    <script>
        $(document).ready(function() {
            $('[data-page="quiz"]').addClass('active');
        });
    </script>
</body>
</html>
