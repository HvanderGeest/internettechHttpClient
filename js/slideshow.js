//image Array to add an image to the slide show add the path to the array here
var imageArray = ["images/slideshow/img-1.png", "images/slideshow/img-2.png", "images/slideshow/img-3.png"];
//the next image that is going to be displayed from the array
var current = 1;
function startSlideshow(){
	var imageElement = document.getElementById("diaimg");
	if(imageElement == null){
		//element not succesfully retrieved
		console.log("null");
		
	} else {
		console.log("not null");
		if(current >= imageArray.length){
			//reset to beginning no more next images
			current = 0;
		}
		//set the next image and increase the current for the next time the function is called	
		imageElement.src = imageArray[current];
		current++;
	}

}

function init(){
	//initializes the function with a interval of 3000 ms
	setInterval(startSlideshow, 3000);
}

init();