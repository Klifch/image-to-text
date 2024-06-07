document.addEventListener("DOMContentLoaded", async function () {
    const processing = document.getElementById("processing").value === 'true';

    if (processing) {
        const bucketName = document.getElementById("bucketName").value;
        const fileName = document.getElementById("fileName").value;

        const encodedBucketName = encodeURIComponent(bucketName);
        const encodedFileName = encodeURIComponent(fileName);

        try {
            const response = await fetch(`/proxy/text-from-image?bucket=${encodedBucketName}&image=${encodedFileName}`)
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