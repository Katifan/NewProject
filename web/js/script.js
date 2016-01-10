//Глобальная переменная, которая хранит индекс элементов записи, для того чтобы знать на какой именно странице мы сидим
var currentIndex = 0;

//Глобальный массив в котором я храню отсортированные записи. Сделано так, для увеличения производительности
var jarr = [];
jarr = sortData();

//Метод для проверки корректности ввода пароля и логина
function hello(){
	var ajax = new XMLHttpRequest();
	var xxx = document.getElementById("textEdit2").value;
	var zzz = document.getElementById("textEdit3").value;
	ajax.open('POST', '/JEETest/login', false);
	var json = new Object();
	json.login = xxx;
	json.password = zzz;
	ajax.send(JSON.stringify(json));
	var yyy =  ajax.responseText;
	console.log(yyy);
}

//Метод для получения json с сервера
function getDataFromServer () {
	var ajax = new XMLHttpRequest();
	ajax.open('GET', 'http://smart-route.ru:8100/adapter-web/rest/dictionary/c580d006-f86f-4bdd-84be-b51de6f626c6', false);
	ajax.send();
	var response = ajax.responseText;
	return response;
}

//Метод для парсинга json и превращения в массив данных
function parseData(){
	var json = getDataFromServer();
	var data = JSON.parse(json);
	var arr = [];
	arr = data.documents;
	return arr;
}

//Метод для показана массива пользователей на вкладку
function setStrings(){
	var data = jarr;
	for (var i = 0; i < 10; i++) {
		var a = document.createElement('p');
		a.innerHTML = (i + 1) + " " + data[i].fio + " " + data[i].gender + " " + data[i].birthDate + " " + data[i].phone;
		var b = document.getElementById("content3");
		b.appendChild(a);
	};
}

//Сортировка пользователей по ФИО
function sortData(){
	var data = parseData();
	data.sort(function(obj1, obj2)
	{
		if (obj1.fio < obj2.fio) return -1;
		if (obj1.fio > obj2.fio) return 1;
		return 0;
	});
	return data;
}

//Метод для поиска пользователей
function searchValue(){
	var data = jarr;
  	var jq = jQuery('p');
	jq.remove();
	var str = document.getElementById('textEdit1').value;
	str = str.toLowerCase();
	if(str.length < 3) //Если в строке меньше 3 элементов
	{
	  	setStrings();
	  	return;
	}
	var array = [];

	//Поиск элементов в записях и добавление их в массив
	for (var i = 0, j = 0; i < data.length; i++) {
		var buff = data[i].fio.toLowerCase();
		if (buff.indexOf(str) != -1) {
		   	array[j] = data[i];
		   	j++;
  		};
	}
	data = array;

	//Отображение найденных элементов на экран
	for (var i = 0; i < data.length; i++) {
		var a = document.createElement('p');
		a.innerHTML = (i + 1) + " " + data[i].fio + " " + data[i].gender + " " + data[i].birthDate + " " + data[i].phone;
		var b = document.getElementById("content3");
		b.appendChild(a);
	}
}

//Метод возврата на предыдущую страницу
function previousPage(){
	document.getElementById('next').disabled = false;
	var data = jarr;
	if (currentIndex < 10) {
		console.log("STOP");
	}else{
		currentIndex -= 10;
		console.log("Index: " + currentIndex);
		var jq = jQuery('p');
		jq.remove();
		for (var i = currentIndex; i < currentIndex + 10; i++) {
			var a = document.createElement('p');
			a.innerHTML = (i + 1) + " " + data[i].fio + " " + data[i].gender + " " + data[i].birthDate + " " + data[i].phone;
			var b = document.getElementById("content3");
			b.appendChild(a);
		};
	}
}

//Метод перехода на следующую страницу
function nextPage(){
	var cntr = 10;
	currentIndex += 10;
	//Проверка того что это последняя страница грида
	if (currentIndex + 10 >= jarr.length)
		document.getElementById('next').disabled = true; //отключаем нашу кнопку перехода на следующую страницу
	if (currentIndex >= jarr.length){
		currentIndex -=10;
		return;
	}
	var jq = jQuery('p');
	jq.remove();
	var data = jarr;
  	for (var i = currentIndex; i < currentIndex + cntr; i++) {
		var a = document.createElement('p');
		a.innerHTML = (i + 1) + " " + data[i].fio + " " + data[i].gender + " " + data[i].birthDate + " " + data[i].phone;
		var b = document.getElementById("content3");
		b.appendChild(a);
	};
	console.log("Index: " + currentIndex);
}