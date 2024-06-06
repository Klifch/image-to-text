document.addEventListener("DOMContentLoaded", async function () {
    const processing = document.getElementById("processing").value === 'true';

    if (processing) {
        const bucketName = document.getElementById("bucketName").value;
        const fileName = document.getElementById("fileName").value;
        // const apiUrl = `http://localhost:8081/api/text-from-image?bucket=${bucketName}&image=${fileName}`;

        try {
            const response = await fetch(
                `http://app2-service:8081/api/text-from-image?bucket=${bucketName}&image=${fileName}`);
            if (!response.ok) {
                throw new Error('Network response was not ok. Could not reach the resource.');
            }
            const data = await response.json();
            displayExtractedText(data.text)
            console.log('API call successful:', data);
        } catch (error) {
            console.error('Error making API call:', error);
        }
    }
});

function displayExtractedText(text) {
    const textContainer = document.createElement('pre');
    textContainer.classList.add("mt-2");
    textContainer.textContent = text;
    document.querySelector('.container.col-md-4').appendChild(textContainer);
}