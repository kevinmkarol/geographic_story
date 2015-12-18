/**
 * FileUploaderController
 *
 * @description :: Server-side logic for managing fileuploaders
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {

  uploadMP3: function(req, res){
    console.log("request recieved");
    req.file("geoMP3").upload({
      dirName: require('path').resolve(sails.config.appPath, '/assets/sounds'),
      maxBytes:10000000
    }, function whenDone(err, uploadedFiles){
      if(err){
        console.log(err);
        return res.negotiate(err);
      }

      if(uploadedFiles.length === 0){
        console.log("no files uploaded");
        return res.badRequest("No file uploaded");
      }

      // Save the "fd" and the url where the avatar for a user can be accessed
      FileUploader.update(req.session.me, {
        // Generate a unique URL where the avatar can be downloaded.
        mp3Url: require('util').format('%s/user/sounds/%s', sails.getBaseUrl(), req.session.me),

        // Grab the first file and use it's `fd` (file descriptor)
        mp3Fd: uploadedFiles[0].fd
      })
       .exec(function (err){
          if (err) return res.negotiate(err);
          return res.ok();
       });
      });

      console.log("successfully uploaded");
      return res.ok();

    },

  downloadMP3: function(req, res){
    FileUploader.findOne(req.param('id')).exec(function (err, user){
      if (err) return res.negotiate(err);
      if (!user) return res.notFound();

      // User has no avatar image uploaded.
      // (should have never have hit this endpoint and used the default image)
      if (!user.avatarFd) {
        return res.notFound();
      }

      var SkipperDisk = require('skipper-disk');
      var fileAdapter = SkipperDisk(/* optional opts */);

      // Stream the file down
      fileAdapter.read(user.avatarFd)
           .on('error', function (err){
                return res.serverError(err);
           })
           .pipe(res);
    });   

  }

};

