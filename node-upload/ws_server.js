/**
 * node版websocket上传Demo
 * 代码粗陋，只能用于学习
 * @Author   SAI
 * @DateTime 2016-11-12T19:43:12+0800
 */
var fs = require("fs")
  , path = require("path")
  , server = require('http').createServer()
  , WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ server: server,path : '/upload' })
  , port = 8081
  , ProtoBuf = require("protobufjs");
  

var UploadManage = function  () {
  
}

UploadManage.prototype.saveChunk = function(params,_Bufferfile) {
  try{
    var id = params.id,chunk = params.chunk,name = params.name;
    var _path = __dirname+'/uploadFile/'+id+'/';
    if(!fs.existsSync(_path)){
      fs.mkdirSync(_path);
    }
    fs.writeFileSync(__dirname+'/uploadFile/'+id+'/'+name+'_'+chunk, _Bufferfile);
    var result = {code : 200}
  }catch(e){
    return {code : -1}
  }
  return result;
  
}

// Initialize and load upload.proto file
var builder = ProtoBuf.loadProtoFile(path.join(__dirname, "static","proto", "upload.proto"));
var Upload = builder.build("Upload"); 

wss.on('connection', function connection(ws) {
  var uploadManage = new UploadManage();

  ws.on('message', function incoming(data,flags) {
    console.log(arguments);
     if (flags.binary) {
      var upload = Upload.decode(data);
      console.log(upload.filename,upload.uid);
      uploadManage.saveChunk({id : upload.uid,chunk: upload.currchunk,name : upload.filename},upload.upload_file);
     }
  });
 
});
 
server.listen(port);



