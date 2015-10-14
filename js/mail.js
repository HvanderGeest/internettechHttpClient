//function that is called in the send message button wich will prompt the user to open its desird mail client and will fill the receiver, subject and body of the mail
function openMailProvider(){
	var mailAdress = document.getElementById("emailaddres").value;
	var mailText = document.getElementById("emailtext").value;
	window.open("mailto:"+"thost@example.nl"+"?subject=Coole bericht Thost website van "+mailAdress+ "&body="+mailText);



}