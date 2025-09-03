<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="admin-header">
    <div class="header-content">
        <h1 class="page-title">
            <i class="${pageIcon}"></i>${pageTitle}
        </h1>
        <div class="header-actions">
            <c:if test="${not empty addButton}">
                <button class="btn btn-primary me-3">
                    <i class="fas fa-plus me-2"></i>${addButton}
                </button>
            </c:if>
            <span class="user-info">
                <i class="fas fa-user"></i>Admin
            </span>
            <button class="btn logout-btn" onclick="handleLogout()">
                <i class="fas fa-sign-out-alt"></i>로그아웃
            </button>
        </div>
    </div>
</header>
