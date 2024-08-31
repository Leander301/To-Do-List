document.getElementById("taskForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const taskDescription = document.getElementById("taskDescription").value;
    const taskList = document.getElementById("taskList");

    fetch("/", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "description=" + encodeURIComponent(taskDescription)
    }).then(response => response.text()).then(data => {
        // After adding task, refresh the task list
        loadTasks();
        document.getElementById("taskDescription").value = "";
    });
});

function loadTasks() {
    fetch("/")
        .then(response => response.text())
        .then(html => {
            // Update task list with the new HTML content
            document.body.innerHTML = html;
        });
}

// Load tasks on page load
loadTasks();
