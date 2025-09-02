-- 테스트용 사용자 데이터
INSERT INTO users (
    id, email, password, role, status, character_created, created_at, updated_at
) VALUES
(1, 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnOmTOPSwz5WJQGYM/cR5lRhXRHGkDTcmK', 'USER', 'ACTIVE', false, NOW(), NOW());

-- 테스트용 뉴스 데이터
INSERT INTO news_article (
    id, news_item_id, contents_status, modify_id, modify_date, approve_date, approver_name,
    grouping_code, title, sub_title1, contents_type, data_contents, plain_text_content,
    content_hash, thumbnail_url, ai_summary, created_at, updated_at
) VALUES
-- 어제 뉴스 (ID: 1)
(1, 'news001', 'U', 1, 
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '테스트승인자1',
 'policy', '어제 정책 뉴스 제목', '어제 부제목1',
 'H', '<p>어제 뉴스 내용입니다.</p>', '어제 뉴스 내용입니다.',
 'hash001', 'https://example.com/thumb1.jpg', 'AI 요약: 어제 중요한 정책 발표', NOW(), NOW()),

-- 오늘 뉴스 (ID: 2) 
(2, 'news002', 'U', 1,
 NOW(), NOW(), '테스트승인자2',
 'policy', '오늘 정책 뉴스 제목', '오늘 부제목1',
 'H', '<p>오늘 뉴스 내용입니다.</p>', '오늘 뉴스 내용입니다.',
 'hash002', 'https://example.com/thumb2.jpg', 'AI 요약: 오늘 새로운 정책 방향', NOW(), NOW()),

-- 일주일 전 뉴스 (조회되면 안됨)
(3, 'news003', 'U', 1,
 DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), '테스트승인자3',
 'policy', '일주일 전 뉴스', '오래된 부제목',
 'H', '<p>일주일 전 뉴스</p>', '일주일 전 뉴스',
 'hash003', 'https://example.com/thumb3.jpg', 'AI 요약: 오래된 뉴스', NOW(), NOW());

-- 뉴스 컨텐츠 블록 (첫 번째 뉴스용)
INSERT INTO news_content_block (
    id, news_article_id, block_type, original_content, plain_content, url, alt_text, block_order, created_at, updated_at
) VALUES
(1, 1, 'TEXT', '<p>첫 번째 텍스트 블록</p>', '첫 번째 텍스트 블록', NULL, NULL, 1, NOW(), NOW()),
(2, 1, 'IMAGE', '<img src="test.jpg" alt="테스트 이미지">', '', 'https://example.com/test.jpg', '테스트 이미지', 2, NOW(), NOW());
