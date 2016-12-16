# web上传

web上传在网站开发中经常会用到，web上传在很早前就有，可通过表单提交实现上传，

后来flash插件大举进入网站，使用flash实现可以实现强调的上传功能

在后来有了HTML5,ajax支持了上传文件，不需要flash也能实现强大的上传。 

本栗子只做了简单的上传Demo，想了解web上传相关可以查看[聊聊web上传](http://huangxiangsai.github.io/2016/11/08/talk-web-upload/)

## 提供了两个版本的上传服务

node版web上传 、 java版web上传

## 如何使用demo

### node版web上传

* 进入node-upload目录
* 使用npm i 安装依赖(当然已经配置了cnpm，也可以使用cnpm)
* 执行命令npm start启动服务(将会启动两个服务：web服务，websocket服务)
* 打开浏览器访问静态文件127.0.0.1:8083/index.html

### java版web上传

* 导入到IDE
* 加载maven依赖
* 启动jetty(当然也可以其他web服务)
* 打开浏览器访问静态文件127.0.0.1:8888/static/index.html 
* 执行com.devsai.ws.EventServer.java的main方法启动websocket服务


### 跨域资源共享（用于测试上传跨域的简单请求及预检测请求）

此部分内容为<a href="" target="_blank">你所不知道的跨域资源共享(CORS)</a>

以node版为例，
在node版服务端中添加了跨域资源共享的支持，并添加了**corsUpload**路由接口

通过访问`http://127.0.0.1:8083/index.html` >> 跨域上传 进入页面

html页面路径为**/static/uploadMethod/corsUpload.html**

打开控制台，上传文件，观察上传的请求情况， 

也可修改html页面中JS，根据博文中所说的，进行修改，再次上传，观察上传请求情况。

