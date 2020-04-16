var mongoose = require("mongoose");

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
// mongoose.connect('mongodb://localhost:27017/CIS_350_Final_Project_Database');
mongoose.connect("mongodb+srv://Max:Max@cis350project-8hdrl.mongodb.net/test?retryWrites=true&w=majority");
// above: running mongoose on Atlas

var Schema = mongoose.Schema;

var userSchema = new Schema({
	email: {type: String, required: true, unique: true},
	password: {type: String, required: true},
	name: {type: String, required: true},
	school: {type: String, required: true}
    });

// export userSchema as a class called User
module.exports = mongoose.model('User', userSchema);

userSchema.methods.standardizeName = function() {
    this.email = this.email.toLowerCase();
    return this.email;
}