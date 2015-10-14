var content = "";

//function that creates a hosting service object
function hostingservice(title, text){
	this.title = title;
	this.text = text;

}

var cheapHostingtitle = "Cheap Ass Hosting";
var cheapHostingText = "Cheap ass server. We will dig up the most old DL380 G2 we got in store and your website will be slow. Don't blame us.. You dont want to pay for quality. For example for someone who wants to host a game server for their friends.";



var mediumHostingTitle = "Best Choise Hosting";
var mediumHostingText = "The best choice servers. Decent speed servers for a decent price. Best choice for small companies or enthusiastic individuals. This packet will handel a site with medium trafic perfectly. Also perfectly for hosting public game servers.";

var expensiveHostingTitle = "Overpriced hosting";
var expensiveHostingText = "Over priced hosting servers for only the most hardcore individuals or companies. With this packet you will be able to host your website or program of choice at the fastest speeds.";

//creating the three hosting service objects
var cheapHosting = new hostingservice(cheapHostingtitle, cheapHostingText);
var mediumHosting  = new hostingservice(mediumHostingTitle, mediumHostingText);
var expensiveHosting = new hostingservice(expensiveHostingTitle, expensiveHostingText);


//function that is called in the button to display the cheap ass hosting title and text
function cheapAssHosting(){
	content = document.getElementById("packet");
	content.style.borderColor= "#94FF66";
	document.getElementById("hostingTitle").innerHTML = cheapHosting.title;
	document.getElementById("hostingText").innerHTML = cheapHosting.text;


}
//function that is called in the button to display the best choice hosting title and text
function bestChoiseHosting(){
	content = document.getElementById("packet");
	content.style.borderColor= "#6699FF";
	document.getElementById("hostingTitle").innerHTML = mediumHosting.title;
	document.getElementById("hostingText").innerHTML = mediumHosting.text;

}
//function that is called in the button to display the overpricedHosting hosting title and text
function overpricedHosting(){
	content = document.getElementById("packet");
	content.style.borderColor= "#FF0000";
	document.getElementById("hostingTitle").innerHTML = expensiveHosting.title;
	document.getElementById("hostingText").innerHTML = expensiveHosting.text;

}