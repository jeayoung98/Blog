document.getElementById('image').addEventListener('change', function(event) {
    const files = event.target.files;
    Array.from(files).forEach(file => {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.style.width = '100px'; // 썸네일 크기 조정
            document.body.appendChild(img);
        };
        reader.readAsDataURL(file);
    });
});
