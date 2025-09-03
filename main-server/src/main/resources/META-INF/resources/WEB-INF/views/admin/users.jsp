<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>사용자 관리 - 파이낸셜 프리덤</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="/static/admin/css/admin-common.css" rel="stylesheet">
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="common/sidebar.jsp"/>

        <div class="admin-content">
            <c:set var="pageTitle" value="사용자 관리"/>
            <c:set var="pageIcon" value="fas fa-users"/>
            <jsp:include page="common/header.jsp"/>

            <main class="page-content">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>이메일</th>
                                <th>닉네임</th>
                                <th>가입일</th>
                                <th>상태</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="6" class="text-center text-muted py-4">
                                    사용자 데이터를 불러오는 중입니다...
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
            $('[data-page="users"]').addClass('active');
        });
    </script>
</body>
</html>
