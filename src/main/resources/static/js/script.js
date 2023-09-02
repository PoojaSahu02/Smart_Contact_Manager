console.log("this is script file");
const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "2%");

	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");

	}
};

// function deleteUser(userId) {
//     if (confirm('Are you sure you want to delete this user?')) {
//         // Make an AJAX request to delete the user
//         $.ajax({
//             url: '/deleteUser/' + userId,
//             method: 'POST',
//             success: function(response) {
//                 // Handle success, e.g., display a message
//                 alert('User deleted successfully.');
//                 // Reload the page or perform other actions as needed
//                 location.reload();
//             },
//             error: function(error) {
//                 // Handle error
//                 alert('Error deleting user.');
//             }
//         });
//     }
// }


//search
const search = () => {
	// sending request to server
	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	} else {
		let url = `http://localhost:8080/search/${query}`;
		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				//  data
				let text = `<div class='list-group'>`;
				data.forEach((contact) => {
					text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;

				});
				text += `</div>`;
				$(".search-result").html(text);
				$(".search-result").show();
			});
	}
};