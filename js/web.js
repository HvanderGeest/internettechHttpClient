var content = "";

//function that creates a hosting service object
function hostingservice(title, text){
	this.title = title;
	this.text = text;

}

var smallPersonaltitle = "Small Personal";
var smallPersonalText = "Small Personal web hosting for individuals or very smal firms, like for example for a local barber shop. Due to the small bandwith prone to DDOS attacks.";



var mediumPersonalTitle = "Medium Personal";
var mediumPersonalText = "Medium sized web hosting for very enthusiastic individuals or medium sized companies who want to run their website at decent speeds."

var bigPersonalTitle = "Big personal";
var bigPersonalText = "Not fit for the individual, unles obsessed with reliable fast speeds and has too much money to spend. Perfect for big companies who want to reliably host their website.";

//creating the three hosting service objects
var smallHosting = new hostingservice(smallPersonaltitle, smallPersonalText);
var mediumHosting  = new hostingservice(mediumPersonalTitle, mediumPersonalText);
var bigHosting = new hostingservice(bigPersonalTitle, bigPersonalText);


//function that is called in the button to display the cheap ass hosting title and text
function smallPersonal(){
	content = document.getElementById("packet");
	content.style.borderColor= "#94FF66";
	document.getElementById("hostingTitle").innerHTML = smallHosting.title;
	document.getElementById("hostingText").innerHTML = smallHosting.text;


}
//function that is called in the button to display the best choice hosting title and text
function mediumPersonal(){
	content = document.getElementById("packet");
	content.style.borderColor= "#6699FF";
	document.getElementById("hostingTitle").innerHTML = mediumHosting.title;
	document.getElementById("hostingText").innerHTML = mediumHosting.text;

}
//function that is called in the button to display the overpricedHosting hosting title and text
function bigPersonal(){
	content = document.getElementById("packet");
	content.style.borderColor= "#FF0000";
	document.getElementById("hostingTitle").innerHTML = bigHosting.title;
	document.getElementById("hostingText").innerHTML = bigHosting.text;

}