// Connect to MongoDB Atlas cluster
const mongoose = require('mongoose');
const connector = mongoose.connect("mongodb+srv://Max:Max@cis350project-8hdrl.mongodb.net/test?retryWrites=true&w=majority");

// set up Express
var express = require('express');
var app = express();

// set up EJS -- WE DON'T NEED THIS FOR OUR PROJECT, RIGHT? -Max
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// import the User class from User.js
var User = require('./User.js');

// import the Post class from Post.js
var Post = require('./Post.js');

/************************ USER STUFF ***************************/

// route for creating a new User
// this is the action of the SignUp button on the SignUp Page
app.use('/createNewUser', (req, res) => {
	// construct the User from the form data which is in the request body
	var newUser = new User ({
		email: req.query.email,
		password: req.query.password,
		name: req.query.name,
		school: req.query.school,
		bio: "About me...",
		rank: 0,
		points: 0,
		phoneNumber: "xxx xxx xxxx",
		});
		
	console.log("Creating new User...");
	console.log(newUser.email);
	console.log(newUser.password);
	console.log(newUser.name);
	console.log(newUser.school);
	//res.status(200).json({
	//	message: "JSON Data received successfully"
	//});
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
	console.log("Show all users");
	
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
	    }).sort({ 'email': 'asc' }); // this sorts them BEFORE rendering the results
    });

// route for accessing data via the web api
// to use this, make a request for /api to get an array of all User objects
// or /api?username=[whatever] to get a single object
app.use('/search_user', (req, res) => {
	console.log("Searching for user");

	// construct the query object
	var queryObject = {};
	if (req.query.email) {
	    // if there's a email in the query parameter, use it here
		queryObject = { "email" : req.query.email };
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
		
		else if (users.length > 0 ) {
		    var user = users[0];
		    // send back a single JSON object
			res.json( { "email" : user.email , "password" : user.password , "name" : user.name , "school" : user.school, "bio" : user.bio,
			"rank" : user.rank, "points" : user.points, "phoneNumber" : user.phoneNumber} );
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

//route to update profile information for user
app.use('/update_profile', (req, res) => {
	var new_bio = req.query.bio;
	var new_phoneNumber = req.query.phoneNumber;

	console.log("Updating Profile...");
	console.log("Bomb Bio: " + new_bio);
	console.log("My Numba: " + new_phoneNumber);

	// Find the User 
	var query = {};
	if (req.query.email) {
	    // if there's a email in the query parameter, use it here
		query = { "email" : req.query.email };
	}

	var updateProfile = { $set: {bio: new_bio, phoneNumber: new_phoneNumber} };

	User.updateOne( query, updateProfile, (err, users) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
			if (users.length == 0) {
				res.type('html').status(200);
				res.write('User does not exist');
				res.end()
				return;
			}
		} 
			
	});
});

/********************************* POST STUFF ****************************************/

// route for creating a new Post
// this is the action of the <createNewPost> button on the <New Post> Page
app.use('/createNewPost', (req, res) => {
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		name: req.query.name,
		category: req.query.category,
		avail: req.query.avail,
		imgURL: req.query.imgURL,
		});
		
	console.log("Creating new Post...");
	console.log(newPost.name);
	console.log(newPost.category);
	console.log(newPost.avail);
	console.log(newPost.imgURL);
	
	// save the user to the database
	newPost.save( (err) => { 
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

/*************************************************/


app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
