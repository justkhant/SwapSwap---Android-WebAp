// Connect to MongoDB Atlas cluster
const mongoose = require('mongoose');
const connector = mongoose.connect("mongodb+srv://Max:Max@cis350project-8hdrl.mongodb.net/test?retryWrites=true&w=majority");

// set up Express
var express = require('express');
var app = express();

// set up EJS -- WE DON'T NEED THIS FOR OUR PROJECT, RIGHT? -Max
//app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
var urlencodedParser = bodyParser.urlencoded({ extended: false })

app.use(bodyParser.json({ limit: "500mb" }));
app.use(bodyParser.urlencoded({ extended: true }));


// import the User class from User.js
var User = require('./User.js');

// import the Post class from Post.js
var Post = require('./Post.js');

// import the Admin class from Admin.js
var Admin = require('./Admin.js');

/************************ USER STUFF ***************************/

// route for creating a new User
// this is the action of the SignUp button on the SignUp Page
app.use('/createNewUser', (req, res) => {
	// construct the User from the form data which is in the request body
	var newUser = new User({
		email: req.body.email,
		password: req.body.password,
		name: req.body.name,
		school: req.body.school,
		bio: "",
		rank: 0,
		points: 0,
		phoneNumber: "",
		profilePic: req.body.profilePic
	});

	console.log("Creating new User...");
	console.log("Email: " + newUser.email);
	console.log("Password: " + newUser.password);
	console.log("Name: " + newUser.name);
	console.log("School: " + newUser.school);
	console.log("Strong default profile picture...")

	// save the user to the database
	newUser.save((err) => {
		if (err) {
			res.type('html').status(200);
			res.write('uh oh: ' + err);
			console.log(err);
			return res.status(200).json({
				message: "Error creating user"
			});
		}
		else {

			console.log("New User Created Successfully...")
			return res.status(200).json({
				message: "User Created Successfully"
			});
		}
	});
});


// route for showing all the users
app.use('/all', (req, res) => {
	console.log("Show all users");

	// find all the User objects in the database
	User.find({}, (err, users) => {
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
	console.log("Searching for User:" + req.query.email + "...");

	// construct the query object
	var queryObject = {};
	if (req.query.email) {
		// if there's a email in the query parameter, use it here
		queryObject = { "email": req.query.email };
	}

	User.find(queryObject, (err, users) => {
		if (err) {
			console.log('uh oh' + err);
			return res.json({});
		}
		else if (users.length == 0) {
			// no objects found, so send back empty json
			return res.json({});
		}

		else if (users.length > 0) {
			var user = users[0];
			var temp = {
				"email": user.email, "password": user.password, "name": user.name, "school": user.school, "bio": user.bio,
				"rank": user.rank, "points": user.points, "phoneNumber": user.phoneNumber
			};
			console.log(temp);

			// send back a single JSON object
			return res.json({
				"email": user.email, "password": user.password, "name": user.name, "school": user.school, "bio": user.bio,
				"rank": user.rank, "points": user.points, "phoneNumber": user.phoneNumber, "profilePic": user.profilePic
			});

		}

	});
});

//route to update profile information for user
app.use('/update_profile', (req, res) => {
	var new_bio = req.body.bio;
	var new_phoneNumber = req.body.phoneNumber;
	var new_school = req.body.school;
	var new_profilePic = req.body.profilePic;

	console.log("Updating Profile of User:" + req.body.email + "...");
	console.log("Bomb Bio: " + new_bio);
	console.log("My Numba: " + new_phoneNumber);
	console.log("School: " + new_school);
	if (new_profilePic) {
		console.log("New Profile Pic Received...")
	}
	//console.log("Profile Pic:" + new_profilePic);
	// Find the User 
	var query = {};
	if (req.body.email) {
		// if there's a email in the query parameter, use it here
		query = { "email": req.body.email };
	}

	var updateProfile = { $set: { bio: new_bio, phoneNumber: new_phoneNumber, school: new_school, profilePic: new_profilePic } };

	User.updateOne(query, updateProfile, (err, users) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
			return res.json({
				message: "Error updating data"
			});
		}
		else {
			if (users.length == 0) {
				res.type('html').status(200);
				res.write('User does not exist');
				return res.status(200).json({
					message: "User not Found"
				});

			}
			console.log("Updated in database");
			return res.json({
				message: "User updated Successfully"
			});
		}

	});

});

app.use('/deleteUser', (req, res) => {
	var userToDelete = req.query.userToDelete;
	console.log("Deleting profile...");
	console.log("Profile being deleted: " + userToDelete);

	User.deleteOne({ "email": userToDelete }, (err, results) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
			return res.status(200).json({
				message: "Error Deleting User"
			});
		} else {
			Post.deleteMany({ "owner": userToDelete }, (err, results) => {
				if (err) {
					res.type('html').status(200);
					console.log('uh oh' + err);
					res.write(err);
					return res.status(200).json({
						message: "Error Deleting User Posts"
					});
				} else {
					console.log("Deleting...");
					console.log(results);
					console.log("Successful deletion");
					return res.status(200).json({
						message: "User Deleted"
					});
				}
			});
		}
	});
});



/********************************* POST STUFF ****************************************/

// route for creating a new Post
// this is the action of the <createNewPost> button on the <New Post> Page
app.use('/createNewPost', (req, res) => {
	// construct the Post from the form data which is in the request body
	var newPost = new Post({
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
	newPost.save((err) => {
		if (err) {
			res.type('html').status(200);
			res.write('uh oh: ' + err);
			console.log(err);
			return res.json({
				message: "Error Creating Post"
			});
		}
		else {
			console.log("New Post Created Successfully");
			return res.json({ "_id": newPost._id });
		}
	});
});


// route for finding a specific Post by id
app.use('/getPost', (req, res) => {
	// construct the Post from the form data which is in the request body
	console.log("Getting Post by ID");

	// construct the query object
	var queryObject = {};
	if (req.query._id) {
		// if there's a id in the query parameter, use it here
		queryObject = { "_id": req.query._id };
	}

	console.log("Finding Post by ID...");

	Post.find(queryObject, (err, posts) => {
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

		else if (posts.length > 0) {
			var post = posts[0];

			console.log("title: " + post.title);
			console.log("category: " + post.category);
			console.log("completed: " + post.completed);
			console.log("details: " + post.details);
			console.log("owner: " + post.owner);

			//get owner's name
			User.find({ "email": post.owner }, (err, users) => {
				if (err) {
					console.log('uh oh' + err);
				}
				else if (users.length == 0) {
					// no objects found, so send back empty json
				} else {
					user = users[0];
					console.log("owner_name: " + user.name);
					// send back a single JSON object
					return res.json({
						"title": post.title,
						"category": post.category,
						"completed": post.completed,
						"imgURL": post.imgURL,
						"details": post.details,
						"owner": post.owner,
						"_id": post._id,
						"owner_name": user.name
					});
				}
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
		queryObject = { "owner": req.query.owner };
	}

	console.log("Finding User's Posts...");

	Post.find(queryObject, (err, posts) => {
		console.log(posts);
		if (err) {
			console.log('uh oh' + err);
			return res.json({});
		}
		else {
			if (posts.length == 0) {
				res.type('html').status(200);
				//res.write('There are no posts for this user yet.');
				return res.json({});
			}

			//otherwise return all posts

			//posts.forEach (post => 
			//	console.log("title: " + post.title));

			return res.json(posts);
		}

	});

});

//route to update post information
app.use('/updatePost', (req, res) => {
	var new_title = req.query.title;
	var new_category = req.query.category;
	var new_completed = req.query.completed;
	var new_imgURL = req.query.imgURL;
	var new_details = req.query.details;

	console.log("Updating Post...");

	console.log("New Title: " + new_title);
	console.log("New Categroy: " + new_category);
	console.log("New Completed: " + new_completed);
	console.log("New Details: " + new_details);

	// Find the post 
	var query = {};
	if (req.query._id) {
		// if there's a email in the query parameter, use it here
		query = { "_id": req.query._id };
	}

	var updatePost = { $set: { title: new_title, category: new_category, completed: new_completed, imgURL: new_imgURL, details: new_details } };

	Post.updateOne(query, updatePost, (err, posts) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
			return res.json({
				message: "Error updating post"
			});
		}
		else {
			if (posts.length == 0) {
				res.type('html').status(200);
				res.write('post does not exist');
				return res.status(200).json({
					message: "post not Found"
				});

			}
			console.log("Updated in database");
			return res.json({
				message: "Post updated Successfully"
			});
		}

	});

});



// route for showing all the posts
app.use('/allPosts', (req, res) => {
	console.log("Show all Posts");

	// find all the User objects in the database
	Post.find({}, (err, posts) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
			res.end();
			return res.json({});;
		}
		else {
			if (posts.length == 0) {
				res.type('html').status(200);
				res.write('There are no posts yet.');
				res.end();
				return res.json({});
			}
			// use EJS to show all the users
			//res.render('all_posts', { posts: posts });
			return res.json(posts);

		}
	})
});



/********************* WEB STUFF *******************/
app.get('/', (req, res) => {
	res.render('login_signup', { qs: req.query });
});

app.get('/home', (req, res) => {
	res.render('home', { qs:req.query});
});

//admin signup
app.post('/signup', urlencodedParser, function (req, res) {
	console.log("signup button clicked");
	var email = req.body.signup_email;
	var name = req.body.signup_name;
	var password = req.body.signup_password;
	if (name.length == 0 || email.length  == 0 || password.length == 0) {
			console.log("empty fields");
			res.redirect('/?signup_empty=true&signup_email='+ email + '&signup_name=' + name);
			return;
	}

	var newAdmin = new Admin({
		name: req.body.signup_name,
		email: req.body.signup_email,
		password: req.body.signup_password,
	});

	var query = {"email": req.body.signup_email};
	Admin.find(query, (err, admins) => {
		if (err) {
			console.log('uh oh ' + err);
			res.end('Error');
		}
		else if (admins.length == 0) {
			console.log("Creating new Admin...");
			console.log("Name: " + newAdmin.name);
			console.log("Email: " + newAdmin.email);
			console.log("Password: " + newAdmin.password);

			// save the user to the database
			newAdmin.save((err) => {
				console.log("save")
				if (err) {
					res.type('html').status(200);
					res.write('uh oh: ' + err);
					console.log(err);
				}
				else {
					console.log("New User Created Successfully...")
				}
			});
			res.redirect('/?signup_success=true');
		}

		else if (admins.length > 0) {
			res.redirect('/?account_exists=true');
		}
	});
});

  
  //admin login
app.post('/home', urlencodedParser, function (req, res) {
	console.log("login button clicked");
	console.log(req.body.login_email);
	console.log(req.body.login_password);

	var bodyObject = {};
	
	// inputs are named in login_signup.ejs
	if (req.body.login_email && req.body.login_password) {
		// if there's a email in the query parameter, use it here
		bodyObject = { "email": req.body.login_email, "password": req.body.login_password };
	}

	if (req.body.login_email.length == 0 || req.body.login_password.length == 0) {
		console.log("empty fields");
		res.redirect('/?empty_fields=true');
		return;
	}

	Admin.find(bodyObject, (err, admins) => {
		if (err) {
			console.log('uh oh ' + err);
			res.end('Error');
		}
		else if (admins.length == 0) {
			console.log('Invalid User');
			res.redirect('/?invalid_username=true');
			// no objects found, so send back empty json
		}

		else if (admins.length > 0) {
			var admin = admins[0];
			var temp = { "email": admin.email, "password": admin.password, "name": admin.name };
			console.log(temp);
			//res.redirect('/public/temp.html');
			res.render('home', { data: admin });
		}

	});
});


// render search_by_teacher.ejs when the "Find Teacher" button in the homepage menu is clicked
app.get('/findTeacher', (req, res) => {
	res.render('search_by_teacher', { qs: req.query });
});

// This method is run when the FIND PROFILE button is clicked.
// (Done similarly to findPost - we search by email)
app.post('/getTeacher', urlencodedParser, function (req, res) {

	// need an empty string check to make sure it doesn't search with an empty queryObject
	if (req.body.search_email.length == 0) {
		console.log("Empty search field");
		res.redirect('/findTeacher?empty=true'); // goes back home if invalid entry
		return;
	}

	console.log("Find Profile button clicked");
	console.log(req.body.search_email); // input is named in <search_by_teacher.ejs>
	console.log("Searching for user by email...");
	
	// construct the query object
	var queryObject = {};
	if (req.body.search_email) {
		// if there's an email in the query parameter, use it here
		queryObject = { "email": req.body.search_email };
	}
	
	console.log("Retrieving User...");
	var userToShow = {};
	User.find(queryObject, (err, users) => {
		if (err) {
			console.log('uh oh ' + err);
			res.end('Error');
		}
		else if (users.length == 0) {
			console.log('No users found');
			res.redirect('/findTeacher?no_users=true&search_email=' + req.body.search_email);
		}
		else if (users.length > 0) {
			userToShow = users[0];
			console.log(userToShow);
			//find the user's posts
			var posts = {};
			Post.find({"owner": req.body.search_email}, (err, result_posts) => {
				console.log(result_posts);
				if (err) {
					console.log('uh oh' + err);
				}
				else {
					if (posts.length == 0) {
						console.log('No posts found');
					}	
					posts = result_posts;
					res.render('find_teacher', { data: { user_data: userToShow, post_data: posts} } );
				}
			});
		}
	});



	

});

// render search_by_school.ejs when the "Find School" button in the homepage menu is clicked
app.get('/findSchool', (req, res) => {
	
	// create an empty array to be populated later by school strings
	var schoolSet = new Set();
	
	// find all the User objects in the database and store their school strings in the schoolSet
	User.find({}, (err, users) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh ' + err);
			res.write(err);
		}
		else {
			if (users.length == 0) {
				res.type('html').status(200);
				res.redirect('/findSchool');
			}
			
			// fetch the school from each user and put it in the schoolArray
			for (i = 0; i < users.length; i++) {
				console.log('Pushing user school: ' + users[i].school);
				schoolSet.add(users[i].school);
			}
			
			res.render('search_by_school', { qs: req.query, data: schoolSet });
		}
	});
	
});

// This method is run when the FIND SCHOOL button is clicked.
// (Done similarly to findPost - we search for users by school)
app.post('/getSchool', urlencodedParser, function (req, res) {

	console.log("Find School button clicked");
	console.log(req.body.search_school); // input is named in <search_by_school.ejs>
	console.log("Searching for user by school...");
	
	var search = req.body.search_school;
	if (search.length == 0) {
		search = req.body.school;
	}

	// construct the query object
	var queryObject = {};
	if (search) {
		// if there's a school in the query parameter, use it here
		if (search == "All Schools") {
		} else {
			queryObject = { "school" : search };
		}
	}
	
	// need an empty string check to make sure it doesn't search with an empty queryObject
	if (search.length == 0) {
		console.log("Empty search field");
		res.redirect('/findSchool?empty=true');
		return;
	}
	
	console.log("Retrieving User...");
	
	User.find(queryObject, async (err, users) => {
		if (err) {
			console.log('uh oh ' + err);
			res.end('Error');
		}
		else if (users.length == 0) {
			console.log('No users found');
			res.redirect('/findSchool?no_users=true&school=' + search);
		}
		else if (users.length > 0) {			
			res.render('find_school', { data: users } );
		}
	});
});

// render delete_by_teacher.ejs when the "Delete Teacher" button in the homepage menu is clicked
app.get('/deleteTeacher', (req, res) => {
	res.render('delete_by_teacher', { qs: req.query });
});

// This method is run when the DELETE USER button is clicked.
app.post('/executeDeleteTeacher', urlencodedParser, function (req, res) {

	console.log("Delete User button clicked");
	var queryObject = {};
	
	if (req.body.delete_email) {
		queryObject = { "email": req.body.delete_email };
	}
	
	if (req.body.delete_email.length == 0) {
		console.log("Empty delete field.");
		res.redirect('/home'); // goes back to home if invalid entry
		return;
	}
	
	console.log("Attempting to deleting user: " + req.body.delete_email);
	
	User.deleteOne(queryObject, (err, result) => {
		if (err) {
			res.type('html').status(200);				
			console.log('uh oh' + err);
			res.write(err);
			return res.status(200).json({
				message: "Error Deleting User"
			});
		} else if (result.deletedCount != 1) {
			console.log(result.ok);
			console.log(result.n);
			console.log(result.deletedCount);
			console.log("No such user found.");
			res.render('delete_by_teacher', { qs: req.query });
		} else {
			//delete posts
			Post.deleteMany({ "owner": req.body.delete_email }, (err, results) => {
				if (err) {
					res.type('html').status(200);
					console.log('uh oh' + err);
					res.write(err);
					return res.status(200).json({
						message: "Error Deleting User Posts"
					});
				} else {
					console.log("Successful deletion of " + result.ok + " user!");
					res.render('delete_teacher', {data: req.body.delete_email});
				}
			});
			
		}
	});
});



// app.post('/temp', urlencodedParser, function(req, res){
// //app.get('/temp', (req, res) => {
// 	console.log('temp:' + req.body.email);
// 	console.log('temp:' + req.body.password);
// 	res.render('temp', {qs: req.query});
// });



/*************************************************/
app.use('/public', express.static('public'));

//app.use('/', (req, res) => { res.redirect('/public/temp.html'); } );


app.listen(3000, () => {
	console.log('Listening on port 3000');
});
