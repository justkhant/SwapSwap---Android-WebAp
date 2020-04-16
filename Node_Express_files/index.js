// Connect to MongoDB Atlas cluster
const mongoose = require('mongoose');
const connector = mongoose.connect("mongodb+srv://Max:Max@cis350project-8hdrl.mongodb.net/test?retryWrites=true&w=majority");

// set up Express
var express = require('express');
var app = express();

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// import the User class from User.js
var User = require('./User.js');

/***************************************/

// route for creating a new User
// this is the action of the SignUp button on the SignUp Page
app.use('/createNewUser', (req, res) => {
	// construct the User from the form data which is in the request body
	var newUser = new User ({
		email: req.query.email,
		password: req.query.password,
		name: req.query.name,
		school: req.query.school,
	    });
	console.log(newUser.username);
	console.log(newUser.password);
	console.log(newUser.name);
	console.log(newUser.school);
	res.status(200).json({
		message: "JSON Data received successfully"
	});
	// save the user to the database
	newUser.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // display the "successfull created" page using EJS
		    // res.render('created', {user : newUser});
		}
	    } );
    }
    );


// route for showing all the users
app.use('/all', (req, res) => {
    
	// find all the User objects in the database
	User.find( {}, (err, users) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
		    if (users.length == 0) {
			res.type('html').status(200);
			res.write('There are no users.');
			res.end();
			return;
		    }
		    // use EJS to show all the users
		    res.render('all', { users: users });

		}
	    }).sort({ 'username': 'asc' }); // this sorts them BEFORE rendering the results
    });

// route for accessing data via the web api
// to use this, make a request for /api to get an array of all User objects
// or /api?username=[whatever] to get a single object
app.use('/api', (req, res) => {
	console.log("LOOKING FOR SOMETHING?");

	// construct the query object
	var queryObject = {};
	if (req.query.username) {
	    // if there's a username in the query parameter, use it here
	    queryObject = { "username" : req.query.username };
	}
    
	User.find( queryObject, (err, users) => {
		console.log(users);
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else if (users.length == 0) {
		    // no objects found, so send back empty json
		    res.json({});
		}
		
		else if (users.length == 1 ) {
		    var user = users[0];
		    // send back a single JSON object
		    res.json( { "username" : user.username , "password" : user.password , "name" : user.name , "school" : user.school} );
		}
		/* We will only return one JSONObject user per login request
		else {
		    // construct an array out of the result
		    var returnArray = [];
		    users.forEach( (user) => {
			    returnArray.push( { "username" : user.username, "password" : user.password } );
			});
		    // send it back as JSON Array
		    res.json(returnArray); 
		}
		*/
		
	    });
    });



/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/personform.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
