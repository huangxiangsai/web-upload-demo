/**
 * node版上传Demo 
 * 代码粗陋，只能用于学习
 * @Author   SAI
 * @DateTime 2016-11-12T19:43:12+0800
 */

const path = require('path');
const koa = require('koa');
const serve = require('koa-static');
const formidable = require('formidable');
const util = require('util');
const body = require('koa-better-body');
const fs = require('fs');
const stringify = require('json-stable-stringify');
const contentType = require('content-type-mime');
const cors = require('koa-cors');

var router = require('koa-router')();
var app = koa();

const _static = serve(path.join(__dirname,'static'));
_static._name = 'static static';


function _upload (req,res,success) {

	var form = new formidable.IncomingForm();
	var path = req.headers.referer;
	form.uploadDir = __dirname+'/uploadFile';
	form.keepExtensions = true;
	form.uploadPath = function(filename){
		return filename;
	}
	var successFlag = false;
  form.parse(req, function (err, fields, files) {
  	if(!err){
  		success && success(files.inputFile.name,'/uploadFile/'+files.inputFile.name);
  	}else{
  	}
  }); 
}

/**
 * 保存完整文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-14T16:08:42+0800
 * @param    {[string]} name [文件名]
 * @param    {[string]} path [保存的路径]
 */
function saveFile(name,path){
	var f1 = fs.readFileSync(path);
	fs.writeFileSync(__dirname+'/uploadFile/'+name, f1);
	fs.unlinkSync(path);
	var result = {
		data : [{
			name : name,
			filePath : '/'+name,
			msg : 'success'
		}]
	}
	return result;
}

/**
 * 保存分片文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-15T09:56:41+0800
 */
function saveChunk(params){
	try{
		var id = params.id,chunk = params.chunk,path = params.path,name = params.name;
		var f1 = fs.readFileSync(path);
		var _path = __dirname+'/uploadFile/'+id+'/';
		if(!fs.existsSync(_path)){
			fs.mkdirSync(_path);
		}
		fs.writeFileSync(__dirname+'/uploadFile/'+id+'/'+name+'_'+chunk, f1);
		fs.unlinkSync(path);
		var result = {code : 200}
	}catch(e){
		return {code : -1}
	}
	return result;
}

/**
 * 普通表单上传文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.post('/formUplaod',function *(){
	var self = this;
	var req = this.req , res = this.res ;
	var path = this.req.headers.referer;
	_upload(req,res);
  self.redirect(path);
});

/**
 * iframe上传文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.post('/iframeUpload',body({files : 'inputFile'}),function *(next){
	var f = this.request.inputFile[0];
	var result = saveFile(f.name,f.path);
	this.body = stringify(result);
});

/**
 * ajax上传文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.post('/ajaxUpload',body({files : 'inputFile'}),function *(){
	var f = this.request.inputFile[0];
	var result = saveFile(f.name,f.path);
	console.log(result);
	this.body = stringify(result);
});

/**
 * cors-ajax上传文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.post('/corsUpload',body({files : 'inputFile'}),function *(){
	var f = this.request.inputFile[0];
	var result = saveFile(f.name,f.path);
	console.log(result);
	this.body = stringify(result);
	this.set('Access-Control-Allow-Origin','*');
});

/**
 * 分片上传文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.post('/chunkUpload',body({files : 'inputFile'}),function *(){
	var fields = this.request.fields;
	var f = this.request.inputFile[0];
	var result = saveChunk({id : fields.uid ,chunk : fields.currchunk, path : f.path,name : fields.filename });
	this.body = stringify(result);
})

/**
 * 获得已上传的文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-12T19:43:12+0800
 */
router.get('/filesList',function *(){
	var _path = path.join(__dirname,'uploadFile');
	var filenames = fs.readdirSync(_path);
	var resultPaths = [];
	for(var i = 0 ; i < filenames.length ; i++){
		var name = filenames[i];
		var lstat = fs.lstatSync(_path+'/'+name);
		if( !(/^\./.test(name)) && lstat.isDirectory()){
			var names = fs.readdirSync(path.join(__dirname,'uploadFile',name));
			var n = names[0].replace(/_\d*$/,'');
			resultPaths.push({path : '/file/'+name+'/'+n});	
		}else if(!(/^\./.test(name)) && lstat.isFile()){
			resultPaths.push({path : '/'+name});
		}
		
	}
	this.body = {data : resultPaths}
})

/**
 * 获得单个文件
 * @Author   xiangsai.huang
 * @DateTime 2016-11-15T09:56:06+0800
 */
router.get('/file/:id/:name',function *(){
	var id = this.params.id;
	var name = this.params.name;
	var fileList = [];
	var _path = path.join(__dirname,'uploadFile',id);
	var filenames = fs.readdirSync(_path);
	for(var i = 0 ; i < filenames.length ; i++){
		var file = fs.readFileSync(path.join(_path,name+"_"+i), '');
		fileList.push(file);
	}
	console.log(fileList.length);
	var buffer = Buffer.concat(fileList);
	console.log(Buffer.byteLength(buffer));
	this.set('Content-Length',Buffer.byteLength(buffer));
	this.type = contentType(name);
	this.body = buffer;
})


app
  .use(cors())
  .use(_static)
  .use(serve(path.join(__dirname,'uploadFile')))
  .use(router.routes())
  .use(router.allowedMethods())
  .listen(8083);
