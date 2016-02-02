#!/usr/bin/env python2.7

import urllib2
import json

urls={"send_code":"http://localhost:8080/common/send_code",
      "register":"http://localhost:8080/account/register",
      "login":"http://localhost:8080/account/login",
      "update":"http://localhost:8080/account/update",
      "list":"http://localhost:8080/patient/list"}

def ProcessResponse(response):
    if response["status"] == 1:
        print("[SUCCESS] {}".format(response["data"]))
        return True
    else:
        print("[FAILURE] {}".format(response["data"]))
        return False
    
def PostData(log, obj):
    post = json.dumps(obj);
    print("[INFO] {} POST: {}".format(log,post))
    return post

def HttpRequest(url,post_data):
    request = urllib2.Request(url)
    request.add_header("Content-Type","application/json")
    request.add_data(post_data)
    try:
        response = urllib2.urlopen(request,timeout=200)
    except Exception as e:
        print("[ECEPTION] HttpRequest - {}".format(e))
    return json.loads(response.read())

def GetCode(cate, phone, token):
    post = PostData("GetCode", {"cate":cate,"phone":phone,"token":token})
    response=HttpRequest(urls["send_code"],post)
    if ProcessResponse(response):
        return response["data"]["code"]
    else:
        return None
