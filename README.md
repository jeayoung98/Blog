# LikeLion_Blog
[Techit] 백엔드 스쿨 10기: velog 클론 프로젝트

## 기능 목록

### 1. 회원가입
- [x] 회원 가입 폼
- [x] 같은 ID, Email Check API
- [x] 회원 등록 기능
- [x] 회원 가입 후 로그인 폼으로 이동

### 2. 로그인
- [x] 로그인 폼
- [x] 로그인 기능
    - [x] 로그인 성공 후 `/`로 이동
    - [x] 로그인 실패 후 다시 로그인 폼으로 이동 (오류 메시지 출력)
- [x] Spring Security 를 이용한 로그인 구현
    - [x] Form Login
    - [x] JWT Login

### 3. 사이트 상단
- [x] 사이트 로고가 좌측 상단에 보여짐
- [x] 로그인 여부에 따른 우측 정보 표시
    - [x] 로그인하지 않았을 경우 로그인 링크
    - [x] 로그인했을 경우 사용자 이름
        - [x] 사용자 이름 클릭 시 설정, 해당 사용자 블로그 이동 링크, 임시 저장글 목록 보기, 로그아웃 링크 표시

### 4. 로그아웃
- [x] 로그아웃 기능

### 5. 메인 페이지
- [x] 블로그 글 목록 보기
    - [x] 최신 순
    - [x] 조회수 높은 순
    - [x] 좋아요 순
- [x] 작성자 이름 누르면 블로그 방문
- [x] 좋아요 수 표시
- [x] 조회수 표시


### 6. 블로그 글 쓰기
- [x] 블로그 제목, 내용, 사진 입력 기능
- [x] "출간하기" 버튼
    - [x] 블로그 썸네일(이미지)
    - [x] 공개 유무
    - [x] 시리즈 설정
    - [x] "출간하기" 클릭 시 글 등록
- [x] "임시저장" 버튼

### 7. 임시 글 저장 목록 보기
- [x] 로그인 시 임시글 저장 목록 보기 링크 표시
- [x] 임시글 저장 목록 표시
    - [x] 글 제목 클릭 시 글 수정 가능
    - [x] "임시저장" 및 "출간하기" 기능

### 8. 특정 사용자 블로그 글 보기
- [x] 사용자 정보 보기
- [x] 사용자 글 목록 보기
    - [x] 최신 순
    - [x] 조회수 높은 순
    - [x] 좋아요 순
- [x] 페이징 처리 또는 무한 스크롤 구현

### 9. 시리즈 목록 보기
- [x] 시리즈 목록 보기 기능
- [x] 시리즈 제목 클릭 시 시리즈에 포함된 블로그 글 목록 보기

### 10. 블로그 글 상세 보기
- [x] 메인 페이지에서 제목 클릭 시 블로그 글 상세 보기
- [x] 특정 사용자 블로그에서 제목 클릭 시 블로그 글 상세 보기
- [x] 시리즈에 속한 블로그 글 목록에서 제목 클릭 시 블로그 글 상세 보기

### 11. 사용자 정보 보기
- [x] 로그인 사용자 이름 클릭 시 사용자 정보 보기
    - [x] 사용자 이름
    - [x] 이메일
    - [x] 회원 탈퇴 링크

### 12. 회원 탈퇴
- [x] 회원 탈퇴 확인 폼
- [x] 폼에서 확인 시 회원 탈퇴 (회원 정보 삭제)

### 13. 블로그 글에 좋아요 하기
- [x] 블로그 글에 좋아요 기능

### 14. 팔로잉 / 팔로우
- [x] 팔로우
- [x] 언팔로우
- [x] 팔로잉 / 팔로워 수
