//event listener that will call the function screenResizes whenever the screen size is changed
window.addEventListener("resize", screenResize);
function screenResize (){
	var width = window.innerWidth;

	if(width <= 760){
		//screen is in mobile format displaying mobile menu.
		if(document.getElementById("menu") === null){
			return;
		}
		document.getElementById("menu").style.visibility = "visible";
		document.getElementById("menu").style.display = "none";
		document.getElementById("togglebutton").style.visibility = "visible";
		document.getElementById("toggle").style.display = "";

	} else {
		//screen is in desktop format displaying desktop menu.
		if(document.getElementById("togglebutton") === null){
			return;

		}
		document.getElementById("togglebutton").style.visibility = "hidden";
		document.getElementById("menu").style.visibility = "visible";
		document.getElementById("menu").style.display = "";
		document.getElementById("toggle").style.display = "none";
	}


}

function toggleMenu() {

	if (document.getElementById("togglebutton").innerHTML == "Close Menu") {
		//closes the dropdown menu in mobile format

		document.getElementById("togglebutton").innerHTML = "Open Menu";
		document.getElementById("menu").style.display = "none";


	} else {
		//opens the dropdown menu in mobile format

		document.getElementById("togglebutton").innerHTML = "Close Menu";
		document.getElementById("menu").style.display = "";

	}
}



