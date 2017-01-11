//窗体改变大小时触发事件  
        window.onresize = setViewSize;  
        var marginLeft=0;  
        var marginTop=0;  
        //设置画布大小，登录页面显示在屏幕最中间  
        function setViewSize() {  
            //计算屏幕大小  
            var w=window.innerWidth  
            || document.documentElement.clientWidth  
            || document.body.clientWidth;  
            var h=window.innerHeight  
            || document.documentElement.clientHeight  
            || document.body.clientHeight;  
            //设置登陆div的位置  
            marginLeft = (w-468)/2;  
            marginTop = (h-262)/2;  
            document.getElementById("loginFormMain").style.marginLeft=marginLeft;  
            document.getElementById("loginFormMain").style.marginTop=marginTop;  
        }  
          
        //默认聚焦在用户名输入框上  
        function focusOnUsername() {  
            //调整画布大小和登陆框位置  
            setViewSize();  
            //默认聚焦在输入框上  
            if (document.loginForm) {  
                var usernameInput = document.loginForm.username;  
                if (usernameInput) {  
                    usernameInput.focus();  
                }  
            }  
            return;  
        }  
      
        //检查用户输入  
        function checkLogin(){  
            if(checkUsername() && checkPassword()){
                 if (check()){ 
                    return true; 
                 } 
             }          
            return false;  
        }  
        //检查登录用户名  
        function checkUsername(){  
            var username = document.loginForm.username;  
            if(username.value.length!=0){  
                return true;  
            }else{  
                alert("请输入用户名");  
                return false;  
            }  
        }  
        //检查登录密码  
        function checkPassword(){  
            var password = document.loginForm.password;  
            if(password.value.length!=0){  
                return true;  
            }else{  
                alert("请输入密码");  
                return false;  
            }  
        }

        //进行检查
        function check(){
            // window.alert($("username").value);
            if($("username").value=="pwd" && $("password").value=="123"){
                return true;
            }else{
                $("username").value="";
                $("password").value="";
                alert("The username and password are not corrected.");
                return false;
            }
        }
        //获取文档对象
        function $(id){
            return document.getElementById(id);
        }