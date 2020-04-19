var mongoose = require("mongoose");

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
// mongoose.connect('mongodb://localhost:27017/CIS_350_Final_Project_Database');

mongoose.connect("mongodb+srv://Max:Max@cis350project-8hdrl.mongodb.net/test?retryWrites=true&w=majority");
// above: running mongoose on Atlas

var Schema = mongoose.Schema;

var postSchema = new Schema({
	title: {type: String, required: true},
	category: {type: String, required: true},
	completed: {type: Boolean, required: true},
	imgURL: {type: String},
	details: {type: String},
	owner: {type: String, required: true}
});

// export postSchema as a class called Post
module.exports = mongoose.model('Post', postSchema);

postSchema.methods.generateUniqueId = function() {
    // this.email = this.email.toLowerCase();
    // return this.email;
}