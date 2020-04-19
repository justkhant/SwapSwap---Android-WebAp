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
		bio: "",
		rank: 0,
		points: 0,
		phoneNumber: "",
	});
		
	console.log("Creating new User...");
	console.log("Email: " + newUser.email);
	console.log("Password: " + newUser.password);
	console.log("Name: " + newUser.name);
	console.log("School: " + newUser.school);
	
	// save the user to the database
	newUser.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
			return res.status(200).json({
				message: "Error creating user"});
		}
		else {

			console.log("New User Created Successfully...")
			return res.status(200).json({
				message: "User Created Successfully"});
		}
	} );
 } );


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
			return res.json({});
		}
		else if (users.length == 0) {
		    // no objects found, so send back empty json
			return res.json({});
		}
		
		else if (users.length > 0 ) {
			var user = users[0];
	
		    // send back a single JSON object
			return res.json( { "email" : user.email , "password" : user.password , "name" : user.name , "school" : user.school, "bio" : user.bio,
			"rank" : user.rank, "points" : user.points, "phoneNumber" : user.phoneNumber });
		}
		
	});
});

//route to update profile information for user
app.use('/update_profile', (req, res) => {
	var new_bio = req.query.bio;
	var new_phoneNumber = req.query.phoneNumber;
	var new_school = req.query.school;

	console.log("Updating Profile...");
	console.log("Bomb Bio: " + new_bio);
	console.log("My Numba: " + new_phoneNumber);
	console.log("School: " + new_school);

	// Find the User 
	var query = {};
	if (req.query.email) {
	    // if there's a email in the query parameter, use it here
		query = { "email" : req.query.email };
	}

	var updateProfile = { $set: {bio: new_bio, phoneNumber: new_phoneNumber, school: new_school} };

	User.updateOne( query, updateProfile, (err, users) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
			res.write(err);
			return res.json({
				message: "Error updating data"});
		}
		else {
			if (users.length == 0) {
				res.type('html').status(200);
				res.write('User does not exist');
				return res.status(200).json({
					message: "User not Found"});
			
			}
			console.log("Updated in database");
			return res.json({
				message: "User updated Successfully"});
		}
			
	});

});

app.use('/deleteUser', (req, res) => {
	var userToDelete = req.query.userToDelete;
	console.log("Deleting profile...");
	console.log("Profile being deleted: " + userToDelete);
		
	User.deleteOne({ "email" : userToDelete}, (err, results) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
			res.end();
		} else {
			console.log("Successful deletion");
		}
	});
});


/********************************* POST STUFF ****************************************/

// route for creating a new Post
// this is the action of the <createNewPost> button on the <New Post> Page
app.use('/createNewPost', (req, res) => {
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		title: req.query.title,
		category: req.query.category,
		completed: req.query.completed,
		imgURL: req.query.imgURL,
		details: req.query.details,
		owner: req.query.owner
	});
		
	console.log("Creating new Post...");
	console.log(newPost._id);
	console.log(newPost.title);
	console.log(newPost.category);
	console.log(newPost.completed);
	console.log(newPost.imgURL);
	console.log(newPost.details);
	console.log(newPost.owner);
	

	// save the post to the database
	newPost.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
			console.log(err);
			return res.json({
				message: "Error Creating Post"});
		}
		else {
			return res.json({
				message: "New Post Created Successfully"});
		}
	});
});


// route for finding a specific Post by id
app.use('/findPostID', (req, res) => {
	// construct the Post from the form data which is in the request body
	console.log("Searching for Post by ID");
	
	// construct the query object
	var queryObject = {};
	if (req.query._id) {
	    // if there's a id in the query parameter, use it here
		queryObject = { "_id" : req.query._id };
	}
		
	console.log("Finding Post by ID...");
	
	Post.find( queryObject, (err, posts) => {
		console.log(posts);
		if (err) {
		    console.log('uh oh' + err);
			return res.json({});
		}
		else if (posts.length == 0) {
		    // no objects found, so send back empty json
			console.log("no posts");
			return res.json({});
		}
		
		else if (posts.length > 0 ) {
			var post = posts[0];

			console.log("title: " + post.title);
			console.log("category: " + post.category);
			console.log("completed: " + post.completed);
			console.log("details: " + post.details);
			console.log("owner: " + post.owner);

		    // send back a single JSON object
			return res.json( { "title" : post.title, 
				"category" : post.category, 
				"completed" : post.completed,
				"imgURL": post.imgURL,
				"details": post.details,
				"owner": post.owner 
			});
		}
		
	});

});


// route for all posts by single user
app.use('/findUserPosts', (req, res) => {
	// construct the Post from the form data which is in the request body
	console.log("Searching for User's Posts");
	
	// construct the query object
	var queryObject = {};
	if (req.query.owner) {
	    // if there's a email in the query parameter, use it here
		queryObject = { "owner" : req.query.owner };
	}
		
	console.log("Finding User's Posts...");
	
	Post.find( queryObject, (err, posts) => {
		console.log(posts);
		if (err) {
		    console.log('uh oh' + err);
			return res.json({});
		}
		else {
		    if (posts.length == 0) {
				res.type('html').status(200);
				res.write('There are no posts for this user yet.');
				res.end();
				return;
			}
			
			//otherwise return all posts

			posts.forEach (post => 
				console.log("title: " + post.title));

			return res.json(posts);
		}
		
	});

});


	
// route for showing all the posts
app.use('/allPosts', (req, res) => {
	console.log("Show all Posts");
	
	// find all the User objects in the database
	Post.find( {}, (err, posts) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
			res.write(err);
			res.end();
			return;
		}
		else {
		    if (posts.length == 0) {
				res.type('html').status(200);
				res.write('There are no posts yet.');
				res.end();
				return;
		    }
		    // use EJS to show all the users
		    res.render('all_posts', { posts: posts });

		}
	    }).sort({ 'title': 'asc' }); // this sorts them BEFORE rendering the results
    });


/*************************************************/


app.listen(3000,  () => {
	console.log('Listening on port 3000');
});
