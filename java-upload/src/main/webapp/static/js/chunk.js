

    function CuteFile(file, chunkSize,onprogress,cutomsSuccess) {
        this.uid = (new Date()).getTime();
        chunkSize =  chunkSize || 2 * 1024 * 1024;
        var pending = [],
            blob = file,
            total = blob.size,
            chunks = chunkSize ? Math.ceil(total / chunkSize) : 1,
            start = 0,
            index = 0,
            len, api;

        while (index < chunks) {
            len = Math.min(chunkSize, total - start);

            pending.push({
                file: file,
                start: start,
                end: chunkSize ? (start + len) : total,
                total: total,
                chunks: chunks,
                chunk: index++,
                cuted: api
            });
            start += len;
        }

        file.blocks = pending.concat();
        file.remaning = pending.length;
        this.file = file;
        this.pending = pending;
        this.onprogress = onprogress;
        this.cutomsSuccess = cutomsSuccess;
    }

    CuteFile.prototype = {
        has: function() {
            return !!this.pending.length;
        },

        shift: function() {
            return this.pending.shift();
        },

        unshift: function(block) {
            this.pending.unshift(block);
        },
        stop : function(){
            if(!this.xhr) return;
            this.xhr.upload.onprogress  = null;
            this.xhr.onreadystatechange = null;
            this.xhr.abort();
            this.unshift(this.currBlock);
            this.isStart = false;
        },
        start : function(){
            this.isStart = true;
            this.send();
        },
        switchs : function(){
            if(this.isStart){
                this.stop();
            }else{
                this.start();
            }
        },
        success : function(){
            this.cutomsSuccess && this.cutomsSuccess(this.file.uid);
        },
        send : function (){
           var cuteAPI = this;
           if(!cuteAPI.has() ){
                this.isStart = false;
                this.success();
               return false;
           }
           var block = cuteAPI.shift();
           var file = block.file;
           var f = file.slice(block.start, block.end);

           var chunks = block.chunks;
           var currchunk = block.chunk;
           var xhr = new XMLHttpRequest();
           xhr.open('POST','/partupload',true);
           var fd = new FormData();
           xhr.upload.onprogress = function(data){
            var result = {};
            result.total = block.total;
            result.loaded = block.start + data.loaded;

            //防止进度条后退
            console.log(cuteAPI.currLoaded,result.loaded);
            if(cuteAPI.currLoaded > result.loaded){
                result.loaded = this.currLoaded;
            }else{
                cuteAPI.currLoaded = result.loaded;
            }
            //total
            cuteAPI.onprogress && cuteAPI.onprogress(file,result);
           }
           xhr.onreadystatechange = function() {
               if (xhr.readyState == 4 && xhr.status == 200) {
                   console.log(new Date());
                 // Every thing ok, file uploaded
                 console.log(xhr.responseText); // handle response.
                   var res = JSON.parse(xhr.responseText);
                   if(res.code ==200){
                       cuteAPI.send();
                   }
               }
           };

            fd.append("upload_file", f);
            fd.append("filename",file.name);
            fd.append('chunks',chunks);
            fd.append('currchunk',currchunk);
            fd.append('uid',this.uid);
            console.log("send:");
            console.log(new Date());
            xhr.send(fd);
            this.currBlock = block;
            this.xhr = xhr;
       }
    }



