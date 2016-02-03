#!/usr/bin/env python2.7

import common
import Login

def Update(token,name,gender,birth,id_number,address):
    post = common.PostData("AccountUpdate",{"token":token,"name":name,"gender":gender,"birth":birth,"id_number":id_number,"address":address})
    response = common.HttpRequest(common.urls["update"],post)
    if common.ProcessResponse(response):
        return response
    else:
        return None

if __name__ == "__main__":
    Update(Login.Login("18810346542","888888"),"qixiang",2,1454430141,"411502198804190538","test")
